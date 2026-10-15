export const formatCurrency = (amount, options = {}) => {
  if (amount === undefined || amount === null) return '₹0';
  const numericAmount = typeof amount === 'number' ? amount : parseFloat(amount);
  if (isNaN(numericAmount)) return '₹0';

  const formatted = Math.abs(numericAmount).toLocaleString('en-IN', {
    maximumFractionDigits: options.decimals !== undefined ? options.decimals : 0,
    minimumFractionDigits: options.decimals !== undefined ? options.decimals : 0,
  });

  if (options.withSign) {
    if (numericAmount > 0) return `+₹${formatted}`;
    if (numericAmount < 0) return `-₹${formatted}`;
    return `₹${formatted}`;
  }

  return `${numericAmount < 0 ? '-' : ''}₹${formatted}`;
};
