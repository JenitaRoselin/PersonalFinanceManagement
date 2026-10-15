import React, { useState, useEffect } from 'react';
import { dashboardService } from '../services/dashboardService';
import { insightService } from '../services/insightService';
import { reportService } from '../services/reportService';
import { transactionService } from '../services/transactionService';
import { SummaryCard } from '../components/dashboard/SummaryCard';
import { SafeToSpendCard } from '../components/dashboard/SafeToSpendCard';
import { IncomeExpenseChart } from '../components/dashboard/IncomeExpenseChart';
import { CategoryChart } from '../components/dashboard/CategoryChart';
import { RecentTransactions } from '../components/dashboard/RecentTransactions';
import { BudgetOverview } from '../components/dashboard/BudgetOverview';
import { TransactionModal } from '../components/transactions/TransactionModal';
import { Loading } from '../components/common/Loading';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { PlusIcon } from '../assets/icons/Icons';
import { getFirstDayOfMonth } from '../utils/dateUtils';

export const Dashboard = ({ onNavigate }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [summary, setSummary] = useState(null);
  const [safeToSpend, setSafeToSpend] = useState(null);
  const [trendData, setTrendData] = useState([]);
  const [categorySpending, setCategorySpending] = useState([]);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [budgetStatus, setBudgetStatus] = useState([]);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);
  const [modalLoading, setModalLoading] = useState(false);

  // Month state (defaults to October 2026 or current month)
  const [selectedMonth, setSelectedMonth] = useState('2026-10-01');

  useEffect(() => {
    fetchDashboardData();
  }, [selectedMonth]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [sum, safe, trends, cats, recents, budgets] = await Promise.all([
        dashboardService.getSummary(),
        insightService.getSafeToSpend(selectedMonth),
        reportService.getIncomeVsExpense(4),
        dashboardService.getCategorySpending(),
        dashboardService.getRecentTransactions(5),
        dashboardService.getBudgetStatus(selectedMonth),
      ]);

      setSummary(sum);
      setSafeToSpend(safe);
      setTrendData(trends);
      setCategorySpending(cats);
      setRecentTransactions(recents);
      setBudgetStatus(budgets);
    } catch (err) {
      console.error('Failed to load dashboard data', err);
      setError('Could not load dashboard data. Please verify your connection.');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveTransaction = async (formData) => {
    setModalLoading(true);
    try {
      if (editingTransaction?.transactionId) {
        await transactionService.updateTransaction(editingTransaction.transactionId, formData);
      } else {
        await transactionService.createTransaction(formData);
      }
      setIsModalOpen(false);
      setEditingTransaction(null);
      await fetchDashboardData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to save transaction');
    } finally {
      setModalLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '6px' }}>
            <span style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--color-charcoal-600)' }}>
              October 2026
            </span>
          </div>
        </div>

        <div className="page-actions">
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => {
              setEditingTransaction(null);
              setIsModalOpen(true);
            }}
          >
            <PlusIcon size={16} />
            <span>Add Transaction</span>
          </button>
        </div>
      </div>

      <ErrorMessage message={error} onRetry={fetchDashboardData} />

      {loading && !summary ? (
        <Loading message="Loading your financial dashboard..." />
      ) : (
        <>
          {/* Top 3 Stat Cards */}
          <div className="grid-3">
            <SummaryCard
              title="Total Balance"
              amount={summary?.totalBalance || 0}
            />
            <SummaryCard
              title="Total Income"
              amount={summary?.totalIncome || 0}
              type="income"
            />
            <SummaryCard
              title="Total Expense"
              amount={summary?.totalExpense || 0}
              type="expense"
            />
          </div>

          {/* Safe to Spend Card */}
          <SafeToSpendCard data={safeToSpend} />

          {/* Middle Row Charts */}
          <div className="grid-2">
            <IncomeExpenseChart data={trendData} />
            <CategoryChart data={categorySpending} />
          </div>

          {/* Bottom Row Recent Transactions & Budget Overview */}
          <div className="grid-2">
            <RecentTransactions
              transactions={recentTransactions}
              onNavigate={onNavigate}
              onEditTransaction={(t) => {
                setEditingTransaction(t);
                setIsModalOpen(true);
              }}
            />
            <BudgetOverview
              budgets={budgetStatus}
              onNavigate={onNavigate}
            />
          </div>
        </>
      )}

      {/* Transaction Modal */}
      <TransactionModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingTransaction(null);
        }}
        initialData={editingTransaction}
        onSubmit={handleSaveTransaction}
        loading={modalLoading}
      />
    </div>
  );
};
