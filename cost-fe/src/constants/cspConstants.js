/**
 * CSP(Cloud Service Provider) related unified constant definitions
 *
 * Centrally manages all CSP-related colors, labels, and configurations.
 * Changes to this file only will be reflected across the entire application when adding/modifying CSPs.
 */

export const CSP_NAMES = {
  AWS: 'AWS',
  AZURE: 'Azure',
  GCP: 'GCP',
  NCP: 'NCP',
  OTHERS: 'OTHERS'
};

/**
 * Unified configuration per CSP
 * @property {string} name - CSP display name
 * @property {string} color - Hex color code (used in charts, etc.)
 * @property {string} colorClass - Bootstrap/Tabler color class (used in badges, etc.)
 */
export const CSP_CONFIG = {
  [CSP_NAMES.AWS]: {
    name: 'AWS',
    color: '#F59E0B', // Yellow
    colorClass: 'bg-warning-subtle text-warning-emphasis'
  },
  [CSP_NAMES.AZURE]: {
    name: 'Azure',
    color: '#3B82F6', // Blue
    colorClass: 'bg-info-subtle text-info-emphasis'
  },
  [CSP_NAMES.GCP]: {
    name: 'GCP',
    color: '#10B981', // Green
    colorClass: 'bg-success-subtle text-success-emphasis'
  },
  [CSP_NAMES.NCP]: {
    name: 'NCP',
    color: '#8B5CF6', // Purple
    colorClass: 'bg-secondary-subtle text-secondary-emphasis'
  },
  [CSP_NAMES.OTHERS]: {
    name: 'Others',
    color: '#6B7280', // Gray
    colorClass: 'bg-secondary-subtle text-secondary-emphasis'
  }
};

/**
 * Hex color code map per CSP (for chart libraries)
 */
export const cspColorMap = Object.entries(CSP_CONFIG).reduce((acc, [key, config]) => {
  acc[key] = config.color;
  return acc;
}, {});

/**
 * Returns Bootstrap/Tabler color class per CSP
 * @param {string} csp - CSP name
 * @returns {string} Bootstrap color class
 */
export const getCSPColorClass = (csp) => {
  return CSP_CONFIG[csp]?.colorClass || CSP_CONFIG.OTHERS.colorClass;
};

/**
 * Returns Hex color code per CSP
 * @param {string} csp - CSP name
 * @returns {string} Hex color code
 */
export const getCSPColor = (csp) => {
  return CSP_CONFIG[csp]?.color || CSP_CONFIG.OTHERS.color;
};

/**
 * Returns all CSP list
 * @returns {Array<string>} CSP name array
 */
export const getAllCSPs = () => {
  return Object.keys(CSP_CONFIG);
};
