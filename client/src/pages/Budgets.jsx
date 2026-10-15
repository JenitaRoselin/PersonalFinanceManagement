import React, { useState, useEffect } from 'react';
import { budgetService } from '../services/budgetService';
import { BudgetCard } from '../components/budgets/BudgetCard';
import { BudgetForm } from '../components/budgets/BudgetForm';
import { Modal } from '../components/common/Modal';
import { Loading } from '../components/common/Loading';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { formatCurrency } from '../utils/formatCurrency';
import { PlusIcon, EditIcon, TrashIcon } from '../assets/icons/Icons';

export const Budgets = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [budgets, setBudgets] = useState([]);
  const [selectedMonth, setSelectedMonth] = useState('2026-10-01');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBudget, setEditingBudget] = useState(null);
  const [modalLoading, setModalLoading] = useState(false);

  useEffect(() => {
    fetchBudgets();
  }, [selectedMonth]);

  const fetchBudgets = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await budgetService.getBudgetsForMonth(selectedMonth);
      setBudgets(data || []);
    } catch (err) {
      console.error(err);
      setError('Failed to load budgets for the selected month');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveBudget = async (formData) => {
    setModalLoading(true);
    try {
      if (editingBudget?.budgetId) {
        await budgetService.updateBudget(editingBudget.budgetId, formData);
      } else {
        await budgetService.createBudget(formData);
      }
      setIsModalOpen(false);
      setEditingBudget(null);
      await fetchBudgets();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save budget');
    } finally {
      setModalLoading(false);
    }
  };

  const handleDeleteBudget = async (id) => {
    try {
      await budgetService.deleteBudget(id);
      await fetchBudgets();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete budget');
    }
  };

  // Aggregated totals
  let totalLimit = 0;
  let totalSpent = 0;
  budgets.forEach((b) => {
    totalLimit += parseFloat(b.limitAmount) || 0;
    totalSpent += parseFloat(b.amountSpent) || 0;
  });

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Budgets</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '6px' }}>
            <span style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--color-charcoal-600)' }}>
              October 2026
            </span>
          </div>
        </div>

        <div className="page-actions">
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => {
              setEditingBudget(null);
              setIsModalOpen(true);
            }}
          >
            <PlusIcon size={16} />
            <span>Create Budget</span>
          </button>
        </div>
      </div>

      <ErrorMessage message={error} onRetry={fetchBudgets} />

      {loading && budgets.length === 0 ? (
        <Loading message="Loading budget statuses..." />
      ) : (
        <>
          {/* Top Overall Monthly Budget Card */}
          <BudgetCard totalLimit={totalLimit} totalSpent={totalSpent} />

          {/* Category Budgets Table */}
          <div className="card" style={{ padding: 0 }}>
            <div className="table-container" style={{ border: 'none' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Category</th>
                    <th style={{ textAlign: 'right' }}>Budget</th>
                    <th style={{ textAlign: 'right' }}>Spent</th>
                    <th style={{ textAlign: 'right' }}>Remaining</th>
                    <th>Status</th>
                    <th style={{ textAlign: 'center', width: '80px' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {budgets.length === 0 ? (
                    <tr>
                      <td colSpan="6" style={{ textAlign: 'center', padding: '36px', color: 'var(--color-charcoal-400)' }}>
                        No category budgets created for this month yet. Click "+ Create Budget" to set spending limits.
                      </td>
                    </tr>
                  ) : (
                    budgets.map((b) => {
                      const pct = b.percentageUsed ? Math.round(b.percentageUsed) : 0;
                      let badgeClass = 'badge-normal';
                      let statusText = `${pct}% Normal`;

                      if (pct >= 100) {
                        badgeClass = 'badge-danger';
                        statusText = `${pct}% Over Budget`;
                      } else if (pct >= 90) {
                        badgeClass = 'badge-danger';
                        statusText = `${pct}% Near Limit`;
                      } else if (pct >= 70) {
                        badgeClass = 'badge-warning';
                        statusText = `${pct}% Approaching`;
                      }

                      return (
                        <tr key={b.budgetId}>
                          <td style={{ fontWeight: 600, color: 'var(--color-charcoal-900)' }}>
                            {b.categoryName}
                          </td>
                          <td style={{ textAlign: 'right', fontWeight: 600 }}>
                            {formatCurrency(b.limitAmount)}
                          </td>
                          <td style={{ textAlign: 'right', fontWeight: 600, color: 'var(--color-charcoal-700)' }}>
                            {formatCurrency(b.amountSpent)}
                          </td>
                          <td style={{
                            textAlign: 'right',
                            fontWeight: 700,
                            color: b.remainingBudget >= 0 ? 'var(--color-turquoise-700)' : 'var(--color-expense)'
                          }}>
                            {formatCurrency(b.remainingBudget)}
                          </td>
                          <td>
                            <span className={`badge ${badgeClass}`}>
                              {statusText}
                            </span>
                          </td>
                          <td style={{ textAlign: 'center' }}>
                            <div style={{ display: 'flex', justifyContent: 'center', gap: '4px' }}>
                              <button
                                type="button"
                                className="notification-btn"
                                onClick={() => {
                                  setEditingBudget(b);
                                  setIsModalOpen(true);
                                }}
                                title="Edit Budget"
                              >
                                <EditIcon size={14} />
                              </button>
                              <button
                                type="button"
                                className="notification-btn"
                                onClick={() => handleDeleteBudget(b.budgetId)}
                                title="Delete Budget"
                                style={{ color: 'var(--color-expense)' }}
                              >
                                <TrashIcon size={14} />
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {/* Create / Edit Budget Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingBudget(null);
        }}
        title={editingBudget ? 'Edit Budget' : 'Create Monthly Budget'}
      >
        <BudgetForm
          initialData={editingBudget}
          selectedMonth={selectedMonth}
          onSubmit={handleSaveBudget}
          onCancel={() => {
            setIsModalOpen(false);
            setEditingBudget(null);
          }}
          loading={modalLoading}
        />
      </Modal>
    </div>
  );
};
