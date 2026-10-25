// Apna Store - Core Application Logic
import { PRODUCTS, CATEGORIES, PROMO_CODES, REVIEWS } from './data.js';

class ApnaStoreApp {
  constructor() {
    this.products = [...PRODUCTS];
    this.categories = [...CATEGORIES];
    this.activeCategory = 'all';
    this.searchQuery = '';
    this.selectedSize = 'all';
    this.maxPrice = 1200;
    this.sortBy = 'featured';

    // Persistent Local State
    this.cart = JSON.parse(localStorage.getItem('apna_store_cart') || localStorage.getItem('lumiere_cart')) || [
      { product: PRODUCTS[0], size: 'M', color: PRODUCTS[0].colors[0], quantity: 1 }
    ];
    this.wishlist = JSON.parse(localStorage.getItem('apna_store_wishlist') || localStorage.getItem('lumiere_wishlist')) || ['prod-02', 'prod-09'];
    
    this.activeModal = null; // 'quickview', 'cart', 'wishlist', 'checkout', 'receipt'
    this.quickViewProduct = null;
    this.selectedModalSize = null;
    this.selectedModalColor = null;
    
    this.checkoutStep = 1;
    this.appliedPromo = null;
    this.lastCompletedOrder = null;

    this.init();
  }

  async init() {
    this.initTheme();
    this.renderHeader();
    this.renderCategoryPills();
    this.renderTestimonials();
    this.setupEventListeners();
    await this.fetchProductsFromBackend();
    this.filterAndRenderProducts();
    this.updateBadges();
  }

  initTheme() {
    const savedTheme = localStorage.getItem('apna_store_theme') || 'light';
    this.setTheme(savedTheme);

    const toggleBtn = document.getElementById('theme-toggle-btn');
    if (toggleBtn) {
      toggleBtn.addEventListener('click', () => {
        const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
        const nextTheme = currentTheme === 'dark' ? 'light' : 'dark';
        this.setTheme(nextTheme);
        this.showToast(`Switched to ${nextTheme === 'dark' ? 'Dark' : 'Light White'} Mode`, nextTheme === 'dark' ? 'fa-moon' : 'fa-sun');
      });
    }
  }

  setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('apna_store_theme', theme);
    const icon = document.getElementById('theme-toggle-icon');
    if (icon) {
      icon.className = theme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
  }

  async fetchProductsFromBackend() {
    try {
      const res = await fetch('http://localhost:8080/api/products');
      if (res.ok) {
        const data = await res.json();
        if (Array.isArray(data) && data.length > 0) {
          this.products = data;
          console.log('✓ Connected to Java Spring Boot REST API! Loaded products from backend.');
        }
      }
    } catch (err) {
      console.log('ℹ Java Backend offline. Operating on client-side catalog mode.');
    }
  }

  saveState() {
    localStorage.setItem('apna_store_cart', JSON.stringify(this.cart));
    localStorage.setItem('apna_store_wishlist', JSON.stringify(this.wishlist));
    this.updateBadges();
  }

  updateBadges() {
    const cartCount = this.cart.reduce((acc, item) => acc + item.quantity, 0);
    const wishlistCount = this.wishlist.length;

    const cartBadge = document.getElementById('cart-badge');
    const wishlistBadge = document.getElementById('wishlist-badge');

    if (cartBadge) cartBadge.innerText = cartCount;
    if (wishlistBadge) wishlistBadge.innerText = wishlistCount;
  }

  // Toast Notifications
  showToast(message, icon = 'fa-shopping-bag') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `
      <i class="fas ${icon} toast-icon"></i>
      <div>
        <div style="font-weight: 700;">Apna Store</div>
        <div style="color: var(--text-muted); font-size: 0.85rem;">${message}</div>
      </div>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      setTimeout(() => toast.remove(), 300);
    }, 3000);
  }

  // Header & Event Binding
  renderHeader() {
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        this.searchQuery = e.target.value.toLowerCase().trim();
        this.filterAndRenderProducts();
      });
    }

    const cartBtn = document.getElementById('open-cart-btn');
    if (cartBtn) cartBtn.addEventListener('click', () => this.openDrawer('cart'));

    const wishlistBtn = document.getElementById('open-wishlist-btn');
    if (wishlistBtn) wishlistBtn.addEventListener('click', () => this.openDrawer('wishlist'));
  }

  // Render Category Filter Pills
  renderCategoryPills() {
    const container = document.getElementById('category-pills-container');
    if (!container) return;

    container.innerHTML = this.categories.map(cat => `
      <button class="category-pill ${this.activeCategory === cat.id ? 'active' : ''}" data-cat="${cat.id}">
        ${cat.name}
      </button>
    `).join('');

    container.querySelectorAll('.category-pill').forEach(btn => {
      btn.addEventListener('click', (e) => {
        this.activeCategory = e.currentTarget.dataset.cat;
        this.renderCategoryPills();
        this.filterAndRenderProducts();
      });
    });
  }

  // Setup Sidebar Filters & Sorting Events
  setupEventListeners() {
    // Sort Select
    const sortSelect = document.getElementById('sort-select');
    if (sortSelect) {
      sortSelect.addEventListener('change', (e) => {
        this.sortBy = e.target.value;
        this.filterAndRenderProducts();
      });
    }

    // Size Chips Filter
    const sizeChips = document.querySelectorAll('.size-chip');
    sizeChips.forEach(chip => {
      chip.addEventListener('click', (e) => {
        const size = e.currentTarget.dataset.size;
        this.selectedSize = (this.selectedSize === size) ? 'all' : size;
        
        sizeChips.forEach(c => c.classList.remove('active'));
        if (this.selectedSize !== 'all') {
          e.currentTarget.classList.add('active');
        }
        this.filterAndRenderProducts();
      });
    });

    // Price Range Slider
    const priceSlider = document.getElementById('price-range-slider');
    const priceDisplay = document.getElementById('max-price-display');
    if (priceSlider && priceDisplay) {
      priceSlider.addEventListener('input', (e) => {
        this.maxPrice = Number(e.target.value);
        priceDisplay.innerText = `₹${this.maxPrice}`;
        this.filterAndRenderProducts();
      });
    }

    // Close Drawers & Modals on Backdrop Click
    document.querySelectorAll('.drawer-backdrop, .modal-backdrop').forEach(backdrop => {
      backdrop.addEventListener('click', (e) => {
        if (e.target === backdrop) this.closeAllModals();
      });
    });

    // Close Buttons
    document.querySelectorAll('.close-btn, .modal-close-btn').forEach(btn => {
      btn.addEventListener('click', () => this.closeAllModals());
    });
  }

  // Product Filtering Engine
  filterAndRenderProducts() {
    let filtered = this.products.filter(product => {
      // Category filter
      if (this.activeCategory !== 'all') {
        if (product.category !== this.activeCategory && product.gender !== this.activeCategory) {
          return false;
        }
      }

      // Search filter
      if (this.searchQuery) {
        const matchesName = product.name.toLowerCase().includes(this.searchQuery);
        const matchesDesc = product.description.toLowerCase().includes(this.searchQuery);
        const matchesCat = product.category.toLowerCase().includes(this.searchQuery);
        if (!matchesName && !matchesDesc && !matchesCat) return false;
      }

      // Size filter
      if (this.selectedSize !== 'all') {
        if (!product.sizes.includes(this.selectedSize)) return false;
      }

      // Price filter
      if (product.price > this.maxPrice) return false;

      return true;
    });

    // Sorting Engine
    if (this.sortBy === 'price-low') {
      filtered.sort((a, b) => a.price - b.price);
    } else if (this.sortBy === 'price-high') {
      filtered.sort((a, b) => b.price - a.price);
    } else if (this.sortBy === 'rating') {
      filtered.sort((a, b) => b.rating - a.rating);
    }

    this.renderProductsGrid(filtered);
  }

  // Render Product Grid Cards
  renderProductsGrid(productsList) {
    const grid = document.getElementById('products-grid');
    const countEl = document.getElementById('results-count-number');
    
    if (countEl) countEl.innerText = productsList.length;
    if (!grid) return;

    if (productsList.length === 0) {
      grid.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 4rem 1rem; color: var(--text-muted);">
          <i class="fas fa-search" style="font-size: 3rem; color: var(--gold-primary); margin-bottom: 1rem;"></i>
          <h3 style="font-family: var(--font-serif); color: var(--text-primary);">No Pieces Found</h3>
          <p>Try resetting your search query or adjusting your filters.</p>
        </div>
      `;
      return;
    }

    grid.innerHTML = productsList.map(prod => {
      const isWishlisted = this.wishlist.includes(prod.id);
      return `
        <div class="product-card">
          <div class="product-image-area">
            ${prod.badge ? `<span class="badge-tag">${prod.badge}</span>` : ''}
            <button class="card-wishlist-btn ${isWishlisted ? 'active' : ''}" data-id="${prod.id}" title="Toggle Wishlist">
              <i class="${isWishlisted ? 'fas' : 'far'} fa-heart"></i>
            </button>
            <img src="${prod.image}" alt="${prod.name}" loading="lazy" />
            <div class="quick-view-overlay">
              <button class="btn-card-action add-cart" data-id="${prod.id}">
                <i class="fas fa-shopping-bag"></i> Add
              </button>
              <button class="btn-card-action quick-view" data-id="${prod.id}">
                <i class="fas fa-eye"></i> View
              </button>
            </div>
          </div>
          <div class="product-info">
            <span class="product-category-text">${prod.category}</span>
            <h4 class="product-name">${prod.name}</h4>
            <div class="rating-row">
              <i class="fas fa-star"></i>
              <span>${prod.rating} (${prod.reviewCount})</span>
            </div>
            <div class="price-row">
              <span class="current-price">₹${prod.price}</span>
              ${prod.originalPrice ? `<span class="original-price">₹${prod.originalPrice}</span>` : ''}
            </div>
          </div>
        </div>
      `;
    }).join('');

    // Attach Event Listeners to rendered cards
    grid.querySelectorAll('.card-wishlist-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        this.toggleWishlist(e.currentTarget.dataset.id);
      });
    });

    grid.querySelectorAll('.btn-card-action.add-cart').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const prod = this.products.find(p => p.id === e.currentTarget.dataset.id);
        if (prod) {
          this.addToCart(prod, prod.sizes[0], prod.colors[0], 1);
        }
      });
    });

    grid.querySelectorAll('.btn-card-action.quick-view').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const prod = this.products.find(p => p.id === e.currentTarget.dataset.id);
        if (prod) this.openQuickViewModal(prod);
      });
    });
  }

  // Wishlist Logic
  toggleWishlist(productId) {
    const index = this.wishlist.indexOf(productId);
    const prod = this.products.find(p => p.id === productId);

    if (index > -1) {
      this.wishlist.splice(index, 1);
      this.showToast(`Removed "${prod.name}" from Wishlist`, 'fa-heart-broken');
    } else {
      this.wishlist.push(productId);
      this.showToast(`Saved "${prod.name}" to Wishlist`, 'fa-heart');
    }

    this.saveState();
    this.filterAndRenderProducts();
    if (this.activeModal === 'wishlist') this.renderWishlistDrawer();
  }

  // Cart Management Logic
  addToCart(product, size, color, quantity = 1) {
    const existingIndex = this.cart.findIndex(
      item => item.product.id === product.id && item.size === size && item.color.name === color.name
    );

    if (existingIndex > -1) {
      this.cart[existingIndex].quantity += quantity;
    } else {
      this.cart.push({ product, size, color, quantity });
    }

    this.saveState();
    this.showToast(`Added ${quantity}x "${product.name}" to Cart`, 'fa-shopping-bag');
    this.openDrawer('cart');
  }

  updateCartQty(index, change) {
    if (this.cart[index]) {
      this.cart[index].quantity += change;
      if (this.cart[index].quantity <= 0) {
        this.cart.splice(index, 1);
      }
      this.saveState();
      this.renderCartDrawer();
    }
  }

  // Drawer Controls
  openDrawer(type) {
    this.closeAllModals();
    this.activeModal = type;

    if (type === 'cart') {
      const drawer = document.getElementById('cart-drawer-backdrop');
      if (drawer) drawer.classList.add('open');
      this.renderCartDrawer();
    } else if (type === 'wishlist') {
      const drawer = document.getElementById('wishlist-drawer-backdrop');
      if (drawer) drawer.classList.add('open');
      this.renderWishlistDrawer();
    }
  }

  closeAllModals() {
    document.querySelectorAll('.drawer-backdrop, .modal-backdrop').forEach(el => el.classList.remove('open'));
    this.activeModal = null;
  }

  // Cart Drawer Renderer
  renderCartDrawer() {
    const content = document.getElementById('cart-drawer-content');
    const footer = document.getElementById('cart-drawer-footer');
    const subtotalEl = document.getElementById('cart-subtotal');
    const totalEl = document.getElementById('cart-total');
    const freeShippingBar = document.getElementById('free-shipping-bar-fill');
    const freeShippingText = document.getElementById('free-shipping-text');

    if (!content) return;

    if (this.cart.length === 0) {
      content.innerHTML = `
        <div class="empty-state">
          <i class="fas fa-shopping-bag empty-state-icon"></i>
          <h4 style="font-family: var(--font-serif); font-size: 1.2rem; color: var(--text-primary); margin-bottom: 0.5rem;">Your Cart is Empty</h4>
          <p style="font-size: 0.85rem;">Discover our couture collection and elevate your wardrobe.</p>
        </div>
      `;
      if (footer) footer.style.display = 'none';
      if (freeShippingText) freeShippingText.innerHTML = `Spend <strong>₹3,000.00</strong> more to unlock Free Express Shipping`;
      if (freeShippingBar) freeShippingBar.style.width = '0%';
      return;
    }

    if (footer) footer.style.display = 'block';

    const subtotal = this.cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
    const shippingThreshold = 3000;
    const progressPercent = Math.min(100, (subtotal / shippingThreshold) * 100);

    if (freeShippingBar) freeShippingBar.style.width = `${progressPercent}%`;
    if (freeShippingText) {
      if (subtotal >= shippingThreshold) {
        freeShippingText.innerHTML = `<span style="color: #10b981; font-weight: 700;"><i class="fas fa-check-circle"></i> You have unlocked Complimentary Express Shipping!</span>`;
      } else {
        const remaining = (shippingThreshold - subtotal).toFixed(2);
        freeShippingText.innerHTML = `Add <strong>₹${remaining}</strong> more to get Free Express Shipping`;
      }
    }

    let discountAmount = 0;
    if (this.appliedPromo) {
      if (this.appliedPromo.fixed) {
        discountAmount = this.appliedPromo.discount;
      } else {
        discountAmount = subtotal * this.appliedPromo.discount;
      }
    }

    const finalTotal = Math.max(0, subtotal - discountAmount);

    if (subtotalEl) subtotalEl.innerText = `₹${subtotal.toFixed(2)}`;
    if (totalEl) totalEl.innerText = `₹${finalTotal.toFixed(2)}`;

    content.innerHTML = this.cart.map((item, index) => `
      <div class="cart-item">
        <img class="cart-item-img" src="${item.product.image}" alt="${item.product.name}" />
        <div class="cart-item-info">
          <h5 class="cart-item-name">${item.product.name}</h5>
          <div class="cart-item-meta">Size: ${item.size} | Color: ${item.color.name}</div>
          <div class="cart-qty-controls">
            <button class="qty-btn minus-btn" data-index="${index}">-</button>
            <span class="qty-num">${item.quantity}</span>
            <button class="qty-btn plus-btn" data-index="${index}">+</button>
          </div>
        </div>
        <div style="display: flex; flex-direction: column; align-items: flex-end; justify-content: space-between;">
          <button class="remove-cart-item-btn" data-index="${index}" style="color: var(--text-muted); font-size: 0.9rem;" title="Remove Item">
            <i class="fas fa-times"></i>
          </button>
          <div class="cart-item-price">₹${(item.product.price * item.quantity).toFixed(2)}</div>
        </div>
      </div>
    `).join('');

    // Event handlers for quantity & delete buttons
    content.querySelectorAll('.minus-btn').forEach(btn => {
      btn.addEventListener('click', (e) => this.updateCartQty(Number(e.currentTarget.dataset.index), -1));
    });
    content.querySelectorAll('.plus-btn').forEach(btn => {
      btn.addEventListener('click', (e) => this.updateCartQty(Number(e.currentTarget.dataset.index), 1));
    });
    content.querySelectorAll('.remove-cart-item-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const index = Number(e.currentTarget.dataset.index);
        this.cart.splice(index, 1);
        this.saveState();
        this.renderCartDrawer();
      });
    });

    // Promo Code Application
    const applyPromoBtn = document.getElementById('apply-promo-btn');
    const promoInput = document.getElementById('promo-input');
    if (applyPromoBtn && promoInput) {
      applyPromoBtn.onclick = () => {
        const code = promoInput.value.trim().toUpperCase();
        if (PROMO_CODES[code]) {
          this.appliedPromo = PROMO_CODES[code];
          this.showToast(`Promo Code "${code}" Applied!`, 'fa-tag');
          this.renderCartDrawer();
        } else {
          this.showToast(`Invalid Promo Code. Try "APNA20"`, 'fa-exclamation-circle');
        }
      };
    }

    // Proceed to Checkout
    const checkoutBtn = document.getElementById('proceed-checkout-btn');
    if (checkoutBtn) {
      checkoutBtn.onclick = () => {
        this.openCheckoutModal();
      };
    }
  }

  // Wishlist Drawer Renderer
  renderWishlistDrawer() {
    const content = document.getElementById('wishlist-drawer-content');
    if (!content) return;

    const savedProducts = this.products.filter(p => this.wishlist.includes(p.id));

    if (savedProducts.length === 0) {
      content.innerHTML = `
        <div class="empty-state">
          <i class="far fa-heart empty-state-icon"></i>
          <h4 style="font-family: var(--font-serif); font-size: 1.2rem; color: var(--text-primary); margin-bottom: 0.5rem;">Wishlist is Empty</h4>
          <p style="font-size: 0.85rem;">Save your favorite runway pieces for later.</p>
        </div>
      `;
      return;
    }

    content.innerHTML = savedProducts.map(prod => `
      <div class="cart-item">
        <img class="cart-item-img" src="${prod.image}" alt="${prod.name}" />
        <div class="cart-item-info">
          <h5 class="cart-item-name">${prod.name}</h5>
          <div class="cart-item-meta">${prod.category}</div>
          <div style="font-weight: 700; color: var(--gold-primary);">₹${prod.price}</div>
        </div>
        <div style="display: flex; flex-direction: column; gap: 0.5rem; justify-content: center;">
          <button class="btn-card-action add-cart move-to-cart-btn" data-id="${prod.id}" style="padding: 0.4rem 0.8rem; font-size: 0.75rem;">
            <i class="fas fa-shopping-bag"></i> Move to Cart
          </button>
          <button class="remove-wishlist-item" data-id="${prod.id}" style="color: var(--text-muted); font-size: 0.8rem; align-self: flex-end;">
            Remove
          </button>
        </div>
      </div>
    `).join('');

    content.querySelectorAll('.move-to-cart-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const prod = this.products.find(p => p.id === e.currentTarget.dataset.id);
        if (prod) {
          this.addToCart(prod, prod.sizes[0], prod.colors[0], 1);
          this.toggleWishlist(prod.id);
        }
      });
    });

    content.querySelectorAll('.remove-wishlist-item').forEach(btn => {
      btn.addEventListener('click', (e) => {
        this.toggleWishlist(e.currentTarget.dataset.id);
      });
    });
  }

  // Quick View Modal
  openQuickViewModal(product) {
    this.closeAllModals();
    this.quickViewProduct = product;
    this.selectedModalSize = product.sizes[0];
    this.selectedModalColor = product.colors[0];

    const modal = document.getElementById('quickview-modal-backdrop');
    if (!modal) return;

    document.getElementById('modal-product-img').src = product.image;
    document.getElementById('modal-product-title').innerText = product.name;
    document.getElementById('modal-product-price').innerText = `₹${product.price}`;
    document.getElementById('modal-product-desc').innerText = product.description;

    // Render Size Options
    const sizeContainer = document.getElementById('modal-size-chips');
    if (sizeContainer) {
      sizeContainer.innerHTML = product.sizes.map(size => `
        <button class="size-chip ${this.selectedModalSize === size ? 'active' : ''}" data-size="${size}">${size}</button>
      `).join('');

      sizeContainer.querySelectorAll('.size-chip').forEach(btn => {
        btn.addEventListener('click', (e) => {
          sizeContainer.querySelectorAll('.size-chip').forEach(c => c.classList.remove('active'));
          e.currentTarget.classList.add('active');
          this.selectedModalSize = e.currentTarget.dataset.size;
        });
      });
    }

    // Render Color Options
    const colorContainer = document.getElementById('modal-color-swatches');
    if (colorContainer) {
      colorContainer.innerHTML = product.colors.map(color => `
        <div class="color-dot ${this.selectedModalColor.name === color.name ? 'active' : ''}" 
             style="background-color: ${color.hex};" 
             title="${color.name}" 
             data-name="${color.name}"></div>
      `).join('');

      colorContainer.querySelectorAll('.color-dot').forEach(dot => {
        dot.addEventListener('click', (e) => {
          colorContainer.querySelectorAll('.color-dot').forEach(d => d.classList.remove('active'));
          e.currentTarget.classList.add('active');
          const colorObj = product.colors.find(c => c.name === e.currentTarget.dataset.name);
          if (colorObj) this.selectedModalColor = colorObj;
        });
      });
    }

    // Modal Add to Cart Button
    const addBtn = document.getElementById('modal-add-cart-btn');
    if (addBtn) {
      addBtn.onclick = () => {
        this.addToCart(this.quickViewProduct, this.selectedModalSize, this.selectedModalColor, 1);
        this.closeAllModals();
      };
    }

    modal.classList.add('open');
  }

  // Checkout Multi-Step Modal
  openCheckoutModal() {
    this.closeAllModals();
    this.checkoutStep = 1;

    const modal = document.getElementById('checkout-modal-backdrop');
    if (!modal) return;

    this.renderCheckoutStep();
    modal.classList.add('open');
  }

  renderCheckoutStep() {
    const step1El = document.getElementById('checkout-step-1');
    const step2El = document.getElementById('checkout-step-2');
    const stepIndicator1 = document.getElementById('step-indicator-1');
    const stepIndicator2 = document.getElementById('step-indicator-2');

    if (this.checkoutStep === 1) {
      if (step1El) step1El.style.display = 'block';
      if (step2El) step2El.style.display = 'none';
      if (stepIndicator1) stepIndicator1.classList.add('active');
      if (stepIndicator2) stepIndicator2.classList.remove('active');
    } else {
      if (step1El) step1El.style.display = 'none';
      if (step2El) step2El.style.display = 'block';
      if (stepIndicator2) stepIndicator2.classList.add('active');
    }

    // Step 1 to Step 2 Transition
    const toStep2Btn = document.getElementById('to-step-2-btn');
    if (toStep2Btn) {
      toStep2Btn.onclick = (e) => {
        e.preventDefault();
        this.checkoutStep = 2;
        this.renderCheckoutStep();
      };
    }

    // Submit Order (Java Spring Boot API + Fallback)
    const submitOrderBtn = document.getElementById('submit-order-btn');
    if (submitOrderBtn) {
      submitOrderBtn.onclick = async (e) => {
        e.preventDefault();
        
        const subtotal = this.cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
        let discount = 0;
        if (this.appliedPromo) {
          discount = this.appliedPromo.fixed ? this.appliedPromo.discount : subtotal * this.appliedPromo.discount;
        }

        const orderPayload = {
          fullName: document.getElementById('checkout-name')?.value || 'Valued Client',
          email: document.getElementById('checkout-email')?.value || 'client@apnastore.com',
          phone: document.getElementById('checkout-phone')?.value || '+91 98765 43210',
          address: document.getElementById('checkout-address')?.value || '108 Heritage Boulevard',
          city: document.getElementById('checkout-city')?.value || 'New Delhi',
          postalCode: document.getElementById('checkout-zip')?.value || '110001',
          paymentMethod: 'Credit Card',
          promoCode: this.appliedPromo ? (this.appliedPromo.code || 'APNA20') : null,
          items: this.cart.map(item => ({
            productId: item.product.id,
            quantity: item.quantity,
            selectedSize: item.size,
            selectedColor: item.color?.name
          }))
        };

        try {
          const res = await fetch('http://localhost:8080/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderPayload)
          });

          if (res.ok) {
            const apiOrder = await res.json();
            this.lastCompletedOrder = {
              id: apiOrder.orderId,
              date: apiOrder.date,
              items: [...this.cart],
              subtotal: apiOrder.subtotal,
              discount: apiOrder.discount,
              total: apiOrder.totalAmount,
              shippingName: apiOrder.customerName
            };
            this.showToast('Order confirmed by Java Spring Boot backend!', 'fa-check-circle');
          } else {
            throw new Error('Backend returned non-200 status');
          }
        } catch (err) {
          // Fallback simulation if backend offline
          this.lastCompletedOrder = {
            id: `APNA-${Math.floor(100000 + Math.random() * 900000)}`,
            date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
            items: [...this.cart],
            subtotal,
            discount,
            total: Math.max(0, subtotal - discount),
            shippingName: orderPayload.fullName
          };
        }

        // Clear Cart
        this.cart = [];
        this.appliedPromo = null;
        this.saveState();

        this.closeAllModals();
        this.showOrderReceipt();
      };
    }
  }

  // Order Confirmation & Printable Receipt Modal
  showOrderReceipt() {
    if (!this.lastCompletedOrder) return;

    const modal = document.getElementById('receipt-modal-backdrop');
    if (!modal) return;

    document.getElementById('receipt-order-id').innerText = this.lastCompletedOrder.id;
    document.getElementById('receipt-date').innerText = this.lastCompletedOrder.date;
    document.getElementById('receipt-total').innerText = `₹${this.lastCompletedOrder.total.toFixed(2)}`;

    const itemsContainer = document.getElementById('receipt-items-list');
    if (itemsContainer) {
      itemsContainer.innerHTML = this.lastCompletedOrder.items.map(item => `
        <div style="display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px dashed var(--border-subtle); font-size: 0.9rem;">
          <div>
            <strong>${item.product.name}</strong> x${item.quantity}
            <div style="font-size: 0.8rem; color: var(--text-muted);">${item.size} / ${item.color.name}</div>
          </div>
          <div style="font-weight: 700; color: var(--gold-primary);">₹${(item.product.price * item.quantity).toFixed(2)}</div>
        </div>
      `).join('');
    }

    modal.classList.add('open');

    const printBtn = document.getElementById('print-receipt-btn');
    if (printBtn) {
      printBtn.onclick = () => window.print();
    }
  }

  // Render Reviews Testimonials
  renderTestimonials() {
    const container = document.getElementById('reviews-grid');
    if (!container) return;

    container.innerHTML = REVIEWS.map(rev => `
      <div class="review-card">
        <div style="color: var(--gold-primary); font-size: 0.9rem; margin-bottom: 0.75rem;">
          ${'<i class="fas fa-star"></i>'.repeat(rev.rating)}
        </div>
        <p class="review-quote">"${rev.comment}"</p>
        <div class="reviewer-info">
          <img class="reviewer-avatar" src="${rev.avatar}" alt="${rev.author}" />
          <div>
            <h5 style="font-size: 0.95rem; font-weight: 700;">${rev.author}</h5>
            <span style="font-size: 0.8rem; color: var(--gold-primary);">${rev.role}</span>
          </div>
        </div>
      </div>
    `).join('');
  }
}

// Initialize App on DOM Content Loaded
document.addEventListener('DOMContentLoaded', () => {
  window.apnaStoreApp = new ApnaStoreApp();
  window.lumiereApp = window.apnaStoreApp;
});
