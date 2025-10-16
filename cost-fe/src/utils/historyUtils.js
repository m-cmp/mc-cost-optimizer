/**
 * @file historyUtils.js
 * @description Utility functions related to Alarm History
 */

import { logger } from "./logger";

/**
 * Converts ISO date string to readable format
 * @param {string} isoDateString - Date in "2025-09-23T14:21:57" format
 * @returns {string} Date in "2025-09-23 14:21" format
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
    return isoDateString; // Return original on error
  }
};

/**
 * Converts API response data to table format
 * @param {Array} apiData - alarmHistory array received from API
 * @returns {Array} Data converted to table-compatible format
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