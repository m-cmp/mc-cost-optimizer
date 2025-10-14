/**
 * 날짜 관련 상수 정의
 */

/**
 * 월 이름 (영문 약자)
 * @constant {Array<string>}
 * @description 1월부터 12월까지의 영문 약자 배열
 */
export const MONTH_NAMES = [
  "Jan",
  "Feb",
  "Mar",
  "Apr",
  "May",
  "Jun",
  "Jul",
  "Aug",
  "Sep",
  "Oct",
  "Nov",
  "Dec",
];

/**
 * 월 이름 (영문 전체)
 * @constant {Array<string>}
 * @description 1월부터 12월까지의 영문 전체 이름 배열
 */
export const MONTH_NAMES_FULL = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

/**
 * 월 인덱스로 월 이름 반환 (영문 약자)
 * @param {number} monthIndex - 월 인덱스 (0-11)
 * @returns {string} 월 이름 (예: "Jan")
 */
export const getMonthName = (monthIndex) => {
  return MONTH_NAMES[monthIndex] || "";
};

/**
 * 월 인덱스로 월 이름 반환 (영문 전체)
 * @param {number} monthIndex - 월 인덱스 (0-11)
 * @returns {string} 월 이름 (예: "January")
 */
export const getMonthNameFull = (monthIndex) => {
  return MONTH_NAMES_FULL[monthIndex] || "";
};
