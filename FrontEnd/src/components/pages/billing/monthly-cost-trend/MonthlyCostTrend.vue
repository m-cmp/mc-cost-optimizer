<template>
  <b-col class="px-0 monthly-cost-trend-wrapper">
    <b-col class="px-0">
      <b-card
        class="card-custom"
        border-variant="transparent"
        header-border-variant="lightgray-1"
        header-bg-variant="transparent">
        <b-row
          slot="header"
          align-h="between"
          align-v="center"
          no-gutters
          class="pl-20 py-2">
          <b-row
            no-gutters
            class="monthly-bill-trend-tittle"
          >
            <b-col cols="12">
              <p class="font-16 medium font-family-notosanscjkkr-medium text-darkgray-1">{{ $t('billing.monthlyBillTrend.monthlyBillTrendTitle') }}</p>
            </b-col>
            <b-col cols="12">
              <p class="font-12 text-paging-btn">{{ $t('billing.monthlyBillTrend.showsMonthlyBillTrendChartWithActualCost') }}</p>
            </b-col>
          </b-row>
          <b-row
            no-gutters
            class="monthly-cost-header"
          >
            <b-button
              :disabled="!isEnabledPrevMonthButton"
              class="btn-icon-arrow"
              @click="onClickPrev()">
              <base-material
                :size="14"
                class="preview-last-month"
                name="keyboard_arrow_left"/>
            </b-button>
            <div class=" px-2 text-paging-btn">
              <p>
                <span>{{ labelFirstMonthYear }}</span>
                <span>-</span>
                <span>{{ labelLastMonthYear }}</span>
              </p>
            </div>
            <b-button
              id="icon-arrow-right"
              :disabled="!isEnabledNextMonthButton"
              class="btn-icon-arrow"
              @click="onClickNext()">
              <base-material
                :size="14"
                name="keyboard_arrow_right"
              />
            </b-button>
            <div
              v-show="selectedVendor != 'OPENSTACK'"
              class="monthly-cost-download"
              @click="onSelectChartDownloadOption2">
              <b-button
                :id="chartDownload.chartDownloadBtnId"
                class="btn-download"
                variant="transparent">
                <base-icon
                  :size="24"
                  width="12"
                  height="14"
                  name="icon_download"
                  color="#7b8088"/>
              </b-button>
            </div>
            <b-tooltip
              :target="chartDownload.chartDownloadBtnId"
              :show.sync="showTooltip"
            >
              {{ $t('download') }}
            </b-tooltip>
            <div
              :id="containerCustomPopoverId"
              class="custom-popover-monthly-cost-trend"/>
              <!--            <BasePopoverDropdown-->
              <!--              :target="chartDownload.chartDownloadBtnId"-->
              <!--              :placement="chartDownload.placement"-->
              <!--              :options="chartDownload.options"-->
              <!--              :show-popover="chartDownload.showPopover"-->
              <!--              :enabled-localization="chartDownload.enabledLocalization"-->
              <!--              :container-custom-popover="containerCustomPopoverId"-->
              <!--              @selectOption="onSelectChartDownloadOption"-->
              <!--            />-->
          </b-row>
        </b-row>
        <b-col
          v-show="isBillListLoading"
          class="monthly-cost-trend-height"
        >
          <BaseLoadingIndicator
            :loading-height="265"
            :loading-item-right-px="0"
          />
        </b-col>
        <div v-show="!isBillListLoading">
          <b-col
            v-show="!hasBillListData"
            class="monthly-cost-trend-height"
          >
            <BaseNotificationNoData/>
          </b-col>
          <b-col
            v-show="hasBillListData">
            <PrimeBillingSimpleStackedChart
              :stacked-chart-data="monthlyCostTrend"
              :first-month-idx="firstMonthIdx"
              :last-month-idx="lastMonthIdx"
              :start-chart-idx="monthlyChartStartIndex"
              @selectColumn="onSelectColumn"
            />
          </b-col>
        </div>
      </b-card>
    </b-col>
  </b-col>
</template>

<script>
  import {getStartAndEndLocationByTargetData} from "@/util/montlyCostTrend";
  import {CHART_DOWNLOAD_OPTION_VALUE, CHART_DOWNLOAD_OPTIONS} from "@/constants/billingConstants";
  import {FILE_TYPE, TIME_CONST, CURRENCY_SYMBOL, BROWSER, CURRENCY} from "@/constants/constants";
  import {saveAndReturnDownloadFile} from '@/util/excelJS';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData'
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import _isEmpty from 'lodash/isEmpty';
  import { mapGetters } from  'vuex';
  import {formatMonthYearByLocalization} from "@/util/dateTimeUtils";
  import PrimeBillingSimpleStackedChart from "@/components/common/base-charts/billing-simple-stacked-chart/PrimeBillingSimpleStackedChart";
  export default {
    name: 'MonthlyCostTrend',
    components: {
      PrimeBillingSimpleStackedChart,
      BaseNotificationNoData,
      BaseLoadingIndicator
    },
    props: {
      billList: {
        type: Array,
        required: true
      },
      activeMonthIdx: {
        type: Number,
        required: true
      },
      firstMonthIdx: {
        type: Number,
        required: true
      },
      lastMonthIdx: {
        type: Number,
        required: true
      },
      isEnabledPrevMonthButton: {
        type: Boolean,
        required: true
      },
      isEnabledNextMonthButton: {
        type: Boolean,
        required: true
      },
      browser: {
        type: String,
        default: BROWSER.CHROME
      },
      selectedVendor: {
        type: String,
        default() {
          return 'AWS'
        }
      }
    },
    data() {
      const $vm =this;
      return {
        // chartLegendLabelsColor: am4core.color('#000000'),
        // categoryLabelsColor: am4core.color('#898D94'),
        // categoryDataFields: TIME_CONST,
        // categoryMinGridDistance: 30,
        // valueAxisMinGridDistance: 30,
        // valueAxisLabelsColor: am4core.color('#898D94'),
        // seriesFillColor: am4core.color('#99A3BF'),
        // seriesTooltipBackgroundColor: am4core.color('#ffffff'),
        // seriesTooltipLabelColor: am4core.color('#222222'),
        // seriesName: 'billing.monthlyBillTrend.actualBillCost',
        // seriesDataFieldsCategoryX: TIME_CONST,
        // seriesDataFieldsValueY: 'totalChargeValue',
        // activeSeriesFillColor: am4core.color('#0672FF'),
        // secondSeriesName: 'billing.monthlyBillTrend.nowShowing',
        // firstSeriesValueY: "exchangedCloudServiceCharge",
        // secondSeriesValueY: "additionalServiceChargeValue",
        // activeSeriesColor: am4core.color('#0672FF'),
        // hoverSeriesColor: am4core.color('#535A80'),
        // markerWidth: 20,
        // markerHeight: 5,
        // seriesTooltipHtml: `
        // <table class="tooltip-table">
        //   <tr>
        //     <th align="left" >${this.$t('billing.monthlyBillTrend.cloudServiceCharge')}</th>
        //     <td align="right">{companyCurrencySymbol}{exchangedCloudServiceCharge}</td>
        //   </tr>
        //   <tr class="custom-tr">
        //     <th align="left" class="tooltip-padding-left-name">${this.$t('billing.monthlyBillTrend.additionalServiceFee')}</th>
        //     <td align="right">{companyCurrencySymbol}{additionalServiceCharge}</td>
        //   </tr>
        //   <hr>
        //   <tr>
        //     <th align="left">${this.$t('billing.monthlyBillTrend.total')}</th>
        //     <td align="right">{companyCurrencySymbol}{totalCharge}</td>
        //   </tr>
        // </table>
        // `,
        getStartAndEndLocationByTargetData: getStartAndEndLocationByTargetData,
        chartDownload: {
          options: CHART_DOWNLOAD_OPTIONS,
          showPopover: false,
          chartDownloadBtnId: 'monthly-bill-trend-download-btn',
          placement: 'bottomleft',
          enabledLocalization: true
        },
        showTooltip: false,
        containerCustomPopoverId: 'monthly-cost-trend-custom-popover',
        BROWSER: BROWSER
      }
    },
    computed: {
      ...mapGetters({
        isBillListLoading: 'billing/isBillListLoading',
      }),
      monthlyCostTrend: {
        cache: true,
        get() {
          return getMonthlyCostTrendFromBills(this.billList, this.activeMonthIdx, this.firstMonthIdx, this.lastMonthIdx, this.selectedVendor, this);
        }
      },
      labelFirstMonthYear: {
        cache: true,
        get() {
          return getLabelFirstMonthYear(this.billList, this.firstMonthIdx)
        }
      },
      labelLastMonthYear: {
        cache: true,
        get() {
          return getLabelLastMonthYear(this.billList, this.lastMonthIdx)
        }
      },
      hasBillListData() {
        return this.billList && !_isEmpty(this.billList)
      },
      chartNumberFormat: {
        get(){
          if(this.billList.includes(CURRENCY.KRW)){
            return '#,###.';
          }else{
            return '#,###.##';
          }
        }
      },
      monthlyChartStartIndex:{
        cache:true,
        get(){
          return getStartChartIndexValue(this.billList,this.firstMonthIdx,this.activeMonthIdx,this.lastMonthIdx)
        }
      }
    },

    watch: {
      '$i18n.locale': {
        handler() {
          this.seriesTooltipHtml = `
        <table class="tooltip-table">
          <tr>
            <th align="left">${this.$t('billing.monthlyBillTrend.cloudServiceCharge')}</th>
            <td align="right">{companyCurrencySymbol}{exchangedCloudServiceCharge}</td>
          </tr>
          <tr class="custom-tr">
            <th align="left" class="tooltip-padding-left-name">${this.$t('billing.monthlyBillTrend.additionalServiceFee')}</th>
            <td align="right">{companyCurrencySymbol}{additionalServiceCharge}</td>
          </tr>
          <hr>
          <tr>
            <th align="left">${this.$t('billing.monthlyBillTrend.total')}</th>
            <td align="right">{companyCurrencySymbol}{totalCharge}</td>
          </tr>
        </table>
        `
        }
      },
    },
    mounted() {
      this.$root.$on('bv::tooltip::show', bvEvent => {
        if (this.chartDownload.showPopover === true) {
          this.$root.$emit('bv::hide::tooltip')
          bvEvent.preventDefault()
        }
      })
      this.$root.$on('bv::popover::hide', bvEvent => {
        this.chartDownload.showPopover = false
      })
      this.$root.$on('bv::popover::show', bvEventObj => {
        this.showTooltip = false
      })
    },
    methods: {
      onSelectColumn(index) {
        if (this.activeMonthIdx !== index) {
          this.$emit('setActiveMonthIdx', index);
        }
      },
      onClickPrev() {
        this.$emit('onClickPrev')
      },
      onClickNext() {
        this.$emit('onClickNext')
      },
      // Back_up
      // onSelectChartDownloadOption(selectedOption) {
      //   const activeMonthBill = this.billList[this.activeMonthIdx];
      //   let i18nParams = {
      //     currency: activeMonthBill.companyCurrency
      //   };
      //   let amchartsDataFields = {
      //     chargeYear: this.$t('billing.monthlyBillTrend.download.columnHeader.chargeYear'),
      //     chargeMonth: this.$t('billing.monthlyBillTrend.download.columnHeader.chargeMonth'),
      //     totalCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.total', i18nParams),
      //     /*cloudServiceCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.cloudServiceCharge', i18nParams),*/
      //     exchangedCloudServiceCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.cloudServiceCharge', i18nParams),
      //     additionalServiceCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.additionalServiceFee', i18nParams)
      //   };
      //   let fileName = `MonthlyBillTrend_${activeMonthBill.chargeYear}${activeMonthBill.chargeMonth}`;
      //   let fileType = '';
      //   switch (selectedOption) {
      //     case CHART_DOWNLOAD_OPTION_VALUE.CSV:
      //       fileType = FILE_TYPE.XLSX;
      //       break;
      //     default:
      //       fileType = FILE_TYPE.XLSX;
      //   }
      //   this.$refs.billingSimpleStackedChart.exportChart(fileType, fileName, amchartsDataFields)
      // },
      onSelectChartDownloadOption2(selectedOption) {
        let workbook = new this.$excel.Workbook();
        let worksheet = workbook.addWorksheet("Data");
        const activeMonthBill = this.billList[this.activeMonthIdx];
        let i18nParams = {
          currency: activeMonthBill.companyCurrency
        };
        let DataFields = {
          chargeYear: this.$t('billing.monthlyBillTrend.download.columnHeader.chargeYear'),
          chargeMonth: this.$t('billing.monthlyBillTrend.download.columnHeader.chargeMonth'),
          totalCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.total', i18nParams),
          /*cloudServiceCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.cloudServiceCharge', i18nParams),*/
          exchangedCloudServiceCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.cloudServiceCharge', i18nParams),
          additionalServiceCharge: this.$t('billing.monthlyBillTrend.download.columnHeader.additionalServiceFee', i18nParams)
        };
        let setColumnName = Object.values(DataFields)
        let xlsxData = this.billList
          .slice(this.firstMonthIdx,this.lastMonthIdx+1)
          .map((bill) => {
            const data = []
            data.push(bill.chargeYear)
            data.push(bill.chargeMonth)
            data.push(bill.companyCurrency != CURRENCY.USD ? this.selectedVendor == 'GCP' ? Math.floor(bill.totalCharge) : Math.round(bill.exchangedCloudServiceCharge) + Math.round(bill.additionalServiceCharge) : bill.cloudServiceCharge + bill.additionalServiceCharge)
            data.push(bill.companyCurrency != CURRENCY.USD ? this.selectedVendor == 'GCP' ? Math.floor(bill.cloudServiceCharge)
                        : Math.round(bill.exchangedCloudServiceCharge)
                      :bill.cloudServiceCharge)
            data.push(bill.additionalServiceCharge)
            return data
          })

        xlsxData.unshift(setColumnName)
        let fileName = `MonthlyBillTrend_${activeMonthBill.chargeYear}${activeMonthBill.chargeMonth}`;
        xlsxData.forEach(function(row) {
          worksheet.addRow(row);
        });
        switch (selectedOption) {
          case CHART_DOWNLOAD_OPTION_VALUE.CSV:
            saveAndReturnDownloadFile(workbook, fileName)
            break;
          default:
            saveAndReturnDownloadFile(workbook, fileName)
        }

      },
      togglePopoverTooltip() {
        this.chartDownload.showPopover = !this.chartDownload.showPopover
        this.showTooltip = false
      }
    }
  };

  // Back_up
  // function getMonthlyCostTrendFromBills(billList, activeMonthIdx, firstMonthIdx, lastMonthIdx, selectedVendor) {
  //   let idx = firstMonthIdx;
  //   //let selectedVendor = this.selectedVendor;
  //
  //   return billList
  //     .slice(firstMonthIdx, lastMonthIdx + 1)
  //     .map(bill => {
  //       const newBill = {
  //         cloudServiceChargeValue: bill.cloudServiceCharge,
  //         additionalServiceChargeValue: bill.additionalServiceCharge,
  //         totalChargeValue: bill.cloudServiceCharge + bill.additionalServiceCharge,
  //         chargeMonth: bill.chargeMonth,
  //         chargeYear: bill.chargeYear,
  //         idx: idx,
  //         time: formatMonthYearByLocalization(bill.chargeMonth, bill.chargeYear),
  //         currentActive: activeMonthIdx === idx,
  //         billListLength: billList.length,
  //         firstMonthIdx: firstMonthIdx,
  //         lastMonthIdx: lastMonthIdx,
  //         activeMonthIdx: activeMonthIdx,
  //         cloudServiceCharge: bill.cloudServiceCharge,
  //         additionalServiceCharge: Math.round(bill.additionalServiceCharge ) ,
  //         /*totalCharge: bill.totalCharge,*/ // 환율 계산이 적용 안 된 오리지널 합계 totalCharge
  //         //totalCharge : bill.exchangedCloudServiceCharge + bill.additionalServiceCharge, // SQL에서 환율 계산처리 함 : exchangedCloudServiceCharge
  //         //exchangedCloudServiceCharge: bill.exchangedCloudServiceCharge,
  //         totalCharge :
  //           bill.companyCurrency != CURRENCY.USD ?
  //             selectedVendor == 'GCP' ? Math.floor(bill.totalCharge)
  //             : Math.round(bill.exchangedCloudServiceCharge) + Math.round(bill.additionalServiceCharge)
  //           :
  //             bill.cloudServiceCharge + bill.additionalServiceCharge
  //         ,
  //         exchangedCloudServiceCharge: bill.companyCurrency != CURRENCY.USD ?
  //           selectedVendor == 'GCP' ? Math.floor(bill.cloudServiceCharge)
  //             : Math.round(bill.exchangedCloudServiceCharge)
  //           :bill.cloudServiceCharge
  //         ,
  //         companyCurrencySymbol: CURRENCY_SYMBOL[bill.companyCurrency],
  //       };
  //       idx++;
  //       return newBill;
  //     });
  //
  //
  //
  // }
  function getMonthlyCostTrendFromBills(billList, activeMonthIdx, firstMonthIdx, lastMonthIdx, selectedVendor,$vm) {
    let newBillData = []
    let idx = firstMonthIdx;
    let stakedDataForm = {
      labels:new Array(12),
      datasets:[
        {
          type:'bar',
          label: $vm.$t('billing.monthlyBillTrend.cloudServiceCharge'),
          backgroundColor:new Array(12),
          data:new Array(12),
        },
        {
          type:'bar',
          label: $vm.$t('billing.monthlyBillTrend.additionalServiceFee'),
          backgroundColor:new Array(12),
          data:new Array(12),
        }
      ]
    }

    let startChartIndex = getStartChartIndexValue(billList,firstMonthIdx,activeMonthIdx,lastMonthIdx) ;
    let companyCurrency = new Array(12) ;
    billList
      .slice(firstMonthIdx, lastMonthIdx + 1)
      .map(function (bill,index) {
        let startIndex = startChartIndex+index
        stakedDataForm.labels[startIndex]=formatMonthYearByLocalization(bill.chargeMonth, bill.chargeYear)
        stakedDataForm.datasets[0].data[startIndex] =
          bill.companyCurrency != CURRENCY.USD ?
            selectedVendor == 'GCP' ? Math.floor(bill.cloudServiceCharge)
              : Math.round(bill.exchangedCloudServiceCharge)
            :bill.cloudServiceCharge
        let currentActive = activeMonthIdx === idx
        stakedDataForm.datasets[0].backgroundColor[startIndex] = currentActive ? '#0672ff':'#6c7994'
        stakedDataForm.datasets[1].backgroundColor[startIndex] = currentActive ? '#addeff':'#99a3bf'
        stakedDataForm.datasets[1].data[startIndex]=Math.round(bill.additionalServiceCharge)
        idx ++
        companyCurrency[startIndex] = CURRENCY_SYMBOL[bill.companyCurrency]
      })
    newBillData.push(stakedDataForm)
    newBillData.push(startChartIndex)
    newBillData.push(companyCurrency)
    return newBillData;
  }
  function getStartChartIndexValue(billList,firstMonthIdx,activeMonthIdx,lastMonthIdx){
    let startIndex =0;
    if(lastMonthIdx <12 ){ // slice 된 데이터가 초기 부터 1년치가 안될 때 => 데이터 맨 앞 혹은 맨 끝 포함
      if (billList.length >12 ){ // 빌링데이터는 1년 이상을 가지고 있을 때
        startIndex = 12-Math.abs(lastMonthIdx -firstMonthIdx) -1

      }
      else { // 빌링데이터가 1년치 데이터를 넘지 않았을 때
        if(lastMonthIdx != activeMonthIdx){ // 초기 화면 로드이후에 로직 적용
          startIndex = 5 - activeMonthIdx
        }
      }
    }
    return startIndex
  }

  function getLabelFirstMonthYear(billList, firstMonthIdx) {
    let labelFirstMonth = (billList && billList[firstMonthIdx]) ? billList[firstMonthIdx].chargeMonth : ''
    let labelFirstYear = (billList && billList[firstMonthIdx]) ? billList[firstMonthIdx].chargeYear : ''
    return formatMonthYearByLocalization(labelFirstMonth, labelFirstYear)
  }

  function getLabelLastMonthYear(billList, lastMonthIdx) {
    let labelLastMonth = (billList && billList[lastMonthIdx]) ? billList[lastMonthIdx].chargeMonth : ''
    let labelLastYear = (billList && billList[lastMonthIdx]) ? billList[lastMonthIdx].chargeYear : ''
    return formatMonthYearByLocalization(labelLastMonth, labelLastYear)
  }

</script>
<style lang="scss">
  .monthly-cost-trend-wrapper {
    .preview-last-month {
      margin-left: -0.5px;
    }
    .monthly-cost-trend-height {
      height: 265px;
    }
  }
  @media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
    .monthly-bill-trend-tittle {
      display: inline
    }
    .margin-left-currency {
      margin-left: 60px
    }
  }
  .tooltip-table {
    .tooltip-padding-left-name {
      padding-right: 50px;
    }
    .custom-tr {
      border-bottom: 1px solid;
    }
    td {
      font-family: Montserrat-Regular!important;
    }
    th, td {
      padding: 5px 0;
    }
  }

  .btn-icon-arrow {
    background-color: #0672FF !important;
    border: none !important;
    border-radius: 10px !important;
    width: 14px !important;
    height: 14px !important;
    padding: 0 !important;
    color: #ffffff !important;
    margin-top: 3px;
    cursor: pointer !important;
    &:focus {
      outline: none !important;
      border: none !important;
      box-shadow: none !important;
    }
  }
  .btn-icon-arrow:disabled,
  .btn-icon-arrow[disabled]{
    border: none !important;
    background-color: #D5DAE0 !important;
    color: #ffffff !important;
  }
  .tooltip {
    color: red !important;
  }
  #icon-arrow-right {
    margin-right: 13px;
  }
  .custom-popover-monthly-cost-trend {
    .popover {
      top: 0 !important;
      left: 14px !important;
      .arrow {
        left: 88% !important;
      }
      .arrow:before {
        border-bottom-color: #ffffff;
      }
    }
  }
  @media only screen and (max-width: 1280px) {
    .custom-popover-monthly-cost-trend {
      .popover {
        .arrow {
          left: 85.5% !important;
        }
      }
    }
  }
  @media only screen and (max-width: 1024px) {
    .custom-popover-monthly-cost-trend {
      .popover {
        .arrow {
          left: 80% !important;
        }
      }
    }
  }
  .monthly-test{
    padding-top:10px
  }
  .monthly-cost-header{
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .monthly-cost-download {
    margin-right: 10px;
    margin-top:5px;
    .btn-download{
      margin: 0 !important;
    }
    width: 25px;
    height: 25px;
    display: flex;
    align-content: center;
    justify-content: center;
    &:hover {
      background-color: #f0f0f0;
      border-radius: 5px;
      cursor: pointer;
    }
    &:active{
      background-color:#e0e0e0!important
    }
  }
</style>
