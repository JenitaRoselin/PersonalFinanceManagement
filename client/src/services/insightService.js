import api from './api';

export const insightService = {
  getSafeToSpend: async (month) => {
    const res = await api.get('/insights/safe-to-spend', { params: { month } });
    return res.data;
  },

  getSpendingAlerts: async () => {
    const res = await api.get('/insights/spending-alerts');
    return res.data;
  },

  getBudgetAlerts: async (month) => {
    const res = await api.get('/insights/budget-alerts', { params: { month } });
    return res.data;
  },
};
