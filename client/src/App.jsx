import React, { useState } from 'react';
import { AuthProvider } from './context/AuthContext';
import { useAuth } from './hooks/useAuth';
import { Navbar } from './components/layout/Navbar';
import { Sidebar } from './components/layout/Sidebar';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Dashboard } from './pages/Dashboard';
import { Transactions } from './pages/Transactions';
import { Budgets } from './pages/Budgets';
import { Reports } from './pages/Reports';
import { SavingsGoals } from './pages/SavingsGoals';
import { Categories } from './pages/Categories';
import { Settings } from './pages/Settings';

const MainApp = () => {
  const { isAuthenticated } = useAuth();
  const [authView, setAuthView] = useState('login'); // 'login' or 'register'
  const [activeTab, setActiveTab] = useState('dashboard');

  if (!isAuthenticated) {
    if (authView === 'register') {
      return <Register onNavigateToLogin={() => setAuthView('login')} />;
    }
    return <Login onNavigateToRegister={() => setAuthView('register')} />;
  }

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard onNavigate={setActiveTab} />;
      case 'transactions':
        return <Transactions />;
      case 'budgets':
        return <Budgets />;
      case 'reports':
        return <Reports />;
      case 'goals':
        return <SavingsGoals />;
      case 'categories':
        return <Categories />;
      case 'settings':
        return <Settings />;
      default:
        return <Dashboard onNavigate={setActiveTab} />;
    }
  };

  return (
    <div className="app-container">
      <Navbar activeTab={activeTab} onNavigate={setActiveTab} />
      <div className="app-body">
        <Sidebar activeTab={activeTab} onNavigate={setActiveTab} />
        <main className="main-content">
          {renderContent()}
        </main>
      </div>
    </div>
  );
};

export function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}

export default App;
