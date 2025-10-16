/**
 * Currency and exchange rate constants
 */

/**
 * Exchange rate: KRW to USD
 * 1 USD = 1400 KRW
 * TODO: Plan to fetch real-time exchange rate from API
 */
export const EXCHANGE_RATE_KRW_TO_USD = 1400;

/**
 * Convert KRW to USD
 * @param {number} krw - Amount in KRW
 * @returns {number} Amount in USD
 */
export const convertKrwToUsd = (krw) => {
  return krw / EXCHANGE_RATE_KRW_TO_USD;
};

/**
 * Convert USD to KRW
 * @param {number} usd - Amount in USD
 * @returns {number} Amount in KRW
 */
export const convertUsdToKrw = (usd) => {
  return usd * EXCHANGE_RATE_KRW_TO_USD;
};
