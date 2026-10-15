import React from 'react';
import { formatCurrency } from '../../utils/formatCurrency';
import { ArrowRightIcon } from '../../assets/icons/Icons';

export const BudgetOverview = ({ budgets = [], onNavigate }) => {
  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)' }}>
          Budget Overview
        </h3>
        <button
          type="button"
          onClick={() => onNavigate('budgets')}
          className="breakdown-link"
          style={{ fontSize: '0.85rem' }}
        >
          <span>View all</span>
          <ArrowRightIcon size={14} />
        </button>
      </div>

      {budgets.length === 0 ? (
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-charcoal-400)', minHeight: '140px' }}>
          No monthly budgets created yet
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {budgets.slice(0, 4).map((b) => {
            const pct = b.percentageUsed ? Math.round(b.percentageUsed) : 0;
            let fillClass = 'progress-fill-normal';
            if (pct >= 100) fillClass = 'progress-fill-danger';
            else if (pct >= 70) fillClass = 'progress-fill-warning';

            return (
              <div key={b.budgetId}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', marginBottom: '4px' }}>
                  <span style={{ fontWeight: 600, color: 'var(--color-charcoal-800)' }}>{b.categoryName}</span>
                  <span style={{ fontWeight: 600, color: 'var(--color-charcoal-600)' }}>{pct}%</span>
                </div>
                <div className="progress-bar-track">
                  <div
                    className={`progress-bar-fill ${fillClass}`}
                    style={{ width: `${Math.min(100, pct)}%` }}
                  />
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--color-charcoal-400)' }}>
                  <span>{formatCurrency(b.amountSpent)} spent</span>
                  <span>Limit: {formatCurrency(b.limitAmount)}</span>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
