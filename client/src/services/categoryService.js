import api from './api';

export const categoryService = {
  getAllCategories: async () => {
    const res = await api.get('/categories');
    return res.data;
  },

  getCategoriesByType: async (type) => {
    const res = await api.get(`/categories/type/${type}`);
    return res.data;
  },

  createCategory: async (data) => {
    const res = await api.post('/categories', data);
    return res.data;
  },

  updateCategory: async (id, data) => {
    const res = await api.put(`/categories/${id}`, data);
    return res.data;
  },

  deleteCategory: async (id) => {
    const res = await api.delete(`/categories/${id}`);
    return res.data;
  },
};
