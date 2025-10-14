export const ERROR_MESSAGES = {
  400: { code: "BAD_REQUEST", userMessage: "잘못된 요청입니다." },
  401: { code: "UNAUTHORIZED", userMessage: "로그인이 필요합니다." },
  403: { code: "FORBIDDEN", userMessage: "접근 권한이 없습니다." },
  404: { code: "NOT_FOUND", userMessage: "데이터를 찾을 수 없습니다." },
  408: { code: "REQUEST_TIMEOUT", userMessage: "요청 시간이 초과되었습니다." },
  429: {
    code: "TOO_MANY_REQUESTS",
    userMessage: "요청이 너무 많습니다. 잠시 후 다시 시도하세요.",
  },
  500: {
    code: "INTERNAL_SERVER_ERROR",
    userMessage: "서버 오류가 발생했습니다.",
  },
  502: { code: "BAD_GATEWAY", userMessage: "게이트웨이 오류가 발생했습니다." },
  503: {
    code: "SERVICE_UNAVAILABLE",
    userMessage: "서비스를 사용할 수 없습니다.",
  },
  504: {
    code: "GATEWAY_TIMEOUT",
    userMessage: "게이트웨이 시간 초과 오류입니다.",
  },

  DEFAULT_3XX: {
    code: "REDIRECTION_ERROR",
    userMessage: "요청이 다른 위치로 리다이렉트되었습니다.",
  },
  DEFAULT_4XX: {
    code: "CLIENT_ERROR",
    userMessage: "요청 처리 중 오류가 발생했습니다.",
  },
  DEFAULT_5XX: {
    code: "SERVER_ERROR",
    userMessage: "서버 처리 중 오류가 발생했습니다.",
  },
  UNKNOWN: {
    code: "UNKNOWN_ERROR",
    userMessage: "알 수 없는 오류가 발생했습니다.",
  },
};
