import React from 'react';
import { AlertTriangleIcon } from '../../assets/icons/Icons';

export const ErrorMessage = ({ message, onRetry }) => {
  if (!message) return null;

  return (
    <div className="error-banner" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
        <AlertTriangleIcon size={18} />
        <span>{message}</span>
      </div>
      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="btn btn-sm"
          style={{ backgroundColor: 'var(--color-danger)', color: '#fff', border: 'none' }}
        >
          Retry
        </button>
      )}
    </div>
  );
};
