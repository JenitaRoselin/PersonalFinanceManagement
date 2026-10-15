import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { LogoIcon } from '../assets/icons/Icons';

export const Login = ({ onNavigateToRegister }) => {
  const { login, loading } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await login({ email, password });
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password. Please try again.');
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div style={{ display: 'inline-flex', padding: '10px', backgroundColor: 'var(--color-turquoise-50)', borderRadius: 'var(--radius-md)', marginBottom: '16px', color: 'var(--color-turquoise-700)' }}>
          <LogoIcon size={32} />
        </div>

        <h1 className="auth-title">Personal Finance</h1>
        <p className="auth-subtitle">Take control of your money.</p>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              className="form-input"
              placeholder="jenita@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', padding: '12px', marginTop: '10px' }}
            disabled={loading}
          >
            {loading ? 'AUTHENTICATING...' : 'LOGIN'}
          </button>
        </form>

        <div className="auth-footer">
          Don't have an account?{' '}
          <button
            type="button"
            className="auth-link"
            style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}
            onClick={onNavigateToRegister}
          >
            Register
          </button>
        </div>
      </div>
    </div>
  );
};
