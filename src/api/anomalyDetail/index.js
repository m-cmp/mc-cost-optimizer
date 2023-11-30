import axios from "axios";
import ENDPOINT from '@/api/endpoints';
import {
  AI_MODEL_ANALYSIS_REQUEST_MODEL,
  TOTAL_ALERTS_REQUEST_MODEL,
  AI_POSSIBLE_CAUSE_REQUEST_MODEL,
  AI_ANOMALY_CHANGE_TOP5_REQUEST_MODEL,
  ABNORMAL_DETECTED_LIST_REQUEST_MODEL
} from '../../constants/anomalyDetailConstants';
import {isFailResponse, addLoginUserInfoToPayload} from '@/util/apiUtils';
import store from '@/store';
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';

export function fetchAiModelAnalysis(payload) {
  let aiModelAnalysisPayload = {
    ...AI_MODEL_ANALYSIS_REQUEST_MODEL,
    ...payload
  };
  aiModelAnalysisPayload = addLoginUserInfoToPayload(aiModelAnalysisPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.ANOMALY_DETAIL.AI_MODEL_ANALYSIS, aiModelAnalysisPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchTotalCostAnomalyDetectionAlerts(payload) {
  let aiModelAnalysisPayload = {
    ...TOTAL_ALERTS_REQUEST_MODEL,
    ...payload
  };
  aiModelAnalysisPayload = addLoginUserInfoToPayload(aiModelAnalysisPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.ANOMALY_LIST.TOTAL_ALERTS, aiModelAnalysisPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchAiPossibleCause(payload) {
  let aiPossibleCausePayload = {
    ...AI_POSSIBLE_CAUSE_REQUEST_MODEL,
    ...payload
  };
  aiPossibleCausePayload = addLoginUserInfoToPayload(aiPossibleCausePayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.ANOMALY_DETAIL.AI_POSSIBLE_CAUSE, aiPossibleCausePayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchAiAnomalyChangeTop5(payload) {
  let aiAnomalyChangeTop5Payload = {
    ...AI_ANOMALY_CHANGE_TOP5_REQUEST_MODEL,
    ...payload
  };
  aiAnomalyChangeTop5Payload = addLoginUserInfoToPayload(aiAnomalyChangeTop5Payload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.ANOMALY_LIST.AI_ANOMALY_CHANGE_TOP5, aiAnomalyChangeTop5Payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchAbnormalDetectedList(payload) {
  /*
  * backup
  * */
  // let abnormalDetectedListPayload = {
  //   ...ABNORMAL_DETECTED_LIST_REQUEST_MODEL,
  //   ...payload
  // };
  // abnormalDetectedListPayload = addLoginUserInfoToPayload(abnormalDetectedListPayload, store);
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.ANOMALY_LIST.DETECTED_LIST, abnormalDetectedListPayload)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(response.data.Data);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.abnormal.list);
}

export function fetchUserAnomalyAlertTop5(payload) {
  let fetchUserAnomalyAlertTop5Payload = {
    ...AI_ANOMALY_CHANGE_TOP5_REQUEST_MODEL,
    ...payload
  };
  fetchUserAnomalyAlertTop5Payload = addLoginUserInfoToPayload(fetchUserAnomalyAlertTop5Payload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.ANOMALY_LIST.USER_ANOMALY_ALERT_TOP5, fetchUserAnomalyAlertTop5Payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}
