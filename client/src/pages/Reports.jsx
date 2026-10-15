import React, { useState, useEffect } from 'react';
import { reportService } from '../services/reportService';
import { IncomeExpenseChart } from '../components/dashboard/IncomeExpenseChart';
import { CategoryChart } from '../components/dashboard/CategoryChart';
import { Loading } from '../components/common/Loading';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { formatCurrency } from '../utils/formatCurrency';

export const Reports = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [trendData, setTrendData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [topExpenses, setTopExpenses] = useState([]);
  const [period, setPeriod] = useState('2026-10-01');

  useEffect(() => {
    fetchReportData();
  }, [period]);

  const fetchReportData = async () => {
    setLoading(true);
    setError('');
    try {
      const [trends, cats, top] = await Promise.all([
        reportService.getIncomeVsExpense(6),
        reportService.getCategorySpending(),
        reportService.getTopExpenses(5),
      ]);
      setTrendData(trends);
      setCategoryData(cats);
      setTopExpenses(top);
    } catch (err) {
      console.error(err);
      setError('Failed to generate financial reports');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Reports</h1>
          <p className="page-subtitle">Analytical breakdown of your financial trends and top expenses.</p>
        </div>

        <div className="page-actions">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.88rem', fontWeight: 600, color: 'var(--color-charcoal-600)' }}>Period:</span>
            <select
              className="form-select"
              style={{ padding: '6px 12px', fontSize: '0.9rem' }}
              value={period}
              onChange={(e) => setPeriod(e.target.value)}
            >
              <option value="2026-10-01">October 2026</option>
              <option value="2026-09-01">September 2026</option>
              <option value="2026-08-01">August 2026</option>
            </select>
          </div>
        </div>
      </div>

      <ErrorMessage message={error} onRetry={fetchReportData} />

      {loading && trendData.length === 0 ? (
        <Loading message="Generating financial reports..." />
      ) : (
        <>
          {/* Income vs Expenses Historical Chart */}
          <div style={{ marginBottom: '24px' }}>
            <IncomeExpenseChart data={trendData} />
          </div>

          <div className="grid-2">
            {/* Category Breakdown */}
            <CategoryChart data={categoryData} />

            {/* Top Expenses List */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column' }}>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)', marginBottom: '16px' }}>
                Top Expenses
              </h3>

              {topExpenses.length === 0 ? (
                <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-charcoal-400)' }}>
                  No expense records found
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  {topExpenses.map((expense, idx) => (
                    <div
                      key={expense.transactionId}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '10px 14px',
                        backgroundColor: 'var(--color-ivory-50)',
                        borderRadius: 'var(--radius-sm)',
                        border: '1px solid var(--color-ivory-100)',
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                        <span style={{ fontWeight: 700, color: 'var(--color-charcoal-400)', width: '20px' }}>
                          {idx + 1}.
                        </span>
                        <div>
                          <div style={{ fontWeight: 600, color: 'var(--color-charcoal-900)' }}>
                            {expense.description || expense.categoryName}
                          </div>
                          <div style={{ fontSize: '0.78rem', color: 'var(--color-charcoal-400)' }}>
                            {expense.categoryName}
                          </div>
                        </div>
                      </div>

                      <div style={{ fontWeight: 700, color: 'var(--color-expense)', fontSize: '0.95rem' }}>
                        {formatCurrency(expense.amount)}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
};
