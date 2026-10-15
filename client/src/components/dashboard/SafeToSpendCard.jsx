import React, { useState } from 'react';
import { formatCurrency } from '../../utils/formatCurrency';
import { ArrowRightIcon } from '../../assets/icons/Icons';
import { Modal } from '../common/Modal';

export const SafeToSpendCard = ({ data }) => {
  const [showModal, setShowModal] = useState(false);

  if (!data) return null;

  return (
    <>
      <div className="safe-to-spend-card">
        <div>
          <div className="safe-to-spend-header">Safe to Spend</div>
          <div className="safe-to-spend-amount">{formatCurrency(data.safeToSpend)}</div>
          <p className="safe-to-spend-note" style={{ marginTop: '8px' }}>
            {data.message}
          </p>
        </div>

        <div>
          <button
            type="button"
            className="breakdown-link"
            onClick={() => setShowModal(true)}
          >
            <span>View breakdown</span>
            <ArrowRightIcon size={16} />
          </button>
        </div>
      </div>

      <Modal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        title="Safe to Spend Breakdown"
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid var(--color-ivory-200)' }}>
            <span style={{ color: 'var(--color-charcoal-600)' }}>Recorded Net Balance:</span>
            <span style={{ fontWeight: 600 }}>{formatCurrency(data.recordedBalance)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid var(--color-ivory-200)' }}>
            <span style={{ color: 'var(--color-charcoal-600)' }}>Committed to Remaining Budgets:</span>
            <span style={{ fontWeight: 600, color: 'var(--color-expense)' }}>- {formatCurrency(data.committedExpenses)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid var(--color-ivory-200)' }}>
            <span style={{ color: 'var(--color-charcoal-600)' }}>Committed to Active Savings Goals:</span>
            <span style={{ fontWeight: 600, color: 'var(--color-expense)' }}>- {formatCurrency(data.committedSavings)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontSize: '1.1rem', fontWeight: 700, borderTop: '2px solid var(--color-ivory-300)' }}>
            <span>Available Safe to Spend:</span>
            <span style={{ color: 'var(--color-turquoise-700)' }}>{formatCurrency(data.safeToSpend)}</span>
          </div>
          <button
            type="button"
            className="btn btn-primary"
            style={{ marginTop: '12px', width: '100%' }}
            onClick={() => setShowModal(false)}
          >
            Got It
          </button>
        </div>
      </Modal>
    </>
  );
};
