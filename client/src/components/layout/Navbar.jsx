import React, { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { LogoIcon, BellIcon, UserIcon, ChevronDownIcon, LogoutIcon, SettingsIcon } from '../../assets/icons/Icons';

export const Navbar = ({ activeTab, onNavigate }) => {
  const { user, logout } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);

  const displayName = user?.fullName || 'Jenita';
  const initial = displayName.charAt(0).toUpperCase();

  return (
    <header className="navbar">
      <div
        className="navbar-brand"
        onClick={() => onNavigate('dashboard')}
        style={{ cursor: 'pointer' }}
      >
        <div className="brand-icon">
          <LogoIcon size={18} />
        </div>
        <span>Personal Finance</span>
      </div>

      <div className="navbar-actions">
        <button
          type="button"
          className="notification-btn"
          aria-label="Notifications"
          title="Notifications"
          onClick={() => onNavigate('dashboard')}
        >
          <BellIcon size={19} />
          <span className="notification-dot"></span>
        </button>

        <div style={{ position: 'relative' }}>
          <div
            className="user-profile-menu"
            onClick={() => setShowDropdown(!showDropdown)}
          >
            <div className="user-avatar-circle">{initial}</div>
            <span>{displayName}</span>
            <ChevronDownIcon size={14} />
          </div>

          {showDropdown && (
            <div
              style={{
                position: 'absolute',
                top: 'calc(100% + 8px)',
                right: 0,
                width: '180px',
                backgroundColor: 'var(--color-white)',
                borderRadius: 'var(--radius-sm)',
                border: '1px solid var(--color-ivory-200)',
                boxShadow: 'var(--shadow-dropdown)',
                zIndex: 110,
                padding: '6px',
              }}
            >
              <button
                type="button"
                className="nav-item"
                style={{ color: 'var(--color-charcoal-700)', padding: '8px 12px' }}
                onClick={() => {
                  setShowDropdown(false);
                  onNavigate('settings');
                }}
              >
                <SettingsIcon size={16} />
                <span>Settings</span>
              </button>
              <div style={{ height: '1px', background: 'var(--color-ivory-200)', margin: '4px 0' }} />
              <button
                type="button"
                className="nav-item"
                style={{ color: 'var(--color-expense)', padding: '8px 12px' }}
                onClick={() => {
                  setShowDropdown(false);
                  logout();
                }}
              >
                <LogoutIcon size={16} />
                <span>Logout</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
