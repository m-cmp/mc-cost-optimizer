/**
 * Date-related constant definitions
 */

/**
 * Month names (abbreviated)
 * @constant {Array<string>}
 * @description Array of month abbreviations from January to December
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
 * Month names (full)
 * @constant {Array<string>}
 * @description Array of full month names from January to December
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
 * Returns month name by month index (abbreviated)
 * @param {number} monthIndex - Month index (0-11)
 * @returns {string} Month name (e.g., "Jan")
 */
export const getMonthName = (monthIndex) => {
  return MONTH_NAMES[monthIndex] || "";
};

/**
 * Returns month name by month index (full)
 * @param {number} monthIndex - Month index (0-11)
 * @returns {string} Month name (e.g., "January")
 */
export const getMonthNameFull = (monthIndex) => {
  return MONTH_NAMES_FULL[monthIndex] || "";
};
