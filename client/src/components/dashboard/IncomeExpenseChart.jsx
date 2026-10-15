import React from 'react';
import { formatCurrency } from '../../utils/formatCurrency';

export const IncomeExpenseChart = ({ data = [] }) => {
  if (!data || data.length === 0) {
    return (
      <div className="card" style={{ height: '320px', display: 'flex', flexDirection: 'column' }}>
        <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)', marginBottom: '16px' }}>
          Income vs Expense
        </h3>
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-charcoal-400)' }}>
          No transaction history available yet
        </div>
      </div>
    );
  }

  // Find max value for scaling
  let maxVal = 1000;
  data.forEach((d) => {
    const inc = parseFloat(d.totalIncome) || 0;
    const exp = parseFloat(d.totalExpense) || 0;
    if (inc > maxVal) maxVal = inc;
    if (exp > maxVal) maxVal = exp;
  });

  const chartHeight = 180;

  return (
    <div className="card" style={{ height: '320px', display: 'flex', flexDirection: 'column' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)' }}>
          Income vs Expense
        </h3>
        <div style={{ display: 'flex', gap: '14px', fontSize: '0.8rem', fontWeight: 500 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span style={{ width: '10px', height: '10px', backgroundColor: 'var(--color-turquoise-600)', borderRadius: '2px' }}></span>
            <span>Income</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span style={{ width: '10px', height: '10px', backgroundColor: 'var(--color-expense)', borderRadius: '2px' }}></span>
            <span>Expense</span>
          </div>
        </div>
      </div>

      <div style={{ flex: 1, display: 'flex', alignItems: 'flex-end', gap: '16px', padding: '10px 0 20px', borderBottom: '1px solid var(--color-ivory-200)' }}>
        {data.map((item, idx) => {
          const inc = parseFloat(item.totalIncome) || 0;
          const exp = parseFloat(item.totalExpense) || 0;
          const incHeight = Math.max(4, (inc / maxVal) * chartHeight);
          const expHeight = Math.max(4, (exp / maxVal) * chartHeight);

          const monthLabel = item.month
            ? new Date(item.month).toLocaleDateString('en-US', { month: 'short' })
            : `M${idx + 1}`;

          return (
            <div key={idx} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px' }}>
              <div style={{ display: 'flex', alignItems: 'flex-end', gap: '6px', height: `${chartHeight}px` }}>
                {/* Income Bar */}
                <div
                  title={`Income: ${formatCurrency(inc)}`}
                  style={{
                    width: '16px',
                    height: `${incHeight}px`,
                    backgroundColor: 'var(--color-turquoise-600)',
                    borderRadius: '3px 3px 0 0',
                    transition: 'height 0.4s ease'
                  }}
                />
                {/* Expense Bar */}
                <div
                  title={`Expense: ${formatCurrency(exp)}`}
                  style={{
                    width: '16px',
                    height: `${expHeight}px`,
                    backgroundColor: 'var(--color-expense)',
                    borderRadius: '3px 3px 0 0',
                    transition: 'height 0.4s ease'
                  }}
                />
              </div>
              <span style={{ fontSize: '0.78rem', color: 'var(--color-charcoal-400)', marginTop: '6px' }}>
                {monthLabel}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
};
