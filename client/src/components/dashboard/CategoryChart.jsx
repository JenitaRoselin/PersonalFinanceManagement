import React from 'react';
import { formatCurrency } from '../../utils/formatCurrency';

export const CategoryChart = ({ data = [] }) => {
  const colors = [
    'var(--color-turquoise-700)',
    'var(--color-charcoal-700)',
    'var(--color-turquoise-500)',
    'var(--color-warning)',
    'var(--color-expense)',
    'var(--color-turquoise-400)',
    'var(--color-charcoal-500)',
  ];

  if (!data || data.length === 0) {
    return (
      <div className="card" style={{ height: '320px', display: 'flex', flexDirection: 'column' }}>
        <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)', marginBottom: '16px' }}>
          Spending by Category
        </h3>
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-charcoal-400)' }}>
          No expense transactions recorded yet
        </div>
      </div>
    );
  }

  const topCategories = data.slice(0, 5);

  return (
    <div className="card" style={{ height: '320px', display: 'flex', flexDirection: 'column' }}>
      <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)', marginBottom: '16px' }}>
        Spending by Category
      </h3>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: '12px' }}>
        {topCategories.map((item, idx) => {
          const color = colors[idx % colors.length];
          const pct = item.percentage ? item.percentage.toFixed(1) : '0';

          return (
            <div key={item.categoryId || idx} style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ width: '10px', height: '10px', backgroundColor: color, borderRadius: '2px' }} />
                  <span style={{ fontWeight: 500, color: 'var(--color-charcoal-800)' }}>{item.categoryName}</span>
                </div>
                <div style={{ display: 'flex', gap: '12px' }}>
                  <span style={{ fontWeight: 600 }}>{formatCurrency(item.totalSpent)}</span>
                  <span style={{ color: 'var(--color-charcoal-400)', minWidth: '42px', textAlign: 'right' }}>{pct}%</span>
                </div>
              </div>
              <div className="progress-bar-track" style={{ height: '6px', margin: 0 }}>
                <div
                  className="progress-bar-fill"
                  style={{ width: `${Math.min(100, parseFloat(pct))}%`, backgroundColor: color }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
