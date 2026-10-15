import React from 'react';
import { formatCurrency } from '../../utils/formatCurrency';

export const SummaryCard = ({ title, amount, type = 'default' }) => {
  let valueClass = 'stat-card-value';
  if (type === 'income') valueClass += ' income';
  if (type === 'expense') valueClass += ' expense';

  return (
    <div className="stat-card">
      <div className="stat-card-title">{title}</div>
      <div className={valueClass}>{formatCurrency(amount)}</div>
    </div>
  );
};
