import React from 'react';
import { formatCurrency } from '../../utils/formatCurrency';

export const BudgetCard = ({ totalLimit, totalSpent }) => {
  const limit = parseFloat(totalLimit) || 0;
  const spent = parseFloat(totalSpent) || 0;
  const remaining = limit - spent;
  const pct = limit > 0 ? (spent / limit) * 100 : 0;

  let fillClass = 'progress-fill-normal';
  let badgeLabel = 'Within Budget';
  let badgeClass = 'badge-normal';

  if (pct >= 100) {
    fillClass = 'progress-fill-danger';
    badgeLabel = 'Over Budget';
    badgeClass = 'badge-danger';
  } else if (pct >= 90) {
    fillClass = 'progress-fill-danger';
    badgeLabel = 'Near Limit';
    badgeClass = 'badge-danger';
  } else if (pct >= 70) {
    fillClass = 'progress-fill-warning';
    badgeLabel = 'Approaching Limit';
    badgeClass = 'badge-warning';
  }

  return (
    <div className="card" style={{ marginBottom: '24px', padding: '24px 28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--color-charcoal-900)' }}>
          Monthly Budget
        </h3>
        <span className={`badge ${badgeClass}`}>{badgeLabel}</span>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '8px' }}>
        <div style={{ fontSize: '1.35rem', fontWeight: 700, color: 'var(--color-charcoal-900)' }}>
          {formatCurrency(limit)} <span style={{ fontSize: '0.9rem', fontWeight: 400, color: 'var(--color-charcoal-400)' }}>limit</span>
        </div>
        <div style={{ fontSize: '1.2rem', fontWeight: 600, color: 'var(--color-charcoal-700)' }}>
          {formatCurrency(spent)} <span style={{ fontSize: '0.85rem', fontWeight: 400, color: 'var(--color-charcoal-400)' }}>spent</span>
        </div>
      </div>

      <div className="progress-bar-track" style={{ height: '14px', borderRadius: 'var(--radius-sm)' }}>
        <div
          className={`progress-bar-fill ${fillClass}`}
          style={{ width: `${Math.min(100, pct)}%` }}
        />
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px', fontSize: '0.95rem' }}>
        <span style={{ fontWeight: 600, color: remaining >= 0 ? 'var(--color-turquoise-700)' : 'var(--color-expense)' }}>
          {remaining >= 0 ? `${formatCurrency(remaining)} remaining` : `Over budget by ${formatCurrency(Math.abs(remaining))}`}
        </span>
        <span style={{ fontWeight: 700, color: 'var(--color-charcoal-700)' }}>
          {pct.toFixed(1)}%
        </span>
      </div>
    </div>
  );
};
