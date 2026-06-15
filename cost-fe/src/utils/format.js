/**
 * Utility functions for formatting data display
 */

/**
 * Format currency values
 * @param {number} amount - The amount to format
 * @param {string} currency - Currency symbol (default: '$')
 * @returns {string} Formatted currency string
 */
export const formatCurrency = (amount, currency = '$') => {
  if (amount == null || isNaN(amount)) return `${currency}0`;

  return `${currency}${Number(amount).toLocaleString('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  })}`;
};

/**
 * Format percentage values
 * @param {number} value - The value to format
 * @param {number} decimals - Number of decimal places (default: 1)
 * @returns {string} Formatted percentage string
 */
export const formatPercentage = (value, decimals = 1) => {
  if (value == null || isNaN(value)) return '0%';

  return `${Number(value).toFixed(decimals)}%`;
};

/**
 * Format large numbers with K, M, B suffixes
 * @param {number} num - The number to format
 * @returns {string} Formatted number string
 */
export const formatLargeNumber = (num) => {
  if (num == null || isNaN(num)) return '0';

  if (num >= 1000000000) {
    return (num / 1000000000).toFixed(1) + 'B';
  }
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
};

/**
 * Format numbers in compact notation (K, M, B) using Intl.NumberFormat
 * More standardized and supports internationalization
 * @param {number} num - The number to format
 * @returns {string} Formatted compact number string (e.g., "1.5K", "2.3M")
 */
export const formatCompactNumber = (num) => {
  if (num == null || isNaN(num)) return '0';

  return new Intl.NumberFormat('en', {
    notation: 'compact',
    compactDisplay: 'short',
    maximumFractionDigits: 1,
  }).format(num);
};

/**
 * Format numbers with thousand separators
 * @param {number} num - The number to format
 * @returns {string} Formatted number with commas (e.g., "1,234,567")
 */
export const formatFullNumber = (num) => {
  if (num == null || isNaN(num)) return '0';

  return new Intl.NumberFormat('en').format(num);
};