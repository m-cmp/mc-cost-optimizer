export const ERROR_MESSAGES = {
  400: { code: "BAD_REQUEST", userMessage: "Invalid request." },
  401: { code: "UNAUTHORIZED", userMessage: "Authentication required." },
  403: { code: "FORBIDDEN", userMessage: "Access denied." },
  404: { code: "NOT_FOUND", userMessage: "Data not found." },
  408: { code: "REQUEST_TIMEOUT", userMessage: "Request timeout." },
  429: {
    code: "TOO_MANY_REQUESTS",
    userMessage: "Too many requests. Please try again later.",
  },
  500: {
    code: "INTERNAL_SERVER_ERROR",
    userMessage: "Internal server error occurred.",
  },
  502: { code: "BAD_GATEWAY", userMessage: "Gateway error occurred." },
  503: {
    code: "SERVICE_UNAVAILABLE",
    userMessage: "Service unavailable.",
  },
  504: {
    code: "GATEWAY_TIMEOUT",
    userMessage: "Gateway timeout error.",
  },

  DEFAULT_3XX: {
    code: "REDIRECTION_ERROR",
    userMessage: "Request was redirected to a different location.",
  },
  DEFAULT_4XX: {
    code: "CLIENT_ERROR",
    userMessage: "An error occurred while processing the request.",
  },
  DEFAULT_5XX: {
    code: "SERVER_ERROR",
    userMessage: "An error occurred while processing on the server.",
  },
  UNKNOWN: {
    code: "UNKNOWN_ERROR",
    userMessage: "An unknown error occurred.",
  },
};
