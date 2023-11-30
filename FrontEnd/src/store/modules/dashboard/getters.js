const costMonthToDate = (state) => state.costMonthToDate;
const estimatedCost = (state) => state.estimatedCost;
const budgetData = (state) => state.budgetData;
const totalSaving = (state) => state.totalSaving;
const dashboardData = (state) => state.dashboardDataFromApi.userData.dashboardData;
const dashboardViewMode = (state) => state.dashboardViewMode;
const batchTime = (state) => state.dashboardDataFromApi.batchTime;
const isCostMonthToDateLoading = (state) => state.isCostMonthToDateLoading;
const isBudgetDataLoading = (state) => state.isBudgetDataLoading;
const isTotalSavingLoading = (state) => state.isTotalSavingLoading;
const isEstimatedCostLoading = (state) => state.isEstimatedCostLoading;
const isYearCostFcstLoading = (state) => state.isYearCostFcstLoading;

export default {
  dashboardData,
  costMonthToDate,
  budgetData,
  totalSaving,
  estimatedCost,
  dashboardViewMode,
  batchTime,
  isCostMonthToDateLoading,
  isBudgetDataLoading,
  isTotalSavingLoading,
  isEstimatedCostLoading,
  isYearCostFcstLoading,
};
