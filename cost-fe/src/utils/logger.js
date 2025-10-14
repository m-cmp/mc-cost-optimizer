/**
 * 개발 환경 전용 로거 유틸리티
 *
 * Production 환경에서는 자동으로 비활성화됩니다.
 * console.log 대신 이 logger를 사용하면 환경별 로그 관리가 용이합니다.
 */

const isDevelopment = import.meta.env.MODE === 'development';

/**
 * 로그 레벨별 색상 정의
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
   * 일반 정보 로그
   * @param {string} message - 로그 메시지
   * @param  {...any} args - 추가 인자
   */
  info(message, ...args) {
    if (!isDevelopment) return;
    console.log(`%c[INFO]`, LOG_STYLES.info, message, ...args);
  }

  /**
   * 경고 로그
   * @param {string} message - 로그 메시지
   * @param  {...any} args - 추가 인자
   */
  warn(message, ...args) {
    if (!isDevelopment) return;
    console.warn(`%c[WARN]`, LOG_STYLES.warn, message, ...args);
  }

  /**
   * 에러 로그 (Production에서도 표시)
   * @param {string} message - 로그 메시지
   * @param  {...any} args - 추가 인자
   */
  error(message, ...args) {
    console.error(`%c[ERROR]`, LOG_STYLES.error, message, ...args);
  }

  /**
   * 성공 로그
   * @param {string} message - 로그 메시지
   * @param  {...any} args - 추가 인자
   */
  success(message, ...args) {
    if (!isDevelopment) return;
    console.log(`%c[SUCCESS]`, LOG_STYLES.success, message, ...args);
  }

  /**
   * 디버그 로그 (개발 환경 전용)
   * @param {string} message - 로그 메시지
   * @param  {...any} args - 추가 인자
   */
  debug(message, ...args) {
    if (!isDevelopment) return;
    console.log(`%c[DEBUG]`, LOG_STYLES.debug, message, ...args);
  }

  /**
   * API 요청/응답 로그
   * @param {string} method - HTTP 메서드
   * @param {string} url - API URL
   * @param {object} data - 요청/응답 데이터
   */
  api(method, url, data = null) {
    if (!isDevelopment) return;
    console.group(`%c[API ${method}]`, LOG_STYLES.info, url);
    if (data) console.log('Data:', data);
    console.groupEnd();
  }

  /**
   * 테이블 형식 로그
   * @param {Array|Object} data - 테이블로 표시할 데이터
   */
  table(data) {
    if (!isDevelopment) return;
    console.table(data);
  }

  /**
   * 그룹 로그 시작
   * @param {string} label - 그룹 라벨
   */
  group(label) {
    if (!isDevelopment) return;
    console.group(label);
  }

  /**
   * 그룹 로그 종료
   */
  groupEnd() {
    if (!isDevelopment) return;
    console.groupEnd();
  }
}

export const logger = new Logger();

/**
 * 사용 예시:
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
