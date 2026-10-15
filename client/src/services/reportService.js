import api from './api';

export const reportService = {
  getMonthlyReport: async (year) => {
    const res = await api.get('/reports/monthly', { params: { year } });
    return res.data;
  },

  getCategorySpending: async (startDate, endDate) => {
    const res = await api.get('/reports/category', { params: { startDate, endDate } });
    return res.data;
  },

  getIncomeVsExpense: async (months = 6) => {
    const res = await api.get('/reports/income-vs-expense', { params: { months } });
    return res.data;
  },

  getTopExpenses: async (limit = 10) => {
    const res = await api.get('/reports/top-expenses', { params: { limit } });
    return res.data;
  },

  getTrends: async (months = 6) => {
    const res = await api.get('/reports/trends', { params: { months } });
    return res.data;
  },

  getBudgetPerformance: async (month) => {
    const res = await api.get('/reports/budget-performance', { params: { month } });
    return res.data;
  },
};
