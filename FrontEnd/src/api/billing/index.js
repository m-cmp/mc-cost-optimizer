import axios from 'axios';
import ENDPOINT from './../endpoints';
import _isEmpty from 'lodash/isEmpty';
import _get from 'lodash/get';
import {add0StringToNumberLessThan10} from '@/util/dateTimeUtils';
import {
  REQUEST_BILLING_BILLS_MODEL,
  REQUEST_BILLING_CHARGE_MODEL,
  REQUEST_BILLING_DETAILS_MODEL,
  REQUEST_ADDITIONAL_SERVICES_MODEL,
  REQUEST_TAG_KEYS_MODEL,
  REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL,
  REQUEST_SERVICE_GROUP_VIEWS_MODEL,
} from '@/constants/billingConstants';
import {isFailResponse, addLoginUserInfoToPayload} from '@/util/apiUtils';
import store from '@/store';
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';

/**
 * Fetch charge lists
 *
 * @returns {Promise<any>}
 */
export function fetchChargeList(payload) {
  // BackUp
  let chargesParams;
  if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
    const todayDate = new Date();
    chargesParams = {
      ...REQUEST_BILLING_CHARGE_MODEL,
      chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
      chargeYear: todayDate.getFullYear()
    };

    if (payload.selectedVendor) {
      chargesParams.vendor = payload.selectedVendor;
    }
  } else {
    chargesParams = {
      ...REQUEST_BILLING_CHARGE_MODEL,
      chargeMonth: payload.chargeMonth,
      chargeYear: payload.chargeYear,
      vendor: payload.selectedVendor
    };
  }
  chargesParams = addLoginUserInfoToPayload(chargesParams, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[chargesParams.vendor].CHARGE_LIST, chargesParams)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.charges') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
  // return Promise.resolve(mcmpAPIResponseDummyData.billing.chargeList.charges)

}
/**
 * Fetch bills, use for monthly trend data and bill summary api
 *
 * @returns {Promise<any>}
 */
export function fetchBillList(payload) {
  // BackUp
  let fetchBillListPayload = {
    ...REQUEST_BILLING_BILLS_MODEL,
    vendor: payload.selectedVendor
  };
  fetchBillListPayload = addLoginUserInfoToPayload(fetchBillListPayload, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[fetchBillListPayload.vendor].BILL_LIST, fetchBillListPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.bills') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
  // return Promise.resolve(mcmpAPIResponseDummyData.billing.billList.bills)
}

export function fetchBillingDetail(payload) {
  // BackUp
  let fetchBillingDetailRequest;
  if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
    const todayDate = new Date();
    fetchBillingDetailRequest = {
      ...REQUEST_BILLING_DETAILS_MODEL,
      chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
      chargeYear: todayDate.getFullYear()
    };
    if (payload.selectedVendor) {
      fetchBillingDetailRequest.vendor = payload.selectedVendor;
    }
  } else {
    fetchBillingDetailRequest = {
      ...REQUEST_BILLING_DETAILS_MODEL,
      chargeMonth: payload.chargeMonth,
      chargeYear: payload.chargeYear,
      vendor: payload.selectedVendor,
      tagKey: payload.tagKey
    };
    fetchBillingDetailRequest = addLoginUserInfoToPayload(fetchBillingDetailRequest, store);

    return new Promise((resolve, reject) => {
      return axios.post(ENDPOINT.BILLING[fetchBillingDetailRequest.vendor].BILLING_DETAIL, fetchBillingDetailRequest)
        .then((response) => {
          if (isFailResponse(response)) {
            reject(response.data.error);
          } else {
            resolve(_get(response, 'data.Data.Details') || []);
          }
        })
        .catch((error) => {
          reject(error);
        });
    });
    // return Promise.resolve(mcmpAPIResponseDummyData.billing.detailList.Details)
  }
}

export function fetchBillingDetailWithTag(payload) {
  //const payload = addLoginUserInfoToPayload(REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL, store);

  let fetchBillingDetailWithTagRequest = {
    ...REQUEST_BILLING_DETAILS_MODEL,
    chargeMonth: payload.chargeMonth,
    chargeYear: payload.chargeYear,
    vendor: payload.selectedVendor, // payload : selectedVendor > fetch request : vendor
    tagKey: payload.tagKey,
    viewBy: payload.viewBy
  };

  fetchBillingDetailWithTagRequest = addLoginUserInfoToPayload(fetchBillingDetailWithTagRequest, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[fetchBillingDetailWithTagRequest.vendor].BILLING_DETAIL, fetchBillingDetailWithTagRequest)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.Details') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}


export function fetchBillingDetailWithServiceGroup(payload) {
  //const payload = addLoginUserInfoToPayload(REQUEST_BILLING_DETAILS_WITH_TAG_MODEL, store);

  let fetchBillingDetailWithSvgRequest = {
    ...REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL,
    chargeMonth: payload.chargeMonth,
    chargeYear: payload.chargeYear,
    serviceGroupSetNm: payload.serviceGroupSetNm,
    vendor: payload.selectedVendor, // payload : selectedVendor > fetch request : vendor
    viewBy: payload.viewBy
  };

  fetchBillingDetailWithSvgRequest = addLoginUserInfoToPayload(fetchBillingDetailWithSvgRequest, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[fetchBillingDetailWithSvgRequest.vendor].BILLING_DETAIL, fetchBillingDetailWithSvgRequest)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.Details') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchTagOptions(payload) {
  let fetchTagKeysRequestModel = {
    ...REQUEST_TAG_KEYS_MODEL,
    vendor: payload.selectedVendor,
    yearMonth: payload.yearMonth
  };
  fetchTagKeysRequestModel = addLoginUserInfoToPayload(fetchTagKeysRequestModel, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[payload.selectedVendor].TAG_KEYS, fetchTagKeysRequestModel)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.tagInfo') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchServiceGroupOptions(payload) {
  let fetchServiceGroupViewsRequestModel = {
    ...REQUEST_SERVICE_GROUP_VIEWS_MODEL,
    vendor: payload.selectedVendor,
    yearMonth: payload.yearMonth
  };
  fetchServiceGroupViewsRequestModel = addLoginUserInfoToPayload(fetchServiceGroupViewsRequestModel, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.COMMON.SERVICE_GROUP_SETS, fetchServiceGroupViewsRequestModel)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.serviceGroupInfo') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

/**
 * Fetch Additional Services
 *
 * @returns {Promise<any>}
 */
export function fetchAdditionalServices(payload) {
  let additionalServicesParams;
  if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
    const todayDate = new Date();
    additionalServicesParams = {
      ...REQUEST_ADDITIONAL_SERVICES_MODEL,
      chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
      chargeYear: todayDate.getFullYear()
    };

    if (payload.selectedVendor) {
      additionalServicesParams.vendor = payload.selectedVendor;
    }
  } else {
    additionalServicesParams = {
      ...REQUEST_ADDITIONAL_SERVICES_MODEL,
      chargeMonth: payload.chargeMonth,
      chargeYear: payload.chargeYear,
      vendor: payload.selectedVendor
    };
  }
  additionalServicesParams = addLoginUserInfoToPayload(additionalServicesParams, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[additionalServicesParams.vendor].ADDITIONAL_SERVICES, additionalServicesParams)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.additionalServices') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}


