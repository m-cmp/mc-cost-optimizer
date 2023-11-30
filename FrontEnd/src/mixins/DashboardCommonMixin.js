import _isEmpty from 'lodash/isEmpty';
import {CURRENCY_SYMBOL, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
import BaseNotificationNoData from '@/components/common/BaseNotificationNoData'
import {calculateCostByCurrency, formatCost, formatPercentage} from '@/util/costUtils';

export default {
  components: {
    BaseNotificationNoData
  },
  props: {
    estimatedCost: {
      type: Object,
      required: true
    },
    budgetData: {
      type: Object,
      required: true
    },
    totalSaving: {
      type: Object,
      required: true
    },
    commonUserInfo: {
      type: Object,
      required: true
    },
    exchangeRate: {
      type: Object,
      default() {
        return DEFAULT_EXCHANGE_RATE
      }
    },
  },
  data() {
    return {
      noDataContentDisplayed: false,
      costMonthToDate: null,
      dashboardEstimatedCost: null
    };
  },
  computed: {
    currencySymbol: function() {
      return CURRENCY_SYMBOL[this.commonUserInfo.selectedCurrency];
    },
    hasCostMonthToDateData: function() {
      return !_isEmpty(this.costMonthToDate);
    },
    hasEstimatedCostData: function() {
      return !_isEmpty(this.dashboardEstimatedCost);
    },
    hasBudgetData: function() {
      return !_isEmpty(this.budgetData);
    },
    hasTotalSaving: function() {
      return !_isEmpty(this.totalSaving);
    },
    costMonthToDateChangePercentage: function() {
      if (this.costMonthToDate.lastMonthCost === 0) {
        return 100;
      } else {
        return 100 * ((this.costMonthToDate.currentMonthCost - this.costMonthToDate.lastMonthCost) / Math.abs(this.costMonthToDate.lastMonthCost));
      }
    },
    estimatedCostChangePercentage: function() {
      if (this.dashboardEstimatedCost.lastMonthTotalCost === 0) {
        return 100;
      } else {
        return 100 * ((this.dashboardEstimatedCost.currentMonthEstimatedCost - this.dashboardEstimatedCost.lastMonthTotalCost) / Math.abs(this.dashboardEstimatedCost.lastMonthTotalCost));
      }
    }
  },
  mounted() {
  },
  methods: {
    formatCost(cost) {
      return formatCost(cost);
    },
    formatPercentage(percentage) {
      return formatPercentage(percentage);
    },
    calculateCostByCurrency(costInUsd) {
      return calculateCostByCurrency(costInUsd, this.commonUserInfo.selectedCurrency, this.exchangeRate);
    }
  }
};
