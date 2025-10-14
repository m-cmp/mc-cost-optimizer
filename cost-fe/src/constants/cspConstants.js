/**
 * CSP(Cloud Service Provider) 관련 통합 상수 정의
 *
 * 모든 CSP 관련 색상, 라벨, 설정을 중앙화하여 관리합니다.
 * CSP 추가/수정 시 이 파일만 변경하면 전체 애플리케이션에 반영됩니다.
 */

export const CSP_NAMES = {
  AWS: 'AWS',
  AZURE: 'Azure',
  GCP: 'GCP',
  NCP: 'NCP',
  OTHERS: 'OTHERS'
};

/**
 * CSP별 통합 설정
 * @property {string} name - CSP 표시 이름
 * @property {string} color - Hex 색상 코드 (차트 등에 사용)
 * @property {string} colorClass - Bootstrap/Tabler 색상 클래스 (배지 등에 사용)
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
 * CSP별 Hex 색상 코드 맵 (차트 라이브러리용)
 */
export const cspColorMap = Object.entries(CSP_CONFIG).reduce((acc, [key, config]) => {
  acc[key] = config.color;
  return acc;
}, {});

/**
 * CSP별 Bootstrap/Tabler 색상 클래스 반환
 * @param {string} csp - CSP 이름
 * @returns {string} Bootstrap 색상 클래스
 */
export const getCSPColorClass = (csp) => {
  return CSP_CONFIG[csp]?.colorClass || CSP_CONFIG.OTHERS.colorClass;
};

/**
 * CSP별 Hex 색상 코드 반환
 * @param {string} csp - CSP 이름
 * @returns {string} Hex 색상 코드
 */
export const getCSPColor = (csp) => {
  return CSP_CONFIG[csp]?.color || CSP_CONFIG.OTHERS.color;
};

/**
 * 모든 CSP 목록 반환
 * @returns {Array<string>} CSP 이름 배열
 */
export const getAllCSPs = () => {
  return Object.keys(CSP_CONFIG);
};
