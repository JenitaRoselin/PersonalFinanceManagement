import React, { useState, useEffect } from 'react';
import { savingsGoalService } from '../services/savingsGoalService';
import { GoalCard } from '../components/goals/GoalCard';
import { GoalModal } from '../components/goals/GoalModal';
import { Loading } from '../components/common/Loading';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { PlusIcon } from '../assets/icons/Icons';

export const SavingsGoals = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [goals, setGoals] = useState([]);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGoal, setEditingGoal] = useState(null);
  const [modalLoading, setModalLoading] = useState(false);

  useEffect(() => {
    fetchGoals();
  }, []);

  const fetchGoals = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await savingsGoalService.getAllGoals();
      setGoals(data || []);
    } catch (err) {
      console.error(err);
      setError('Failed to load savings goals');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveGoal = async (formData) => {
    setModalLoading(true);
    try {
      if (editingGoal?.goalId) {
        await savingsGoalService.updateGoal(editingGoal.goalId, formData);
      } else {
        await savingsGoalService.createGoal(formData);
      }
      setIsModalOpen(false);
      setEditingGoal(null);
      await fetchGoals();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save savings goal');
    } finally {
      setModalLoading(false);
    }
  };

  const handleDeleteGoal = async (id) => {
    try {
      await savingsGoalService.deleteGoal(id);
      await fetchGoals();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete goal');
    }
  };

  const handleAddProgress = async (id, amount) => {
    try {
      await savingsGoalService.updateProgress(id, amount, 'ADD');
      await fetchGoals();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update progress');
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Savings Goals</h1>
          <p className="page-subtitle">Track your planned savings, purchases, and emergency milestones.</p>
        </div>

        <div className="page-actions">
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => {
              setEditingGoal(null);
              setIsModalOpen(true);
            }}
          >
            <PlusIcon size={16} />
            <span>Create Goal</span>
          </button>
        </div>
      </div>

      <ErrorMessage message={error} onRetry={fetchGoals} />

      {loading && goals.length === 0 ? (
        <Loading message="Loading savings goals..." />
      ) : (
        <>
          {goals.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '48px', color: 'var(--color-charcoal-400)' }}>
              No savings goals created yet. Click "+ Create Goal" to start tracking your targets.
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '20px' }}>
              {goals.map((goal) => (
                <GoalCard
                  key={goal.goalId}
                  goal={goal}
                  onEdit={(g) => {
                    setEditingGoal(g);
                    setIsModalOpen(true);
                  }}
                  onDelete={handleDeleteGoal}
                  onAddProgress={handleAddProgress}
                />
              ))}
            </div>
          )}
        </>
      )}

      {/* Goal Modal */}
      <GoalModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingGoal(null);
        }}
        initialData={editingGoal}
        onSubmit={handleSaveGoal}
        loading={modalLoading}
      />
    </div>
  );
};
