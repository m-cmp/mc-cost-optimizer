const SET_IS_COST_MONTH_TO_DATE_LOADING = (state, isCostMonthToDateLoading) => {
  state.isCostMonthToDateLoading = isCostMonthToDateLoading;
};

const SET_IS_ESTIMATED_COST_LOADING = (state, isEstimatedCostLoading) => {
  state.isEstimatedCostLoading = isEstimatedCostLoading;
};

const SET_IS_YEAR_COST_FCST_LOADING = (state, isYearCostFcstLoading) => {
  state.isYearCostFcstLoading = isYearCostFcstLoading;
};

const SET_IS_BUDGET_DATA_LOADING = (state, isBudgetDataLoading) => {
  state.isBudgetDataLoading = isBudgetDataLoading;
};

const SET_IS_TOTAL_SAVING_LOADING = (state, isTotalSavingLoading) => {
  state.isTotalSavingLoading = isTotalSavingLoading;
};

const SET_COST_MONTH_TO_DATE = (state, costMonthToDate) => {
  state.costMonthToDate = costMonthToDate;
};

const SET_ESTIMATED_COST = (state, estimatedCost) => {
  state.estimatedCost = estimatedCost;
};

const SET_BUDGET_DATA = (state, budgetData) => {
  state.budgetData = budgetData;
};

const SET_TOTAL_SAVING = (state, totalSaving) => {
  state.totalSaving = totalSaving;
};

const SET_DASHBOARD_DATA = (state, dashboardData) => {
  state.dashboardDataFromApi = dashboardData;
};

const SET_DASHBOARD_VIEW_MODE = (state, viewMode) => {
  state.dashboardViewMode = viewMode;
};

// manually sync. Used in some cases where load whole dashboard is not necessary
const SYNC_DASHBOARD_SELECTED = (state, selectedDashboardIndex) => {
  state.dashboardDataFromApi.userData.dashboardData.forEach(db => {
    if (db.isDashboardSelected) {
      // eslint-disable-next-line no-param-reassign
      db.isDashboardSelected = false;
    } else if (db.index === selectedDashboardIndex) {
      // eslint-disable-next-line no-param-reassign
      db.isDashboardSelected = true;
    }
  });
};

// manually sync. Used in some cases where load whole dashboard is not necessary
const SYNC_UPDATED_DASHBOARD_WIDGET = (state, payload) => {
  const findDashboard = state.dashboardDataFromApi.userData.dashboardData.find(dashboard => {
    return dashboard.index === payload.dashboardIndex;
  });

  findDashboard.widgets[payload.widgetIndex] = payload.updatedWidget;
};

export default {
  SET_DASHBOARD_DATA,
  SET_COST_MONTH_TO_DATE,
  SET_ESTIMATED_COST,
  SET_BUDGET_DATA,
  SET_TOTAL_SAVING,
  SYNC_DASHBOARD_SELECTED,
  SET_DASHBOARD_VIEW_MODE,
  SET_IS_COST_MONTH_TO_DATE_LOADING,
  SET_IS_ESTIMATED_COST_LOADING,
  SET_IS_BUDGET_DATA_LOADING,
  SET_IS_TOTAL_SAVING_LOADING,
  SYNC_UPDATED_DASHBOARD_WIDGET,
};
