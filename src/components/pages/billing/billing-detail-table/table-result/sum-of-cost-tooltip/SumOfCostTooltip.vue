<template>
  <div
    v-show="canShowCloudServiceChargeToolTip || canShowAdditionalServiceFeeToolTip"
    class="custom-tooltip">
    <div class="custom-tooltip-inner">
      <div v-if="canShowCloudServiceChargeToolTip">
        <b-row
          no-gutters
          align-h="between"
          class="content-item">
          <p>{{ $t('billing.chargeList.cloudCost') }}</p>
          <p class=" ml-10 montserrat-r">{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(cloudCost, null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-show="canShowOriginalCost(onDemandDiscount, cloudFrontDiscount)"
          no-gutters
          align-h="between"
          class="gray-title content-item">
          <p>- {{ $t('billing.chargeList.cloudOriginalCost') }}</p>
          <p class=" ml-10 montserrat-r">{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(cloudOriginalCost, null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-show="canShowCostByValue(onDemandDiscount)"
          v-if="selectedVendor === 'AWS'"
          no-gutters
          align-h="between"
          class="gray-title content-item">
          <p>- {{ $t('billing.chargeList.onDemandDiscount') }}</p>
          <p class=" ml-10 montserrat-r"><span v-if="onDemandDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(Math.abs(onDemandDiscount), null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-show="canShowCostByValue(cloudFrontDiscount)"
          v-if="selectedVendor === 'AWS'"
          no-gutters
          align-h="between"
          class="gray-title content-item">
          <p>- {{ $t('billing.chargeList.cFDiscount') }}</p>
          <p class=" ml-10 montserrat-r"><span v-if="cloudFrontDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(Math.abs(cloudFrontDiscount), null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-if="selectedVendor === 'AWS' || selectedVendor === 'AZURE'"
          no-gutters
          align-h="between"
          class="content-item">
          <p>{{ $t('billing.chargeList.supportFee') }}
            <!--            <span v-if="supportFeeApplyType === APPLY_TYPE.RATIO">-->
            <!--              ({{ $t('billing.chargeList.discount') }} {{ `${supportFeeApplyValue}%` }})-->
            <!--            </span>-->
            <!--            <span v-else-if="supportFeeApplyType === APPLY_TYPE.STATIC">-->
            <!--              ({{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ supportFeeApplyValue }})-->
            <!--            </span>-->
            <!--            <span v-else-if="supportFeeApplyType === APPLY_TYPE.INTERVAL">-->
            <!--              ({{ $t('billing.chargeList.usageSection') }})-->
            <!--            </span>-->
          </p>
          <p class=" ml-10 montserrat-r">{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ customFormatCostValue(supportFee, null, invoiceCurrency) }}</p>
        </b-row>
        <b-row
          v-if="selectedVendor === 'NCP'"
          no-gutters
          align-h="between"
          class="content-item">
          <p>{{ $t('billing.chargeList.ncpDiscount') }}
          </p>
          <p class=" ml-10 montserrat-r"><span v-if="ncpDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(Math.abs(ncpDiscount), null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-if="selectedVendor === 'AWS' || selectedVendor === 'AZURE' || selectedVendor === 'NCP' || selectedVendor === 'GCP'"
          no-gutters
          align-h="between"
          class="content-item">
          <p>{{ $t('billing.chargeList.salesDiscount') }}
            <!--            <span v-if="salesDiscountApplyType === APPLY_TYPE.RATIO">-->
            <!--              ({{ $t('billing.chargeList.discount') }} {{ `${salesDiscountApplyValue}%` }})-->
            <!--            </span>-->
            <!--            <span v-else-if="salesDiscountApplyType === APPLY_TYPE.STATIC">-->
            <!--              ({{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ salesDiscountApplyValue }})-->
            <!--            </span>-->
            <!--            <span v-else-if="salesDiscountApplyType === APPLY_TYPE.INTERVAL">-->
            <!--              ({{ $t('billing.chargeList.usageSection') }})-->
            <!--            </span>-->
          </p>
          <p class=" ml-10 montserrat-r"><span v-if="salesDiscount !== 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(Math.abs(salesDiscount), null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-if="selectedVendor === 'AWS' || selectedVendor === 'AZURE' || selectedVendor === 'NCP' || selectedVendor === 'GCP'"
          no-gutters
          align-h="between"
          class="content-item">
          <p>{{ $t('billing.chargeList.credit') }}</p>
          <p class=" ml-10 montserrat-r"><span v-if="credit !== 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(Math.abs(credit), null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-for="(creditDetail,idx) in creditDetails"
          :key="idx"
          :class="idx===creditDetails.length-1?'gray-title content-item -mb-2':'gray-title content-item'"
          no-gutters
          align-h="between">
          <p>- {{ $t('billing.billingChargeDetail.download.costNames.' + creditDetail.crdtTypeCd) }}</p>
          <p class=" ml-10 montserrat-r "><span v-if="creditDetail.crdtAmt !== 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(creditDetail.crdtAmt, null, invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-if="false"
          no-gutters
          align-h="between"
          class="pb-2 content-item">
          <p>{{ $t('billing.chargeList.vat') }}</p>
          <p class=" ml-10 montserrat-r">{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ customFormatCostValue(Math.abs(vatCost), null, invoiceCurrency) }}</p>
        </b-row>
        <b-row
          no-gutters
          align-h="between"
          class="border-top pt-2 last-content-item content-item">
          <p>{{ $t('billing.chargeList.cloudServiceCharge') }} ({{ invoiceCurrency }})</p>
          <p class=" ml-10 montserrat-r"><span v-if="cloudServiceChargeTotal < 0">-</span>{{ CURRENCY_SYMBOL[invoiceCurrency] }}{{ formatCostValue(cloudServiceChargeTotal, 'cloudServiceCharge', invoiceCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          v-if="isCurrencyUSD"
          no-gutters
          align-h="between"
          class="last-content-item">
          <p>{{ $t('billing.chargeList.cloudServiceCharge') }} ({{ companyCurrency }})</p>
          <p class=" ml-10 montserrat-r">{{ CURRENCY_SYMBOL[companyCurrency] }}{{ formatCostValue(krwCloudServiceChargeTotal, 'cloudServiceCharge',companyCurrency,selectedVendor) }}</p>
        </b-row>
      </div>
      <div v-if="canShowAdditionalServiceFeeToolTip">
        <b-row
          v-for="(additionalService, index) in additionalServices"
          :key="additionalService.additionalServiceCode"
          :class="{'border-bottom pb-2': (index === additionalServices.length - 1)}"
          no-gutters
          align-h="between">
          <p>{{ additionalService.additionalServiceName }}</p>
          <p class=" ml-10 montserrat-r"><span v-if="additionalService.additionalServiceCharge < 0">-</span>{{ CURRENCY_SYMBOL[companyCurrency] }}{{ formatCostValue(additionalService.additionalServiceCharge, 'additionalServiceFee',companyCurrency, selectedVendor) }}</p>
        </b-row>
        <b-row
          no-gutters
          align-h="between"
          class="last-content-item content-item">
          <p>{{ $t('billing.chargeList.additionalServiceFee') }}</p>
          <p class=" ml-10 montserrat-r">{{ CURRENCY_SYMBOL[companyCurrency] }}{{ formatCostValue(additionalServicesTotal, 'additionalServiceFee',companyCurrency, selectedVendor) }}</p>
        </b-row>
      </div>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue';
  import { CURRENCY_SYMBOL, CURRENCY, VENDOR } from '@/constants/constants';
  import { APPLY_TYPE } from '@/constants/billingConstants';
  import { getCurrencySymbolByField } from '@/util/currencyConfigs';
  import { formatCostValue, customFormatCostValue } from "@/util/billingUtils";
  import _get from 'lodash/get';
  import _ from "lodash";
  import {sortCrdtDetailNmAsc} from "../../../../../../util/billingUtils";

  export default Vue.extend({
    props:{
      slotProps:{
        type:Object,
        default:null
      },
      applyExchangeRate:{
        type: Number,
        required: true
      }
    },
    data: function () {
      return {
        sumOfCostWithUSDCurrency: 0,
        sumOfCostWithKRWCurrency: 0,
        canShowCloudServiceChargeToolTip: false,
        canShowAdditionalServiceFeeToolTip: false,
        //applyExchangeRate: 0,
        cloudCost: 0,
        cloudOriginalCost: 0,
        onDemandDiscount: 0,
        cloudFrontDiscount: 0,
        supportFee: 0,
        supportFeeApplyType: 'S',
        supportFeeApplyValue: 0,
        salesDiscount: 0,
        ncpDiscount: 0,
        salesDiscountApplyType: 'S',
        salesDiscountApplyValue: 0,
        credit: 0,
        vatCost: 0,
        cloudServiceCharge: 0,
        additionalServiceCharge: 0,
        additionalServices: [],
        creditDetails:[],
        CURRENCY_SYMBOL: CURRENCY_SYMBOL,
        CURRENCY: CURRENCY,
        APPLY_TYPE: APPLY_TYPE,
        companyCurrency: CURRENCY.KRW,
        invoiceCurrency: CURRENCY.USD,
        selectedVendor: this.$store.state.billing.selectedVendor
      };
    },
    computed: {
      additionalServicesTotal: {
        cache: false,
        get() {
          let additionalServicesTotal = 0;
          this.additionalServices && this.additionalServices.forEach(function(additionalService){
            additionalServicesTotal += additionalService.additionalServiceCharge;
          });

          return additionalServicesTotal;
        }
      },
      cloudServiceChargeTotal: {
        cache: false,
        get() {
          if (this.selectedVendor === 'GCP') {
            return this.cloudServiceCharge;
          } else if (this.selectedVendor === 'AZURE') {
            return this.cloudCost + this.supportFee + this.salesDiscount - this.credit - this.ncpDiscount;
          } else {
            return this.cloudCost + this.supportFee - this.salesDiscount - this.credit - this.ncpDiscount;
          }

        }
      },
      krwCloudServiceChargeTotal: {
        cache: false,
        get() {
          return this.cloudServiceChargeTotal * this.applyExchangeRate;
        }
      },
      isCurrencyUSD(){
        return this.invoiceCurrency === CURRENCY.USD ? true : false;
      }
    },
    /* Back_up
    * beforeMount() {
      const cloudServiceChargeColumnIds = ['cloudCost', 'supportFee', 'ncpDiscount', 'salesDiscount', 'credit'];
      console.log(this.slotProps)
      let newData = {};
      const api = this.params.api;
      const rowModel = api.getModel();
      const rowNode = rowModel.getRow(this.params.rowIndex);
      newData.applyExchangeRate = _get(rowNode, 'data.applyExchangeRate', this.params.applyExchangeRate);

      const colId = this.params.column.getColId();
      newData.canShowCloudServiceChargeToolTip = false;
      newData.canShowAdditionalServiceFeeToolTip = false;
      if (cloudServiceChargeColumnIds.includes(colId)) {
        newData.canShowCloudServiceChargeToolTip = true;
        newData.canShowAdditionalServiceFeeToolTip = false;
      } else if (colId.includes('additionalServicesObject')) {
        newData.canShowCloudServiceChargeToolTip = false;
        newData.canShowAdditionalServiceFeeToolTip = true;
      }

      Object.assign(this, newData, rowNode.data);
      //this.selectedVendor = _get(rowNode, 'data.vendor', this.params.vendor);
      //this.selectedVendor = this.$store.state.billing.selectedVendor;

      if(this.selectedVendor==='GCP') {
        if (!_.isEmpty(this.creditDetails)) {
          Object.assign(this.creditDetails,sortCrdtDetailNmAsc(this.creditDetails, this));
        }
      }

    },
    * */
    beforeMount() {
      const cloudServiceChargeColumnIds = ['cloudCost', 'supportFee', 'ncpDiscount', 'salesDiscount', 'credit'];
      let newData = {};

      const rowNode = this.slotProps
      newData.applyExchangeRate = this.applyExchangeRate

      const colId = this.slotProps.column.columnKey
      newData.canShowCloudServiceChargeToolTip = false;
      newData.canShowAdditionalServiceFeeToolTip = false;
      if (cloudServiceChargeColumnIds.includes(colId)) {
        newData.canShowCloudServiceChargeToolTip = true;
        newData.canShowAdditionalServiceFeeToolTip = false;
      } else if (colId.includes('additionalServicesObject')) {
        newData.canShowCloudServiceChargeToolTip = false;
        newData.canShowAdditionalServiceFeeToolTip = true;
      }

      Object.assign(this, newData, rowNode.data);
      //this.selectedVendor = _get(rowNode, 'data.vendor', this.params.vendor);
      //this.selectedVendor = this.$store.state.billing.selectedVendor;

      if(this.selectedVendor==='GCP') {
        if (!_.isEmpty(this.creditDetails)) {
          Object.assign(this.creditDetails,sortCrdtDetailNmAsc(this.creditDetails, this));
        }
      }

    },
    mounted() {
    },
    methods: {
      getCurrencySymbolByField: function(field) {
        return getCurrencySymbolByField(field);
      },
      customFormatCostValue: function(cost, config, selectedCurrency) {
        return customFormatCostValue(cost, config, selectedCurrency);
      },
      formatCostValue: function(cost, config, companyCurrency, selectedVendor) {
        return formatCostValue(cost, config, companyCurrency, selectedVendor);
      },
      canShowOriginalCost(onDemandDiscount, cloudFrontDiscount) {
        return onDemandDiscount < 0 || onDemandDiscount > 0 || cloudFrontDiscount < 0 || cloudFrontDiscount > 0;

      },
      canShowCostByValue(costValue) {
        return costValue < 0 || costValue > 0;

      },
    }
  });
</script>
<style lang="scss" scoped>
  .custom-tooltip {
    position: absolute;
    width: auto;
    height: auto;
    background-color: #222222 !important;
    color: white;
    overflow: hidden;
    pointer-events: none;
    transition: opacity 1s;
    opacity: .9;
    border-radius: 3px !important;
    padding: 0.6rem 0.8rem;
    display: block;
    font-family: NotoSansCJKkr;
    font-size: 12px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.33;
    letter-spacing: normal;
    z-index: 99999;
    &.ag-tooltip-hiding {
      opacity: 0;
    }

    p {
      white-space: nowrap;
    }
    .gray-title {
      height: 16px;
      color: #999999;
    }
    .content-item {
      padding-top: 3px;
    }
    .last-content-item {
      padding-bottom: 3px;
    }
  }
</style>
