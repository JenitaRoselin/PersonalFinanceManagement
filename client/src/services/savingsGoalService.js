import api from './api';

export const savingsGoalService = {
  getAllGoals: async () => {
    const res = await api.get('/savings-goals');
    return res.data;
  },

  getGoalById: async (id) => {
    const res = await api.get(`/savings-goals/${id}`);
    return res.data;
  },

  createGoal: async (data) => {
    const res = await api.post('/savings-goals', data);
    return res.data;
  },

  updateGoal: async (id, data) => {
    const res = await api.put(`/savings-goals/${id}`, data);
    return res.data;
  },

  deleteGoal: async (id) => {
    const res = await api.delete(`/savings-goals/${id}`);
    return res.data;
  },

  updateProgress: async (id, amount, operation = 'ADD') => {
    const res = await api.patch(`/savings-goals/${id}/progress`, null, {
      params: { amount, operation }
    });
    return res.data;
  },

  updateStatus: async (id, status) => {
    const res = await api.patch(`/savings-goals/${id}/status`, null, {
      params: { status }
    });
    return res.data;
  },
};
