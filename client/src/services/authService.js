import api from './api';

export const authService = {
  login: async (credentials) => {
    const res = await api.post('/auth/login', credentials);
    if (res.data.token) {
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('user', JSON.stringify(res.data));
    }
    return res.data;
  },

  register: async (data) => {
    const res = await api.post('/auth/register', data);
    if (res.data.token) {
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('user', JSON.stringify(res.data));
    }
    return res.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser: async () => {
    const res = await api.get('/auth/me');
    return res.data;
  },

  updateProfile: async (data) => {
    const res = await api.put('/auth/me', data);
    return res.data;
  },

  changePassword: async (passwords) => {
    const res = await api.put('/auth/me/password', passwords);
    return res.data;
  },

  deleteAccount: async () => {
    const res = await api.delete('/auth/me');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    return res.data;
  },
};
