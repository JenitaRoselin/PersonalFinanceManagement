import React from 'react';

export const Loading = ({ message = "Loading financial data..." }) => (
  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '48px 0', gap: '16px' }}>
    <div style={{
      width: '36px',
      height: '36px',
      border: '3px solid var(--color-ivory-200)',
      borderTopColor: 'var(--color-turquoise-600)',
      borderRadius: '50%',
      animation: 'spin 0.8s linear infinite'
    }} />
    <style>
      {`@keyframes spin { to { transform: rotate(360deg); } }`}
    </style>
    <p style={{ color: 'var(--color-charcoal-400)', fontSize: '0.9rem' }}>{message}</p>
  </div>
);
