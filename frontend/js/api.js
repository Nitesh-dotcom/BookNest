/**
 * BookNest - API Client
 * Centralized Fetch wrapper handling baseUrl, JWT Bearer tokens, and error handling.
 */

const API_BASE_URL = window.BOOKNEST_API_URL || 'http://localhost:8080/api';

const api = {
  getBaseUrl() {
    return API_BASE_URL;
  },

  getAuthToken() {
    return sessionStorage.getItem('booknest_token');
  },

  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
    
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...options.headers
    };

    const token = this.getAuthToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json().catch(() => null);

      if (!response.ok) {
        let errorMsg = 'An error occurred. Please try again.';
        if (data) {
          if (data.message) {
            errorMsg = data.message;
          } else if (data.validationErrors) {
            const firstKey = Object.keys(data.validationErrors)[0];
            errorMsg = data.validationErrors[firstKey];
          } else if (data.error) {
            errorMsg = data.error;
          }
        }
        
        // Handle 401 Unauthorized for protected pages
        if (response.status === 401 && !url.includes('/auth/login') && !url.includes('/auth/register')) {
          sessionStorage.removeItem('booknest_token');
          sessionStorage.removeItem('booknest_user');
          if (window.location.pathname.includes('account') || 
              window.location.pathname.includes('orders') || 
              window.location.pathname.includes('wishlist') || 
              window.location.pathname.includes('admin')) {
            window.location.href = 'login.html?redirect=' + encodeURIComponent(window.location.pathname);
          }
        }

        const error = new Error(errorMsg);
        error.status = response.status;
        error.data = data;
        throw error;
      }

      return data;
    } catch (err) {
      if (err.name === 'TypeError' && err.message.includes('fetch')) {
        throw new Error('Unable to connect to BookNest backend server. Please verify the backend is running at ' + API_BASE_URL);
      }
      throw err;
    }
  },

  get(endpoint, params = {}) {
    let url = endpoint;
    const query = new URLSearchParams();
    for (const [key, val] of Object.entries(params)) {
      if (val !== undefined && val !== null && val !== '') {
        query.append(key, val);
      }
    }
    const queryString = query.toString();
    if (queryString) {
      url += (url.includes('?') ? '&' : '?') + queryString;
    }
    return this.request(url, { method: 'GET' });
  },

  post(endpoint, body = {}) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body)
    });
  },

  put(endpoint, body = {}) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body)
    });
  },

  patch(endpoint, body = {}) {
    return this.request(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(body)
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
};

window.api = api;
