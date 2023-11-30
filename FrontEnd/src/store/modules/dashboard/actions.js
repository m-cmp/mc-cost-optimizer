import {
  callDeleteDashboard,
  callSaveAsDashboard,
  callSaveDashboard,
  callSaveWidget,
  fetchCostMonthToDate,
  fetchDashboardData,
  fetchEstimatedCost,
  setSelectedDashboard,
} from '@/api/dashboard';
import {DEFAULT_WIDGET_DATA} from '@/constants/dashboardConstants';

const getCostMonthToDate = (context, payload) => {
  context.commit('SET_IS_COST_MONTH_TO_DATE_LOADING', true);
  fetchCostMonthToDate(payload)
    .then((res) => {
      context.commit('SET_IS_COST_MONTH_TO_DATE_LOADING', false);
      context.commit('SET_COST_MONTH_TO_DATE', res);
    })
    .catch((err) => {
      context.commit('SET_IS_COST_MONTH_TO_DATE_LOADING', false);
      context.commit('SET_COST_MONTH_TO_DATE', DEFAULT_WIDGET_DATA.COST_MONTH_TO_DATE);
      console.error(err);
    })
};

const getEstimatedCost = (context, payload) => {
  context.commit('SET_IS_ESTIMATED_COST_LOADING', true);
  fetchEstimatedCost(payload)
    .then((res) => {
      context.commit('SET_IS_ESTIMATED_COST_LOADING', false);
      context.commit('SET_ESTIMATED_COST', res);
    })
    .catch((err) => {
      context.commit('SET_IS_ESTIMATED_COST_LOADING', false);
      context.commit('SET_ESTIMATED_COST', DEFAULT_WIDGET_DATA.ESTIMATED_COST);
      console.error(err);
    })
};

const getDashboardData = (context) => {
  return new Promise((resolve, reject) => {
    fetchDashboardData()
      .then((res) => {
        context.commit('SET_DASHBOARD_DATA', res);
        resolve();
      })
      .catch((err) => {
        reject(err);
      })
  });
};

const setDashboardViewMode = (context, viewMode) => {
  context.commit('SET_DASHBOARD_VIEW_MODE', viewMode);
};

const syncUpdatedDashboardWidget = (context, payload) => {
  context.commit('SYNC_UPDATED_DASHBOARD_WIDGET', payload);
};

const selectDashboard = (context, index) => {
  let setSelectedDashboardPayload = {
    dashboardIndex: index
  };
  // sync FE state, regardless of API call status
  context.commit('SYNC_DASHBOARD_SELECTED', index);
  return new Promise((resolve, reject) => {
    setSelectedDashboard(setSelectedDashboardPayload)
      .then((res) => {
        resolve(res);
      })
      .catch((err) => {
        reject(err);
      })
  });
};

const saveAsDashboard = (context, newDashboard) => {
  return new Promise((resolve, reject) => {
    callSaveAsDashboard(newDashboard)
      .then((res) => {
        resolve(res.Data);
      })
      .catch((err) => {
        reject(err);
      })
  });
};

const saveWidget = (context, widgetData) => {
  return new Promise((resolve, reject) => {
    callSaveWidget(widgetData)
    .then((res) => {
      resolve(res);
    })
    .catch(() => {
      reject();
    })
  });
};

const saveDashboard = (context, dashboard) => {
  return new Promise((resolve, reject) => {
    callSaveDashboard(dashboard)
      .then((res) => {
        resolve(res);
      })
      .catch((err) => {
        reject(err);
      })
  });
};

const deleteDashboard = (context, dashboardIndex) => {
  return new Promise((resolve, reject) => {
    callDeleteDashboard(dashboardIndex)
      .then((res) => {
        resolve(res);
      })
      .catch((err) => {
        reject(err);
      })
  });
};

export default {
  getDashboardData,
  getCostMonthToDate,
  getEstimatedCost,
  setDashboardViewMode,
  saveAsDashboard,
  selectDashboard,
  saveDashboard,
  deleteDashboard,
  saveWidget,
  syncUpdatedDashboardWidget,
};
