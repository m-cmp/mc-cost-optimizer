import axios from 'axios';
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';
import ENDPOINT from '@/api/endpoints';
import {
  COST_MONTH_TO_DATE_REQUEST_MODEL,
  DASHBOARD_ABNORMAL_REQUEST_MODEL,
  DASHBOARD_ML_ABNORMAL_USER_REQUEST_MODEL,
  DASHBOARD_COST_REQUEST_MODEL,
  DASHBOARD_WIDGET_DASHBOARDS_REQUEST_MODEL,
  ESTIMATED_COST_REQUEST_MODEL,
  SAVE_AS_DASHBOARD_REQUEST_MODEL,
  SAVE_DASHBOARD_REQUEST_MODEL,
  DELETE_DASHBOARD_REQUEST_MODEL,
  TOP_5_COST,
  DASHBOARD_TREND_REQUEST_MODEL,
  DASHBOARD_PORTION_REQUEST_MODEL,
  SET_SELECTED_DASHBOARD_REQUEST_MODEL,
  FETCH_DASHBOARD_UUID_REQUEST_MODEL,

} from '@/constants/dashboardConstants';
import {isFailResponse, addLoginUserInfoToPayload} from '@/util/apiUtils';
import store from '@/store';
import {formatMonthlyTimeFrameRequestPayload} from "@/util/dashboardUtils";
import _get from 'lodash/get';

export function fetchCostMonthToDate(payload) {
  // BackUp
  let costMonthToDatePayload = {
    ...COST_MONTH_TO_DATE_REQUEST_MODEL,
    ...payload
  };
  costMonthToDatePayload = addLoginUserInfoToPayload(costMonthToDatePayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.COST_MONTH_TO_DATE, costMonthToDatePayload)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.costMonth);
}

export function fetchEstimatedCost(payload) {
  // BackUp
  let estimatedCostRequestPayload = {
    ...ESTIMATED_COST_REQUEST_MODEL,
    ...payload
  };
  estimatedCostRequestPayload = addLoginUserInfoToPayload(estimatedCostRequestPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.ESTIMATED_COST, estimatedCostRequestPayload)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.costEstimated);
}




export function fetchTop5Cost(payload) {
  // BackUp
  let top5CostParams = {
    ...TOP_5_COST,
    ...payload
  };
  top5CostParams = addLoginUserInfoToPayload(top5CostParams, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.TOP_5_COST, top5CostParams)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.top5);
}

export function fetchDashboardCost(payload) {
  // BackUp
  let dashboardCostParams = {
    ...DASHBOARD_COST_REQUEST_MODEL,
    ...payload
  };
  dashboardCostParams = addLoginUserInfoToPayload(dashboardCostParams, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.DASHBOARD_COST, dashboardCostParams)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.cost);
}

export function fetchDashboardAbnormal(payload) {
  // BackUp
  let abnormalParams = {
    ...DASHBOARD_ABNORMAL_REQUEST_MODEL,
    ...payload
  };
  abnormalParams = addLoginUserInfoToPayload(abnormalParams, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.ABNORMAL, abnormalParams)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.abnormal);
}


export function fetchDashboardMLAbnormalUser(payload) {
  let mlAbnormalParams = {
    ...DASHBOARD_ML_ABNORMAL_USER_REQUEST_MODEL,
    ...payload
  };
  mlAbnormalParams = addLoginUserInfoToPayload(mlAbnormalParams, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.ML_ABNORMAL_USER, mlAbnormalParams)
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




export function fetchDashboardData() {
  // BackUp
  return new Promise((resolve, reject) => {
    let payload = {
      ...addLoginUserInfoToPayload(DASHBOARD_WIDGET_DASHBOARDS_REQUEST_MODEL, store),
      'vendorList' : store.state.vendorInfo
    };
    return axios.post(ENDPOINT.DASHBOARD.WIDGET.DASHBOARDS, payload)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.loadDashboard.widgetDashboard.Data);
}

export function callSaveAsDashboard(dashboard) {
  // BackUp
  let saveAsDashboardPayload = {
    ...SAVE_AS_DASHBOARD_REQUEST_MODEL,
    dashboard: dashboard
  };
  saveAsDashboardPayload = addLoginUserInfoToPayload(saveAsDashboardPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.WIDGET.SAVE_AS_DASHBOARD, saveAsDashboardPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboardWidget.saveAsDashboard);
}

export function callSaveDashboard(dashboard) {
  // BackUp
  let saveDashboardPayload = {
    ...SAVE_DASHBOARD_REQUEST_MODEL,
    dashboard: dashboard
  };
  saveDashboardPayload = addLoginUserInfoToPayload(saveDashboardPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.WIDGET.SAVE_DASHBOARD, saveDashboardPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboardWidget.saveAsDashboard);
}

export function callDeleteDashboard(index) {
  let deletePayload = {
    ...DELETE_DASHBOARD_REQUEST_MODEL,
    dashboardIndex: index
  };
  deletePayload = addLoginUserInfoToPayload(deletePayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.WIDGET.DELETE_DASHBOARD, deletePayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function callSaveWidget(payload) {
  // eslint-disable-next-line no-param-reassign
  payload = addLoginUserInfoToPayload(payload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.WIDGET.SAVE_WIDGET, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchDataCompareCostTrend(payload) {
  // BackUp
  let trendParams = {
    ...DASHBOARD_TREND_REQUEST_MODEL,
    ...payload
  };
  trendParams = addLoginUserInfoToPayload(trendParams, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.COMPARE_COST_TREND, trendParams)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.trend);
}

export function fetchDashboardProductPortion(payload) {
  // BackUp
  let dashboardProductPortionParams = {
    ...DASHBOARD_PORTION_REQUEST_MODEL,
    ...payload
  }
  dashboardProductPortionParams = addLoginUserInfoToPayload(dashboardProductPortionParams, store);
  dashboardProductPortionParams.timeFrame = formatMonthlyTimeFrameRequestPayload(dashboardProductPortionParams.timeFrame, dashboardProductPortionParams.dateType);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.PRODUCT_PORTION, dashboardProductPortionParams)
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
  // return Promise.resolve(mcmpAPIResponseDummyData.dashboard.productPortion);
}
export function setSelectedDashboard(payload) {
  let setSelectedDashboardPayload = {
    ...SET_SELECTED_DASHBOARD_REQUEST_MODEL,
    ...payload
  };
  setSelectedDashboardPayload = addLoginUserInfoToPayload(setSelectedDashboardPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.WIDGET.SET_SELECTED_DASHBOARD, setSelectedDashboardPayload)
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

export function fetchDashboardUuid(payload) {
  let fetchDashboardUuidPayload = {
    ...FETCH_DASHBOARD_UUID_REQUEST_MODEL,
    dashboardIndex : payload
  };
  fetchDashboardUuidPayload = addLoginUserInfoToPayload(fetchDashboardUuidPayload, store);
  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.DASHBOARD.UUID, fetchDashboardUuidPayload)
      .then((response) => {
        if(isFailResponse(response)) {
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
