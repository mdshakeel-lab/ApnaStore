package com.apnastore.service;

import com.apnastore.dto.OrderRequest;
import com.apnastore.dto.OrderResponse;
import com.apnastore.exception.InsufficientStockException;
import com.apnastore.model.Order;
import com.apnastore.model.Product;
import com.apnastore.model.User;
import com.apnastore.repository.OrderRepository;
import com.apnastore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("prod-silk-01");
        testProduct.setName("Royal Silk Kurta");
        testProduct.setPrice(200.0);
        testProduct.setStock(10);
    }

    @Test
    @DisplayName("Total calculation strictly uses database prices and ignores client input")
    void createOrder_CalculatesTotalFromDatabasePrices() {
        // Arrange
        when(productRepository.findById("prod-silk-01")).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = new OrderRequest();
        request.setFullName("Rohan Sharma");
        request.setEmail("rohan@example.com");
        request.setPhone("+91 98765 43210");
        request.setAddress("12 Palace Road");
        request.setItems(List.of(
            new OrderRequest.OrderItem("prod-silk-01", 2, "L", "Gold")
        ));

        // Act
        OrderResponse response = orderService.createOrder(request, null);

        // Assert: 2 items * $200.0 (from DB) = $400.0 subtotal. Over $300 => free shipping.
        assertThat(response.getSubtotal()).isEqualTo(400.0);
        assertThat(response.getDiscount()).isEqualTo(0.0);
        assertThat(response.getShippingFee()).isEqualTo(0.0);
        assertThat(response.getTotalAmount()).isEqualTo(400.0);
    }

    @Test
    @DisplayName("APNA20 promo code applies 20% discount on the calculated subtotal")
    void createOrder_AppliesTwentyPercentPromoDiscount() {
        // Arrange: 1 item at $500.0 DB price
        testProduct.setPrice(500.0);
        when(productRepository.findById("prod-silk-01")).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = new OrderRequest();
        request.setFullName("Priya Patel");
        request.setEmail("priya@example.com");
        request.setPhone("+91 98765 43210");
        request.setAddress("44 Heritage Lane");
        request.setPromoCode("APNA20");
        request.setItems(List.of(
            new OrderRequest.OrderItem("prod-silk-01", 1, "M", "Maroon")
        ));

        // Act
        OrderResponse response = orderService.createOrder(request, null);

        // Assert: $500 subtotal - 20% ($100) discount + $0 shipping = $400 total
        assertThat(response.getSubtotal()).isEqualTo(500.0);
        assertThat(response.getDiscount()).isEqualTo(100.0);
        assertThat(response.getTotalAmount()).isEqualTo(400.0);
    }

    @Test
    @DisplayName("Insufficient stock throws InsufficientStockException and halts order")
    void createOrder_WhenStockInsufficient_ThrowsException() {
        // Arrange: Product only has 2 in stock, request wants 5
        testProduct.setStock(2);
        when(productRepository.findById("prod-silk-01")).thenReturn(Optional.of(testProduct));

        OrderRequest request = new OrderRequest();
        request.setFullName("Aarav Gupta");
        request.setEmail("aarav@example.com");
        request.setPhone("+91 98765 43210");
        request.setAddress("14 Havelock Road");
        request.setItems(List.of(
            new OrderRequest.OrderItem("prod-silk-01", 5, "XL", "Navy")
        ));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request, null))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock for product 'Royal Silk Kurta'");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Product stock in database is reduced after a successful order")
    void createOrder_ReducesStockInDatabase() {
        // Arrange: Initial stock is 10, ordering 3 units
        when(productRepository.findById("prod-silk-01")).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = new OrderRequest();
        request.setFullName("Ananya Singh");
        request.setEmail("ananya@example.com");
        request.setPhone("+91 98765 43210");
        request.setAddress("88 Civil Lines");
        request.setItems(List.of(
            new OrderRequest.OrderItem("prod-silk-01", 3, "S", "Ivory")
        ));

        // Act
        orderService.createOrder(request, null);

        // Assert: Product saved with updated stock = 7 (10 - 3)
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("Querying non-existent order ID returns empty Optional")
    void getOrderById_WhenNotFound_ReturnsEmptyOptional() {
        // Arrange: Neither order number nor numeric ID matches
        when(orderRepository.findByOrderNumber("9999")).thenReturn(Optional.empty());
        when(orderRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act
        Optional<OrderResponse> result = orderService.getOrderById("9999");

        // Assert
        assertThat(result).isEmpty();
    }
}

