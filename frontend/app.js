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
    
    this.activeModal = null; // 'quickview', 'cart', 'wishlist', 'checkout', 'receipt', 'auth'
    this.quickViewProduct = null;
    this.selectedModalSize = null;
    this.selectedModalColor = null;
    
    this.checkoutStep = 1;
    this.appliedPromo = null;
    this.lastCompletedOrder = null;

    // Auth state — token and user info persist in localStorage
    this.authToken = localStorage.getItem('apna_auth_token') || null;
    this.authUser  = JSON.parse(localStorage.getItem('apna_auth_user') || 'null');
    this.pendingCheckout = false;

    this.init();
  }

  async init() {
    this.initTheme();
    this.renderHeader();
    this.renderCategoryPills();
    this.renderTestimonials();
    this.setupEventListeners();
    this.initAuth();
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
        const items = Array.isArray(data) ? data : (data && Array.isArray(data.content) ? data.content : []);
        if (items.length > 0) {
          this.products = items;
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

    // Auth & Account navigation buttons
    const navLoginBtn = document.getElementById('nav-login-btn');
    if (navLoginBtn) navLoginBtn.addEventListener('click', () => this.openAuthModal());

    const navUserBtn = document.getElementById('nav-user-btn');
    if (navUserBtn) navUserBtn.addEventListener('click', () => this.openAuthModal());

    const navOrdersBtn = document.getElementById('nav-my-orders-btn');
    if (navOrdersBtn) navOrdersBtn.addEventListener('click', () => this.openOrdersModal());

    const navLogoutBtn = document.getElementById('nav-logout-btn');
    if (navLogoutBtn) navLogoutBtn.addEventListener('click', () => this.handleLogout());

    const authNavBtn = document.getElementById('auth-nav-btn');
    if (authNavBtn) authNavBtn.addEventListener('click', () => this.openAuthModal());

    const receiptOrdersBtn = document.getElementById('receipt-view-orders-btn');
    if (receiptOrdersBtn) receiptOrdersBtn.addEventListener('click', () => this.openOrdersModal());
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
    // Requirement 4: If not logged in, prompt to log in and open auth modal
    if (!this.authToken || !this.authUser) {
      this.pendingCheckout = true;
      this.showToast('Please sign in or register to complete your order.', 'fa-lock');
      this.openAuthModal();
      return;
    }

    this.closeAllModals();
    this.activeModal = 'checkout';
    this.checkoutStep = 1;

    const modal = document.getElementById('checkout-modal-backdrop');
    if (!modal) return;

    // Prefill user details from authenticated session
    const nameInput = document.getElementById('checkout-name');
    const emailInput = document.getElementById('checkout-email');
    if (nameInput && (!nameInput.value || nameInput.value === 'Valued Client') && this.authUser?.name) {
      nameInput.value = this.authUser.name;
    }
    if (emailInput && (!emailInput.value || emailInput.value === 'client@apnastore.com') && this.authUser?.email) {
      emailInput.value = this.authUser.email;
    }

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
          const headers = { 'Content-Type': 'application/json' };
          if (this.authToken) headers['Authorization'] = `Bearer ${this.authToken}`;

          const res = await fetch('http://localhost:8080/api/orders', {
            method: 'POST',
            headers,
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
          } else if (res.status === 401) {
            // Token expired or invalid — log user out and prompt to sign in again
            this.handleLogout();
            this.pendingCheckout = true;
            this.showToast('Your session has expired. Please sign in again.', 'fa-exclamation-triangle');
            this.openAuthModal();
            return;
          } else {
            const errData = await res.json().catch(() => null);
            let errMsg = 'Order placement failed.';
            if (errData) {
              if (errData.fieldErrors && Object.keys(errData.fieldErrors).length > 0) {
                const firstKey = Object.keys(errData.fieldErrors)[0];
                errMsg = errData.fieldErrors[firstKey];
              } else if (errData.message) {
                errMsg = errData.message;
              }
            } else if (res.status === 409) {
              errMsg = 'Item is out of stock!';
            }
            this.showToast(errMsg, 'fa-exclamation-triangle');
            return;
          }
        } catch (err) {
          // Fallback simulation only if backend offline (network failure)
          console.warn('Backend unavailable, using client fallback:', err);
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

  // ============================================================
  // Auth & Account Management — Login, Register, Logout, My Orders
  // ============================================================

  /** Called once in init() — restores persisted session and wires up auth elements. */
  initAuth() {
    this.updateAuthNavButton();

    // Form submissions
    const loginForm    = document.getElementById('auth-login-form');
    const registerForm = document.getElementById('auth-register-form');
    const logoutBtn    = document.getElementById('auth-logout-btn');
    const tabLogin     = document.getElementById('auth-tab-login');
    const tabRegister  = document.getElementById('auth-tab-register');
    const profileOrdersBtn = document.getElementById('auth-profile-orders-btn');

    if (loginForm) {
      loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await this.handleLogin();
      });
    }

    if (registerForm) {
      registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await this.handleRegister();
      });
    }

    if (tabLogin) {
      tabLogin.addEventListener('click', () => this.switchAuthTab('login'));
    }

    if (tabRegister) {
      tabRegister.addEventListener('click', () => this.switchAuthTab('register'));
    }

    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => this.handleLogout());
    }

    if (profileOrdersBtn) {
      profileOrdersBtn.addEventListener('click', () => this.openOrdersModal());
    }

    // Clear field-level and server errors on input
    const inputsToClear = [
      'auth-login-email', 'auth-login-password',
      'auth-reg-name', 'auth-reg-email', 'auth-reg-password', 'auth-reg-confirm-password'
    ];
    inputsToClear.forEach(id => {
      const el = document.getElementById(id);
      if (el) {
        el.addEventListener('input', () => {
          const fieldError = document.getElementById(`${id}-error`);
          if (fieldError) {
            fieldError.innerText = '';
            fieldError.classList.remove('show');
          }
          const serverError = id.startsWith('auth-login')
            ? document.getElementById('auth-login-error')
            : document.getElementById('auth-reg-error');
          if (serverError) serverError.style.display = 'none';
        });
      }
    });
  }

  /** Helper: validate email address format */
  isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  /** Helper: show error below an input field */
  setFieldError(fieldId, message) {
    const errorEl = document.getElementById(`${fieldId}-error`);
    if (errorEl) {
      errorEl.innerText = message;
      errorEl.classList.add('show');
    }
  }

  /** Helper: clear all field error messages */
  clearAllFieldErrors() {
    document.querySelectorAll('.auth-field-error').forEach(el => {
      el.innerText = '';
      el.classList.remove('show');
    });
    const loginServerError = document.getElementById('auth-login-error');
    if (loginServerError) loginServerError.style.display = 'none';
    const regServerError = document.getElementById('auth-reg-error');
    if (regServerError) regServerError.style.display = 'none';
  }

  /** Open the auth modal and show the correct panel based on login state. */
  openAuthModal() {
    this.closeAllModals();
    this.activeModal = 'auth';
    const modal = document.getElementById('auth-modal-backdrop');
    if (!modal) return;

    this.clearAllFieldErrors();
    this._renderAuthModalState();
    modal.classList.add('open');
  }

  /** Switch between the login and register tab forms. */
  switchAuthTab(tab) {
    this.clearAllFieldErrors();
    const loginForm    = document.getElementById('auth-login-form');
    const registerForm = document.getElementById('auth-register-form');
    const tabLogin     = document.getElementById('auth-tab-login');
    const tabRegister  = document.getElementById('auth-tab-register');

    if (tab === 'login') {
      if (loginForm)    loginForm.style.display    = 'block';
      if (registerForm) registerForm.style.display = 'none';
      if (tabLogin)     tabLogin.classList.add('active');
      if (tabRegister)  tabRegister.classList.remove('active');
    } else {
      if (loginForm)    loginForm.style.display    = 'none';
      if (registerForm) registerForm.style.display = 'block';
      if (tabLogin)     tabLogin.classList.remove('active');
      if (tabRegister)  tabRegister.classList.add('active');
    }
  }

  /** Render the modal content based on login state. */
  _renderAuthModalState() {
    const loginForm      = document.getElementById('auth-login-form');
    const registerForm   = document.getElementById('auth-register-form');
    const loggedinPanel  = document.getElementById('auth-loggedin-panel');
    const tabBar         = document.querySelector('#auth-modal-backdrop .auth-tabs-header');

    if (this.authUser) {
      // Logged in — show user profile panel
      if (loginForm)     loginForm.style.display     = 'none';
      if (registerForm)  registerForm.style.display  = 'none';
      if (tabBar)        tabBar.style.display        = 'none';
      if (loggedinPanel) {
        loggedinPanel.style.display = 'block';
        const nameEl  = document.getElementById('auth-loggedin-name');
        const emailEl = document.getElementById('auth-loggedin-email');
        if (nameEl)  nameEl.innerText  = this.authUser.name;
        if (emailEl) emailEl.innerText = this.authUser.email;
      }
    } else {
      // Logged out — show login form by default
      if (loginForm)     loginForm.style.display     = 'block';
      if (registerForm)  registerForm.style.display  = 'none';
      if (tabBar)        tabBar.style.display        = 'flex';
      if (loggedinPanel) loggedinPanel.style.display = 'none';

      const tabLogin    = document.getElementById('auth-tab-login');
      const tabRegister = document.getElementById('auth-tab-register');
      if (tabLogin)    tabLogin.classList.add('active');
      if (tabRegister) tabRegister.classList.remove('active');
    }
  }

  /** Handle the login form submission with validation & server error handling. */
  async handleLogin() {
    this.clearAllFieldErrors();

    const emailInput    = document.getElementById('auth-login-email');
    const passwordInput = document.getElementById('auth-login-password');
    const email         = emailInput ? emailInput.value.trim() : '';
    const password      = passwordInput ? passwordInput.value : '';
    const errorEl       = document.getElementById('auth-login-error');
    const btn           = document.getElementById('auth-login-btn');

    // Client-side validation
    let hasError = false;
    if (!email) {
      this.setFieldError('auth-login-email', 'Email address is required.');
      hasError = true;
    } else if (!this.isValidEmail(email)) {
      this.setFieldError('auth-login-email', 'Please enter a valid email address.');
      hasError = true;
    }

    if (!password) {
      this.setFieldError('auth-login-password', 'Password is required.');
      hasError = true;
    }

    if (hasError) return;

    if (btn) {
      btn.disabled = true;
      btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Signing In...';
    }

    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await res.json();

      if (res.ok) {
        this._saveAuthSession(data);
        if (!this.pendingCheckout) {
          this.closeAllModals();
          this.showToast(`Welcome back, ${data.name}!`, 'fa-user-circle');
        }
      } else {
        const msg = data.message || 'Login failed. Please check your credentials.';
        if (errorEl) {
          errorEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${msg}`;
          errorEl.style.display = 'flex';
        }
      }
    } catch (err) {
      if (errorEl) {
        errorEl.innerHTML = '<i class="fas fa-wifi"></i> Cannot reach server. Please ensure the backend is running.';
        errorEl.style.display = 'flex';
      }
    } finally {
      if (btn) {
        btn.disabled = false;
        btn.innerHTML = 'Sign In <i class="fas fa-arrow-right"></i>';
      }
    }
  }

  /** Handle the register form submission with validation & server error handling. */
  async handleRegister() {
    this.clearAllFieldErrors();

    const nameInput        = document.getElementById('auth-reg-name');
    const emailInput       = document.getElementById('auth-reg-email');
    const passwordInput    = document.getElementById('auth-reg-password');
    const confirmPassInput = document.getElementById('auth-reg-confirm-password');

    const name            = nameInput ? nameInput.value.trim() : '';
    const email           = emailInput ? emailInput.value.trim() : '';
    const password        = passwordInput ? passwordInput.value : '';
    const confirmPassword = confirmPassInput ? confirmPassInput.value : '';

    const errorEl         = document.getElementById('auth-reg-error');
    const btn             = document.getElementById('auth-reg-btn');

    // Client-side validation
    let hasError = false;
    if (!name) {
      this.setFieldError('auth-reg-name', 'Full name is required.');
      hasError = true;
    } else if (name.length < 2) {
      this.setFieldError('auth-reg-name', 'Name must be at least 2 characters.');
      hasError = true;
    }

    if (!email) {
      this.setFieldError('auth-reg-email', 'Email address is required.');
      hasError = true;
    } else if (!this.isValidEmail(email)) {
      this.setFieldError('auth-reg-email', 'Please enter a valid email address.');
      hasError = true;
    }

    if (!password) {
      this.setFieldError('auth-reg-password', 'Password is required.');
      hasError = true;
    } else if (password.length < 6) {
      this.setFieldError('auth-reg-password', 'Password must be at least 6 characters.');
      hasError = true;
    }

    if (!confirmPassword) {
      this.setFieldError('auth-reg-confirm-password', 'Please confirm your password.');
      hasError = true;
    } else if (password !== confirmPassword) {
      this.setFieldError('auth-reg-confirm-password', 'Passwords do not match.');
      hasError = true;
    }

    if (hasError) return;

    if (btn) {
      btn.disabled = true;
      btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating Account...';
    }

    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
      });

      const data = await res.json();

      if (res.ok) {
        this._saveAuthSession(data);
        if (!this.pendingCheckout) {
          this.closeAllModals();
          this.showToast(`Account created! Welcome to Apna Store, ${data.name}!`, 'fa-crown');
        }
      } else {
        // Show field errors if present
        let topMsg = data.message || 'Registration failed.';
        if (data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
          Object.entries(data.fieldErrors).forEach(([field, msg]) => {
            if (field === 'name') this.setFieldError('auth-reg-name', msg);
            else if (field === 'email') this.setFieldError('auth-reg-email', msg);
            else if (field === 'password') this.setFieldError('auth-reg-password', msg);
          });
          topMsg = Object.values(data.fieldErrors)[0];
        }
        if (errorEl) {
          errorEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${topMsg}`;
          errorEl.style.display = 'flex';
        }
      }
    } catch (err) {
      if (errorEl) {
        errorEl.innerHTML = '<i class="fas fa-wifi"></i> Cannot reach server. Please ensure the backend is running.';
        errorEl.style.display = 'flex';
      }
    } finally {
      if (btn) {
        btn.disabled = false;
        btn.innerHTML = 'Create Account <i class="fas fa-crown"></i>';
      }
    }
  }

  /** Clear the auth session and refresh the navbar. */
  handleLogout() {
    localStorage.removeItem('apna_auth_token');
    localStorage.removeItem('apna_auth_user');
    this.authToken = null;
    this.authUser  = null;
    this.pendingCheckout = false;
    this.updateAuthNavButton();
    this.closeAllModals();
    this.showToast('You have been logged out.', 'fa-sign-out-alt');
  }

  /** Persist auth session to localStorage and update UI. */
  _saveAuthSession(data) {
    this.authToken = data.token;
    this.authUser  = { id: data.id, name: data.name, email: data.email, role: data.role };
    localStorage.setItem('apna_auth_token', data.token);
    localStorage.setItem('apna_auth_user',  JSON.stringify(this.authUser));
    this.updateAuthNavButton();

    // If a checkout was waiting for user login/registration, continue seamlessly!
    if (this.pendingCheckout) {
      this.pendingCheckout = false;
      this.closeAllModals();
      this.showToast(`Welcome ${data.name}! Continuing to checkout...`, 'fa-check');
      setTimeout(() => this.openCheckoutModal(), 350);
    }
  }

  /** Update the auth button/bar in the navbar to reflect login state. */
  updateAuthNavButton() {
    const navLoginBtn = document.getElementById('nav-login-btn');
    const navUserBar  = document.getElementById('nav-user-bar');
    const navUserName = document.getElementById('nav-user-name');

    if (this.authUser) {
      const firstName = this.authUser.name.split(' ')[0];
      if (navLoginBtn) navLoginBtn.style.display = 'none';
      if (navUserBar)  navUserBar.style.display  = 'flex';
      if (navUserName) navUserName.innerText     = firstName;
    } else {
      if (navLoginBtn) navLoginBtn.style.display = 'inline-flex';
      if (navUserBar)  navUserBar.style.display  = 'none';
    }

    // Backwards compatibility with single icon button if present
    const icon  = document.getElementById('auth-nav-icon');
    const label = document.getElementById('auth-nav-label');
    if (icon && label) {
      if (this.authUser) {
        icon.className      = 'fas fa-user-circle';
        label.innerText     = this.authUser.name.split(' ')[0];
        label.style.display = 'inline';
      } else {
        icon.className      = 'fas fa-user';
        label.innerText     = '';
        label.style.display = 'none';
      }
    }
  }

  /** Open the "My Orders" modal and fetch orders from the backend. */
  async openOrdersModal() {
    if (!this.authToken || !this.authUser) {
      this.showToast('Please sign in to view your orders.', 'fa-lock');
      this.openAuthModal();
      return;
    }

    this.closeAllModals();
    this.activeModal = 'orders';
    const modal = document.getElementById('orders-modal-backdrop');
    if (!modal) return;

    modal.classList.add('open');
    await this.loadMyOrders();
  }

  /** Fetch the logged-in user's orders from GET /api/orders/my */
  async loadMyOrders() {
    const container = document.getElementById('orders-content');
    if (!container) return;

    container.innerHTML = `
      <div style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
        <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: var(--gold-primary); margin-bottom: 1rem;"></i>
        <p style="font-family: var(--font-serif); font-size: 1.1rem;">Retrieving your royal couture orders...</p>
      </div>
    `;

    try {
      const res = await fetch('http://localhost:8080/api/orders/my', {
        headers: {
          'Authorization': `Bearer ${this.authToken}`
        }
      });

      if (res.status === 401) {
        this.handleLogout();
        this.showToast('Your session has expired. Please sign in again.', 'fa-exclamation-triangle');
        this.openAuthModal();
        return;
      }

      if (!res.ok) {
        throw new Error(`Failed to load orders (HTTP ${res.status})`);
      }

      const orders = await res.json();
      this.renderMyOrders(orders);
    } catch (err) {
      container.innerHTML = `
        <div class="orders-empty-state">
          <i class="fas fa-exclamation-circle" style="font-size: 3rem; color: #ef4444; margin-bottom: 1rem;"></i>
          <h4 style="font-family: var(--font-serif); font-size: 1.25rem; margin-bottom: 0.5rem;">Could not load orders</h4>
          <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">
            ${err.message || 'There was an issue fetching your order history.'}
          </p>
          <button class="btn-primary" onclick="window.apnaStoreApp.loadMyOrders()">
            Try Again <i class="fas fa-redo"></i>
          </button>
        </div>
      `;
    }
  }

  /** Render the fetched orders list or an elegant empty state. */
  renderMyOrders(orders) {
    const container = document.getElementById('orders-content');
    if (!container) return;

    if (!Array.isArray(orders) || orders.length === 0) {
      container.innerHTML = `
        <div class="orders-empty-state">
          <i class="fas fa-box-open orders-empty-icon"></i>
          <h4 style="font-family: var(--font-serif); font-size: 1.35rem; margin-bottom: 0.5rem; color: var(--text-primary);">
            No Orders Yet
          </h4>
          <p style="color: var(--text-muted); font-size: 0.92rem; max-width: 400px; margin: 0 auto 1.75rem auto;">
            You have not placed any couture orders yet. Discover our handcrafted bridal lehengas, sherwanis, and Kundan jewelry.
          </p>
          <button class="btn-primary" onclick="window.apnaStoreApp.closeAllModals()">
            Explore Collection <i class="fas fa-arrow-right"></i>
          </button>
        </div>
      `;
      return;
    }

    container.innerHTML = orders.map(order => {
      const itemsHtml = (order.items || []).map(item => `
        <div class="order-item-row">
          <img src="${item.image || './assets/royal_lehenga.png'}" alt="${item.name}" class="order-item-img" />
          <div class="order-item-info">
            <div class="order-item-name">${item.name}</div>
            <div class="order-item-meta">Qty: ${item.quantity} × ₹${(item.unitPrice || 0).toLocaleString()}</div>
          </div>
          <div class="order-item-total">₹${((item.unitPrice || 0) * item.quantity).toLocaleString()}</div>
        </div>
      `).join('');

      return `
        <div class="order-card-item">
          <div class="order-card-header">
            <div>
              <span class="order-number">${order.orderId}</span>
              <span class="order-date">${order.date || 'Recent'}</span>
            </div>
            <span class="order-status-badge">
              <i class="fas fa-check-circle"></i> ${order.status || 'CONFIRMED'}
            </span>
          </div>

          <div class="order-card-body">
            <div class="order-items-list">
              ${itemsHtml}
            </div>

            <div class="order-summary-box">
              <div class="order-summary-row">
                <span>Shipping Address:</span>
                <span style="color: var(--text-primary); font-weight: 500;">${order.shippingAddress || 'Standard Address'}</span>
              </div>
              <div class="order-summary-row">
                <span>Payment Method:</span>
                <span style="color: var(--text-primary); font-weight: 500;">${order.paymentMethod || 'Credit Card'}</span>
              </div>
              ${order.discount && order.discount > 0 ? `
              <div class="order-summary-row discount">
                <span>Promo Discount:</span>
                <span>-₹${order.discount.toLocaleString()}</span>
              </div>` : ''}
              <div class="order-summary-row total">
                <span>Total Amount:</span>
                <span>₹${(order.totalAmount || 0).toLocaleString()}</span>
              </div>
            </div>
          </div>
        </div>
      `;
    }).join('');
  }
}

// Initialize App on DOM Content Loaded
document.addEventListener('DOMContentLoaded', () => {
  window.apnaStoreApp = new ApnaStoreApp();
  window.lumiereApp = window.apnaStoreApp;
});
