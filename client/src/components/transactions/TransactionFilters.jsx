import React, { useState, useEffect } from 'react';
import { SearchIcon } from '../../assets/icons/Icons';
import { categoryService } from '../../services/categoryService';

export const TransactionFilters = ({ filters, onFilterChange }) => {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    categoryService.getAllCategories()
      .then(setCategories)
      .catch((err) => console.error(err));
  }, []);

  return (
    <div className="card" style={{ padding: '16px 20px', marginBottom: '20px' }}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '14px', alignItems: 'center' }}>
        {/* Search */}
        <div style={{ position: 'relative' }}>
          <span style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-charcoal-400)' }}>
            <SearchIcon size={16} />
          </span>
          <input
            type="text"
            className="form-input"
            style={{ paddingLeft: '34px' }}
            placeholder="Search transactions..."
            value={filters.query || ''}
            onChange={(e) => onFilterChange({ ...filters, query: e.target.value, page: 0 })}
          />
        </div>

        {/* Category */}
        <div>
          <select
            className="form-select"
            value={filters.categoryId || ''}
            onChange={(e) => onFilterChange({ ...filters, categoryId: e.target.value || null, page: 0 })}
          >
            <option value="">All Categories</option>
            {categories.map((c) => (
              <option key={c.categoryId} value={c.categoryId}>
                {c.name} ({c.type})
              </option>
            ))}
          </select>
        </div>

        {/* Type */}
        <div>
          <select
            className="form-select"
            value={filters.type || ''}
            onChange={(e) => onFilterChange({ ...filters, type: e.target.value || null, page: 0 })}
          >
            <option value="">All Types</option>
            <option value="EXPENSE">Expense</option>
            <option value="INCOME">Income</option>
          </select>
        </div>

        {/* Sort by */}
        <div>
          <select
            className="form-select"
            value={`${filters.sortBy || 'transactionDate'}_${filters.direction || 'DESC'}`}
            onChange={(e) => {
              const [sortBy, direction] = e.target.value.split('_');
              onFilterChange({ ...filters, sortBy, direction, page: 0 });
            }}
          >
            <option value="transactionDate_DESC">Date (Newest first)</option>
            <option value="transactionDate_ASC">Date (Oldest first)</option>
            <option value="amount_DESC">Amount (Highest first)</option>
            <option value="amount_ASC">Amount (Lowest first)</option>
          </select>
        </div>
      </div>
    </div>
  );
};
