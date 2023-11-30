import axios from 'axios';
import ENDPOINT from '@/api/endpoints';
import {isFailResponse, addLoginUserInfoToPayload} from '@/util/apiUtils';
import store from '@/store';
import _get from 'lodash/get';
import UrlsConfig from '../../../config/urls.conf';
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';

export function fetchAvailableCloudVendors() {
  // BackUp
  // return new Promise(function (resolve, reject) {
  //   return axios.get(UrlsConfig.portalUrl + ENDPOINT.CLOUD_PORTAL.VENDOR)
  //     .then((response) => {
  //       resolve(response.data.result || {});
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     })
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.common.vendorList.result);
}

export function fetchAllVendors(payload) {
  // BackUp
  // // eslint-disable-next-line no-param-reassign
  // payload = addLoginUserInfoToPayload(payload, store);
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.COMMON.VENDORS, payload)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         let vendors = response.data.Data.vendors;
  //         resolve(vendors || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.common.allVendors.vendors);
}

export function fetchCommonInfo(payload) {
  // BackUp
  // // eslint-disable-next-line no-param-reassign
  // payload = addLoginUserInfoToPayload(payload, store);
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.COMMON.INFO, payload)
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
  return Promise.resolve(mcmpAPIResponseDummyData.common.info);
}

export function saveSelectedVendors(payload) {
  // eslint-disable-next-line no-param-reassign
  payload = addLoginUserInfoToPayload(payload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.COMMON.SAVE_VENDORS, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve();
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function saveSelectedCurrency(payload) {
  // eslint-disable-next-line no-param-reassign
  return;
  /*payload = addLoginUserInfoToPayload(payload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.COMMON.SAVE_CURRENCY, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve();
        }
      })
      .catch((error) => {
        reject(error);
      });
  });*/
}

export function callSubmitSurvey(payload) {
  // eslint-disable-next-line no-param-reassign
  payload = addLoginUserInfoToPayload(payload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.SURVEY.SUBMIT_SURVEY, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve();
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function callCheckSurveySubmitted(payload) {
  // BackUp
  // // eslint-disable-next-line no-param-reassign
  // payload = addLoginUserInfoToPayload(payload, store);
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.SURVEY.CHECK, payload)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(response.data);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.common.surveyCheck)
}

export function fetchCompanyMaxDatetime(payload) {

  let fetchDataPayload = addLoginUserInfoToPayload(payload, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.COMMON.COMPANY_MAX_DATE, fetchDataPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data') || {});
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}
