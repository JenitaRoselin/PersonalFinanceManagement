import api from './api';

export const dashboardService = {
  getSummary: async () => {
    const res = await api.get('/dashboard/summary');
    return res.data;
  },

  getMonthlySummary: async (month) => {
    const res = await api.get('/dashboard/monthly-summary', { params: { month } });
    return res.data;
  },

  getRecentTransactions: async (limit = 5) => {
    const res = await api.get('/dashboard/recent-transactions', { params: { limit } });
    return res.data;
  },

  getCategorySpending: async (startDate, endDate) => {
    const res = await api.get('/dashboard/category-spending', { params: { startDate, endDate } });
    return res.data;
  },

  getBudgetStatus: async (month) => {
    const res = await api.get('/dashboard/budget-status', { params: { month } });
    return res.data;
  },

  getUpcoming: async () => {
    const res = await api.get('/dashboard/upcoming');
    return res.data;
  },
};
