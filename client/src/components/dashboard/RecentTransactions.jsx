import React from 'react';
import { formatCurrency } from '../../utils/formatCurrency';
import { ArrowRightIcon } from '../../assets/icons/Icons';

export const RecentTransactions = ({ transactions = [], onNavigate, onEditTransaction }) => {
  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)' }}>
          Recent Transactions
        </h3>
        <button
          type="button"
          onClick={() => onNavigate('transactions')}
          className="breakdown-link"
          style={{ fontSize: '0.85rem' }}
        >
          <span>View all</span>
          <ArrowRightIcon size={14} />
        </button>
      </div>

      {transactions.length === 0 ? (
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-charcoal-400)', minHeight: '140px' }}>
          No transactions recorded yet
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {transactions.slice(0, 5).map((t) => {
            const isIncome = t.type === 'INCOME';
            return (
              <div
                key={t.transactionId}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '10px 12px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--color-ivory-50)',
                  border: '1px solid var(--color-ivory-100)',
                  cursor: onEditTransaction ? 'pointer' : 'default'
                }}
                onClick={() => onEditTransaction && onEditTransaction(t)}
              >
                <div>
                  <div style={{ fontWeight: 600, fontSize: '0.92rem', color: 'var(--color-charcoal-900)' }}>
                    {t.description || t.categoryName}
                  </div>
                  <div style={{ fontSize: '0.78rem', color: 'var(--color-charcoal-400)' }}>
                    {t.categoryName} {t.paymentMethodName ? `• ${t.paymentMethodName}` : ''}
                  </div>
                </div>

                <div
                  style={{
                    fontWeight: 700,
                    fontSize: '0.95rem',
                    color: isIncome ? 'var(--color-income)' : 'var(--color-expense)'
                  }}
                >
                  {formatCurrency(t.amount, { withSign: true, decimals: 0 })}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
