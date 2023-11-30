import actions from './actions';
import getters from './getters';
import mutations from './mutations';
import {VIEW_MODE} from '@/constants/dashboardConstants';

const state = {
  costMonthToDate: {},
  estimatedCost: {},
  budgetData: {},
  totalSaving: {},
  dashboardDataFromApi: {
    batchTime: {},
    widgetType: [],
    widgetValues: {},
    userData: {
      dashboardData: [],
    },
  },
  dashboardViewMode: VIEW_MODE.DEFAULT,
  isCostMonthToDateLoading: true,
  isBudgetDataLoading:true,
  isTotalSavingLoading:true,
  isEstimatedCostLoading: true,
  isYearCostFcstLoading: true,
};

export default {
  namespaced: true,
  state,
  actions,
  getters,
  mutations,
};
