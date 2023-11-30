import {
  fetchCommonInfo,
  fetchAllVendors,
  fetchAvailableCloudVendors,
  saveSelectedCurrency,
  saveSelectedVendors,
  fetchCompanyMaxDatetime
} from '@/api/common';
import {
  CHECK_SURVEY_SUBMITTED,
  COMMON_INFO_REQUEST_MODE,
  GET_ALL_VENDORS_REQUEST_MODEL,
  SAVE_SELECTED_CURRENCY,
  SAVE_SELECTED_VENDORS,
  SUBMIT_SURVEY_REQUEST_MODEL
} from '@/constants/constants';

const setLang = (context, payload) => {
  context.commit('SET_LANG', payload);
};

const setIsLoading = (context, isLoading) => {
  context.commit('SET_IS_LOADING', isLoading);
};

const getAllVendors = (context, params) => {
  const fullPayload = {
    ...GET_ALL_VENDORS_REQUEST_MODEL,
    ...params
  };
  return new Promise((resolve, reject) => {
    fetchAllVendors(fullPayload)
      .then((res) => {
        context.commit('SET_ALL_VENDORS', res);
        resolve();
      })
      .catch((err) => {
        reject(err);
      })
  });
};

const getAvailableVendors = (context, availableVendor) => {
  return new Promise((resolve , reject) => {
    fetchAvailableCloudVendors()
      .then((res) => {
        context.commit('SET_AVAILABLE_VENDORS', res);
        resolve();
      })
      .catch((err) => {
        reject(err);
      })
  })
};

const getInfo = (context, params) => {
  const fullPayload = {
    ...COMMON_INFO_REQUEST_MODE,
    ...params
  };
  return new Promise((resolve, reject) => {
    fetchCommonInfo(fullPayload)
      .then((res) => {
        context.commit('SET_INFO', res);
        resolve();
      })
      .catch((err) => {
        reject(err);
      })
  });
};

/**
 * Select vendors like AWS, GCP, AZURE
 *
 * @param context
 * @param selectedVendors
 */
const selectVendors = (context, selectedVendors) => {
  // sync FE state, regardless of API call status
  context.commit('SET_SELECTED_VENDORS', selectedVendors);
  const fullPayload = {
    ...SAVE_SELECTED_VENDORS,
    selectedVendors: selectedVendors,
  };
  return saveSelectedVendors(fullPayload);
};

const selectCurrency = (context, selectedCurrency) => {
  // sync FE state, regardless of API call status
  context.commit('SET_SELECTED_CURRENCY', selectedCurrency);
  const fullPayload = {
    ...SAVE_SELECTED_CURRENCY,
    currency: selectedCurrency
  };
  return saveSelectedCurrency(fullPayload);
};

const setSearchFrom = (context, page) => {
  context.commit('SET_SEARCH_FROM', page);
};
const setBrowser = (context, browserName) => {
  context.commit('SET_BROWSER', browserName);
};

const getCompanyMaxDatetime = (context, payload) => {
  context.commit('SET_IS_COMPANY_MAX_DATETIME_LOADING', true);
  fetchCompanyMaxDatetime(payload).then((response) => {
    context.commit('SET_IS_COMPANY_MAX_DATETIME_LOADING', false);
    context.commit('COMPANY_MAX_DATETIME_UPDATED', response);
  }).catch((error) => {
    // eslint-disable-next-line
    context.commit('SET_IS_COMPANY_MAX_DATETIME_LOADING', false);
    console.error(error);
  });
};

export default {
  setLang,
  getAllVendors,
  getAvailableVendors,
  getInfo,
  selectVendors,
  selectCurrency,
  setSearchFrom,
  setIsLoading,
  setBrowser,
  getCompanyMaxDatetime
};
