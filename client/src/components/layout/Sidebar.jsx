import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import {
  DashboardIcon,
  TransactionsIcon,
  BudgetsIcon,
  ReportsIcon,
  GoalsIcon,
  CategoriesIcon,
  SettingsIcon,
  LogoutIcon,
} from '../../assets/icons/Icons';

export const Sidebar = ({ activeTab, onNavigate }) => {
  const { logout } = useAuth();

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: DashboardIcon },
    { id: 'transactions', label: 'Transactions', icon: TransactionsIcon },
    { id: 'budgets', label: 'Budgets', icon: BudgetsIcon },
    { id: 'reports', label: 'Reports', icon: ReportsIcon },
    { id: 'goals', label: 'Goals', icon: GoalsIcon },
    { id: 'categories', label: 'Categories', icon: CategoriesIcon },
    { id: 'settings', label: 'Settings', icon: SettingsIcon },
  ];

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              type="button"
              className={`nav-item ${isActive ? 'active' : ''}`}
              onClick={() => onNavigate(item.id)}
            >
              <Icon size={18} />
              <span>{item.label}</span>
            </button>
          );
        })}

        <div className="sidebar-divider" />

        <button
          type="button"
          className="nav-item"
          style={{ color: 'var(--color-charcoal-400)' }}
          onClick={logout}
        >
          <LogoutIcon size={18} />
          <span>Logout</span>
        </button>
      </nav>
    </aside>
  );
};
