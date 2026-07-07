import axios from "axios";
import { ERROR_MESSAGES } from "../constants/errorMessages";
import { logger } from "../utils/logger";

// Dynamically generate API URL and Mock mode (based on domain)
function getApiConfig() {
  const hostname = window.location.hostname;
  const protocol = window.location.protocol; // "http:" | "https:" — follow the page's scheme
  console.log("Domain check:", hostname, protocol);

  // localhost defaults to mock (local dev); IP/domain hit the real API
  const USE_MOCK = hostname.includes("localhost");

  // Always match the page's scheme so there is no mixed-content,
  // whether served over http (IP) or https (self-signed IP / real domain).
  const API_BE_URL = `${protocol}//${hostname}:9090`;
  const API_ALARM_URL = `${protocol}//${hostname}:9000`;

  console.log("API_BE_URL:", API_BE_URL);
  console.log("API_ALARM_URL:", API_ALARM_URL);
  console.log("USE_MOCK:", USE_MOCK);

  return { API_BE_URL, API_ALARM_URL, USE_MOCK };
}

const { API_BE_URL, USE_MOCK } = getApiConfig();

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
    },
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
  5000,
);
export const budgetClient = createClient(
  `${API_BE_URL}${BASE_PATH}/budget`,
  5000,
);

// LLM recommender (port 9090, BE base path) — long timeout: each call hits an LLM
export const llmClient = createClient(
  `${API_BE_URL}${BASE_PATH}/llm_recommender`,
  30000,
);

// Alarm Service API — BE(:9090) 리버스 프록시를 경유한다.
// 브라우저가 알람 서비스(:9000) 의 자체 서명 인증서를 직접 만지면 iframe 안에서
// ERR_CERT_AUTHORITY_INVALID 로 차단되므로, 이미 신뢰된 BE origin 으로 우회한다.
// (BE 의 AlertProxyController 가 /api/costopti/be/alert/** -> 알람서비스로 포워딩)
export const alertClient = createClient(`${API_BE_URL}${BASE_PATH}/alert`, 20000);

// API Key Management
export const apikeyClient = createClient(
  `${API_BE_URL}${BASE_PATH}/llm_recommender/apikey`,
  5000,
);

// Export Mock mode (used in API files)
export { USE_MOCK };
