import React, { useState, useEffect } from 'react';
import { categoryService } from '../../services/categoryService';

export const BudgetForm = ({ initialData, selectedMonth, onSubmit, onCancel, loading }) => {
  const [categoryId, setCategoryId] = useState(initialData?.categoryId || '');
  const [limitAmount, setLimitAmount] = useState(initialData?.limitAmount || '');
  const [categories, setCategories] = useState([]);
  const [formError, setFormError] = useState('');

  useEffect(() => {
    categoryService.getCategoriesByType('EXPENSE')
      .then(setCategories)
      .catch((err) => console.error(err));
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    setFormError('');

    if (!categoryId) {
      setFormError('Please select a category');
      return;
    }
    if (!limitAmount || parseFloat(limitAmount) <= 0) {
      setFormError('Please enter a budget limit greater than 0');
      return;
    }

    onSubmit({
      categoryId: parseInt(categoryId),
      limitAmount: parseFloat(limitAmount),
      budgetMonth: selectedMonth,
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      {formError && <div className="error-banner">{formError}</div>}

      <div className="form-group">
        <label className="form-label">Expense Category</label>
        <select
          className="form-select"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
          disabled={Boolean(initialData?.budgetId)} // Don't change category on edit
          required
        >
          <option value="">Select an expense category...</option>
          {categories.map((c) => (
            <option key={c.categoryId} value={c.categoryId}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label className="form-label">Monthly Limit</label>
        <div style={{ position: 'relative' }}>
          <span style={{
            position: 'absolute',
            left: '12px',
            top: '50%',
            transform: 'translateY(-50%)',
            fontWeight: 600,
            color: 'var(--color-charcoal-500)'
          }}>
            ₹
          </span>
          <input
            type="number"
            step="1"
            min="1"
            className="form-input"
            style={{ paddingLeft: '30px', fontSize: '1.1rem', fontWeight: 600 }}
            placeholder="5000"
            value={limitAmount}
            onChange={(e) => setLimitAmount(e.target.value)}
            required
          />
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={onCancel}
          disabled={loading}
        >
          CANCEL
        </button>
        <button
          type="submit"
          className="btn btn-primary"
          disabled={loading}
        >
          {loading ? 'SAVING...' : 'SAVE BUDGET'}
        </button>
      </div>
    </form>
  );
};
