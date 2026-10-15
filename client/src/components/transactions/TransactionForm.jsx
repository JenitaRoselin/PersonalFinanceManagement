import React, { useState, useEffect } from 'react';
import { getTodayFormatted } from '../../utils/dateUtils';
import { categoryService } from '../../services/categoryService';
import { paymentMethodService } from '../../services/paymentMethodService';

export const TransactionForm = ({ initialData, onSubmit, onCancel, loading }) => {
  const [type, setType] = useState(initialData?.type || 'EXPENSE');
  const [amount, setAmount] = useState(initialData?.amount || '');
  const [categoryId, setCategoryId] = useState(initialData?.categoryId || '');
  const [paymentMethodId, setPaymentMethodId] = useState(initialData?.paymentMethodId || '');
  const [transactionDate, setTransactionDate] = useState(initialData?.transactionDate || getTodayFormatted());
  const [description, setDescription] = useState(initialData?.description || '');

  const [categories, setCategories] = useState([]);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [formError, setFormError] = useState('');

  useEffect(() => {
    loadCategoriesAndMethods();
  }, []);

  // When type changes, clear categoryId if current selected category doesn't match new type
  useEffect(() => {
    const selectedCat = categories.find((c) => String(c.categoryId) === String(categoryId));
    if (selectedCat && selectedCat.type !== type) {
      setCategoryId('');
    }
  }, [type, categories]);

  const loadCategoriesAndMethods = async () => {
    try {
      const [catList, methodList] = await Promise.all([
        categoryService.getAllCategories(),
        paymentMethodService.getAllPaymentMethods(),
      ]);
      setCategories(catList);
      setPaymentMethods(methodList);

      // Default selection if not already selected
      if (!categoryId && catList.length > 0) {
        const defaultCat = catList.find((c) => c.type === type);
        if (defaultCat) setCategoryId(defaultCat.categoryId);
      }
      if (!paymentMethodId && methodList.length > 0) {
        setPaymentMethodId(methodList[0].paymentMethodId);
      }
    } catch (err) {
      console.error('Failed to load categories or payment methods', err);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setFormError('');

    if (!amount || parseFloat(amount) <= 0) {
      setFormError('Please enter a valid amount greater than 0');
      return;
    }
    if (!categoryId) {
      setFormError('Please select a category');
      return;
    }
    if (!transactionDate) {
      setFormError('Please select a date');
      return;
    }

    onSubmit({
      type,
      amount: parseFloat(amount),
      categoryId: parseInt(categoryId),
      paymentMethodId: paymentMethodId ? parseInt(paymentMethodId) : null,
      transactionDate,
      description: description.trim(),
    });
  };

  const filteredCategories = categories.filter((c) => c.type === type);

  return (
    <form onSubmit={handleSubmit}>
      {formError && <div className="error-banner">{formError}</div>}

      <div className="form-group">
        <label className="form-label">Type</label>
        <div className="type-toggle-group">
          <button
            type="button"
            className={`type-toggle-btn ${type === 'EXPENSE' ? 'active-expense' : ''}`}
            onClick={() => setType('EXPENSE')}
          >
            EXPENSE
          </button>
          <button
            type="button"
            className={`type-toggle-btn ${type === 'INCOME' ? 'active-income' : ''}`}
            onClick={() => setType('INCOME')}
          >
            INCOME
          </button>
        </div>
      </div>

      <div className="form-group">
        <label className="form-label">Amount</label>
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
            step="0.01"
            min="0.01"
            className="form-input"
            style={{ paddingLeft: '30px', fontSize: '1.1rem', fontWeight: 600 }}
            placeholder="0.00"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            required
          />
        </div>
      </div>

      <div className="form-group">
        <label className="form-label">Category</label>
        <select
          className="form-select"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
          required
        >
          <option value="">Select category...</option>
          {filteredCategories.map((c) => (
            <option key={c.categoryId} value={c.categoryId}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label className="form-label">Payment Method</label>
        <select
          className="form-select"
          value={paymentMethodId}
          onChange={(e) => setPaymentMethodId(e.target.value)}
        >
          <option value="">Select payment method (optional)...</option>
          {paymentMethods.map((m) => (
            <option key={m.paymentMethodId} value={m.paymentMethodId}>
              {m.name}
            </option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label className="form-label">Date</label>
        <input
          type="date"
          className="form-input"
          value={transactionDate}
          onChange={(e) => setTransactionDate(e.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label className="form-label">Description</label>
        <input
          type="text"
          className="form-input"
          placeholder="e.g. Lunch with friends, Groceries, Salary"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          maxLength={255}
        />
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '26px' }}>
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
          {loading ? 'SAVING...' : 'SAVE TRANSACTION'}
        </button>
      </div>
    </form>
  );
};
