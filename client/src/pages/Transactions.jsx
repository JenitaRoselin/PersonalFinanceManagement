import React, { useState, useEffect } from 'react';
import { transactionService } from '../services/transactionService';
import { TransactionFilters } from '../components/transactions/TransactionFilters';
import { TransactionTable } from '../components/transactions/TransactionTable';
import { TransactionModal } from '../components/transactions/TransactionModal';
import { Loading } from '../components/common/Loading';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { PlusIcon } from '../assets/icons/Icons';

export const Transactions = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [filters, setFilters] = useState({
    query: '',
    categoryId: null,
    type: null,
    sortBy: 'transactionDate',
    direction: 'DESC',
  });

  // Modal State for in-place Add / Edit
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);
  const [modalLoading, setModalLoading] = useState(false);

  useEffect(() => {
    fetchTransactions();
  }, [filters, page]);

  const fetchTransactions = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await transactionService.getTransactions({
        page,
        size: 15,
        sortBy: filters.sortBy,
        direction: filters.direction,
        query: filters.query || undefined,
        type: filters.type || undefined,
        categoryId: filters.categoryId || undefined,
      });
      setTransactions(data.content || []);
      setTotalPages(data.totalPages || 1);
    } catch (err) {
      console.error(err);
      setError('Failed to load transactions');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (formData) => {
    setModalLoading(true);
    try {
      if (editingTransaction?.transactionId) {
        await transactionService.updateTransaction(editingTransaction.transactionId, formData);
      } else {
        await transactionService.createTransaction(formData);
      }
      setIsModalOpen(false);
      setEditingTransaction(null);
      await fetchTransactions();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save transaction');
    } finally {
      setModalLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await transactionService.deleteTransaction(id);
      await fetchTransactions();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete transaction');
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Transactions</h1>
          <p className="page-subtitle">View, search, and manage all your income and expenses.</p>
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

      <TransactionFilters
        filters={filters}
        onFilterChange={(newFilters) => {
          setFilters(newFilters);
          setPage(0);
        }}
      />

      <ErrorMessage message={error} onRetry={fetchTransactions} />

      {loading && transactions.length === 0 ? (
        <Loading message="Loading transactions..." />
      ) : (
        <TransactionTable
          transactions={transactions}
          page={page}
          totalPages={totalPages}
          onPageChange={setPage}
          onEdit={(t) => {
            setEditingTransaction(t);
            setIsModalOpen(true);
          }}
          onDelete={handleDelete}
        />
      )}

      {/* In-place Add/Edit Modal */}
      <TransactionModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingTransaction(null);
        }}
        initialData={editingTransaction}
        onSubmit={handleSave}
        loading={modalLoading}
      />
    </div>
  );
};
