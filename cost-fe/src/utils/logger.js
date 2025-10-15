/**
 * Development-only logger utility
 *
 * Automatically disabled in production environment.
 * Using this logger instead of console.log makes environment-specific log management easier.
 */

const isDevelopment = import.meta.env.MODE === 'development';

/**
 * Color definitions for each log level
 */
const LOG_STYLES = {
  info: 'color: #3B82F6; font-weight: bold',
  warn: 'color: #F59E0B; font-weight: bold',
  error: 'color: #EF4444; font-weight: bold',
  success: 'color: #10B981; font-weight: bold',
  debug: 'color: #6B7280; font-weight: bold'
};

class Logger {
  /**
   * General information log
   * @param {string} message - Log message
   * @param  {...any} args - Additional arguments
   */
  info(message, ...args) {
    if (!isDevelopment) return;
    console.log(`%c[INFO]`, LOG_STYLES.info, message, ...args);
  }

  /**
   * Warning log
   * @param {string} message - Log message
   * @param  {...any} args - Additional arguments
   */
  warn(message, ...args) {
    if (!isDevelopment) return;
    console.warn(`%c[WARN]`, LOG_STYLES.warn, message, ...args);
  }

  /**
   * Error log (also displayed in production)
   * @param {string} message - Log message
   * @param  {...any} args - Additional arguments
   */
  error(message, ...args) {
    console.error(`%c[ERROR]`, LOG_STYLES.error, message, ...args);
  }

  /**
   * Success log
   * @param {string} message - Log message
   * @param  {...any} args - Additional arguments
   */
  success(message, ...args) {
    if (!isDevelopment) return;
    console.log(`%c[SUCCESS]`, LOG_STYLES.success, message, ...args);
  }

  /**
   * Debug log (development environment only)
   * @param {string} message - Log message
   * @param  {...any} args - Additional arguments
   */
  debug(message, ...args) {
    if (!isDevelopment) return;
    console.log(`%c[DEBUG]`, LOG_STYLES.debug, message, ...args);
  }

  /**
   * API request/response log
   * @param {string} method - HTTP method
   * @param {string} url - API URL
   * @param {object} data - Request/response data
   */
  api(method, url, data = null) {
    if (!isDevelopment) return;
    console.group(`%c[API ${method}]`, LOG_STYLES.info, url);
    if (data) console.log('Data:', data);
    console.groupEnd();
  }

  /**
   * Table format log
   * @param {Array|Object} data - Data to display in table format
   */
  table(data) {
    if (!isDevelopment) return;
    console.table(data);
  }

  /**
   * Start group log
   * @param {string} label - Group label
   */
  group(label) {
    if (!isDevelopment) return;
    console.group(label);
  }

  /**
   * End group log
   */
  groupEnd() {
    if (!isDevelopment) return;
    console.groupEnd();
  }
}

export const logger = new Logger();

/**
 * Usage example:
 *
 * import { logger } from '@/utils/logger';
 *
 * logger.info('User logged in', { userId: 123 });
 * logger.warn('API response slow');
 * logger.error('Failed to fetch data', error);
 * logger.success('Budget saved successfully');
 * logger.debug('Component mounted', props);
 * logger.api('GET', '/api/billing', responseData);
 */
