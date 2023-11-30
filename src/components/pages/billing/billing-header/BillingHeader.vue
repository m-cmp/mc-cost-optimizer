<template>
  <b-container
    id="billing-header-wrapper"
    class="billing-header-class"
    fluid>
    <fragment>
      <mcmp-base-header
        @selectUserFilter="onChangeUserFilter"
      />
      <base-title
        :class="isSidebarActive ? 'sidebar-active': ''"
        class="base-title"
      >
        <b-row
          v-if="isBillingChargeDetailVisible"
          class="billing-charge-detail-title"
          no-gutters
        >
          <b-button
            variant="transparent"
            class="btn btn-primary custom-back-button"
            @click="onClickBackBtn"
          >
            <base-material
              :size="14"
              class="custom-arrow-back-icon"
              name="arrow_back"
              color="gray-1"
            />
            {{ $t('billing.billingChargeDetail.backBtn') }}
          </b-button>
          <p class="billing-charge-detail-title-text font-family-notosanscjkkr-Bold">{{ $t('billing.billingChargeDetail.title') }}</p>
        </b-row>
        <b-row
          v-else
          no-gutters
        >
          <p class="billing-title font-family-notosanscjkkr-Bold">{{ $t('header.billingTitle') }}</p>
        </b-row>
        <b-row
          no-gutters
          class="billing-vendor-dropdown">
          <span class="margin-right-displaying font-family-notosanscjkkr-medium">{{ $t('header.displaying') }}:</span>
          <vendor-dropdown
            :options="allVendors"
            :default-selected-option="selectedVendor"
            :current-vendor-option="selectedVendor"
            :label="$t('billing.billingChargeDetail.chargeAccountList.chargeList')"
            :style="marginLeftByVendor"
            variant="primary"
            class="vendor-dropdown"
            @selectedOption="onSelectedOption"
          />

          <span class="margin-right-currency margin-left-currency-billing font-family-notosanscjkkr-medium">{{ $t('header.currency') }}: </span>
          <span
            id="billing-currency-tooltip"
            class="custom-currency-billing-header font-family-notosanscjkkr-medium"
          >
            {{ $t('header.fixed') }} ({{ displayCurrency.companyCurrency }} {{ displayCurrency.currencySymbol }})
          </span>
          <b-tooltip
            v-if="displayCurrency.companyCurrency !== displayCurrency.invoiceCurrency"
            placement="bottom"
            target="billing-currency-tooltip"
          >
            <div v-html="getCurrencyTooltip()"/>
          </b-tooltip>
        </b-row>
      </base-title>
    </fragment>
  </b-container>
</template>
<script>
  import {CURRENCY, CURRENCY_SYMBOL, DEFAULT_VENDOR_OPTIONS } from '@/constants/constants';
  import {VENDOR_NAMES} from "@/constants/billingConstants";
  import _get from 'lodash/get';
  import _isNil from 'lodash/isNil';
  import { formatCost } from '@/util/costUtils';
  import { getReverseArrIndex, prepareBillSummaryData, prepareMonthToDateOptions } from '@/util/billingUtils';
  // import BaseHeader from '@/components/common/base-header/BaseHeader';
  import McmpBaseHeader from '@/components/common/base-header/McmpBaseHeader';
  import { getFullDateFormatByLocalization } from '@/util/dateTimeUtils';
  import dayjs from 'dayjs';
  import BaseDropdown from '../../../common/BaseDropdown';
  import VendorDropdown from '../../../common/VendorDropdown';
  import _isEmpty from "lodash/isEmpty";

  export default {
    name: 'BillingHeader',
    components: {
      VendorDropdown,
      BaseDropdown,
      // BaseHeader,
      McmpBaseHeader,
    },
    props: {
      allVendors: {
        type: Array,
        default() {
          return []
        }
      },
      billList: {
        type: Array,
        required: true
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      activeMonthIdx: {
        type: Number,
        required: true
      },
      selectedVendor: {
        type: [String, Object],
        required: true
      },
      isSidebarActive: {
        type: Boolean,
        default: false
      },
      isBillingChargeDetailVisible: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        CURRENCY: CURRENCY,
        CURRENCY_SYMBOL: CURRENCY_SYMBOL
      }
    },
    computed: {
      // vendorOptions() {
      //   return DEFAULT_VENDOR_OPTIONS.filter(option => this.allVendors.includes(option.value) && !option.disabled);
      // },
      displayCurrency() {
        let billingDetail = this.billList[this.activeMonthIdx];
        if(_isNil(billingDetail)) {
          return {
            currency: '',
            currencySymbol: ''
          }
        }
        return {
          companyCurrency: billingDetail.companyCurrency,
          currencySymbol: CURRENCY_SYMBOL[billingDetail.companyCurrency],
          invoiceCurrency: billingDetail.invoiceCurrency
        }
      },
      isCompanyCurrencyUSD(){
        let billingDetail = this.billList[this.activeMonthIdx];
        if(_isNil(billingDetail)) {
          return {
            currency: '',
            currencySymbol: ''
          }
        }
        return billingDetail.companyCurrency !== CURRENCY.USD;
      },
      marginLeftByVendor() {
        return {
          '--margin-left-by-vendor': this.selectedVendor.length>=7 ?'-30px':'-13px'
        }
      }
    },
    watch: {
      // selectedVendor: {
      //   handler() {
      //     this.$store.dispatch('billing/setSelectedVendor', this.selectedVendor);
      //   },
      //   immediate: true,
      // }
    },
    methods: {
      onSelectedOption(option) {
        this.$emit('setSelectedVendor', option.value);
      },
      onChangeUserFilter(userFilterIdx) {
        this.$emit('changeUserFilter', userFilterIdx)
      },
      getCurrencyTooltip() {
        //let applyExchangeRateDate = this.$dayjs(this.commonUserInfo.exchangeRateDate).format(getFullDateFormatByLocalization())
        let applyExchangeRateDate = dayjs.utc(_get(this.billList[this.activeMonthIdx], 'applyExchangeRateDate')).format(getFullDateFormatByLocalization());
        const activeMonthBill = this.billList[this.activeMonthIdx]
        return `
          <div class="txt-left">
            <p class="currency-tooltip">
              1 ${_get(activeMonthBill, 'invoiceCurrency')}<span class="ml-1 mr-1">=</span>${formatCost(_get(activeMonthBill, 'applyExchangeRate'), {mantissa: 2})} ${_get(activeMonthBill, 'companyCurrency')}
            </p>
            <div class="billing-currency-tooltip">
              <p>${this.$t("header.currencyTooltip.actualChargeCanBeChanged")}</p>
              <p>${this.$t("header.currencyTooltip.currentExchangeRateTime", {'applyExchangeRateDate': applyExchangeRateDate})}</p>
              <p>${this.$t("header.currencyTooltip.provider#1")}</p>
              <p>${this.$t("header.currencyTooltip.provider#2")}</p>
              <p>${this.$t("header.currencyTooltip.provider#3")}</p>
            </div>
          </div>
        `;
      },
      onClickBackBtn() {
        this.$emit('clickBackBtn');
      },
    }
  };
</script>
<style lang="scss">
  .currency-tooltip {
    font-size: 14px;
    padding: 0 3.2px
  }
  #billing-header-wrapper {
    padding: 6.875rem 0.625rem 0 3.75rem;
    min-width: 1024px;
    max-width: 1440px;
  }
  #title {
    .custom-radio {
      .custom-control-label::before  {
        border: #b0b7bf solid 1px;
      }
    }
  }
  .base-title {
    .billing-charge-detail-title-text {
      font-weight: bold;
      font-size: 14px !important;
      color: #222222 !important;
    }
    .billing-charge-detail-title {
      display: block;
      padding-left: 6px;
      .custom-back-button {
        color: #0672FF;
        padding: 0;
        font-family: "NotoSansCJKkr-Medium", "Apple SD Gothic", sans-serif;
        &:focus {
          color: #0672FF;
        }
        .custom-arrow-back-icon {
          margin-right: 2px;
          margin-top: 3px;
        }
      }
    }
    &.sidebar-active {
      padding-left: 300px !important;
    }
    .billing-title {
      font-weight: bold;
      font-size: 16px;
      color: #222222;
    }
    .billing-vendor-dropdown {
      .margin-right-currency {
        margin-right: 4px;
        color: #222;
      }
      .custom-currency-billing-header {
        color: #0672ff;
        cursor: default;
      }
    }
    .row-radio-group-displaying {
      align-items: center;
      justify-content: center;
      flex-wrap: nowrap;
      .radio-group-displaying {
        height: 19px;
        .custom-control-inline {
          padding-left: 0;
        }
        [type="radio"]:checked,
        [type="radio"]:not(:checked) {
          position: absolute;
          left: -9999px;
        }
        [type="radio"]:checked + label,
        [type="radio"]:not(:checked) + label
        {
          position: relative;
          padding-left: 20px;
          cursor: pointer;
          line-height: 20px;
          display: inline-block;
          color: #222222;
        }
        [type="radio"]:checked + label:before,
        [type="radio"]:not(:checked) + label:before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          width: 12px;
          height: 12px;
          border-radius: 100%;
          background: #ffffff;
          margin-top: 4px;
        }
        [type="radio"]:checked + label:before {
          background: #0672ff;
        }
        [type="radio"]:checked + label:after,
        [type="radio"]:not(:checked) + label:after {
          content: '';
          width: 4px;
          height: 4px;
          background: #ffffff;
          position: absolute;
          top: 8px;
          left: 4px;
          border-radius: 100%;
          -webkit-transition: all 0.2s ease;
          transition: all 0.2s ease;
        }
        [type="radio"]:not(:checked) + label:after {
          opacity: 0;
          -webkit-transform: scale(0);
          transform: scale(0);
        }
        [type="radio"]:checked + label:after {
          opacity: 1;
          -webkit-transform: scale(1);
          transform: scale(1);
        }
      }
      .margin-right-displaying {
        margin-right: 8px;
        color: #222222;
      }
      .margin-right-currency {
        margin-right: 4px;
        color: #222222;
      }
      .custom-currency-billing-header {
        color: #0672ff;
        cursor: default;
      }
    }
  }

  @media only screen and (max-width: 1024px) {
    #app {
      #wrapper {
        &.wrapper-billing-layout {
          #billing-header-wrapper {
            /*padding: 0px;*/
            &.billing-header-class {
              min-width: 1024px !important;
              /*min-width: 100%;*/
              #header {
                position: fixed!important;
                top: -2px !important;
                left: 0px;
                width: auto;
              }
              #title {
                top: 38px;
                padding-left: 4.375rem !important;
              }
            }
          }
        }
      }
    }
  }
  @media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
    .margin-left-currency-billing {
      margin-left: 60px
    }
  }
  .tooltip-inner {
    .billing-currency-tooltip {
      font-size: 12px;
      padding-top: 8px;
    }
  }
  .billing-vendor-dropdown {
    align-items: center;
    justify-items: center;
    .vendor-dropdown {
      padding:0 0.5rem;
    }
    #vendor-dropdown {
      padding-left: 18px;
      margin-right: 10px;
      button {
        top: -2px;
        font-size: 12px !important;
      }
    }
    .vendor-dropdown-wrapper .primary .dropdown-toggle::after {
      font-family: 'NotoSansCJKkr-Bold', "Apple SD Gothic", sans-serif;
      font-size: 14px !important;
      color: #222222 !important;
    }
    .custom-badge {
      background-color: #e9ebf5;
      margin-left: 4px;
      border-radius: 4px;
      margin-top: 7px;
    }
    .confirmed-month-text {
      color: #6c7994;
      //font-size: 11px;
      font-weight: 500;
    }
  }
</style>
