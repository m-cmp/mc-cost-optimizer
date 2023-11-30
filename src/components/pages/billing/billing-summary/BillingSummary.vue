<template>
  <b-col class="px-0 billing-summary-wrapper">
    <b-card
      class="card-custom"
      border-variant="transparent"
      header-border-variant="lightgray-1"
      header-bg-variant="transparent"
      footer-border-variant="transparent"
      footer-bg-variant="transparent">
      <b-row
        slot="header"
        align-h="between"
        align-v="center"
        no-gutters
        class="px-20 py-2">
        <p class="font-16 medium margin-left-7px font-family-notosanscjkkr-medium text-darkgray-1">{{ $t('billing.billingSummary.billingSummary') }}
          <base-material
            v-b-tooltip.html="$t('header.azureBillingInvoiceGuide')"
            v-if="selectedVendor == 'AZURE'"
            :size="12"
            class="representative-exchange-rate"
            color="gray-1"
            name="info"/>
        </p>
      </b-row>
      <b-col class="mt-8">
        <b-row
          class="billing-month-year-dropdown"
          no-gutters
          align-v="center">
          <month-year-dropdown
            :options="options"
            :current-month-option="defaultMonthToDateOption"
            :default-selected-option="currentMonthLocalization"
            :option-index="selectedOptionIndex"
            @selectedOption="onSelectedOption"
          />
          <b-badge
            v-if="selectedVendor == 'AWS'"
            :class="{'confirmed-month': isConfirmedMonth}"
            class="custom-badge">
            <span
              class="confirmed-month-text text-gray-3">
              {{ isConfirmedMonth ? $t('billing.billingSummary.confirmed') : $t('billing.billingSummary.unconfirmed') }}
            </span>
          </b-badge>
          <b-col cols="12">
            <p class="font-12 text-paging-btn">{{ billingSummaryData.startDate }} – {{ billingSummaryData.endDate }}</p>
          </b-col>
        </b-row>
      </b-col>
      <b-col class="mb-16">
        <b-row
          no-gutters
          align-v="center"
          align-h="end"
          class="mt-8 base-font-special">
          <base-material
            v-if="billingSummaryData.increaseDecreaseRate == 0"
            :size="20"
            color="gray-1"
            name="remove"/>
          <base-material
            v-if="billingSummaryData.increaseDecreaseRate < 0"
            :size="20"
            color="blue-1"
            name="arrow_drop_down"/>
          <base-material
            v-if="billingSummaryData.increaseDecreaseRate > 0"
            :size="20"
            color="red-1"
            name="arrow_drop_up"/>
          <p class="font-12 text-black-1">{{ absNumber(billingSummaryData.increaseDecreaseRate) }}%</p>
          <p
            class="font-24 ml-10 text-black-1 total-charge">{{ CURRENCY_SYMBOL[billingSummaryData.companyCurrency] }}{{ isCompanyCurrencyUSD ? formatCostValue(billingSummaryData.totalCharge, 'cloudServiceChargeByUSD') : formatCostValue(billingSummaryData.totalCharge, 'totalCharge') }}</p>

        </b-row>
      </b-col>
      <b-col :class="`px-10 overview-background ${additionalServiceFees.length === 0 ? 'pb-8' : ''}`">
        <b-row
          align-h="between"
          no-gutters
          class="border-bottom font-weight-bold border-gray-2 py-8 pr-10">
          <b-row
            align-v="center"
            no-gutters>
            <b-button
              variant="transparent"
              class="only-button padding-left2px font-family-notosanscjkkr-Bold"/>
            <p class="text-darkgray-1 font-family-notosanscjkkr-Bold">{{ $t('billing.billingSummary.cloudServiceCharges') }}</p>
          </b-row>
          <b-row>
            <p
              :class="billingSummaryData.invoiceCurrency == billingSummaryData.companyCurrency ? 'cloud-service-charge-by-usd' : 'cloud-service-change-by-no-usd'"
              class="base-font-special text-black-1 font-family-notosanscjkkr-regular">
              {{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.cloudServiceCharge, 'cloudCost') }}
            </p>
            <div
              v-show="billingSummaryData.invoiceCurrency != billingSummaryData.companyCurrency"
              id="bill-summary-exchange-button"
              class="exchange_icon"
            />
          </b-row>
        </b-row>
        <b-col
          class="pr-10 cloud-service-charges-wrapper">
          <b-row
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p class="text-paging-btn">{{ $t('billing.billingSummary.cloudCost') }}
              <base-material
                v-show="canShowOriginalCost(billingSummaryData.onDemandDiscount, billingSummaryData.cloudFrontDiscount)"
                id="tooltip-cloud-cost"
                :size="12"
                color="gray-1"
                name="info"/>
            </p>
            <p class="base-font-special text-paging-btn">{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.cloudCost, 'cloudCost') }}</p>
          </b-row>
          <b-row
            v-if="selectedVendor === 'AWS' || selectedVendor === 'AZURE'"
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p class="text-paging-btn">{{ $t('billing.billingSummary.supportFee') }}</p>
            <p class="base-font-special text-paging-btn">{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.supportFee, 'supportFee') }}</p>
          </b-row>
          <b-row
            v-if="selectedVendor === 'NCP'"
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p class="text-paging-btn">{{ $t('billing.billingSummary.ncpDiscount') }}</p>
            <p class="base-font-special text-paging-btn"><span v-if="billingSummaryData.ncpDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.ncpDiscount, 'ncpDiscount') }}</p>
          </b-row>
          <b-row
            v-if="selectedVendor === 'AWS' || selectedVendor === 'NCP' || selectedVendor === 'GCP' || selectedVendor === 'OCI' || selectedVendor === 'AZURE'"
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p class="text-paging-btn">{{ $t('billing.billingSummary.salesDiscount') }}</p>
            <p class="base-font-special text-paging-btn"><span v-if="billingSummaryData.salesDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.salesDiscount, 'salesDiscount') }}</p>
          </b-row>
          <b-row
            v-if="selectedVendor === 'AWS' || selectedVendor === 'NCP' || selectedVendor === 'GCP' || selectedVendor === 'AZURE'"
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p class="text-paging-btn">{{ $t('billing.billingSummary.credit') }}</p>
            <p class="base-font-special text-paging-btn"><span v-if="typeof billingSummaryData.credit !=='undefined' && billingSummaryData.credit !== 0">-</span>{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.credit, 'credit') }}</p>
          </b-row>
          <b-row
            v-if="false"
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p class="text-paging-btn">VAT</p>
            <p class="base-font-special text-paging-btn">{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.vatCost, 'vatCost') }}</p>
          </b-row>

        </b-col>
        <b-row
          align-h="between"
          no-gutters
          class="border-bottom border-gray-2 py-8 pr-10 font-weight-bold">
          <b-row
            align-v="center"
            no-gutters>
            <b-button
              variant="transparent"
              class="only-button padding-left2px font-family-notosanscjkkr-Bold"/>
            <p class="text-darkgray-1 font-family-notosanscjkkr-Bold">{{ $t('billing.billingSummary.additionalServiceFee') }}</p>
          </b-row>
          <p class="base-font-special text-black-1">{{ CURRENCY_SYMBOL[billingSummaryData.companyCurrency] }}{{ formatCostValue(additionalServiceSum, 'additionalServiceFee') }}</p>
        </b-row>
        <b-col
          class="pr-10 additional-service-fees-wrapper">
          <b-row
            v-for="(additionalServiceFee, index) in additionalServiceFees"
            :key="index"
            align-h="between"
            no-gutters
            class="pt-8 pb-8">
            <p
              :title="additionalServiceFee.additionalServiceName"
              class="text-paging-btn truncate-text">{{ additionalServiceFee.additionalServiceName }}</p>
            <p class="base-font-special text-paging-btn"><span v-if="additionalServiceFee.additionalServiceCharge < 0">-</span>{{ CURRENCY_SYMBOL[billingSummaryData.companyCurrency] }}{{ formatCostValue(additionalServiceFee.additionalServiceCharge, 'additionalServiceFee') }}</p>
          </b-row>
        </b-col>
      </b-col>
      <b-row
        v-if="billingSummaryData.invoiceCurrency != billingSummaryData.companyCurrency"
        slot="footer"
        class="px-20 py-8">
        <b-col
          cols="12"
          class="margin-left-7px">
          <p class="text-paging-btn"> {{ $t("billing.billingSummary.representativeExchangeRate") }}</p>
          <p
            v-if="vatYn === 'N'"
            class="text-paging-btn"
          > {{ $t('billing.billingSummary.vatExcluded') }}</p>
          <p
            v-if="vatYn === 'Y'"
            class="text-paging-btn"
          > {{ $t('billing.billingSummary.vatIncluded') }}</p>
          <p class="text-paging-btn"> {{ $t('billing.billingSummary.lastUpdated') }}: {{ lastUpdatedBillingSummary }} (UTC)</p>
          <p
            v-if="selectedVendor !== 'NCP' && selectedVendor !== 'TENCENT'"
            class="text-paging-btn"
          > {{ $t('billing.billingSummary.exchangeRate') }}: 1 {{ CURRENCY.USD }} = {{ formatCostValue(billingSummaryData.exchangeRate) }} {{ CURRENCY.KRW }}
            <base-material
              v-b-tooltip.html="representativeExchangeRate"
              :size="12"
              class="representative-exchange-rate"
              color="gray-1"
              name="info"/>
          </p>
        </b-col>
      </b-row>
    </b-card>
    <b-tooltip target="bill-summary-exchange-button">{{ CURRENCY_SYMBOL[billingSummaryData.companyCurrency] }}{{ formatCostValue(billingSummaryData.exchangedCloudServiceCharge, 'exchangedCloudServiceCharge') }}</b-tooltip>
    <b-tooltip
      target="tooltip-cloud-cost"
      placement="top"
      container="hello">
      <b-row
        no-gutters
        align-h="between"
        class="cloud-original-cost pb-2">
        <p>{{ $t('billing.billingSummary.cloudOriginalCost') }}</p>
        <p class="ml-10">{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.cloudCost + billingSummaryData.cloudFrontDiscount + billingSummaryData.onDemandDiscount, 'cloudCost') }}</p>
      </b-row>
      <b-row
        v-if="selectedVendor === 'AWS'"
        v-show="canShowCostByValue(billingSummaryData.onDemandDiscount)"
        no-gutters
        align-h="between"
        class="pb-2">
        <p>{{ $t('billing.billingSummary.onDemandDiscount') }}</p>
        <p class=" ml-10"><span v-if="billingSummaryData.onDemandDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(absNumber(billingSummaryData.onDemandDiscount), 'cloudCost') }}</p>
      </b-row>
      <b-row
        v-if="selectedVendor === 'AWS'"
        v-show="canShowCostByValue(billingSummaryData.cloudFrontDiscount)"
        no-gutters
        align-h="between"
        class="pb-2">
        <p>{{ $t('billing.billingSummary.cFDiscount') }}</p>
        <p class=" ml-10"><span v-if="billingSummaryData.cloudFrontDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(absNumber(billingSummaryData.cloudFrontDiscount), 'cloudCost') }}</p>
      </b-row>
      <b-row
        :class="[canShowOriginalCost(billingSummaryData.onDemandDiscount, billingSummaryData.cloudFrontDiscount) ? 'border-top pt-2' : '']"
        no-gutters
        align-h="between"
        class="cloud-cost"
      >
        <p>{{ $t('billing.billingSummary.cloudCost') }}</p>
        <p class=" ml-10"> {{ CURRENCY_SYMBOL[billingSummaryData.invoiceCurrency] }}{{ formatCostValue(billingSummaryData.cloudCost, 'cloudCost') }}</p>
      </b-row>
    </b-tooltip>
  </b-col>
</template>

<script>
import MonthYearDropdown from '@/components/common/MonthYearDropdown';
import {getReverseArrIndex, prepareBillSummaryData, prepareMonthToDateOptions} from '@/util/billingUtils';
import {getCurrencySymbolByField} from '@/util/currencyConfigs';
import {CURRENCY, CURRENCY_SYMBOL, KRW_CURRENCY_FIELDS, VENDOR} from '@/constants/constants';

import _isNil from 'lodash/isNil';
import _isEmpty from 'lodash/isEmpty';
import _get from 'lodash/get';
import {formatCost} from '@/util/costUtils';
import dayjs from 'dayjs';
import {getFullDateFormatByLocalization, getMonthYearDateFormatByLocalization} from "@/util/dateTimeUtils";
import {formatCostValue} from "../../../../util/billingUtils";

const BILL_SUMMARY_HOTSPOT = 'bill-summary-hotspot';
  const BILLING_CONFIRM_STATE = {
    YES: 'Y',
    NO: 'N'
  };

  export default {
    name: 'BillingSummary',
    components: {
      MonthYearDropdown,
    },
    props: {
      billList: {
        type: Array,
        required: true,
      },
      activeMonthIdx: {
        type: Number,
        required: true
      },
      currentStep: {
        type: Number,
        required: true
      },
      selectedVendor: {
        type: String,
        default() {
          return VENDOR.AWS
        }
      }
    },
    data() {
      return {
        isCompanyCurrencyUSD: false,
        defaultMonthToDateOption: '',
        CURRENCY_SYMBOL: CURRENCY_SYMBOL,
        CURRENCY: CURRENCY,
        additionalServiceFees: [],
        additionalServiceSum: 0,
        BILL_SUMMARY_HOTSPOT: BILL_SUMMARY_HOTSPOT
      }
    },
    computed: {
      selectedOptionIndex: function () {
        //reverse options for push current month on top of dropdown
        return getReverseArrIndex(this.billList.length, this.activeMonthIdx);
      },
      options: function () {
        if (_isEmpty(this.billList)) {
          return [dayjs().format(getMonthYearDateFormatByLocalization())];
        }
        return prepareMonthToDateOptions(this.billList, this.defaultMonthToDateOption, this.$t("billing.billingSummary.currentMonth"));
      },
      billingDetail: function () {
        return prepareBillSummaryData(this.activeMonthIdx, this.billList)
      },
      isConfirmedMonth: function () {
        if (!_isNil(this.billingDetail)) {
          return this.billingDetail.billConfirmationYn === BILLING_CONFIRM_STATE.YES;
        }
      },
      billingSummaryData: function () {
        const data = {};
        if (!_isNil(this.billingDetail)) {
          data.cloudServiceCharge = this.billingDetail.cloudServiceCharge;
          data.cloudCost = this.billingDetail.cloudUseOriginalCost;
          data.supportFee = this.billingDetail.supportFee;
          data.salesDiscount = this.billingDetail.salesDiscount < 0 ? Math.abs(this.billingDetail.salesDiscount) : this.billingDetail.salesDiscount;
          data.ncpDiscount = this.billingDetail.ncpDiscount < 0 ? Math.abs(this.billingDetail.ncpDiscount) : this.billingDetail.ncpDiscount;
          data.credit = this.billingDetail.credit < 0 ? Math.abs(this.billingDetail.credit) : this.billingDetail.credit;

          if (!_isNil(this.billingDetail.additionalServices)) {
            data.MCMPUsageCharge = !_isNil(this.billingDetail.additionalServices[0]) ? this.billingDetail.additionalServices[0].additionalServiceCharge : 0;
            data.autospotUsageCharge = !_isNil(this.billingDetail.additionalServices[1]) ? this.billingDetail.additionalServices[1].additionalServiceCharge : 0;
            data.additionalService1Fee = !_isNil(this.billingDetail.additionalServices[2]) ? this.billingDetail.additionalServices[2].additionalServiceCharge : 0;
          } else {
            data.MCMPUsageCharge = 0;
            data.autospotUsageCharge = 0;
            data.additionalService1Fee = 0;
          }

          data.additionalServiceFee = data.MCMPUsageCharge + data.autospotUsageCharge + data.additionalService1Fee;
          data.exchangeRate = this.billingDetail.applyExchangeRate ? this.billingDetail.applyExchangeRate.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,') : 0;
          data.increaseDecreaseRate = this.billingDetail.increaseDecreaseRate;
          data.totalCharge = this.billingDetail.totalCharge;// ? this.billingDetail.totalCharge.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,') : 0;
          data.cloudOriginalCost = this.billingDetail.cloudOriginalCost;
          data.cloudFrontDiscount = this.billingDetail.cloudFrontDiscount;
          data.onDemandDiscount = this.billingDetail.onDemandDiscount;
          data.startDate = this.billingDetail.startDate ? dayjs.utc(this.billingDetail.startDate).format(getFullDateFormatByLocalization()) : this.$t("billing.billingSummary.invalidDate");
          data.endDate = this.billingDetail.endDate ? dayjs.utc(this.billingDetail.endDate).format(getFullDateFormatByLocalization()) : this.$t("billing.billingSummary.invalidDate");
          data.exchangedCloudServiceCharge = this.billingDetail.exchangedCloudServiceCharge;
          data.lastUpdated = this.billingDetail.lastBillUpdateDate ? this.billingDetail.lastBillUpdateDate : this.$t("billing.billingSummary.invalidDate");

          data.invoiceCurrency = this.billingDetail.invoiceCurrency;
          data.companyCurrency = this.billingDetail.companyCurrency;
          data.vatCost = this.billingDetail.vatCost;
        } else {
          data.startDate = dayjs().startOf('month').format(getFullDateFormatByLocalization());
          data.endDate = dayjs().format(getFullDateFormatByLocalization());
        }
        return data;
      },
      currentMonthLocalization: function () {
        return this.$t("billing.billingSummary.currentMonth");
      },
      representativeExchangeRate: function () {
        let applyExchangeRateDate = dayjs.utc(_get(this.billingDetail, 'applyExchangeRateDate')).format(getFullDateFormatByLocalization());
        if(this.isConfirmedMonth) {
          return `
            <div class="txt-left exchange-rate-tooltip">
              <p>${this.$t("billing.billingSummary.reportedExchangeRateTime", {'applyExchangeRateDate': applyExchangeRateDate})}</p>

              <p>${this.$t("header.currencyTooltip.provider#1")}</p>
              <p>${this.$t("header.currencyTooltip.provider#2")}</p>
              <p>${this.$t("header.currencyTooltip.provider#3")}</p>
            </div>
          `;
        } else {
          return `
            <div class="txt-left exchange-rate-tooltip">
              <p>${this.$t("billing.billingSummary.actualChargeCanBeChanged")}</p>
              <p>${this.$t("billing.billingSummary.currentExchangeRateTime", {'applyExchangeRateDate': applyExchangeRateDate})}</p>

              <p>${this.$t("header.currencyTooltip.provider#1")}</p>
              <p>${this.$t("header.currencyTooltip.provider#2")}</p>
              <p>${this.$t("header.currencyTooltip.provider#3")}</p>
            </div>
          `;
        }
      },
      lastUpdatedBillingSummary() {
        if (_isNil(this.billingSummaryData.lastUpdated)) {
          return;
        }
        return dayjs.utc(this.billingSummaryData.lastUpdated).format(getFullDateFormatByLocalization())
      },
      vatYn: function() {
        let billingDetail = this.billList[this.activeMonthIdx];
        if(billingDetail) {
          return billingDetail.vatYn;
        }
        return 'N';

      }
    },
    watch: {
      'billingSummaryData.companyCurrency' : {
        handler() {
          this.isCompanyCurrencyUSD = _.isEqual("USD", this.billingSummaryData.companyCurrency);
        }
      },
      billingDetail: function() {
        this.additionalServiceFees = _get(this.billingDetail, 'additionalServices', []);
        this.additionalServiceSum = _get(this.billingDetail, 'additionalServiceCharge');
      }
    },
    mounted() {
      let dt = new Date();
      let month = dt.getMonth() + 1;
      if (month < 10) {
        month = '0' + month;
      }
      this.defaultMonthToDateOption = dt.getFullYear() + '-' + month;
    },
    methods: {
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
      onSelectedOption(option) {
        this.$emit('setActiveMonthIdx', getReverseArrIndex(this.billList.length, option.idx));
      },
      getCurrencySymbolByField: function(field) {
        return getCurrencySymbolByField(field);
      },
      absNumber(number) {
        return (_isNil(number) || number === 0) ? '0.00' : formatCost(Math.abs(number), {mantissa: 2});
      },
      formatCostValue(cost, config) {
          //return formatCostValue(cost, config, this.billingSummaryData.invoiceCurrency, this.selectedVendor);
          if(KRW_CURRENCY_FIELDS.includes(config) && _.isEqual(CURRENCY.KRW, this.billingSummaryData.companyCurrency)) { //원화일 경우만 소수점 버림
            return formatCostValue(cost, config, this.billingSummaryData.companyCurrency, this.selectedVendor);
          }else if(!KRW_CURRENCY_FIELDS.includes(config) && !_.isEqual(CURRENCY.KRW, this.billingSummaryData.invoiceCurrency)){
            return formatCostValue(cost, config, this.billingSummaryData.invoiceCurrency, this.selectedVendor)
          }else if(!KRW_CURRENCY_FIELDS.includes(config) && _.isEqual(CURRENCY.KRW, this.billingSummaryData.companyCurrency)){
            return formatCostValue(cost, config, this.billingSummaryData.companyCurrency, this.selectedVendor)
          }else{
            if(cost || cost === 0){
              if(typeof cost === 'string'){
                return cost
              }else{
                return  Number(cost.toFixed(2)).toLocaleString('ko');
              }
            }
          }


      },
      customCostValue(cost, currency) {
        if(_.isEqual(CURRENCY.KRW, currency)) {
          return formatCost(cost, {mantissa: 0})
        } else {
          return formatCost(cost, {mantissa: 2})
        }
      },
      canShowOriginalCost(onDemandDiscount, cloudFrontDiscount) {
        if ((onDemandDiscount < 0 || onDemandDiscount > 0 || cloudFrontDiscount < 0 || cloudFrontDiscount > 0) && this.selectedVendor=='AWS') {
          return true
        }
        return false
      },
      canShowCostByValue(costValue) {
        if (costValue < 0 || costValue > 0) {
          return true
        }
        return false
      }
    },
  }
  ;
</script>
<style lang="scss" scoped>
  .billing-summary-wrapper {
    #tooltip-cloud-cost, .representative-exchange-rate {
      cursor: default;
      position: relative;
      top:1px
    }
    .total-charge {
      margin-right: 4px;
    }
    .cloud-service-charge-by-usd {
      margin-right: 15px;
    }
    .cloud-service-change-by-no-usd {
      margin-right: 34px;
    }
    .cloud-service-charges-wrapper {
      padding-left: 2px;
    }
    .additional-service-fees-wrapper {
      padding-left: 2px;
    }
    .cloud-original-cost {
      padding-top: 3px;
    }
    .cloud-cost {
      padding-bottom: 3px;
    }
  }
  .base-hotspot-container {
    &.in-bill-summary {
      left: 90px;
      top: 6px;
    }
  }
  .overview-background {
    background: #F2F4F6 !important;
    .truncate-text {
      text-overflow: ellipsis;
      width: 188px;
      white-space: nowrap;
      overflow: hidden;
    }
  }

  .wrapper-billing-layout {
    .billing-month-year-dropdown {
      .badge-secondary {
        &.confirmed-month {
          background: rgba(173, 222, 255, 0.32)!important;
          .confirmed-month-text {
            color: #0672FF!important;
          }
        }
      }
    }
  }

  .font-weight-bold {
    font-weight: bold;
  }

  .margin-left-7px {
    margin-left: -7px;
  }

  .padding-left2px {
    padding-left: 2px;
  }

  .tooltip {
    .tooltip-inner {
      border-radius: 3px !important;
    }
  }

  .exchange_icon {
    background:url(/static/svg/icon_currency_swap.svg) no-repeat;
    width: 16px;
    height: 16px;
    position: absolute;
    top: 10px;
    right: 17px;
  }

  .billing-month-year-dropdown {
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
