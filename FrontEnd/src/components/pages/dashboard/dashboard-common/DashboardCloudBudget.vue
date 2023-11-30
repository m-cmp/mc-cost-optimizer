<template>
  <div class="dashboard-common-item">
    <b-col
      class="equal-element bg-white base-wrapper-radius py-16 custom-notification-no-data col-height custom-dashboard-common-item">
      <b-row class="dashboard-cloud-budget-title">
        <p class="font-14 bold custom-label font-family-notosanscjkkr-medium">
          {{ $t('dashboard.dashboardCommon.cloudBudget') }}
        </p>
      </b-row>
      <div v-show="isBudgetDataLoading">
        <b-col class="cost-budget-loading">
          <BaseLoadingIndicator :loading-height="78"/>
        </b-col>
      </div>

      <div v-if="hasBudgetData && !isBudgetDataLoading">
        <b-row
          class="base-font-special mt-10 ml-0 mr-0 current-budget-data"
          align-h="center"
          align-v="center">
          <span class="font-24 currency-text">
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(budgetData.awsBdgtCost)) }}
          </span>
        </b-row>
        <p v-if="budgetData.awsBdgtCost>0">
          <span class="color-gray-1">
            {{ $t('dashboard.dashboardCommon.used#1') }}{{ formatPercentage(((costMonthToDate.currentMonthCost)*100/budgetData.awsBdgtCost) > 100 ? 100 : ((costMonthToDate.currentMonthCost)*100/budgetData.awsBdgtCost)) }}% {{ $t('dashboard.dashboardCommon.used#2') }}
          </span>
        </p>
      </div>

      <BaseNotificationNoData
        v-if="!hasBudgetData && !isBudgetDataLoading"
        :content-displayed="noDataContentDisplayed"
        class="no-data"
      />
    </b-col>
  </div>
</template>

<script>
  import DashboardCommonMixin from '@/mixins/DashboardCommonMixin';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import {mapGetters} from "vuex";

  export default {
    name: 'DashboardCloudBudget',
    components: {
      BaseLoadingIndicator
    },
    mixins: [DashboardCommonMixin],
    computed: {
      ...mapGetters({
        isBudgetDataLoading: 'dashboard/isBudgetDataLoading',
      }),
    }
  };
</script>
