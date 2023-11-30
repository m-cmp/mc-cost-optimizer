export const AI_MODEL_ANALYSIS_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ],
  "sensitivity": "M"
}


export const AI_POSSIBLE_CAUSE_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ],
  "sensitivity": "M"
}

export const TOTAL_ALERTS_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "",
  "userId": "",
  "userName": "",
  "vendors": [
    "AWS"
  ]
}

export const AI_ANOMALY_CHANGE_TOP5_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ],
  "startDt": "string",
  "endDt": "string"
}

export const TOTAL_ALERTS = {
  "totalCnt": "",
  "details": {
    "criticalUser": "",
    "criticalAi": "",
    "criticalTotal": "",
    "majorUser": "",
    "majorAi": "",
    "majorTotal": "",
    "minorUser": "",
    "minorAi": "",
    "minorTotal": ""
  },
}

export const ABNORMAL_DETECTED_LIST_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userId": "string",
  "startDate":"string",
  "endDate":"string",
}
