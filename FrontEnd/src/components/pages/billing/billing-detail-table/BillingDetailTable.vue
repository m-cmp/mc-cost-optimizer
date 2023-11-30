<template>
  <b-col
    class="bg-white mt-20 px-0 billing-detail-table"
  >
    <b-row no-gutters/>
    <BillingDetailTableTabs
      :bill-list="billList"
      :active-month-idx="activeMonthIdx"
      :charge-list="chargeList"
      :apply-exchange-rate="applyExchangeRate"
      :selected-vendor="selectedVendor"
      :selected-month-year="selectedMonthYear"
      :current-step="currentStep"
      :selected-tab-index="selectedTabIndex"
      :has-auto-spot-data="hasAutoSpotData"
      @showHotspot="showHotspot"
      @clickChargeListCell="onClickChargeListCell"
    />
  </b-col>
</template>

<script>

  import BillingDetailTableTabs from './table-tabs/BillingDetailTableTabs';
  import {getValueFromStorageByKey, setValueToStorageByKey, LOCAL_STORAGE_KEY} from '@/util/localStorage';
  import _isEmpty from 'lodash/isEmpty';
  import { VENDOR } from '@/constants/constants';

  export default {
    name: 'BillingDetailTable',
    components: {
      BillingDetailTableTabs
    },
    props: {
      billList: {
        type: Array,
        required: true
      },
      chargeList: {
        type: Array,
        default() {
          return []
        }
      },
      activeMonthIdx: {
        type: Number,
        required: true
      },
      selectedVendor: {
        type: Array[String],
        required: true
      },
      selectedMonthYear: {
        type: Object,
        required: true
      },
      selectedTabIndex: {
        type: Number,
        required: true
      },
      currentStep: {
        type: Number,
        required: true
      }
    },
    data() {
      return { }
    },
    computed: {
      applyExchangeRate: {
        cache: false,
        get() {
          const currentBillByMonth = this.billList[this.activeMonthIdx];
          return (currentBillByMonth && currentBillByMonth.applyExchangeRate) ? currentBillByMonth.applyExchangeRate : 0;
        }
      },
      hasAutoSpotData: {
        get(){
          let isShow = false;
          const currentBillByMonth = this.billList[this.activeMonthIdx];

          if(this.selectedVendor == VENDOR.AWS && currentBillByMonth && ('additionalServices' in currentBillByMonth)){
            currentBillByMonth.additionalServices.some(additionalService => {
              if(additionalService.additionalServiceCode == 'AUTOSPOT') {
                isShow = true;
              }
            });
          }

          return isShow;
        }
      }
    },
    mounted() {
      if (_isEmpty(getValueFromStorageByKey(LOCAL_STORAGE_KEY.BILLING_TABLE_STATE))) {
        const defaultStatus = {
          chargeList: {
            displayedWarning: false,
          },
          cloudInvoiceList: {
            displayedWarning: true
          },
          cloudBillDetails: {
            displayedWarning: true
          },
          cloudInvoiceInsight: {
            displayedWarning: true
          }

        };
        setValueToStorageByKey(LOCAL_STORAGE_KEY.BILLING_TABLE_STATE, defaultStatus);
        this.$store.dispatch('billing/setChargeTableState', defaultStatus);
      } else {
        const value = getValueFromStorageByKey(LOCAL_STORAGE_KEY.BILLING_TABLE_STATE);
        if(value.cloudInvoiceInsight === undefined){
          value.cloudInvoiceInsight = {};
          value.cloudInvoiceInsight.displayedWarning = true;
        }
        this.$store.dispatch('billing/setChargeTableState', value);
      }
    },
    methods: {
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
      onClickChargeListCell(params) {
        this.$emit('clickChargeListCell', params);
      }
    }
  };
</script>
<style lang="scss">
  $ag-range-selected-border: #1772FF;
  $ag-range-selected-color-1: #F2F8FF;
  .billing-detail-table {
    border-radius: 4px;
    .billing-detail-table-height {
      height: 188px;
    }
    .ag-pinned-left-header {
      .ag-header-row {
        top: 25px !important;
        height: 35px !important;
      }
    }

    .ag-pinned-right-header {
      .ag-header-row {
        top: 25px !important;
        height: 35px !important;
      }
    }
    .charge-list-table {
      .first-cell {
        border-left: 5px solid #1772FF !important;
      }

      .age-warning {
        background-color: orangered !important;
      }

      .age-error {
        background-color: indianred !important;
      }


      .td-triangle {
        position: relative;
      }

      .triangle {
        position: absolute;
        right: -19px;
        bottom: -3px;
        display: inline-block;
      }

      .triangle-0 {
        border-right: solid 15px #0672FF;
        border-top: solid 15px transparent;
      }
    }
  }
  .ag-row {
    .show-tooltip-cell {
      cursor: default;
    }
    .highlight {
      background-color: $ag-range-selected-color-1 !important;
      &.first {
        border-left: 2px solid $ag-range-selected-border !important;
      }
      &.last {
        border-right: 2px solid $ag-range-selected-border !important;
      }
    }
    .ag-cell {
      color: #222222;
      &.ag-cell-range-selected {
        //Overwrite for background of ag cell focus
        &.ag-cell-focus {
          &:not(.highlight) {
            background-color: white;
          }
        }
      }
      &.ag-cell-range-selected-1 {
        &:not(.highlight) {
          background-color: white !important;
        }
      }
    }
    .highlight {
      background-color: $ag-range-selected-color-1 !important;
      &.first {
        border-left: 2px solid $ag-range-selected-border !important;
      }
      &.last {
        border-right: 2px solid $ag-range-selected-border !important;
      }
    }
  }
</style>
