/**
 * BookNest - Authentication & Session Management
 */

const auth = {
  isLoggedIn() {
    return !!sessionStorage.getItem('booknest_token');
  },

  getUser() {
    const userStr = sessionStorage.getItem('booknest_user');
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  },

  isAdmin() {
    const user = this.getUser();
    return user && user.role === 'ADMIN';
  },

  getToken() {
    return sessionStorage.getItem('booknest_token');
  },

  async login(email, password) {
    const res = await api.post('/auth/login', { email, password });
    if (res && res.success && res.data) {
      sessionStorage.setItem('booknest_token', res.data.token);
      sessionStorage.setItem('booknest_user', JSON.stringify(res.data.user));

      // Merge temporary guest cart with user cart if items exist
      if (window.cart && window.cart.getGuestCartCount() > 0) {
        await window.cart.mergeGuestCart();
      }

      return res.data.user;
    }
    throw new Error('Login failed');
  },

  async register(fullNameOrObj, email, password, confirmPassword, phone) {
    let payload;
    if (typeof fullNameOrObj === 'object' && fullNameOrObj !== null) {
      payload = {
        fullName: fullNameOrObj.fullName,
        email: fullNameOrObj.email,
        password: fullNameOrObj.password,
        confirmPassword: fullNameOrObj.confirmPassword || fullNameOrObj.password,
        phone: fullNameOrObj.phone
      };
    } else {
      payload = {
        fullName: fullNameOrObj,
        email,
        password,
        confirmPassword: confirmPassword || password,
        phone
      };
    }
    const res = await api.post('/auth/register', payload);
    if (res && res.success && res.data) {
      sessionStorage.setItem('booknest_token', res.data.token);
      sessionStorage.setItem('booknest_user', JSON.stringify(res.data.user));

      // Merge guest cart on new registration
      if (window.cart && window.cart.getGuestCartCount() > 0) {
        await window.cart.mergeGuestCart();
      }

      return res.data.user;
    }
    return res;
  },

  async logout() {
    try {
      await api.post('/auth/logout', {});
    } catch (e) {
      // Ignore network failures on logout
    } finally {
      sessionStorage.removeItem('booknest_token');
      sessionStorage.removeItem('booknest_user');
      window.location.href = 'index.html';
    }
  },

  requireAuth(redirectUrl = null) {
    if (!this.isLoggedIn()) {
      const target = redirectUrl || window.location.pathname.split('/').pop() || 'index.html';
      window.location.href = `login.html?redirect=${encodeURIComponent(target)}`;
      return false;
    }
    return true;
  },

  requireAdmin() {
    if (!this.isLoggedIn()) {
      window.location.href = 'login.html?redirect=admin.html';
      return false;
    }
    if (!this.isAdmin()) {
      alert('Access restricted. Administrator privileges required.');
      window.location.href = 'index.html';
      return false;
    }
    return true;
  }
};

window.auth = auth;
