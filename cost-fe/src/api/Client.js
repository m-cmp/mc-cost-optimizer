import axios from "axios";
import { ERROR_MESSAGES } from "../constants/errorMessages";
import { logger } from "../utils/logger";

// Dynamically generate API URL and Mock mode (based on domain)
function getApiConfig() {
  const hostname = window.location.hostname;
  // const hostname = "35.239.209.190";
  console.log("Domain check: " + hostname);
  const isNumericAndDotsOnly = /^[0-9.]+$/.test(hostname);

  let API_BE_URL = "";
  let API_ALARM_URL = "";
  let USE_MOCK = false;

  if (hostname.includes("localhost")) {
    API_BE_URL = `http://${hostname}:9090`;
    API_ALARM_URL = `http://${hostname}:9000`;
    USE_MOCK = true; // localhost uses mock, change to false for API testing
  } else if (isNumericAndDotsOnly) {
    API_BE_URL = `http://${hostname}:9090`;
    API_ALARM_URL = `http://${hostname}:9000`;
    USE_MOCK = false; // IP uses real API
  } else {
    API_BE_URL = `https://${hostname}`;
    API_ALARM_URL = `https://${hostname}`;
    USE_MOCK = false; // Domain uses real API
  }

  console.log("API_BE_URL:", API_BE_URL);
  console.log("API_ALARM_URL:", API_ALARM_URL);
  console.log("USE_MOCK:", USE_MOCK);

  return { API_BE_URL, API_ALARM_URL, USE_MOCK };
}

const { API_BE_URL, API_ALARM_URL, USE_MOCK } = getApiConfig();

function createClient(baseURL, timeout = 5000) {
  const client = axios.create({ baseURL, timeout });

  client.interceptors.response.use(
    (res) => res,
    (error) => {
      const status = error.response?.status;
      const mapping =
        ERROR_MESSAGES[status] ||
        (status >= 300 && status < 400
          ? ERROR_MESSAGES.DEFAULT_3XX
          : status >= 400 && status < 500
          ? ERROR_MESSAGES.DEFAULT_4XX
          : status >= 500
          ? ERROR_MESSAGES.DEFAULT_5XX
          : ERROR_MESSAGES.UNKNOWN);

      const formattedError = {
        status,
        code: mapping.code,
        userMessage: mapping.userMessage,
        raw: error,
      };

      // Also displayed in production (logger.error always outputs)
      logger.error(`[API Error] ${status} ${mapping.code}`, error);

      return Promise.reject(formattedError);
    }
  );

  return client;
}

// costBE API (port 9090)
export const dashboardClient = createClient(API_BE_URL, 5000);

// Create client by domain (base path: /api/costopti/be)
const BASE_PATH = "/api/costopti/be";
export const billingClient = createClient(`${API_BE_URL}${BASE_PATH}`, 5000);
export const invoiceClient = createClient(
  `${API_BE_URL}${BASE_PATH}/invoice`,
  5000
);
export const budgetClient = createClient(
  `${API_BE_URL}${BASE_PATH}/budget`,
  5000
);

// Alarm Service API (port 9000)
export const alertClient = createClient(API_ALARM_URL, 20000);

// Export Mock mode (used in API files)
export { USE_MOCK };
