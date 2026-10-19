/**
 * BookNest - Cart Management
 * Handles persistent database cart for authenticated users,
 * and temporary browser cart for guest users with seamless merge on login.
 */

const cart = {
  GUEST_CART_KEY: 'booknest_guest_cart',

  getGuestCart() {
    try {
      const items = localStorage.getItem(this.GUEST_CART_KEY);
      return items ? JSON.parse(items) : [];
    } catch {
      return [];
    }
  },

  saveGuestCart(items) {
    localStorage.setItem(this.GUEST_CART_KEY, JSON.stringify(items));
    this.updateBadge();
  },

  getGuestCartCount() {
    return this.getGuestCart().reduce((acc, it) => acc + (it.quantity || 1), 0);
  },

  async getCart() {
    if (auth.isLoggedIn()) {
      try {
        const res = await api.get('/cart');
        return res.data;
      } catch (err) {
        console.error('Failed to fetch user cart:', err);
        return { items: [], itemCount: 0, subtotal: '0.00', tax: '0.00', shippingFee: '0.00', totalAmount: '0.00' };
      }
    } else {
      // Calculate guest cart offline
      const guestItems = this.getGuestCart();
      const count = guestItems.reduce((acc, it) => acc + it.quantity, 0);
      const subtotalNum = guestItems.reduce((acc, it) => acc + (parseFloat(it.price || 0) * it.quantity), 0);
      const taxNum = subtotalNum * 0.05;
      const shippingNum = (subtotalNum >= 50 || subtotalNum === 0) ? 0 : 4.99;
      const totalNum = subtotalNum + taxNum + shippingNum;

      return {
        id: null,
        items: guestItems.map((it, idx) => ({
          id: idx + 1,
          book: {
            id: it.bookId,
            title: it.title,
            author: it.author,
            price: it.price,
            coverImageUrl: it.coverImageUrl,
            stockQuantity: it.stockQuantity || 10
          },
          quantity: it.quantity,
          unitPrice: it.price,
          lineTotal: (parseFloat(it.price) * it.quantity).toFixed(2)
        })),
        itemCount: count,
        subtotal: subtotalNum.toFixed(2),
        tax: taxNum.toFixed(2),
        shippingFee: shippingNum.toFixed(2),
        totalAmount: totalNum.toFixed(2)
      };
    }
  },

  async addItem(bookOrId, quantity = 1) {
    let book = bookOrId;
    let bookId = (typeof bookOrId === 'object' && bookOrId !== null) ? bookOrId.id : bookOrId;

    if (auth.isLoggedIn()) {
      const res = await api.post('/cart/items', {
        bookId: bookId,
        quantity: quantity
      });
      const cartData = res.data || res;
      this.updateBadge(cartData ? cartData.itemCount : undefined);
      return cartData;
    } else {
      // Guest cart
      if (typeof book !== 'object' || book === null) {
        try {
          const res = await api.get('/books/' + bookId);
          book = res.data || res;
        } catch {
          book = { id: bookId, title: 'Book #' + bookId, price: 0, stockQuantity: 99 };
        }
      }

      const guestItems = this.getGuestCart();
      const existing = guestItems.find(it => it.bookId === book.id);
      const currentQty = existing ? existing.quantity : 0;
      const newQty = currentQty + quantity;

      if (book.stockQuantity !== undefined && newQty > book.stockQuantity) {
        throw new Error(`Cannot add more. Available stock is ${book.stockQuantity}`);
      }

      if (existing) {
        existing.quantity = newQty;
      } else {
        guestItems.push({
          bookId: book.id,
          title: book.title,
          author: book.author,
          price: book.price,
          coverImageUrl: book.coverImageUrl || book.imageUrl,
          stockQuantity: book.stockQuantity,
          quantity: quantity
        });
      }

      this.saveGuestCart(guestItems);
      return await this.getCart();
    }
  },

  async updateQuantity(itemId, bookIdOrQty, newQuantity) {
    let bookId, qty;
    if (newQuantity !== undefined) {
      bookId = bookIdOrQty;
      qty = newQuantity;
    } else {
      qty = bookIdOrQty;
      bookId = null;
    }

    if (auth.isLoggedIn()) {
      const res = await api.patch(`/cart/items/${itemId}`, { quantity: qty });
      const cartData = res.data || res;
      this.updateBadge(cartData ? cartData.itemCount : undefined);
      return cartData;
    } else {
      let guestItems = this.getGuestCart();
      const item = guestItems.find(it => it.bookId === (bookId || itemId) || String(it.bookId) === String(itemId));
      if (item) {
        item.quantity = qty;
      }
      this.saveGuestCart(guestItems);
      return await this.getCart();
    }
  },

  async removeItem(itemId, bookId) {
    if (auth.isLoggedIn()) {
      const res = await api.delete(`/cart/items/${itemId}`);
      const cartData = res.data || res;
      this.updateBadge(cartData ? cartData.itemCount : undefined);
      return cartData;
    } else {
      let guestItems = this.getGuestCart();
      guestItems = guestItems.filter(it => it.bookId !== (bookId || itemId) && String(it.bookId) !== String(itemId));
      this.saveGuestCart(guestItems);
      return await this.getCart();
    }
  },

  async clearCart() {
    if (auth.isLoggedIn()) {
      const res = await api.delete('/cart');
      this.updateBadge(0);
      return res.data;
    } else {
      localStorage.removeItem(this.GUEST_CART_KEY);
      this.updateBadge(0);
      return await this.getCart();
    }
  },

  async mergeGuestCart() {
    const guestItems = this.getGuestCart();
    if (!guestItems || guestItems.length === 0) return;

    try {
      const payload = {
        items: guestItems.map(it => ({
          bookId: it.bookId,
          quantity: it.quantity
        }))
      };
      await api.post('/cart/merge', payload);
      localStorage.removeItem(this.GUEST_CART_KEY);
      await this.updateBadge();
    } catch (e) {
      console.warn('Could not merge guest cart:', e);
    }
  },

  async updateBadge(explicitCount = null) {
    const badgeEl = document.getElementById('cart-badge');
    if (!badgeEl) return;

    let count = explicitCount;
    if (count === null) {
      if (auth.isLoggedIn()) {
        try {
          const res = await api.get('/cart');
          count = res.data.itemCount;
        } catch {
          count = 0;
        }
      } else {
        count = this.getGuestCartCount();
      }
    }

    badgeEl.textContent = count;
    badgeEl.style.display = count > 0 ? 'flex' : 'none';
  }
};

window.cart = cart;
