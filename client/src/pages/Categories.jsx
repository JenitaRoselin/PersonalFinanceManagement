import React, { useState, useEffect } from 'react';
import { categoryService } from '../services/categoryService';
import { CategoryModal } from '../components/categories/CategoryModal';
import { Loading } from '../components/common/Loading';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { PlusIcon, EditIcon, TrashIcon } from '../assets/icons/Icons';

export const Categories = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [categories, setCategories] = useState([]);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [defaultType, setDefaultType] = useState('EXPENSE');
  const [modalLoading, setModalLoading] = useState(false);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await categoryService.getAllCategories();
      setCategories(data || []);
    } catch (err) {
      console.error(err);
      setError('Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveCategory = async (formData) => {
    setModalLoading(true);
    try {
      if (editingCategory?.categoryId) {
        await categoryService.updateCategory(editingCategory.categoryId, formData);
      } else {
        await categoryService.createCategory(formData);
      }
      setIsModalOpen(false);
      setEditingCategory(null);
      await fetchCategories();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save category');
    } finally {
      setModalLoading(false);
    }
  };

  const handleDeleteCategory = async (cat) => {
    try {
      await categoryService.deleteCategory(cat.categoryId);
      await fetchCategories();
    } catch (err) {
      // Safe rejection if transactions depend on it
      setError(err.response?.data?.message || 'Cannot delete category associated with transactions.');
    }
  };

  const expenseCategories = categories.filter((c) => c.type === 'EXPENSE');
  const incomeCategories = categories.filter((c) => c.type === 'INCOME');

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Categories</h1>
          <p className="page-subtitle">Organize and classify your income streams and expense categories.</p>
        </div>

        <div className="page-actions">
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => {
              setEditingCategory(null);
              setDefaultType('EXPENSE');
              setIsModalOpen(true);
            }}
          >
            <PlusIcon size={16} />
            <span>Add Category</span>
          </button>
        </div>
      </div>

      <ErrorMessage message={error} onRetry={fetchCategories} />

      {loading && categories.length === 0 ? (
        <Loading message="Loading categories..." />
      ) : (
        <div className="grid-2">
          {/* EXPENSE Column */}
          <div className="card" style={{ padding: 0 }}>
            <div style={{ padding: '18px 24px', borderBottom: '1px solid var(--color-ivory-200)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h2 style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--color-charcoal-900)' }}>
                EXPENSE
              </h2>
              <span className="badge badge-expense">{expenseCategories.length} Categories</span>
            </div>

            <div style={{ padding: '12px 16px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {expenseCategories.map((c) => (
                <div
                  key={c.categoryId}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '12px 14px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--color-ivory-50)',
                    border: '1px solid var(--color-ivory-100)',
                  }}
                >
                  <span style={{ fontWeight: 600, color: 'var(--color-charcoal-900)' }}>{c.name}</span>
                  <div style={{ display: 'flex', gap: '4px' }}>
                    <button
                      type="button"
                      className="notification-btn"
                      onClick={() => {
                        setEditingCategory(c);
                        setIsModalOpen(true);
                      }}
                      title="Edit category"
                    >
                      <EditIcon size={14} />
                    </button>
                    <button
                      type="button"
                      className="notification-btn"
                      onClick={() => handleDeleteCategory(c)}
                      title="Delete category"
                      style={{ color: 'var(--color-expense)' }}
                    >
                      <TrashIcon size={14} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* INCOME Column */}
          <div className="card" style={{ padding: 0 }}>
            <div style={{ padding: '18px 24px', borderBottom: '1px solid var(--color-ivory-200)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h2 style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--color-charcoal-900)' }}>
                INCOME
              </h2>
              <span className="badge badge-income">{incomeCategories.length} Categories</span>
            </div>

            <div style={{ padding: '12px 16px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {incomeCategories.map((c) => (
                <div
                  key={c.categoryId}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '12px 14px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--color-ivory-50)',
                    border: '1px solid var(--color-ivory-100)',
                  }}
                >
                  <span style={{ fontWeight: 600, color: 'var(--color-charcoal-900)' }}>{c.name}</span>
                  <div style={{ display: 'flex', gap: '4px' }}>
                    <button
                      type="button"
                      className="notification-btn"
                      onClick={() => {
                        setEditingCategory(c);
                        setIsModalOpen(true);
                      }}
                      title="Edit category"
                    >
                      <EditIcon size={14} />
                    </button>
                    <button
                      type="button"
                      className="notification-btn"
                      onClick={() => handleDeleteCategory(c)}
                      title="Delete category"
                      style={{ color: 'var(--color-expense)' }}
                    >
                      <TrashIcon size={14} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Category Modal */}
      <CategoryModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingCategory(null);
        }}
        initialData={editingCategory}
        defaultType={defaultType}
        onSubmit={handleSaveCategory}
        loading={modalLoading}
      />
    </div>
  );
};
