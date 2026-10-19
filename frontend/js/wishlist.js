/**
 * BookNest - Wishlist Management
 */

const wishlist = {
  async getWishlist() {
    if (!auth.isLoggedIn()) {
      return { items: [], itemCount: 0 };
    }
    try {
      const res = await api.get('/wishlist');
      return res.data;
    } catch (e) {
      console.warn('Failed to load wishlist:', e);
      return { items: [], itemCount: 0 };
    }
  },

  async toggle(bookOrId) {
    if (!auth.isLoggedIn()) {
      showToast('Please sign in to add books to your wishlist', 'error');
      setTimeout(() => {
        window.location.href = 'login.html?redirect=' + encodeURIComponent(window.location.pathname);
      }, 1200);
      return;
    }

    try {
      const bookId = (typeof bookOrId === 'object' && bookOrId !== null) ? bookOrId.id : bookOrId;
      const title = (typeof bookOrId === 'object' && bookOrId !== null) ? bookOrId.title : 'Book';
      const current = await this.getWishlist();
      const existing = (current.items || []).find(it => (it.book ? it.book.id : it.bookId) == bookId);

      if (existing) {
        await api.delete(`/wishlist/items/${bookId}`);
        showToast(`Removed from wishlist`, 'success');
      } else {
        await api.post('/wishlist/items', { bookId: bookId });
        showToast(`Added to wishlist`, 'success');
      }
      await this.updateBadge();
    } catch (e) {
      showToast(e.message || 'Could not update wishlist', 'error');
    }
  },

  async removeItem(bookId) {
    if (!auth.isLoggedIn()) return;
    try {
      await api.delete(`/wishlist/items/${bookId}`);
      await this.updateBadge();
      showToast('Item removed from wishlist', 'success');
    } catch (e) {
      showToast(e.message || 'Could not remove item', 'error');
    }
  },

  async moveToCart(bookId) {
    if (!auth.isLoggedIn()) return;
    try {
      await api.post(`/wishlist/items/${bookId}/move-to-cart`, {});
      await this.updateBadge();
      if (window.cart) await window.cart.updateBadge();
      showToast('Moved item to shopping cart', 'success');
    } catch (e) {
      showToast(e.message || 'Could not move item to cart', 'error');
    }
  },

  async updateBadge() {
    const badgeEl = document.getElementById('wishlist-badge');
    if (!badgeEl) return;

    if (!auth.isLoggedIn()) {
      badgeEl.style.display = 'none';
      return;
    }

    try {
      const data = await this.getWishlist();
      const count = data.itemCount || (data.items ? data.items.length : 0);
      badgeEl.textContent = count;
      badgeEl.style.display = count > 0 ? 'flex' : 'none';
    } catch {
      badgeEl.style.display = 'none';
    }
  }
};

window.wishlist = wishlist;
