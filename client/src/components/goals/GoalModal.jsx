import React, { useState, useEffect } from 'react';
import { Modal } from '../common/Modal';

export const GoalModal = ({ isOpen, onClose, initialData, onSubmit, loading }) => {
  const [name, setName] = useState(initialData?.name || '');
  const [targetAmount, setTargetAmount] = useState(initialData?.targetAmount || '');
  const [currentAmount, setCurrentAmount] = useState(initialData?.currentAmount || '0');
  const [targetDate, setTargetDate] = useState(initialData?.targetDate || '');
  const [status, setStatus] = useState(initialData?.status || 'ACTIVE');
  const [formError, setFormError] = useState('');

  useEffect(() => {
    if (initialData) {
      setName(initialData.name || '');
      setTargetAmount(initialData.targetAmount || '');
      setCurrentAmount(initialData.currentAmount || '0');
      setTargetDate(initialData.targetDate || '');
      setStatus(initialData.status || 'ACTIVE');
    } else {
      setName('');
      setTargetAmount('');
      setCurrentAmount('0');
      setTargetDate('');
      setStatus('ACTIVE');
    }
    setFormError('');
  }, [initialData, isOpen]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setFormError('');

    if (!name.trim()) {
      setFormError('Please enter a goal name');
      return;
    }
    if (!targetAmount || parseFloat(targetAmount) <= 0) {
      setFormError('Please enter a target amount greater than 0');
      return;
    }

    onSubmit({
      name: name.trim(),
      targetAmount: parseFloat(targetAmount),
      currentAmount: parseFloat(currentAmount) || 0,
      targetDate: targetDate || null,
      status,
    });
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialData ? 'Edit Savings Goal' : 'Create Savings Goal'}
    >
      <form onSubmit={handleSubmit}>
        {formError && <div className="error-banner">{formError}</div>}

        <div className="form-group">
          <label className="form-label">Goal Name</label>
          <input
            type="text"
            className="form-input"
            placeholder="e.g. New Laptop, Vacation Trip, Emergency Fund"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Target Amount (₹)</label>
          <input
            type="number"
            step="1"
            min="1"
            className="form-input"
            placeholder="80000"
            value={targetAmount}
            onChange={(e) => setTargetAmount(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Initial Saved Amount (₹)</label>
          <input
            type="number"
            step="1"
            min="0"
            className="form-input"
            placeholder="0"
            value={currentAmount}
            onChange={(e) => setCurrentAmount(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label className="form-label">Target Date (Optional)</label>
          <input
            type="date"
            className="form-input"
            value={targetDate}
            onChange={(e) => setTargetDate(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            disabled={loading}
          >
            CANCEL
          </button>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'SAVING...' : 'SAVE GOAL'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
