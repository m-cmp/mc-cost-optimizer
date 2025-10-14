/**
 * @file historyUtils.js
 * @description Alarm History 관련 유틸리티 함수들
 */

import { logger } from "./logger";

/**
 * ISO 날짜 문자열을 읽기 쉬운 형식으로 변환
 * @param {string} isoDateString - "2025-09-23T14:21:57" 형식의 날짜
 * @returns {string} "2025-09-23 14:21" 형식의 날짜
 */
export const formatOccureTime = (isoDateString) => {
  if (!isoDateString) return "";

  try {
    const date = new Date(isoDateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hour = String(date.getHours()).padStart(2, "0");
    const minute = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day} ${hour}:${minute}`;
  } catch (error) {
    logger.error("Date formatting error:", error);
    return isoDateString; // 에러 시 원본 반환
  }
};

/**
 * API 응답 데이터를 테이블 형식으로 변환
 * @param {Array} apiData - API에서 받은 alarmHistory 배열
 * @returns {Array} 테이블에 맞는 형식으로 변환된 데이터
 */
export const transformAlarmData = (apiData) => {
  if (!Array.isArray(apiData)) return [];

  return apiData.map(item => ({
    date: formatOccureTime(item.occure_time),
    csp: item.csp_type,
    resourceId: item.resource_id,
    resourceType: item.resource_type,
    alarmType: item.event_type,
    alarmMessage: item.note,
    recommendType: item.plan
  }));
};