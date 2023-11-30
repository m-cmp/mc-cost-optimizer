import axios from 'axios';
import ENDPOINT from '@/api/endpoints';
import {isFailResponse, addLoginUserInfoToPayload} from '@/util/apiUtils';
import {getDates} from '@/util/dateTimeUtils';
import {combineCompareCostAnalyticsData, getCombineDailyCosts} from '@/util/costAnalyticsUtil';
import store from '@/store';
import {
  COST_ANALYTICS_DATA_REQUEST_MODEL,
  DELETE_USER_FILTER_REQUEST_MODEL,
  DETAIL_COST_ANALYTICS_DATA_REQUEST_MODEL,
  FILTER_DATA_REQUEST_MODEL,
  PORTION_DETAIL_COST_ANALYTICS_DATA_REQUEST_MODEL,
  RELATED_FILTER_REQUEST_MODEL,
  SAVE_AS_USER_FILTER_REQUEST_MODEL,
  SAVE_USER_FILTER_REQUEST_MODEL,
  FILTER_SEARCH_REQUEST_MODEL,
  USER_FILTER_REQUEST_MODEL
} from '@/constants/costAnalyticsConstants';
import _isEmpty from 'lodash/isEmpty';
import _get from 'lodash/get';
import _cloneDeep from 'lodash/cloneDeep';
import _isEqual from "lodash/isEqual";

export function fetchCostAnalyticsData(payload) {
  return new Promise((resolve, reject) => {
    let costAnalyticsParams = {
      ...COST_ANALYTICS_DATA_REQUEST_MODEL,
      ...payload
    };
    costAnalyticsParams = addLoginUserInfoToPayload(costAnalyticsParams, store);
    if(_isEqual(costAnalyticsParams.selectedVendors[0], 'GCP') && _isEqual(costAnalyticsParams.viewBy, "account")) {
      costAnalyticsParams.viewBy = 'project'
    }

    if (costAnalyticsParams.isCompare) {
      let compareCostAnalyticsParams = _cloneDeep(costAnalyticsParams);
      compareCostAnalyticsParams.startDate = compareCostAnalyticsParams.compareStartDate;
      compareCostAnalyticsParams.endDate = compareCostAnalyticsParams.compareEndDate;
      return axios.all([
        axios.post(ENDPOINT.COST_ANALYTICS.COST_DATA, costAnalyticsParams),
        axios.post(ENDPOINT.COST_ANALYTICS.COST_DATA, compareCostAnalyticsParams),
      ])
      .then(axios.spread((response, compareResponse) => {
        if (isFailResponse(response) || isFailResponse(compareResponse)) {
          reject(response.data.error || compareResponse.data.error);
        } else {
          let isMonthyly = response.data.Data.isMonthly;
          const combineDailyCosts = getCombineDailyCosts(costAnalyticsParams, compareCostAnalyticsParams, isMonthyly);
          const timeFrameDays = getDates(new Date(costAnalyticsParams.startDate), new Date(costAnalyticsParams.endDate), isMonthyly);
          const compareTimeFrameDays = getDates(new Date(compareCostAnalyticsParams.startDate), new Date(compareCostAnalyticsParams.endDate), isMonthyly);

          let combineResponse = combineCompareCostAnalyticsData(response.data.Data, compareResponse.data.Data, combineDailyCosts, timeFrameDays, compareTimeFrameDays, costAnalyticsParams.compareCostType);
          combineResponse.compareTimeFrame = {
            startDate: compareCostAnalyticsParams.startDate,
            endDate: compareCostAnalyticsParams.endDate
          };

          combineResponse.averageCompareCost = compareResponse.data.Data.totalCost / compareTimeFrameDays.length;
          combineResponse.averageCost = response.data.Data.totalCost / timeFrameDays.length;
          combineResponse.combineDailyCosts = combineDailyCosts;
          combineResponse.dateRange = response.data.Data.dateRange;

          resolve(combineResponse);
        }
      }));
    }

    return axios.post(ENDPOINT.COST_ANALYTICS.COST_DATA, costAnalyticsParams)
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

export function fetchDetailCostAnalyticsData(payload) {
  return new Promise((resolve, reject) => {
    let detailCostAnalyticsParams = {
      ...DETAIL_COST_ANALYTICS_DATA_REQUEST_MODEL,
      costDate: payload.costDate,
      totalCost: payload.isCompare ? 0.0 : payload.totalCost,
      visibleFields: payload.visibleFields
    };
    detailCostAnalyticsParams = addLoginUserInfoToPayload(detailCostAnalyticsParams, store);
    detailCostAnalyticsParams.isMonthly = payload.isMonthly;

    if (!_isEmpty(payload.compareCostDate)) {
      detailCostAnalyticsParams.compareCostDate = payload.compareCostDate;
    }

    if (!_isEmpty(payload.filters)) {
      detailCostAnalyticsParams.filters = payload.filters;
    }

    if (!_isEmpty(payload.viewByItem)) {
      detailCostAnalyticsParams.viewByItem = payload.viewByItem;
    }

    if (!_isEmpty(payload.viewBy)) {
      detailCostAnalyticsParams.viewBy = payload.viewBy;
    }

    if (!_isEmpty(payload.selectedVendor)) {
      detailCostAnalyticsParams.selectedVendor = payload.selectedVendor;
    }

    if (typeof payload.isAmortizedCost !== 'undefined') {
      detailCostAnalyticsParams.isAmortizedCost = payload.isAmortizedCost;
    }

    if (!_isEmpty(payload.detailFilters)) {
      const detailFilters = {};
      if (!_isEmpty(payload.detailFilters.productCode)) {
        detailFilters.productCode = payload.detailFilters.productCode;
      }
      if (!_isEmpty(payload.detailFilters.region)) {
        detailFilters.region = payload.detailFilters.region;
      }
      if (!_isEmpty(payload.detailFilters.usageType)) {
        detailFilters.usageType = payload.detailFilters.usageType;
      }
      if (!_isEmpty(payload.detailFilters.linkedAccountId)) {
        detailFilters.linkedAccountId = payload.detailFilters.linkedAccountId;
      }

      detailCostAnalyticsParams.detailFilters = detailFilters;
    }

    return axios.post(ENDPOINT.COST_ANALYTICS.DETAIL_COST_DATA, detailCostAnalyticsParams)
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

export function fetchPortionDetailCostAnalyticsData(payload) {
  return new Promise((resolve, reject) => {
    let portionDetailCostAnalyticsParams = {
      ...PORTION_DETAIL_COST_ANALYTICS_DATA_REQUEST_MODEL,
      costDate: payload.costDate,
      totalCost: payload.totalCost,
      isMonthly: payload.isMonthly,
    };

    portionDetailCostAnalyticsParams = addLoginUserInfoToPayload(portionDetailCostAnalyticsParams, store);

    if (!_isEmpty(payload.compareCostDate)) {
      portionDetailCostAnalyticsParams.compareCostDate = payload.compareCostDate;
    }

    if (!_isEmpty(payload.viewByItem)) {
      portionDetailCostAnalyticsParams.viewByItem = payload.viewByItem;
    }

    if (!_isEmpty(payload.viewBy)) {
      portionDetailCostAnalyticsParams.viewBy = payload.viewBy;
    }

    if (!_isEmpty(payload.selectedVendor)) {
      portionDetailCostAnalyticsParams.selectedVendor = payload.selectedVendor;
    }

    if (payload.isAmortizedCost !== 'undefined') {
      portionDetailCostAnalyticsParams.isAmortizedCost = payload.isAmortizedCost;
    }

    if (!_isEmpty(payload.filters)) {
      portionDetailCostAnalyticsParams.filters = payload.filters;
    }

    return axios.post(ENDPOINT.COST_ANALYTICS.PORTION_DETAIL_COST_DATA, portionDetailCostAnalyticsParams)
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

export function fetchFilterOptions(params) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...FILTER_DATA_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    if(_isEmpty(params.selectedVendors) || params.selectedVendors == undefined) {
      payload = {
        ...payload,
        selectedVendors: [payload.defaultVendor]
      }
    }
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.ALL_OPTIONS, payload)
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

export function fetchSubFilterOptions(params) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...RELATED_FILTER_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.SUB_FILTER, payload)
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

export function fetchRelatedFilterOptions(params) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...RELATED_FILTER_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);

    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.RELATED, payload)
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

export function fetchUserFilters(params) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...USER_FILTER_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.USER_FILTER, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function callSaveUserFilter(params) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...SAVE_USER_FILTER_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.SAVE, payload)
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

export function callSaveAsUserFilter(params) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...SAVE_AS_USER_FILTER_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.SAVE_AS, payload)
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

export function callDeleteUserFilter(filterIndex) {
  return new Promise((resolve, reject) => {
    let payload = {
      ...DELETE_USER_FILTER_REQUEST_MODEL,
      filterIndex: filterIndex
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.DELETE, payload)
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
  })
}

export function fetchResultSearch(params) {
  // eslint-disable-next-line no-param-reassign
  return new Promise((resolve, reject) => {
    let payload = {
      ...FILTER_SEARCH_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.FILTER.SEARCH, payload)
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

export function fetchExportEmailCostAnalyticsCostTrendAll(params){
  return new Promise((resolve, reject) => {
    let payload = {
      ...FILTER_SEARCH_REQUEST_MODEL,
      ...params
    };
    payload = addLoginUserInfoToPayload(payload, store);
    return axios.post(ENDPOINT.COST_ANALYTICS.EXPORT_EMAIL_DATA, payload)
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
