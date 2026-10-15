import React, { useState } from 'react';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate } from '../../utils/dateUtils';
import { EditIcon, TrashIcon, PlusIcon } from '../../assets/icons/Icons';

export const GoalCard = ({ goal, onEdit, onDelete, onAddProgress }) => {
  const [quickAmount, setQuickAmount] = useState('');
  const [showAddInput, setShowAddInput] = useState(false);

  const pct = goal.percentageComplete ? Math.min(100, Math.round(goal.percentageComplete)) : 0;
  const isCompleted = goal.status === 'COMPLETED' || pct >= 100;

  const handleDeposit = (amount) => {
    onAddProgress(goal.goalId, amount);
  };

  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
      <div>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
          <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--color-charcoal-900)' }}>
            {goal.name}
          </h3>
          <div style={{ display: 'flex', gap: '4px' }}>
            <button
              type="button"
              className="notification-btn"
              onClick={() => onEdit(goal)}
              title="Edit goal"
            >
              <EditIcon size={15} />
            </button>
            <button
              type="button"
              className="notification-btn"
              onClick={() => onDelete(goal.goalId)}
              title="Delete goal"
              style={{ color: 'var(--color-expense)' }}
            >
              <TrashIcon size={15} />
            </button>
          </div>
        </div>

        <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--color-charcoal-900)', marginBottom: '8px' }}>
          {formatCurrency(goal.currentAmount)} <span style={{ fontSize: '0.9rem', fontWeight: 400, color: 'var(--color-charcoal-400)' }}>/ {formatCurrency(goal.targetAmount)}</span>
        </div>

        <div className="progress-bar-track" style={{ height: '10px' }}>
          <div
            className="progress-bar-fill"
            style={{
              width: `${pct}%`,
              backgroundColor: isCompleted ? 'var(--color-turquoise-600)' : 'var(--color-turquoise-700)'
            }}
          />
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginTop: '6px', color: 'var(--color-charcoal-500)' }}>
          <span style={{ fontWeight: 600, color: 'var(--color-turquoise-700)' }}>{pct}% complete</span>
          {goal.targetDate && <span>Target: {formatDate(goal.targetDate)}</span>}
        </div>
      </div>

      {/* Quick Deposit Actions */}
      <div style={{ marginTop: '20px', paddingTop: '14px', borderTop: '1px solid var(--color-ivory-200)' }}>
        <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => handleDeposit(500)}
          >
            +₹500
          </button>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => handleDeposit(2000)}
          >
            +₹2,000
          </button>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => setShowAddInput(!showAddInput)}
          >
            Custom
          </button>
        </div>

        {showAddInput && (
          <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
            <input
              type="number"
              className="form-input"
              style={{ padding: '6px 10px', fontSize: '0.88rem' }}
              placeholder="Amount (₹)"
              value={quickAmount}
              onChange={(e) => setQuickAmount(e.target.value)}
            />
            <button
              type="button"
              className="btn btn-primary btn-sm"
              onClick={() => {
                if (quickAmount && parseFloat(quickAmount) > 0) {
                  handleDeposit(parseFloat(quickAmount));
                  setQuickAmount('');
                  setShowAddInput(false);
                }
              }}
            >
              Add
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
