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


export function fetchAbnormalDetectedList(payload) {
  /*
  * backup
  * */
  let abnormalDetectedListPayload = {
    ...ABNORMAL_DETECTED_LIST_REQUEST_MODEL,
    ...payload
  };
  abnormalDetectedListPayload = addLoginUserInfoToPayload(abnormalDetectedListPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.ANOMALY_LIST.DETECTED_LIST, abnormalDetectedListPayload)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.abnormal.list);
}

