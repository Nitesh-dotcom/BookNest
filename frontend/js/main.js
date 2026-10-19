/**
 * BookNest - Main Application Logic
 * Shared components: Header, Footer, Toast notifications, image fallbacks, and currency formatters.
 */

function formatPrice(amount) {
  if (amount === undefined || amount === null) return '$0.00';
  return '$' + parseFloat(amount).toFixed(2);
}

function handleImageError(img) {
  img.onerror = null;
  img.src = 'assets/placeholder.svg';
}

function showToast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      ${type === 'success' 
        ? '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline>' 
        : '<circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line>'}
    </svg>
    <span>${message}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function renderStars(rating = 0) {
  const rounded = Math.round(rating);
  let stars = '';
  for (let i = 1; i <= 5; i++) {
    stars += i <= rounded ? '★' : '☆';
  }
  return `<span class="stars">${stars}</span>`;
}

// Render Shared Header
function initHeader() {
  const headerContainer = document.getElementById('site-header');
  if (!headerContainer) return;

  const user = window.auth ? window.auth.getUser() : null;
  const isLoggedIn = !!user;
  const isAdmin = user && user.role === 'ADMIN';

  headerContainer.innerHTML = `
    <div class="top-announcement">
      Curated Literature &amp; Rare Finds &bull; Enjoy complimentary shipping on orders over $50!
      <a href="books.html">Explore Catalog &rarr;</a>
    </div>
    <div class="container header-main">
      <a href="index.html" class="brand-logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
          <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
        </svg>
        BookNest
      </a>

      <form class="header-search" id="global-search-form" onsubmit="handleGlobalSearch(event)">
        <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input type="text" id="global-search-input" placeholder="Search by title, author, or ISBN..." autocomplete="off">
      </form>

      <nav class="header-nav">
        <ul class="nav-links">
          <li><a href="index.html" class="nav-link">Home</a></li>
          <li><a href="books.html" class="nav-link">Books</a></li>
          <li><a href="about.html" class="nav-link">About</a></li>
          <li><a href="contact.html" class="nav-link">Contact</a></li>
          ${isAdmin ? '<li><a href="admin.html" class="nav-link" style="color:var(--accent-primary);font-weight:700;">Admin</a></li>' : ''}
        </ul>

        <div class="header-actions">
          <!-- Wishlist Button -->
          <a href="wishlist.html" class="action-btn" title="Wishlist">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
            <span class="badge-count" id="wishlist-badge" style="display:none;">0</span>
          </a>

          <!-- Cart Button -->
          <a href="cart.html" class="action-btn" title="Shopping Cart">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="9" cy="21" r="1"></circle>
              <circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            <span class="badge-count" id="cart-badge" style="display:none;">0</span>
          </a>

          <!-- Account Dropdown -->
          <div class="user-menu-wrap">
            <button class="action-btn" id="user-menu-toggle" onclick="toggleUserDropdown(event)" title="My Account">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </button>

            <div class="user-dropdown" id="user-dropdown">
              ${isLoggedIn ? `
                <div class="user-dropdown-header">
                  <div class="user-dropdown-name">${user.fullName || 'Reader'}</div>
                  <div class="user-dropdown-email">${user.email}</div>
                </div>
                <a href="account.html">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                  My Profile
                </a>
                <a href="orders.html">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"></rect><path d="M6 8h12M6 12h8"></path></svg>
                  Order History
                </a>
                <a href="wishlist.html">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                  My Wishlist
                </a>
                ${isAdmin ? `
                  <a href="admin.html" style="color:var(--accent-primary);font-weight:600;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
                    Admin Dashboard
                  </a>
                ` : ''}
                <div class="user-dropdown-divider"></div>
                <button onclick="window.auth.logout()">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                  Sign Out
                </button>
              ` : `
                <a href="login.html">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path><polyline points="10 17 15 12 10 7"></polyline><line x1="15" y1="12" x2="3" y2="12"></line></svg>
                  Sign In
                </a>
                <a href="register.html">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
                  Create Account
                </a>
              `}
            </div>
          </div>
        </div>
      </nav>
    </div>
  `;

  // Highlight active link
  const currentPath = window.location.pathname.split('/').pop() || 'index.html';
  const navLinks = headerContainer.querySelectorAll('.nav-link');
  navLinks.forEach(link => {
    if (link.getAttribute('href') === currentPath) {
      link.classList.add('active');
    }
  });

  // Close dropdown on outside click
  document.addEventListener('click', (e) => {
    const dropdown = document.getElementById('user-dropdown');
    const toggle = document.getElementById('user-menu-toggle');
    if (dropdown && toggle && !toggle.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.classList.remove('show');
    }
  });

  // Init badges
  if (window.cart) window.cart.updateBadge();
  if (window.wishlist) window.wishlist.updateBadge();
}

function toggleUserDropdown(event) {
  event.stopPropagation();
  const dropdown = document.getElementById('user-dropdown');
  if (dropdown) {
    dropdown.classList.toggle('show');
  }
}

function handleGlobalSearch(event) {
  event.preventDefault();
  const input = document.getElementById('global-search-input');
  const term = input ? input.value.trim() : '';
  if (term) {
    window.location.href = `books.html?query=${encodeURIComponent(term)}`;
  } else {
    window.location.href = 'books.html';
  }
}

// Render Shared Footer
function initFooter() {
  const footerContainer = document.getElementById('site-footer');
  if (!footerContainer) return;

  footerContainer.innerHTML = `
    <div class="container">
      <div class="footer-grid">
        <div class="footer-brand">
          <a href="index.html" class="brand-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
            </svg>
            BookNest
          </a>
          <p>BookNest is an independent online bookstore dedicated to passionate readers, timeless storytelling, and thought-provoking literature.</p>
        </div>

        <div class="footer-col">
          <h4>Explore</h4>
          <ul class="footer-links">
            <li><a href="books.html">Browse All Books</a></li>
            <li><a href="books.html?sort=rating">Top Rated Titles</a></li>
            <li><a href="books.html?featured=true">Featured Staff Picks</a></li>
            <li><a href="books.html?sort=newest">New Arrivals</a></li>
          </ul>
        </div>

        <div class="footer-col">
          <h4>Customer Care</h4>
          <ul class="footer-links">
            <li><a href="account.html">My Account</a></li>
            <li><a href="orders.html">Track Orders</a></li>
            <li><a href="wishlist.html">Wishlist</a></li>
            <li><a href="cart.html">Shopping Cart</a></li>
            <li><a href="contact.html">Shipping &amp; Returns</a></li>
          </ul>
        </div>

        <div class="footer-col">
          <h4>About BookNest</h4>
          <ul class="footer-links">
            <li><a href="about.html">Our Story</a></li>
            <li><a href="contact.html">Contact Us</a></li>
            <li><a href="about.html#locations">Independent Publishing</a></li>
            <li><a href="contact.html#faq">Frequently Asked Questions</a></li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <div>&copy; ${new Date().getFullYear()} BookNest Online Bookstore. All rights reserved.</div>
        <div>Crafted with Java 17, Spring Boot, MySQL 8 &amp; Vanilla ES6 JavaScript.</div>
      </div>
    </div>
  `;
}

// Newsletter Subscription Handler
async function handleNewsletterSubmit(event) {
  event.preventDefault();
  const input = event.target.querySelector('input[type="email"]');
  if (!input || !input.value.trim()) return;

  try {
    const res = await api.post('/newsletter/subscribe', { email: input.value.trim() });
    showToast(res.message || 'Thank you for subscribing!', 'success');
    input.value = '';
  } catch (err) {
    showToast(err.message || 'Could not subscribe. Please try again.', 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  initHeader();
  initFooter();
});
