<template>
  <fragment>
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span>{{ $t('dashboard.productPortion.title') }}</span>
      </p>
      <p class="-description -color-darkgray-1">{{ $t('dashboard.productPortion.desc') }}</p>
    </b-row>
    <b-form
      v-if="show"
      class="edit-portion-by-account-form"
      @submit="onSubmit"
      @reset="onReset">
      <!--      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.portionByAccount.previews') }}</div>-->
      <!--      <b-form-group-->
      <!--        label-for="preview-edit-portion-by-account">-->
      <!--        <BaseLoadingIndicator-->
      <!--          v-show="isLoading"-->
      <!--          :loading-height="152"-->
      <!--          :loading-item-right-px="0"-->
      <!--        />-->
      <!--        <div-->
      <!--          v-show="!isLoading"-->
      <!--          class="portion-widget-chart edit-form">-->
      <!--          <b-card-->
      <!--            v-show="hasProductPortionData"-->
      <!--            class="preview-bar-chart-wrapper"-->
      <!--          >-->
      <!--            <BaseSimpleBarChart-->
      <!--              :simple-bar-chart-id="simpleBarChartId"-->
      <!--              :simple-bar-chart-data="simpleBarChartData"-->
      <!--              :category-data-fields="barChartFieldName"-->
      <!--              :value-data-fields="xAxisFieldName"-->
      <!--              :color-data-fields="barChartFieldColor"-->
      <!--              :children-data-fields="barChartFieldChildren"-->
      <!--              :scale="scaleValue"-->
      <!--              :category-min-grid-distance="10"-->
      <!--              :column-width="7"-->
      <!--              :chart-height="172"-->
      <!--              :category-axis-font-size="4"-->
      <!--              :value-axis-font-size="4"-->
      <!--              :value-min-grid-distance="40"-->
      <!--              :min-width-category-labels="75"-->
      <!--              :max-width-category-labels="75"-->
      <!--              :transform-translate-chart="transformTranslateBarChart"-->
      <!--              :is-preview-mode="true"-->
      <!--              :value-prefix="currencySymbol"-->
      <!--              class="bar-chart-content"-->
      <!--            />-->
      <!--          </b-card>-->
      <!--          <BaseNotificationNoData v-show="!hasProductPortionData"/>-->
      <!--        </div>-->
      <!--      </b-form-group>-->
      <b-form-group
        id="cloud-service-group"
        :label="$t('dashboard.topCost.cloudService')"
        label-for="selectVendor">
        <b-form-select
          id="selectVendor"
          v-model="selectedVendorByOption"
          :options="vendorOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          @change="onSelectVendor"/>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ selectedVendorByOption==='GCP'?$t('dashboard.portionByAccount.project') : $t('dashboard.portionByAccount.account') }}</div>
      <b-form-group
        label-for="edit-portion-by-account">
        <b-form-select
          id="edit-portion-by-account"
          v-model="selectedAccount"
          :disabled="vendorOptions.length > 0 ? false : true"
          :options="accounts"/>
          <!--          @change="onSelectAccount"/>-->
      </b-form-group>
      <b-form-group>
        <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.portionByAccount.dateRange') }}</div>
        <b-form-select
          v-model="selectedDateType"
          :disabled="vendorOptions.length > 0 ? false : true"
          :options="dateTypes"
          class="date-type-options"
          @change="onSelectDateType"/>
        <b-form-select
          v-model="selectedTimeFrame"
          :disabled="vendorOptions.length > 0 ? false : true"
          :options="timeFrameOptions"
          class="time-frame-options"
          @change="onSelectTimeFrame"/>
      </b-form-group>
      <!--      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.portionByAccount.dateRange') }}</div>-->
      <!--      <div class="date-type-options">-->
      <!--        <b-form-select-->
      <!--          v-model="selectedDateType"-->
      <!--          :options="dateTypes"-->
      <!--          @change="onSelectDateType"/>-->
      <!--      </div>-->
      <!--      <div class="time-frame-options">-->
      <!--        <b-form-group>-->
      <!--          <b-form-select-->
      <!--            v-model="selectedTimeFrame"-->
      <!--            :options="timeFrameOptions"/>-->
      <!--            &lt;!&ndash;            @change="onSelectTimeFrame"/>&ndash;&gt;-->
      <!--        </b-form-group>-->
      <!--      </div>-->
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.dashboardCost.editWidgetForm.setScale') }}</div>
      <b-form-radio-group
        id="scale-radio-group"
        v-model="scaleValue"
        :disabled="vendorOptions.length > 0 ? false : true"
        stacked
      >
        <b-form-radio
          :value="SCALE.VALUE"
          class="mb-1"
        >
          <span class="option-value-label">{{ $t('dashboard.scaleOption.value') }}</span>
        </b-form-radio>
        <b-form-radio
          :value="SCALE.PERCENTAGE"
          class="mb-4 pb-1"
        >
          <span class="option-value-label">{{ $t('dashboard.scaleOption.percentage') }}</span>
        </b-form-radio>
      </b-form-radio-group>
      <p
        v-if="!hasCollectedData"
        class="-color-red-1">
        {{ $t('dashboard.noCollectedData') }}
      </p>
      <div class="float-right">
        <b-button
          variant="outline-secondary"
          type="reset"
          class="cancel-button font-family-notosanscjkkr-medium"
          @click="onClickCancelButton"
        >{{ $t('dashboard.topCost.cancel') }}</b-button>
        <b-button
          :disabled="(vendorOptions.length > 0 ? false : true) || !canSaveWidget"
          type="submit"
          variant="primary"
          class="font-family-notosanscjkkr-medium">{{ saveButtonLocalization }}
        </b-button>
      </div>
    </b-form>
  </fragment>
</template>

<script>
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import {
    DASHBOARD_DATE_TYPE,
    DASHBOARD_DATE_TYPE_OPTIONS, PORTION_DEFAULT_SELECTED_ACCOUNT,
    PORTION_DEFAULT_TIME_FRAME,
    VIEW_MODE,
    DASHBOARD_VIEW_BY,
    SCALE,
    PRODUCT_PORTION_VENDORS,
    SELECTED_VENDOR
  } from "@/constants/dashboardConstants";
  import _isEmpty from 'lodash/isEmpty';
  import _isNil from 'lodash/isNil';
  import _get from 'lodash/get';
  import {fetchDashboardProductPortion} from "@/api/dashboard";
  import {getTimeFrameListOption, getPortionChartData, getSelectedVendorsByWidget,availableVendors} from "@/util/dashboardUtils";
  import {CURRENCY, CURRENCY_SYMBOL, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import {getDisplayItemWithVendorBaseOnViewBy} from "@/util/formatAccountUtils";
  import {EXCEPTION} from "../../../../constants/constants";

  export default {
    name: 'EditProductPortionForm',
    components: {
      BaseLoadingIndicator,
      BaseNotificationNoData
    },
    props: {
      widgetConfig: {
        type: Object,
        required: true
      },
      allVendors: {
        type: Array,
        required: true,
        default() {
          return []
        }
      },
      dashboardViewMode: {
        type: String,
        required: true,
        default: VIEW_MODE.DEFAULT
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
      valueDataFields: {
        type: String,
        default: SCALE.COST
      },
    },
    data() {
      return {
        show: true,
        vendorOptions: availableVendors(PRODUCT_PORTION_VENDORS, this),
        accounts: [],
        dateTypes: DASHBOARD_DATE_TYPE_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        timeFrameOptions: [],
        selectedDateType: '',
        selectedTimeFrame: '',
        selectedAccount: '',
        previewDashboardData: {
          portion: {},
          accounts: [],
          timeFrameList: []
        },
        simpleBarChartId: 'preview-portion-by-account-simple-bar-chart',
        barChartFieldChildren: 'children',
        barChartFieldName: 'name',
        xAxisFieldName: SCALE.PERCENTAGE,
        barChartFieldColor: 'color',
        scaleValue: SCALE.PERCENTAGE,
        SCALE: SCALE,
        transformTranslateBarChart: {
          x: -10,
          y: -5
        },
        WEEK_SIGN: 'W',
        canSaveWidget: false,
        isLoading: true,
        tempSelectedTimeFrame: '',
        hasCollectedData: true
      }
    },
    computed: {
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.widgetConfig, this, PRODUCT_PORTION_VENDORS);
        },
        set(selectedVendorsByWidget) {
          this.$set(this.widgetConfig, 'selectedVendorsByWidget', Array.isArray(selectedVendorsByWidget)?selectedVendorsByWidget:[selectedVendorsByWidget]);
        }
      }
      //preview 관련 메소드 주석 처리
      // simpleBarChartData: {
      //   cache: false,
      //   get() {
      //     if (_isEmpty(this.previewDashboardData.portion)) {
      //       return [];
      //     } else {
      //       let accountInfo = this.previewDashboardData.accounts.find(account => account.item === this.selectedAccount);
      //       let vendor = "";
      //       if(!_isNil(accountInfo)) {
      //         vendor = accountInfo.vendor
      //       }
      //       let widgetConfig = {
      //         viewBy: this.widgetConfig.viewBy,
      //         widgetType: this.widgetConfig.widgetType
      //       }
      //       return getPortionChartData(this.previewDashboardData.portion, vendor, this.commonUserInfo.selectedCurrency, this.exchangeRate, widgetConfig, true, this.$t('dashboard.others'))
      //     }
      //   }
      // },
      // currencySymbol: function() {
      //   return CURRENCY_SYMBOL[this.commonUserInfo ? this.commonUserInfo.selectedCurrency : CURRENCY.USD];
      // },
      // hasProductPortionData() {
      //   if (!this.previewDashboardData || _isEmpty(this.previewDashboardData.portion)) {
      //     return false;
      //   }
      //   return Object.values(this.previewDashboardData.portion).some(costsByCurrentView => !_isEmpty(costsByCurrentView));
      // },
    },
    watch: {
      'widgetConfig.scale': {
        handler() {
          this.setAxisFieldName()
        },
        immediate: true
      },
      scaleValue: {
        handler: function () {
          switch (this.scaleValue) {
            case SCALE.VALUE:
              this.xAxisFieldName = SCALE.COST;
              break;
            case SCALE.PERCENTAGE:
              this.xAxisFieldName = SCALE.PERCENTAGE
              break;
            default:
              this.xAxisFieldName = SCALE.PERCENTAGE;
              break;
          }
        },
        immediate: true
      }
    },
    mounted() {
    },
    created() {
      this.tempSelectedTimeFrame = this.widgetConfig.timeFrame;
      if(this.widgetConfig.selectedVendorsByWidget !== undefined
          && this.widgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.NCP)){
        this.dateTypes = DASHBOARD_DATE_TYPE_OPTIONS.filter(option => option.value != DASHBOARD_DATE_TYPE.WEEKLY).map(opt => {
          return {
            ...opt,
            text: this.$t(opt.text)
          };
        });
        this.widgetConfig.dateType = DASHBOARD_DATE_TYPE.MONTHLY;
        //this.onChangeDateType(this.widgetConfig.dateType);
      }else{
        this.dateTypes = DASHBOARD_DATE_TYPE_OPTIONS.map(opt => {
          return {
            ...opt,
            text: this.$t(opt.text)
          };
        });
      }
      let defaultVendor = this.allVendors != null && this.allVendors.length > 0 ? [this.allVendors[0]] : null;
      let payload = {
        dateType: this.widgetConfig.dateType,
        timeFrame: this.widgetConfig.timeFrame,
        selectedAccount: _isEmpty(this.widgetConfig.selectedAccount) ? PORTION_DEFAULT_SELECTED_ACCOUNT : this.widgetConfig.selectedAccount,
        widgetCurrency : this.$store.state.common.info.currencies.KRW,
        widgetType: this.widgetConfig.widgetType,
        vendors: this.commonUserInfo.selectedVendors,
        selectedVendorsByWidget: _isEmpty(this.allVendors.filter(vendor => vendor == this.widgetConfig.selectedVendorsByWidget))?defaultVendor:this.allVendors.filter(vendor => vendor == this.widgetConfig.selectedVendorsByWidget)
      };
      if (_isEmpty(this.widgetConfig.dateType)) {
        payload.dateType = this.dateTypes[0].value;
        this.selectedDateType = this.dateTypes[0].value;
      }
      this.fetchPortionData(payload);
    },
    methods: {
      onSubmit(evt) {
        evt.preventDefault();
        if (this.canSaveWidget) {
          let payload = {
            ...this.widgetConfig,
            dateType: this.selectedDateType,
            timeFrame: this.selectedTimeFrame,
            selectedAccount: this.selectedAccount,
            scale: this.scaleValue
          };
          this.$emit('save', payload);
        }
      },
      onReset(evt) {
        evt.preventDefault()
      },
      onClickCancelButton() {
        this.$emit('hideModal');
      },
      fetchPortionData(payload) {
        this.isLoading = true
        fetchDashboardProductPortion(payload).then((res) => {
          this.processData(res);
          this.canSaveWidget = true;
          this.isLoading = false
        }).catch((err) => {
          this.isLoading = false;
          console.error(err);
        })
      },
      refreshDataWhenChangeOption(isSelectDateType) {
        let isType = isSelectDateType;
        if(this.widgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.NCP)){
          this.dateTypes = DASHBOARD_DATE_TYPE_OPTIONS.filter(option => option.value != DASHBOARD_DATE_TYPE.WEEKLY).map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
          this.widgetConfig.dateType = DASHBOARD_DATE_TYPE.MONTHLY;
          this.selectedDateType = DASHBOARD_DATE_TYPE.MONTHLY;
          this.selectedTimeFrame = PORTION_DEFAULT_TIME_FRAME;
          isType = true
          //this.onChangeDateType(this.widgetConfig.dateType);
        }else{
          this.dateTypes = DASHBOARD_DATE_TYPE_OPTIONS.map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
        }
        let defaultVendor = this.allVendors != null && this.allVendors.length > 0 ? [this.allVendors[0]] : null;
        this.canSaveWidget = false;
        this.isLoading = true;
        let requestPayload = {
          selectedAccount: _isEmpty(this.selectedAccount) ? "" : this.selectedAccount,
          dateType: this.selectedDateType,
          timeFrame: isType ? PORTION_DEFAULT_TIME_FRAME : this.selectedTimeFrame,
          selectedVendorsByWidget: _isEmpty(this.allVendors.filter(vendor => vendor == this.widgetConfig.selectedVendorsByWidget))?defaultVendor:this.allVendors.filter(vendor => vendor == this.widgetConfig.selectedVendorsByWidget)
        };

        fetchDashboardProductPortion(requestPayload).then((res) => {
          this.processDataWhenChangedOption(res, isType);
          this.canSaveWidget = true;
          this.isLoading = false
          this.hasCollectedData = true;
        }).catch((err) => {
          this.isLoading = false;
          if(err.code === EXCEPTION.NO_DATA_COLLECTED.code) {
            this.hasCollectedData = false;
          }
          console.error(err);
        })
      },
      processData(res) {
        this.previewDashboardData = res;

        this.accounts = this.previewDashboardData.accounts.map(account => {
          return {
            text: getDisplayItemWithVendorBaseOnViewBy(account, DASHBOARD_VIEW_BY.ACCOUNT),
            value: account.item
          };
        });

        this.calculateTimeFrameOptions(this.widgetConfig.dateType, res);

        this.selectedAccount = res.selectedAccount;
        this.selectedDateType = this.widgetConfig.dateType;

        let timeFrameOptionValues = this.timeFrameOptions.map(timeFrame => timeFrame.value);
        this.setTimeFrameDefaultOption(timeFrameOptionValues, res.selectedTimeFrame);
      },
      processDataWhenChangedOption(res) {
        this.previewDashboardData = res;
        this.accounts = this.previewDashboardData.accounts.map(account => {
          return {
            text: getDisplayItemWithVendorBaseOnViewBy(account, DASHBOARD_VIEW_BY.ACCOUNT),
            value: account.item
          };
        });
        this.selectedAccount = res.selectedAccount;
        this.calculateTimeFrameOptions(this.selectedDateType, res);
        this.selectedTimeFrame = res.selectedTimeFrame
        this.setTimeFrameDefaultOption(this.timeFrameOptions.map(timeFrame => timeFrame.value), res.selectedTimeFrame);
      },
      // onSelectAccount() {
      //   this.refreshDataWhenChangeOption()
      // },
      onSelectTimeFrame() {
        // this.refreshDataWhenChangeOption()
        this.tempSelectedTimeFrame = this.selectedTimeFrame;
      },
      onSelectVendor() {
        this.selectedAccount = "";
        this.refreshDataWhenChangeOption()
      },
      onSelectDateType() {
        const isSelectDateType = true;
        this.refreshDataWhenChangeOption(isSelectDateType)
      },
      calculateTimeFrameOptions(dateType, portionData) {
        switch (dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY:
            this.selectedDateType = DASHBOARD_DATE_TYPE.MONTHLY;
            this.timeFrameOptions = getTimeFrameListOption(this.selectedDateType, portionData.timeFrameList, this);
            break;
          case DASHBOARD_DATE_TYPE.WEEKLY:
            this.selectedDateType = DASHBOARD_DATE_TYPE.WEEKLY;
            this.timeFrameOptions = getTimeFrameListOption(this.selectedDateType, portionData.timeFrameList, this);
            break;
        }
      },
      setTimeFrameDefaultOption(timeFrameOptionValues, selectedTimeFrame) {
        if (_isNil(this.tempSelectedTimeFrame) || !timeFrameOptionValues.includes(this.tempSelectedTimeFrame) || !timeFrameOptionValues.includes(selectedTimeFrame)) {
          this.selectedTimeFrame = this.timeFrameOptions[0].value
        } else {
          this.selectedTimeFrame = selectedTimeFrame;
        }
      },
      setAxisFieldName() {
        switch (_get(this.widgetConfig, 'scale')) {
          case SCALE.VALUE:
            this.xAxisFieldName = SCALE.COST;
            this.scaleValue = SCALE.VALUE;
            break;
          case SCALE.PERCENTAGE:
            this.xAxisFieldName = SCALE.PERCENTAGE
            this.scaleValue = SCALE.PERCENTAGE;
            break;
          default:
            this.xAxisFieldName = SCALE.PERCENTAGE;
            this.scaleValue = SCALE.PERCENTAGE;
            break;
        }
      },
    },
  };

</script>
<style lang="scss" scoped>
  #view-by-group {
    position: relative;
    #view-by {
      &:focus {
        box-shadow: none !important;
      }
    }
  }
  #time-frame-group {
    position: relative;
    #set-time-frame {
      &:focus {
        box-shadow: none !important;
      }
    }
  }
  .cancel-button {
    margin-right: 6px;
  }

  .edit-portion-by-account-small-dropdown {
    .custom-select {
      width: 50%;
    }
  }
  /* OIO-3674 '단위 설정' 라디오버튼 레이블 색상 설정값 삭제 */
  /*#scale-radio-group {*/
  /*  color: #b0b7bf*/
  /*}*/

  .portion-widget-chart {
    height: 152px;
    .preview-bar-chart-wrapper {
      max-height: 152px;
      padding: 0px;
      .card-body {
        padding: 0px;
        .bar-chart-content {
          width: 276px;
          margin: auto;
        }
      }
    }
  }

</style>
