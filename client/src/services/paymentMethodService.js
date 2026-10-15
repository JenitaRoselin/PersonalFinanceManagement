import api from './api';

export const paymentMethodService = {
  getAllPaymentMethods: async () => {
    const res = await api.get('/payment-methods');
    return res.data;
  },

  createPaymentMethod: async (data) => {
    const res = await api.post('/payment-methods', data);
    return res.data;
  },

  updatePaymentMethod: async (id, data) => {
    const res = await api.put(`/payment-methods/${id}`, data);
    return res.data;
  },

  deletePaymentMethod: async (id) => {
    const res = await api.delete(`/payment-methods/${id}`);
    return res.data;
  },
};
