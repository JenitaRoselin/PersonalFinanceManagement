import React, { useState, useEffect } from 'react';
import { Modal } from '../common/Modal';

export const CategoryModal = ({ isOpen, onClose, initialData, defaultType = 'EXPENSE', onSubmit, loading }) => {
  const [name, setName] = useState('');
  const [type, setType] = useState(defaultType);
  const [formError, setFormError] = useState('');

  useEffect(() => {
    if (initialData) {
      setName(initialData.name || '');
      setType(initialData.type || defaultType);
    } else {
      setName('');
      setType(defaultType);
    }
    setFormError('');
  }, [initialData, defaultType, isOpen]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setFormError('');

    if (!name.trim()) {
      setFormError('Please enter a category name');
      return;
    }

    onSubmit({
      name: name.trim(),
      type,
    });
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialData ? 'Edit Category' : 'Add Category'}
    >
      <form onSubmit={handleSubmit}>
        {formError && <div className="error-banner">{formError}</div>}

        <div className="form-group">
          <label className="form-label">Category Type</label>
          <div className="type-toggle-group">
            <button
              type="button"
              className={`type-toggle-btn ${type === 'EXPENSE' ? 'active-expense' : ''}`}
              onClick={() => setType('EXPENSE')}
              disabled={Boolean(initialData)} // Don't allow changing type on edit
            >
              EXPENSE
            </button>
            <button
              type="button"
              className={`type-toggle-btn ${type === 'INCOME' ? 'active-income' : ''}`}
              onClick={() => setType('INCOME')}
              disabled={Boolean(initialData)}
            >
              INCOME
            </button>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Category Name</label>
          <input
            type="text"
            className="form-input"
            placeholder="e.g. Healthcare, Groceries, Dividends"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={60}
            required
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
            {loading ? 'SAVING...' : 'SAVE CATEGORY'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
