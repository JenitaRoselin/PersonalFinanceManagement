import React from 'react';
import { Modal } from '../common/Modal';
import { TransactionForm } from './TransactionForm';

export const TransactionModal = ({ isOpen, onClose, initialData, onSubmit, loading }) => {
  const isEditing = Boolean(initialData?.transactionId);

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? 'Edit Transaction' : 'Add Transaction'}
    >
      <TransactionForm
        initialData={initialData}
        onSubmit={onSubmit}
        onCancel={onClose}
        loading={loading}
      />
    </Modal>
  );
};
