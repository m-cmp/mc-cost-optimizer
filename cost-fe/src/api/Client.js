import axios from "axios";
import { ERROR_MESSAGES } from "../constants/errorMessages";
import { logger } from "../utils/logger";

// 동적으로 API URL 및 Mock 모드 생성 (도메인 기반)
function getApiConfig() {
  const hostname = window.location.hostname;
  console.log("도메인 확인: " + hostname);
  const isNumericAndDotsOnly = /^[0-9.]+$/.test(hostname);

  let API_BE_URL = "";
  let API_ALARM_URL = "";
  let USE_MOCK = false;

  if (hostname.includes("localhost")) {
    API_BE_URL = `http://${hostname}:9090`;
    API_ALARM_URL = `http://${hostname}:9000`;
    USE_MOCK = true; // localhost는 mock 사용
  } else if (isNumericAndDotsOnly) {
    API_BE_URL = `http://${hostname}:9090`;
    API_ALARM_URL = `http://${hostname}:9000`;
    USE_MOCK = false; // IP는 실제 API 사용
  } else {
    API_BE_URL = `https://${hostname}`;
    API_ALARM_URL = `https://${hostname}`;
    USE_MOCK = false; // 도메인은 실제 API 사용
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

      // Production에서도 표시 (logger.error는 항상 출력)
      logger.error(`[API Error] ${status} ${mapping.code}`, error);

      return Promise.reject(formattedError);
    }
  );

  return client;
}

// costBE API (9090 포트)
export const dashboardClient = createClient(API_BE_URL, 5000);

// 도메인별 클라이언트 생성 (base path: /api/costopti/be)
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

// Alarm Service API (9000 포트)
export const alertClient = createClient(API_ALARM_URL, 20000);

// Mock 모드 export (API 파일들에서 사용)
export { USE_MOCK };
