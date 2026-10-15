import React, { useState } from 'react';
import { formatDate } from '../../utils/dateUtils';
import { formatCurrency } from '../../utils/formatCurrency';
import { MoreVerticalIcon, EditIcon, TrashIcon, ChevronLeftIcon, ChevronRightIcon } from '../../assets/icons/Icons';

export const TransactionTable = ({
  transactions = [],
  page = 0,
  totalPages = 1,
  onPageChange,
  onEdit,
  onDelete,
}) => {
  const [activeMenuId, setActiveMenuId] = useState(null);

  if (!transactions || transactions.length === 0) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '48px 20px', color: 'var(--color-charcoal-400)' }}>
        No transactions match your current filters.
      </div>
    );
  }

  return (
    <div className="card" style={{ padding: 0 }}>
      <div className="table-container" style={{ border: 'none' }}>
        <table className="data-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Description</th>
              <th>Category</th>
              <th>Method</th>
              <th style={{ textAlign: 'right' }}>Amount</th>
              <th style={{ textAlign: 'center', width: '60px' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((t) => {
              const isIncome = t.type === 'INCOME';
              const isMenuOpen = activeMenuId === t.transactionId;

              return (
                <tr key={t.transactionId}>
                  <td style={{ whiteSpace: 'nowrap', fontWeight: 500 }}>
                    {formatDate(t.transactionDate)}
                  </td>
                  <td style={{ fontWeight: 600, color: 'var(--color-charcoal-900)' }}>
                    {t.description || '—'}
                  </td>
                  <td>
                    <span className={`badge ${isIncome ? 'badge-income' : 'badge-expense'}`}>
                      {t.categoryName}
                    </span>
                  </td>
                  <td style={{ color: 'var(--color-charcoal-500)', fontSize: '0.88rem' }}>
                    {t.paymentMethodName || '—'}
                  </td>
                  <td
                    style={{
                      textAlign: 'right',
                      fontWeight: 700,
                      fontSize: '0.95rem',
                      color: isIncome ? 'var(--color-income)' : 'var(--color-expense)',
                    }}
                  >
                    {formatCurrency(t.amount, { withSign: true, decimals: 0 })}
                  </td>
                  <td style={{ textAlign: 'center', position: 'relative' }}>
                    <div style={{ display: 'inline-block' }}>
                      <button
                        type="button"
                        className="notification-btn"
                        onClick={() => setActiveMenuId(isMenuOpen ? null : t.transactionId)}
                        aria-label="Row actions"
                      >
                        <MoreVerticalIcon size={16} />
                      </button>

                      {isMenuOpen && (
                        <div
                          style={{
                            position: 'absolute',
                            right: '10px',
                            top: '100%',
                            backgroundColor: 'var(--color-white)',
                            border: '1px solid var(--color-ivory-200)',
                            borderRadius: 'var(--radius-sm)',
                            boxShadow: 'var(--shadow-dropdown)',
                            zIndex: 50,
                            padding: '4px',
                            minWidth: '110px',
                            textAlign: 'left',
                          }}
                        >
                          <button
                            type="button"
                            className="nav-item"
                            style={{ padding: '6px 10px', fontSize: '0.85rem', color: 'var(--color-charcoal-700)' }}
                            onClick={() => {
                              setActiveMenuId(null);
                              onEdit(t);
                            }}
                          >
                            <EditIcon size={14} />
                            <span>Edit</span>
                          </button>
                          <button
                            type="button"
                            className="nav-item"
                            style={{ padding: '6px 10px', fontSize: '0.85rem', color: 'var(--color-expense)' }}
                            onClick={() => {
                              setActiveMenuId(null);
                              onDelete(t.transactionId);
                            }}
                          >
                            <TrashIcon size={14} />
                            <span>Delete</span>
                          </button>
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', padding: '16px' }}>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            disabled={page === 0}
            onClick={() => onPageChange(page - 1)}
          >
            <ChevronLeftIcon size={14} />
          </button>
          <span style={{ fontSize: '0.88rem', color: 'var(--color-charcoal-500)', padding: '0 8px' }}>
            Page {page + 1} of {totalPages}
          </span>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            disabled={page >= totalPages - 1}
            onClick={() => onPageChange(page + 1)}
          >
            <ChevronRightIcon size={14} />
          </button>
        </div>
      )}
    </div>
  );
};
