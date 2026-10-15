import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { authService } from '../services/authService';
import { transactionService } from '../services/transactionService';

export const Settings = () => {
  const { user, updateUser, logout } = useAuth();

  // Profile State
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [profileMsg, setProfileMsg] = useState('');
  const [profileLoading, setProfileLoading] = useState(false);

  // Password State
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordMsg, setPasswordMsg] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordLoading, setPasswordLoading] = useState(false);

  // Preferences State
  const [currency, setCurrency] = useState('INR');
  const [dateFormat, setDateFormat] = useState('DD/MM/YYYY');

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setProfileLoading(true);
    setProfileMsg('');
    try {
      const updated = await authService.updateProfile({ fullName });
      updateUser(updated);
      setProfileMsg('Profile updated successfully!');
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setProfileLoading(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setPasswordMsg('');
    setPasswordError('');

    if (newPassword !== confirmPassword) {
      setPasswordError('New passwords do not match');
      return;
    }
    if (newPassword.length < 6) {
      setPasswordError('Password must be at least 6 characters');
      return;
    }

    setPasswordLoading(true);
    try {
      await authService.changePassword({ oldPassword, newPassword });
      setPasswordMsg('Password changed successfully!');
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setPasswordError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleExportData = async () => {
    try {
      const data = await transactionService.getTransactions({ size: 1000 });
      const jsonString = `data:text/json;charset=utf-8,${encodeURIComponent(
        JSON.stringify(data.content || [], null, 2)
      )}`;
      const downloadAnchor = document.createElement('a');
      downloadAnchor.setAttribute('href', jsonString);
      downloadAnchor.setAttribute('download', 'personal_finance_transactions.json');
      document.body.appendChild(downloadAnchor);
      downloadAnchor.click();
      downloadAnchor.remove();
    } catch (err) {
      alert('Failed to export transactions data');
    }
  };

  const handleDeleteAccount = async () => {
    try {
      await authService.deleteAccount();
      logout();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete account');
    }
  };

  return (
    <div style={{ maxWidth: '780px' }}>
      <div className="page-header">
        <div>
          <h1 className="page-title">Settings</h1>
          <p className="page-subtitle">Manage your account profile, preferences, and security settings.</p>
        </div>
      </div>

      {/* Profile Section */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-charcoal-900)', marginBottom: '4px' }}>
          Profile
        </h2>
        <div style={{ height: '1px', backgroundColor: 'var(--color-ivory-200)', margin: '12px 0 20px' }} />

        {profileMsg && <div className="success-banner">{profileMsg}</div>}

        <form onSubmit={handleUpdateProfile}>
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text"
              className="form-input"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              className="form-input"
              value={user?.email || ''}
              disabled
              style={{ backgroundColor: 'var(--color-ivory-100)', color: 'var(--color-charcoal-400)' }}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={profileLoading}
          >
            {profileLoading ? 'Saving...' : 'Update Profile'}
          </button>
        </form>

        <div style={{ height: '1px', backgroundColor: 'var(--color-ivory-200)', margin: '28px 0 20px' }} />

        <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-charcoal-800)', marginBottom: '14px' }}>
          Change Password
        </h3>

        {passwordMsg && <div className="success-banner">{passwordMsg}</div>}
        {passwordError && <div className="error-banner">{passwordError}</div>}

        <form onSubmit={handleChangePassword}>
          <div className="form-group">
            <label className="form-label">Current Password</label>
            <input
              type="password"
              className="form-input"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">New Password</label>
            <input
              type="password"
              className="form-input"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Confirm New Password</label>
            <input
              type="password"
              className="form-input"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-secondary"
            disabled={passwordLoading}
          >
            {passwordLoading ? 'Updating...' : 'Change Password'}
          </button>
        </form>
      </div>

      {/* Preferences Section */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-charcoal-900)', marginBottom: '4px' }}>
          Preferences
        </h2>
        <div style={{ height: '1px', backgroundColor: 'var(--color-ivory-200)', margin: '12px 0 20px' }} />

        <div className="form-group">
          <label className="form-label">Currency</label>
          <select
            className="form-select"
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
          >
            <option value="INR">INR (₹)</option>
            <option value="USD">USD ($)</option>
            <option value="EUR">EUR (€)</option>
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">Date Format</label>
          <select
            className="form-select"
            value={dateFormat}
            onChange={(e) => setDateFormat(e.target.value)}
          >
            <option value="DD/MM/YYYY">DD/MM/YYYY</option>
            <option value="YYYY-MM-DD">YYYY-MM-DD</option>
            <option value="MM/DD/YYYY">MM/DD/YYYY</option>
          </select>
        </div>
      </div>

      {/* Data Section */}
      <div className="card">
        <h2 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-charcoal-900)', marginBottom: '4px' }}>
          Data & Security
        </h2>
        <div style={{ height: '1px', backgroundColor: 'var(--color-ivory-200)', margin: '12px 0 20px' }} />

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <div style={{ fontWeight: 600 }}>Export Transactions Data</div>
              <div style={{ fontSize: '0.85rem', color: 'var(--color-charcoal-500)' }}>Download a complete JSON export of all your recorded financial transactions.</div>
            </div>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={handleExportData}
            >
              Export Data
            </button>
          </div>

          <div style={{ height: '1px', backgroundColor: 'var(--color-ivory-100)' }} />

          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <div style={{ fontWeight: 600, color: 'var(--color-expense)' }}>Delete Account</div>
              <div style={{ fontSize: '0.85rem', color: 'var(--color-charcoal-500)' }}>Permanently remove your account and all associated categories, transactions, and budgets.</div>
            </div>
            <button
              type="button"
              className="btn btn-danger"
              onClick={handleDeleteAccount}
            >
              Delete Account
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
