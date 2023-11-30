<template>
  <div class="abnormal-change-wrapper">
    <b-card
      :id="widgetWrapperId"
      border-variant="transparent"
      class="card-custom dashboard-widget-height"
      header-bg-variant="transparent"
      header-border-variant="lightgray-1">
      <b-row
        slot="header"
        align-h="between"
        align-v="center"
        class="px-20 py-16 abnormal-change-header"
        no-gutters>
        <b-row
          class="abnormal-change-header-left"
          no-gutters>
          <b-col cols="12">
            <div class="font-16 medium font-family-notosanscjkkr-medium abnormal-change-view-by">
              <b-row
                no-gutters
                align-v="center">
                <span
                  :id="headerLeftSide1"
                  class="-tag gray-">
                  <span class="material-icons">person</span>
                  <p>User</p>
                </span>
                <span :id="headerLeftSide2">
                  {{ selectedVendor }}
                </span>
                <span
                  ref="widgetTitle"
                  :id="widgetTitle"
                  class="-ellipsis pl-1">{{ getTitle }}</span>
              </b-row>
              <b-tooltip
                :disabled="!isWidgetTitleTooltipEnabled"
                :target="widgetTitle"
                custom-class="custom-accountOptions-tooltip"
                triggers="hover"
              >
                <div>{{ getTitle }}</div>
              </b-tooltip>
            </div>
          </b-col>
        </b-row>
        <b-row
          :id="headerRightSide"
          class="font-family-notosanscjkkr-medium abnormal-change-header-right"
        >
          <base-dropdown
            ref="abnormalChangeViewByDropdown"
            :options="abnormalChangeViewByOptions"
            :enabled-localization="true"
            :disabled="widgetLoadingState[widgetConfig.index]"
            variant="default"
            class="font-family-notosanscjkkr-medium custom-abnormal-view-by-dropdown"
            @selectOption="onSelectOptionViewBy"
            @mounted="onAbnormalChangeViewByDropdownMounted"
          />
          <p class="change-title pl-2">{{ $t('dashboard.abnormalChange.change') }}</p>
          <base-dropdown
            ref="abnormalThresholdDropdown"
            :options="thresholdOptions"
            :disabled="widgetLoadingState[widgetConfig.index]"
            variant="default"
            class="font-family-notosanscjkkr-medium custom-abnormal-threshold-dropdown"
            @selectOption="onThresholdChanged"
            @mounted="onThresholdDropdownMounted"
          />
          <b-button
            :id="abnormalTableOption.detailTableOptionBtnId"
            :disabled="widgetLoadingState[widgetConfig.index]"
            class="dropdown-button"
            variant="transparent">
            <!--@click="togglePopoverOption">-->
            <base-material
              :size="24"
              name="more_vert"
              color="gray-1"/>
          </b-button>
          <div
            :id="containerCustomPopover"
            class="custom-popover-abnormal-change"/>
          <BasePopoverDropdown
            ref="abnormalPopover"
            :id="abnormalDropdown"
            :target="abnormalTableOption.detailTableOptionBtnId"
            :placement="abnormalTableOption.placement"
            :options="abnormalTableOption.options"
            :show-popover="abnormalTableOption.showPopover"
            :enabled-localization="abnormalTableOption.enabledLocalization"
            :container-custom-popover="containerCustomPopover"
            @selectOption="onSelectOption"
          />
        </b-row>
        <b-col
          cols="12"
          class="abnormal-change-header-tooltip">
          <p
            class="sub-title"
          >
            <span>{{ renderTimeFrameLabel }}</span>
            <span
              :id="dashboardDateRange"
              class="material-icons color-gray-1 dashboard-date-range">info
            </span>
            <!--            <span> | {{ $t('dashboard.abnormalChange.change') }} {{ getThresholdOptionText }}</span>-->
          </p>
        </b-col>
      </b-row>
      <div v-show="widgetLoadingState[widgetConfig.index]">
        <b-col
          class="dashboard-widget-height"
        >
          <BaseLoadingIndicator/>
        </b-col>
      </div>
      <div v-show="!widgetLoadingState[widgetConfig.index] && typeof selectedVendor == 'undefined'">
        <b-col
          class="dashboard-widget-height"
        >
          <BaseNotificationNotSupport
            :support-vendors="supportVendors"
            class="no-data"
          />
        </b-col>
      </div>
      <div v-show="!widgetLoadingState[widgetConfig.index] && typeof selectedVendor != 'undefined'">
        <b-row
          v-if="hasAnomalyDetect"
          no-gutters
          class="-inline-notification with-icon- red- w-100 px-0">
          <b-button
            :class="!enableDrillDown ? 'cursor-default' : ''"
            class="-bg-transparent -with-btn-vue w-100 h-100"
            @click="changeHasAnomalyDetect()">
            <span class="material-icons">warning</span>
            <p class="anomaly-detect-text"> {{ getAnomalyDetectText }} </p>
            <p
              v-if="enableDrillDown"
              class="-icon-wrapper">
              <span
                class="material-icons -font-size-24"
                @click="changeHasAnomalyDetect()">keyboard_arrow_right</span>
            </p>
          </b-button>
        </b-row>
        <b-col :class="enableDrillDown?'abnormal-change-table-wrapper enable-drill-down':'abnormal-change-table-wrapper'">
          <!--
          <ag-grid-vue
            :animate-rows="true"
            :column-defs="columnDefs"
            :default-col-def="defaultColDef"
            :enable-range-selection="false"
            :grid-options="gridOptions"
            :header-height="30"
            :pagination="false"
            :row-data="standardizedAbnormals"
            :row-height="40"
            :sorting-order="sortingOrder"
            :framework-components="frameworkComponents"
            :loading-overlay-component="loadingOverlayComponent"
            :loading-overlay-component-params="loadingOverlayComponentParams"
            :no-rows-overlay-component="noRowsOverlayComponent"
            :no-rows-overlay-component-params="noRowsOverlayComponentParams"
            :enable-browser-tooltips="true"
            :style="{height: agGridHeight + 'px'}"
            class="ag-theme-bootstrap abnormal-change-table"
            @grid-ready="onGridReady"
            @column-moved="columnMoved"
            @cellClicked="onCellClicked"
          />
          -->
          <PrimeAbnormalChangeTable
            v-if="showPrimeComponent"
            :columns="columnDefs"
            :row-data="standardizedAbnormals"
            :selected-vendor="selectedVendor"
            :selected-currency="internalCommonUserInfo.selectedCurrency"
            :internal-widget-config="internalWidgetConfig"
            :value-prefix="currencySymbol"
            :widget-size-w="widgetConfig.w"
            :widget-size-h="widgetConfig.h"
            @cellClicked="onCellClicked"
          />
        </b-col>
      </div>
      <!--
      <div class="divider"/>
      -->
      <b-row
        v-show="!widgetLoadingState[widgetConfig.index] && enableDetectList"
        no-gutters
        class="anomaly-widget-footer with-icon- w-100 px-0 font-14 font-family-notosanscjkkr-regular p-divider">
        <b-button
          v-show="typeof selectedVendor != 'undefined'"
          class="go-to-alert-list -bg-transparent -with-btn-vue"
          @click="goToAnomalyList()">
          <p style="margin-bottom: 2px"> {{ $t('dashboard.abnormalChange.viewAlertList') }} </p>
          <p
            class="-icon-wrapper"
            style="margin-top:4px">
            <span
              class="material-icons -font-size-24">keyboard_arrow_right</span>
          </p>
        </b-button>
      </b-row>
      <b-modal
        ref="edit-abnormal-form"
        v-model="widgetConfig.isEditFormVisible"
        :title="$t('dashboard.abnormalChange.editUserRuleAbnormalChangeByWidget')"
        modal-class="right-wing"
        hide-footer
        hide-backdrop
      >
        <div v-if="widgetConfig.isEditFormVisible">
          <EditAbnormalForm
            :common-user-info="internalCommonUserInfo"
            :exchange-rate="internalExchangeRate"
            :dashboard-view-mode="dashboardViewMode"
            :widget-config="previewWidgetConfig"
            class="edit-abnormal-form-wrapper"
            @hideModal="hideModal"
            @save="onSaveEditAbnormalForm"
          />
        </div>
      </b-modal>
      <b-tooltip
        :id="dashboardDate"
        :target="dashboardDateRange"
        class="dashboard-date"
      >
        {{ renderTimeFrameLabelWithTime }}
        <br>
        {{ renderTimeFrameLabelWithTimeRange }}
      </b-tooltip>
    </b-card>
  </div>
</template>

<script>
  import PrimeAbnormalChangeTable from '@/components/common/prime-abnormal-table/PrimeAbnormalChangeTable';
  // BackUp
  // import { AgGridVue } from 'ag-grid-vue';
  import _isNil from 'lodash/isNil';
  import {calculateCostByCurrency, formatCost, formatPercentage} from '@/util/costUtils';
  import BaseDropdown from '@/components/common/BaseDropdown';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNotSupport from '@/components/common/BaseNotificationNotSupport';
  import { CURRENCY_SYMBOL, DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE, ROUTE_NAME } from '@/constants/constants';
  import {
    ABNORMAL_THRESHOLD,
    ABNORMAL_TIME_FRAME,
    ABNORMAL_NOTIFICATION,
    ABNORMAL_VIEW_BY_VENDORS,
    AI_ABNORMAL_ALARM,
    AI_ABNORMAL_ALARM_CHANNEL,
    ABNORMAL_GRID_SORT_ALARM_LEVEL,
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
    DEFAULT_WIDGET_DATA,
    THRESHOLD_OPTIONS,
    VIEW_MODE,
    DASHBOARD_VIEW_BY_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS_NO_REGION,
    DASHBOARD_VIEW_BY,
    DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR,
    SELECTED_VENDOR
  } from '@/constants/dashboardConstants';
  import dayjs from 'dayjs';
  import _isEmpty from 'lodash/isEmpty';
  import _isEqual from 'lodash/isEqual';
  import _cloneDeep from 'lodash/cloneDeep';
  import _get from 'lodash/get';
  import { isAbnormalChangeWidgetDataConfigChanged, getSelectedVendorsByWidget } from '@/util/dashboardUtils';
  import { getDisplayItemWithVendorBaseOnViewBy } from '@/util/formatAccountUtils';
  import ShowEditFormModalMixin from '@/mixins/ShowEditFormModalMixin';
  import { fetchDashboardAbnormal } from '@/api/dashboard';
  import { Trans } from "@/components/common/base-i18n/Translation";
  import {SUPPORTED_LANGUAGE} from "@/constants/trans";

  const EditAbnormalForm = () => import('@/components/pages/dashboard/abnormal-change/EditAbnormalForm');
  import CustomLoadingOverlay from '@/components/common/custom-loading-overlay/CustomLoadingOverlay';
  // import CustomNoRowsOverlay from '@/components/common/custom-no-rows-overlay/CustomNoRowsOverlay';
  import CustomNoAnomalyCostDataOverlay from '@/components/common/custom-no-anomaly-cost-data-overlay/CustomNoAnomalyCostDataOverlay';
  import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from '@/constants/costAnalyticsConstants';
  import {
    initWorkBookViaExcelJs,
    saveAndReturnSupportedUTF18CSVFile
  } from "@/util/excelJS";

  const DATE_FORMAT = 'YYYY-MM-DD';
  const DISTANCE_BETWEEN_OUTSIDE = 32;
  const HEADER_RIGHT_SIDE_WIDTH = 298; // 위젯 타이틀 영역 길이 제한용 값

  export default {
    name: 'DashboardAbnormalChange',
    components: {
      PrimeAbnormalChangeTable,
      // BackUp
      // AgGridVue,
      BaseDropdown,
      EditAbnormalForm,
      BaseLoadingIndicator,
      BaseNotificationNotSupport
    },
    mixins: [ShowEditFormModalMixin],
    props: {
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
      widgetConfig: {
        type: Object,
        required: true,
        default: null
      },
      dashboardViewMode: {
        type: String,
        default: VIEW_MODE.DEFAULT
      },
      currentDashboard: {
        type: Object,
        required: true
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
      isSidebarActive: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        showPrimeComponent: true,
        rawAbnormals: DEFAULT_WIDGET_DATA.ABNORMAL_CHANGE,
        internalWidgetConfig: DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendorsByWidget: []
        },
        internalExchangeRate: DEFAULT_EXCHANGE_RATE,
        previewWidgetConfig: {
          threshold: ABNORMAL_THRESHOLD._10,
          viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
          selectedVendorsByWidget: [],
          isAbnormalNotiOn:ABNORMAL_NOTIFICATION.NOTIFICATION_OFF,
          mailSendCond:{},
          alarmCondition:AI_ABNORMAL_ALARM.BY_INTEGRATION,
          alarmChannel: [AI_ABNORMAL_ALARM_CHANNEL.BY_EMAIL],
          mailReceivers:{}
        },
        // BackUp
        // gridOptions: null,
        columnDefs: [],
        sortingOrder: null,
        defaultColDef: null,
        thresholdOptions: THRESHOLD_OPTIONS,
        containerCustomPopover: `abnormal-custom-popover-${this.widgetConfig.index}`,
        abnormalDropdown: `abnormal-dropdown-${this.widgetConfig.index}`,
        dashboardDateRange: `dashboard-date-range-${this.widgetConfig.index}`,
        dashboardDate: `dashboard-date-${this.widgetConfig.index}`,
        widgetTitle: `widget-title-${this.widgetConfig.index}`,
        widgetWrapperId: `widget-wrapper-id-${this.widgetConfig.index}`,
        headerLeftSide1: `header-left-side-1-${this.widgetConfig.index}`,
        headerLeftSide2: `header-left-side-2-${this.widgetConfig.index}`,
        headerRightSide: `header-right-side-${this.widgetConfig.index}`,
        abnormalTableOption: {
          options: DASHBOARD_DROPDOWN_OPTIONS,
          detailTableOptionBtnId: `abnormal-option-btn-${this.widgetConfig.index}`,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        noDataContentDisplayed: false,
        showEditFormModal: false,
        frameworkComponents: null,
        loadingOverlayComponent: null,
        loadingOverlayComponentParams: null,
        noRowsOverlayComponent: null,
        noRowsOverlayComponentParams: null,
        widgetCurrency : this.$store.state.common.info.currencies.KRW,
        hasAnomalyDetect : false,
        isWidgetTitleTooltipEnabled: false,
        // BackUp
        // AG_GRID_MAX_HEIGHT: 370,
        supportVendors: ABNORMAL_VIEW_BY_VENDORS
      };
    },
    computed: {
      currencySymbol: function() {
        return CURRENCY_SYMBOL[this.internalCommonUserInfo ? this.internalCommonUserInfo.selectedCurrency : CURRENCY.USD];
      },
      standardizedAbnormals: {
        cache: true,
        get() {
          let abnormalList = _get(this.rawAbnormals, 'abnormalList') || []
          return abnormalList
            .filter(item => Math.abs(item.increaseDecreaseRate) >= this.previewWidgetConfig.threshold)
            .map(item => {
              return {
                ...item,
                currentCost: calculateCostByCurrency(item.currentCost, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate),
                lastCost: calculateCostByCurrency(item.lastCost, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate)
              }
            })
        },
      },
      renderTimeFrameLabelWithTime: {
        cache: true,
        get() {
          return this.getRenderTimeFrameLabelWithTime(this)
        }
      },
      renderTimeFrameLabelWithTimeRange: {
        cache: true,
        get() {
          return this.getRenderTimeFrameLabelWithTimeRange(this)
        }
      },
      renderTimeFrameLabel: {
        cache: true,
        get() {
          return this.getRenderTimeFrameLabel(this)
        }
      },
      i18nViewBy() {
        const langKey = `dashboard.viewByOption.${this.internalWidgetConfig.viewBy}`;
        return this.$t(langKey);
      },
      selectedVendor: {
        get() {
          return getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS, true);
        }
      },
      getTitle: {
        get() {
          let title = _isEmpty(this.previewWidgetConfig.title) ? this.$t('dashboard.abnormalChange.widgetTitle.userRule') : this.previewWidgetConfig.title;
          return title;
        }
      },
      getAnomalyDetectText: {
        get() {
          let increaseDecreaseTextOption = (this.rawAbnormals.totalIncreaseDecreaseCost >= 0) ? 'anomalyDetectIncreasedTextOptions' : 'anomalyDetectDecreasedTextOptions';
          let cost = `${CURRENCY_SYMBOL[this.internalCommonUserInfo.selectedCurrency]}${formatCost(Math.abs(calculateCostByCurrency(this.rawAbnormals.totalIncreaseDecreaseCost, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate)))}`;
          let rate = formatPercentage(Math.abs(this.rawAbnormals.totalIncreaseDecreaseRate));
          switch (this.internalWidgetConfig.timeFrame) {
            case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
              return this.$t('dashboard.abnormalChange.' + increaseDecreaseTextOption + '.latest3DayTotalVs3DaysBefore',
                {
                  'count': `${this.rawAbnormals.abnormalList.length}`,
                  'viewBy': this.getViewByText(),
                  'cost': cost,
                  'rate': rate
                });
            case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
              return this.$t('dashboard.abnormalChange.' + increaseDecreaseTextOption + '.latest7DayTotalVs7DaysBefore',
                {
                  'count': `${this.rawAbnormals.abnormalList.length}`,
                  'viewBy': this.getViewByText(),
                  'cost': cost,
                  'rate': rate
                });
            case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
              return this.$t('dashboard.abnormalChange.' + increaseDecreaseTextOption + '.latestTotalCostVsAverageCostOfLatest7Days',
                {
                  'count': `${this.rawAbnormals.abnormalList.length}`,
                  'viewBy': this.getViewByText(),
                  'cost': cost,
                  'rate': rate
                });
            case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
              return this.$t('dashboard.abnormalChange.' + increaseDecreaseTextOption + '.thisMonthSoFarVsSamePeriodOfLastMonth',
                {
                  'count': `${this.rawAbnormals.abnormalList.length}`,
                  'viewBy': this.getViewByText(),
                  'cost': cost,
                  'rate': rate
                });
          }
        }
      },
      // BackUp
      // agGridHeight: function(){
      //   let agGridHeight = this.AG_GRID_MAX_HEIGHT;
      //   if(this.hasAnomalyDetect){
      //     agGridHeight -= 40
      //   }
      //   if(this.enableDetectList){
      //     agGridHeight -= 25
      //   }
      //   return agGridHeight;
      // },
      enableDrillDown: {
        get() {
          if(_isEqual(this.profile.env, "CHINA") && _isEqual(this.selectedVendor, "GCP"))
            return false
          return this.Common.checkCostAnalyticsMenuAuth(this) && this.Common.checkVendorAvailableFromSelectedVendor(getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS),COST_ANALYTICS_VIEW_BY_VENDORS )
        }
      },
      enableDetectList: {
        get(){
          return this.Common.checkCostAnomalyDetectionAuth(this)
        }
      },
      abnormalChangeViewByOptions: {
        get(){
          let vendor = '';
          if(this.widgetConfig.selectedVendorsByWidget === undefined) {
            vendor = getSelectedVendorsByWidget(this.widgetConfig, this, ABNORMAL_VIEW_BY_VENDORS)
          } else {
            vendor = this.widgetConfig.selectedVendorsByWidget[0]
          }
          if(!_isEmpty(DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[vendor])){
            return DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[vendor]
          }else{
            return DASHBOARD_VIEW_BY_OPTIONS
          }
        }
      }
    },
    watch: {
      currentDashboard: {
        handler() {
          // BackUp
          // this.gridColumnApi.resetColumnState();
          this.resetPrimeComponent();
        }
      },
      'commonUserInfo.selectedVendorsByWidget': {
        handler(newSelectedVendors) {
          if (_isEqual(this.internalCommonUserInfo.selectedVendorsByWidget, newSelectedVendors)) {
            return;
          }
          this.internalCommonUserInfo.selectedVendorsByWidget = _cloneDeep(this.commonUserInfo.selectedVendorsByWidget);
        },
        immediate: false
      },
      widgetConfig: {
        handler(newValue, oldValue) {
          if (!isAbnormalChangeWidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if (!isAbnormalChangeWidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            //In case user change preview widget config in dashboard page & click to open edit form modal but make no change and then click save button
            // -> we have to update preview widget config like edit form modal
            if (isAbnormalChangeWidgetDataConfigChanged(this.previewWidgetConfig, this.widgetConfig, true)) {
              if(_isNil(this.widgetConfig.isNew)){
                this.updatePreviewWidgetConfig();
                this.resetPrimeComponent();
              }
            } else {
              this.hideModal();
            }
            return;
          }
          this.resetPrimeComponent();
          this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: true
      },
      internalWidgetConfig: {
        handler() {
          if (_isNil(this.internalWidgetConfig)) {
            return;
          }
          this.updatePreviewWidgetConfig();
          this.resetPrimeComponent();
        },
        immediate: true
      },
      'commonUserInfo.selectedCurrency': {
        handler(newSelectedCurrency) {
          if (this.internalCommonUserInfo.selectedCurrency === newSelectedCurrency) {
            return;
          }
          this.internalCommonUserInfo.selectedCurrency = this.commonUserInfo.selectedCurrency;
          this.resetPrimeComponent();
        },
        immediate: true
      },
      'internalCommonUserInfo.selectedVendorsByWidget': {
        handler() {
          const widgetConfig = {
            ...this.internalWidgetConfig,
            ...this.previewWidgetConfig
          };
          this.loadAbnormal(widgetConfig)
        },
        immediate: false
      },
      exchangeRate: {
        handler() {
          if (_isEqual(this.internalExchangeRate, this.exchangeRate)) {
            return;
          }
          this.internalExchangeRate = _cloneDeep(this.exchangeRate);
        },
        immediate: true
      },
      dashboardViewMode: function () {
        this.setAbnormalTableOption();
      },
      '$i18n.locale': {
        handler() {
          this.columnDefs = this.getColumnDefs();
          // BackUp
          // this.setViewByHeaderValue();
          // this.genderHeaderColumnsTime(this);
          // this.gridOptions.api.setColumnDefs(this.columnDefs);
          // this.gridOptions.api.refreshHeader();
          // this.gridOptions.api.sizeColumnsToFit();
          this.handleWindowResize();
          this.resetPrimeComponent();
        },
      },
      isSidebarActive: function () {
        setTimeout(this.handleWindowResize, 100);
      },
    },
    created() {
      const $vm = this;
      // BackUp
      // this.gridOptions = {
      //   localeText: {
      //     noRowsToShow: this.$t('billing.chargeList.noResultsFound')
      //   },
      //   processCellForClipboard: params => {
      //     if (params.column.colId && ['currentCost', 'lastCost', 'increaseDecreaseRate'].includes(params.column.colDef.field)) {
      //       return _get(params, `node.data.${params.column.colDef.field}`);
      //     }
      //     return params.value;
      //   }
      // };
      this.columnDefs = this.getColumnDefs();
      this.defaultColDef = {
        sortable: true, suppressMenu: true, resizable: true, unSortIcon: true, suppressHorizontalScroll: true
      };
      this.sortingOrder = [null, 'desc', 'asc'];
      this.frameworkComponents = {
        customLoadingOverlay: CustomLoadingOverlay,
        customNoRowsOverlay: CustomNoAnomalyCostDataOverlay
      };
      this.loadingOverlayComponent = "customLoadingOverlay";
      this.loadingOverlayComponentParams = {};
      this.noRowsOverlayComponent = "customNoRowsOverlay";
      this.noRowsOverlayComponentParams = {};
      if (_isEmpty(this.internalCommonUserInfo.selectedVendors)) {
        this.loadingOverlayComponent = "customNoRowsOverlay";
        this.loadingOverlayComponentParams = {};
      }
    },
    mounted() {
      // BackUp
      // this.gridApi = this.gridOptions.api;
      // this.gridColumnApi = this.gridOptions.columnApi;
      // this.genderHeaderColumnsTime(this);
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setAbnormalTableOption();
      // this.gridOptions.api.showLoadingOverlay();
      window.addEventListener('resize', this.handleWindowResize);
    },
    methods: {
      resetPrimeComponent() {
        this.showPrimeComponent = false;
        this.$nextTick(() => {
          this.showPrimeComponent = true;
        })
      },
      getColumnDefs() {
        return [
          {
            headerName: this.$t('dashboard.abnormalChange.alarmLevel'),
            headerTooltip: this.$t('dashboard.abnormalChange.alarmLevel'),
            field: 'alarmLevel',
            cellRenderer: alarmLevelRender(this),
            tooltipValueGetter: alarmLevelGetter(this),
            valueGetter: alarmLevelGetter(this),
            width: 100,
            comparator: (valueA, valueB, nodeA, nodeB, isInverted) => customAlarmComparator(valueA, valueB, nodeA, nodeB, isInverted)
          },
          {
            headerName: this.getViewByText(),
            headerTooltip: this.getViewByText(),
            // BackUp
            // field: 'linkedAccountAlias',
            field: 'itemAlias',
            cellRenderer: serviceRender(this),
            tooltipValueGetter: serviceRender(this),
            valueGetter: serviceGetter,
            width: 160,
            comparator: customComparator,
          },
          {
            headerName: this.$t('dashboard.abnormalChange.thisMonth'),
            headerTooltip: this.$t('dashboard.abnormalChange.thisMonth'),
            field: 'currentCost',
            cellStyle: { textAlign: 'right' },
            cellRenderer: currentMonthCostRenderCallback(this),
            tooltipValueGetter: currentMonthCostRenderCallback(this),
            valueFormatter: formatCurrentCostValueGetter,
            width: 180,
          },
          {
            headerName: this.$t('dashboard.abnormalChange.lastMonth'),
            headerTooltip: this.$t('dashboard.abnormalChange.lastMonth'),
            field: 'lastCost',
            cellRenderer: lastMonthCostRenderCallback(this),
            tooltipValueGetter: lastMonthCostRenderCallback(this),
            cellStyle: { textAlign: 'right' },
            valueFormatter: formatLastCostValueGetter,
            width: 180,
          },
          {
            headerName: this.$t('dashboard.abnormalChange.change'),
            headerTooltip: this.$t('dashboard.abnormalChange.change'),
            field: 'increaseDecreaseRate',
            cellStyle: { textAlign: 'right' },
            width: 100,
            tooltipValueGetter: increaseDecreaseRateGTooltip,
            cellRenderer: increaseDecreaseRateRender,
            valueFormatter: increaseDecreaseRateGetter,
            comparator: (valueA, valueB, nodeA, nodeB, isInverted) => changeComparator(valueA, valueB, nodeA, nodeB, isInverted)
          },
          // BackUp
          // {
          //   headerName: this.$t('dashboard.abnormalChange.vendor'),
          //   field: 'vendor',
          //   hide: true,
          // },
        ];
      },
      onAbnormalChangeViewByDropdownMounted() {
        const selectedAbnormalChangeViewByOpt = this.abnormalChangeViewByOptions.find(opt => opt.value === this.internalWidgetConfig.viewBy);
        this.$refs.abnormalChangeViewByDropdown.changeSelectedOptionText(selectedAbnormalChangeViewByOpt.text);
      },
      handleWindowResize() {
        this.setWidgetTitleArea();
        this.resetPrimeComponent();
      },
      onSelectOptionViewBy(option) {
        if (this.previewWidgetConfig.viewBy === option) {
          return;
        }
        this.previewWidgetConfig.viewBy = option;
        this.internalWidgetConfig.viewBy = option;
        let widgetConfig = {
          ...this.internalWidgetConfig,
          ...this.previewWidgetConfig
        };
        this.loadAbnormal(widgetConfig)
        this.resetPrimeComponent();
      },
      makeSizeColumnsToFit() {
        const $vm = this;
        setTimeout(function() {
          //We have to use timeout & $nextTick to make sizeColumnsToFit workable
          // BackUp
          // $vm.gridApi.sizeColumnsToFit();
        }, 0);
        this.$nextTick();
      },
      updatePreviewWidgetConfig() {
        this.previewWidgetConfig = {
          ...this.internalWidgetConfig,
          minAlert : _isNil(this.internalWidgetConfig.minAlert)?100:this.internalWidgetConfig.minAlert,
          maxAlert :_isNil(this.internalWidgetConfig.maxAlert)?1000:this.internalWidgetConfig.maxAlert,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS)],
          isAbnormalNotiOn:_isNil(this.internalWidgetConfig.isAbnormalNotiOn)?ABNORMAL_NOTIFICATION.NOTIFICATION_OFF:this.internalWidgetConfig.isAbnormalNotiOn,
          mailSendCond:_isNil(this.internalWidgetConfig.mailSendCond)?{}:this.internalWidgetConfig.mailSendCond,
          alarmCondition:_isNil(this.internalWidgetConfig.alarmCondition)?AI_ABNORMAL_ALARM.BY_INTEGRATION:this.internalWidgetConfig.alarmCondition,
          mailReceivers:_isNil(this.internalWidgetConfig.mailReceivers)?{}:this.internalWidgetConfig.mailReceivers,
          alarmChannel:_isEmpty(this.internalWidgetConfig.alarmChannel)?[]:this.internalWidgetConfig.alarmChannel
        };
        if (!_isNil(this.$refs.abnormalThresholdDropdown)) {
          const selectedThresholdOpt = this.thresholdOptions.find(opt => opt.value === this.internalWidgetConfig.threshold);
          this.$refs.abnormalThresholdDropdown.changeSelectedOptionText(selectedThresholdOpt.text);
        }
        if (!_isNil(this.$refs.abnormalChangeViewByDropdown)) {
          const selectedAbnormalChangeViewByOpt = this.abnormalChangeViewByOptions.find(opt => opt.value === this.internalWidgetConfig.viewBy);
          this.$refs.abnormalChangeViewByDropdown.changeSelectedOptionText(selectedAbnormalChangeViewByOpt.text);
        }
        this.loadAbnormal(this.internalWidgetConfig);
        // BackUp
        // if (!_isEmpty(this.columnDefs) && !_isNil(this.gridOptions)) {
        //   this.genderHeaderColumnsTime(this);
        // }
        // this.resetPrimeComponent();
      },
      // BackUp
      // loadAbnormal(widgetConfig) {
      //   this.widgetLoadingState[this.widgetConfig.index] = true;
      //   this.widgetConfig.isEditFormVisible = false;
      //   let payload = {
      //     threshold: widgetConfig.threshold,
      //     timeFrame: widgetConfig.timeFrame,
      //     viewBy: widgetConfig.viewBy,
      //     // vendors: this.internalCommonUserInfo.selectedVendors,
      //     selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS)],
      //     widgetCurrency: this.widgetCurrency,
      //     minAlert: this.previewWidgetConfig.minAlert,
      //     maxAlert: this.previewWidgetConfig.maxAlert
      //   };
      //   if(payload.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP)) {
      //     if(_isEqual(payload.viewBy, "account")) {
      //       payload.viewBy = "project";
      //     }
      //   }
      //   fetchDashboardAbnormal(payload)
      //     .then(res => {
      //       this.widgetLoadingState[this.widgetConfig.index] = false;
      //       this.rawAbnormals = res;
      //       this.genderHeaderColumnsTime(this);
      //       this.getRenderTimeFrameLabelWithTime(this);
      //       this.getRenderTimeFrameLabel(this);
      //       this.setSortModel();
      //       this.onAbnormalChangeViewByDropdownMounted();
      //       this.getAnomalyDetectSummary();
      //       this.setViewByHeaderValue();
      //       this.setWidgetTitleArea();
      //     })
      //     .catch(err => {
      //       this.widgetLoadingState[this.widgetConfig.index] = false;
      //       console.error(err);
      //       this.rawAbnormals = null;
      //       this.onAbnormalChangeViewByDropdownMounted();
      //       this.hasAnomalyDetect = false;
      //       this.setWidgetTitleArea();
      //     })
      // },
      loadAbnormal(widgetConfig) {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;
        this.columnDefs = this.getColumnDefs(); // for refresh columns
        let payload = {
          threshold: widgetConfig.threshold,
          timeFrame: widgetConfig.timeFrame,
          viewBy: widgetConfig.viewBy,
          // vendors: this.internalCommonUserInfo.selectedVendors,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS)],
          widgetCurrency: this.widgetCurrency,
          minAlert: this.previewWidgetConfig.minAlert,
          maxAlert: this.previewWidgetConfig.maxAlert
        };
        if(payload.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP)) {
          if(_isEqual(payload.viewBy, "account")) {
            payload.viewBy = "project";
          }
        }
        fetchDashboardAbnormal(payload)
          .then(res => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.rawAbnormals = res;
            // this.genderHeaderColumnsTime(this);
            this.getRenderTimeFrameLabelWithTime(this);
            this.getRenderTimeFrameLabel(this);
            // this.setSortModel();
            // this.onAbnormalChangeViewByDropdownMounted();
            this.getAnomalyDetectSummary();
            // this.setViewByHeaderValue();
            // this.setWidgetTitleArea();
          })
          .catch(err => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            console.error(err);
            this.rawAbnormals = null;
            this.onAbnormalChangeViewByDropdownMounted();
            this.hasAnomalyDetect = false;
            this.setWidgetTitleArea();
          })
      },
      setAbnormalTableOption() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.abnormalTableOption.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.abnormalTableOption.options = DASHBOARD_DROPDOWN_OPTIONS
        }
      },
      // BackUp
      // onGridReady(params) {
      //   this.api = params.api;
      //   let sortDefault = [
      //     {
      //       colId: "increaseDecreaseRate",
      //       sort: "desc"
      //     }
      //   ];
      //   this.gridApi.setSortModel(sortDefault);
      //   this.gridApi.sizeColumnsToFit();
      // },
      columnMoved() {
        if (this.internalWidgetConfig.isColumnSave) {
          this.onSaveEditAbnormalForm(this.internalWidgetConfig);
        }
      },
      onThresholdChanged(threshold) {
        if (this.previewWidgetConfig.threshold === threshold) {
          return;
        }
        this.previewWidgetConfig.threshold = threshold;
        let widgetConfig = {
          ...this.internalWidgetConfig,
          ...this.previewWidgetConfig
        };
        this.loadAbnormal(widgetConfig);
        this.resetPrimeComponent();
      },
      onSelectOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            // BackUp
            // this.exportAbnormalList();
            this.exportPrimeAbnormalList();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_WIDGET: {
            this.$emit('delete');
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DUPLICATE_WIDGET: {
            this.$emit('duplicateWidget');
            break;
          }
        }
        this.$refs.abnormalPopover.close();
      },
      // togglePopoverOption() {
      //   this.abnormalTableOption.showPopover = !this.abnormalTableOption.showPopover
      // },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      },
      getRenderTimeFrameLabelWithTime($vm) {
        const rangeTime = exportRangeTime(this.internalWidgetConfig.timeFrame, getFullDateFormatByLocalization(), $vm);
        switch (this.internalWidgetConfig.timeFrame){
          case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latest3DayTotalVs3DaysBeforeWithTime.#1');
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latest7DayTotalVs7DaysBeforeWithTime.#1');
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latestTotalCostVsAverageCostOfLatest7DaysWithTime.#1');
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.thisMonthSoFarVsSamePeriodOfLastMonthWithTime.#1');
          default:
            return ''
        }
      },
      getRenderTimeFrameLabelWithTimeRange($vm){
        const rangeTime = exportRangeTime(this.internalWidgetConfig.timeFrame, getFullDateFormatByLocalization(), $vm)
        switch (this.internalWidgetConfig.timeFrame){
          case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
            return '';
            // OIO-3533 User Dashboard 이상비용 위젯 툴팁 terminology 변경(툴팁에서 조회기간 삭제)
            // return this.$t('dashboard.abnormalChange.timeFrameOptions.latest3DayTotalVs3DaysBeforeWithTime.#2',
            //   { 'first': `${rangeTime.latest3dayStart} ~ ${rangeTime.latest3dayEnd}`,
            //     'second': `${rangeTime.before3dayEnd} ~ ${rangeTime.before3dayStart}` });
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
            return '';
            // OIO-3533 User Dashboard 이상비용 위젯 툴팁 terminology 변경(툴팁에서 조회기간 삭제)
            // return this.$t('dashboard.abnormalChange.timeFrameOptions.latest7DayTotalVs7DaysBeforeWithTime.#2',
            //   { 'first': `${rangeTime.latest7dayStart} ~ ${rangeTime.latest7dayEnd}`,
            //     'second': `${rangeTime.before7dayEnd} ~ ${rangeTime.before7dayStart}` });
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latestTotalCostVsAverageCostOfLatest7DaysWithTime.#2',
              { 'first': `${rangeTime.latestTotalCost}`,
                'second': `${rangeTime.average7DayEnd} ~ ${rangeTime.latestDay}` });
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.thisMonthSoFarVsSamePeriodOfLastMonthWithTime.#2',
              { 'first': `${rangeTime.thisMonthStart} ~ ${rangeTime.today}`, 'second': `${rangeTime.lastMonthStart} ~ ${rangeTime.lastMonthFromToday}` });
          default:
            return ''
        }
      },
      getRenderTimeFrameLabel($vm) {
        exportRangeTime(this.internalWidgetConfig.timeFrame, getFullDateFormatByLocalization(), $vm)
        switch (this.internalWidgetConfig.timeFrame){
          case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latest3DayTotalVs3DaysBefore');
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latest7DayTotalVs7DaysBefore');
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.latestTotalCostVsAverageCostOfLatest7Days');
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
            return this.$t('dashboard.abnormalChange.timeFrameOptions.thisMonthSoFarVsSamePeriodOfLastMonth');
          default:
            return ''
        }
      },
      genderHeaderColumnsTime($vm) {
        const timeFrame = this.internalWidgetConfig.timeFrame;
        const rangeTime = exportRangeTime(timeFrame, getFullDateFormatByLocalization(), $vm);
        switch (timeFrame) {
          case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
            // this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.latest3DayTotal');
            // this.columnDefs[3].headerName = this.$t('dashboard.abnormalChange.threeDaysBeforeThat');
            this.columnDefs[2].headerName = `${rangeTime.latest3dayStart} ~ ${rangeTime.latest3dayEnd}`;
            this.columnDefs[3].headerName = `${rangeTime.before3dayEnd} ~ ${rangeTime.before3dayStart}`;
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.latest3DayTotal');
            this.columnDefs[3].headerTooltip = this.$t('dashboard.abnormalChange.threeDaysBeforeThat');
            break;
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
            // this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.latest7DaysTotal');
            // this.columnDefs[3].headerName = this.$t('dashboard.abnormalChange.sevenDaysBeforeThat');
            this.columnDefs[2].headerName = `${rangeTime.latest7dayStart} ~ ${rangeTime.latest7dayEnd}`;
            this.columnDefs[3].headerName = `${rangeTime.before7dayEnd} ~ ${rangeTime.before7dayStart}`;
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.latest7DaysTotal');
            this.columnDefs[3].headerTooltip = this.$t('dashboard.abnormalChange.sevenDaysBeforeThat');
            break;
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
            this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.latestTotalCost');
            this.columnDefs[3].headerName = this.$t('dashboard.abnormalChange.averageCostOfLatest7Days');
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.latestTotalCost');
            this.columnDefs[3].headerTooltip = this.$t('dashboard.abnormalChange.averageCostOfLatest7Days');
            break;
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
            this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.thisMonthSoFar');
            this.columnDefs[3].headerName = this.$t('dashboard.abnormalChange.samePeriodOfLastMonth');
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.thisMonthSoFar');
            this.columnDefs[3].headerTooltip = this.$t('dashboard.abnormalChange.samePeriodOfLastMonth');
            break;
          default:
            break;
        }
        // BackUp
        // this.gridOptions.api.setColumnDefs(this.columnDefs);
      },
      setViewByHeaderValue() {
        switch (this.internalWidgetConfig.viewBy) {
          case DASHBOARD_VIEW_BY.ACCOUNT:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.download.account');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.download.account');
            break;
          case DASHBOARD_VIEW_BY.PROJECT:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.download.project');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.download.project');
            break;
          case DASHBOARD_VIEW_BY.PRODUCT:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.download.product');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.download.product');
            break;
          case DASHBOARD_VIEW_BY.REGION:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.download.region');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.download.region');
            break;
          default:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.download.account');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.download.account');
            break;
        }
        // BackUp
        // this.gridOptions.api.setColumnDefs(this.columnDefs);
      },
      // BackUp
      onSaveEditAbnormalForm(widgetConfigForm) {
        let widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        // BackUp
        // if (widgetConfig.isColumnSave) {
        //   let columnInfo = this.gridColumnApi.getColumnState();
        //   for(let column of columnInfo){
        //     if(column['colId']=='alarmLevel'){
        //       column['hide']=false;
        //     }
        //   }
        //   this.$set(widgetConfig, 'columnState', JSON.stringify(columnInfo));
        // } else {
          this.$set(widgetConfig, 'columnState', '');
        //}

        widgetConfig.mailSendCond = {
          "alarmCond": widgetConfig.alarmCondition,
          "isIncludeLowerLevel": widgetConfig.isIncludeLowerLevel,
          "mailReceivers": widgetConfig.mailReceivers
        };

        this.$emit('save', widgetConfig);
      },
      onThresholdDropdownMounted() {
        const selectedThresholdOpt = this.thresholdOptions.find(opt => opt.value === this.internalWidgetConfig.threshold);
        this.$refs.abnormalThresholdDropdown.changeSelectedOptionText(selectedThresholdOpt.text);
      },
      // BackUp
      /*
      exportAbnormalList(){
        const params = {
          fileName: `${this.$t('dashboard.addWidget.abnormalChangeBy')} ${this.i18nViewBy}(${this.internalCommonUserInfo.selectedCurrency})`,
          processHeaderCallback: getProcessHeaderCallbackForExport(this.gridApi, this.internalWidgetConfig, this),
          processCellCallback: getProcessCellCallbackForExport(),
          // columnKeys: ['linkedAccountAlias', 'vendor', 'currentCost', 'lastCost', 'increaseDecreaseRate']
          columnKeys: ['alarmLevel', 'linkedAccountAlias', 'vendor', 'currentCost', 'lastCost', 'increaseDecreaseRate']
        }
        this.gridApi.exportDataAsCsv(params);
      },
       */
      exportPrimeAbnormalList(){
        const $vm = this;
        let workbook = new this.$excel.Workbook();
        initWorkBookViaExcelJs(workbook);
        let excelFileName = `${this.$t('dashboard.addWidget.abnormalChangeBy')} ${this.i18nViewBy}(${this.internalCommonUserInfo.selectedCurrency})`;
        let worksheet = workbook.addWorksheet(excelFileName);
        const rangeTime = exportRangeTime(this.internalWidgetConfig.timeFrame, getFullDateFormatByLocalization(), this)

        worksheet.columns = [
          {header: this.$t('dashboard.abnormalChange.alarmLevel')},
          {header: this.getViewByText()},
          {header: this.$t('dashboard.abnormalChange.cloudService')},
          {header: rangeTime[this.internalWidgetConfig.timeFrame].firstTime},
          {header: rangeTime[this.internalWidgetConfig.timeFrame].lastTime},
          {header: this.$t('dashboard.abnormalChange.change')}
        ]
        function alarmLevelRenderText(alarmLevel){
          switch (alarmLevel) {
            case 'Minor':
              return $vm.$t('dashboard.abnormalChange.alarmLevels.minor');
            case 'Critical':
              return $vm.$t('dashboard.abnormalChange.alarmLevels.critical');
            case 'Major':
            default:
              return $vm.$t('dashboard.abnormalChange.alarmLevels.major');
          }
        }
        let rowData = [];

        rowData = this.standardizedAbnormals.map(function (item) {
          let data = [
            alarmLevelRenderText(item.alarmLevel),
            item.itemAlias + '(' + item.item + ')',
            item.vendor,
            item.currentCost,
            item.lastCost,
            item.increaseDecreaseRate + '%'
            ]
          ;
          return data;
        })

        worksheet.addRows(rowData);
        saveAndReturnSupportedUTF18CSVFile(workbook, excelFileName);
      },
      // BackUp
      // setSortModel() {
      //   if (!_isEmpty(this.internalWidgetConfig.colId) && !_isEmpty(this.internalWidgetConfig.sortType)) {
      //     this.gridApi.setSortModel([
      //       {
      //         colId: this.internalWidgetConfig.colId,
      //         sort: this.internalWidgetConfig.sortType
      //       }
      //     ]);
      //   }
      //   if (this.internalWidgetConfig.isColumnSave) {
      //     this.gridColumnApi.setColumnState(JSON.parse(this.internalWidgetConfig.columnState));
      //   } else {
      //     this.gridColumnApi.resetColumnState();
      //   }
      //   this.gridApi.sizeColumnsToFit();
      // },
      getViewByText() {
        if (!_isEmpty(this.rawAbnormals) && this.rawAbnormals.abnormalList.length > 1) {
          switch (this.internalWidgetConfig.viewBy) {
            case DASHBOARD_VIEW_BY.ACCOUNT:
              return this.$t('dashboard.abnormalChange.viewByPlurals.account');
            case DASHBOARD_VIEW_BY.PROJECT:
              return this.$t('dashboard.abnormalChange.viewByPlurals.project');
            case DASHBOARD_VIEW_BY.PRODUCT:
              return this.$t('dashboard.abnormalChange.viewByPlurals.product');
            case DASHBOARD_VIEW_BY.REGION:
              return this.$t('dashboard.abnormalChange.viewByPlurals.region');
            default:
              return this.$t('dashboard.abnormalChange.viewByPlurals.account');
          }
        } else {
          switch (this.internalWidgetConfig.viewBy) {
            case DASHBOARD_VIEW_BY.ACCOUNT:
              return this.$t('dashboard.abnormalChange.viewBySingular.account');
            case DASHBOARD_VIEW_BY.PROJECT:
              return this.$t('dashboard.abnormalChange.viewBySingular.project');
            case DASHBOARD_VIEW_BY.PRODUCT:
              return this.$t('dashboard.abnormalChange.viewBySingular.product');
            case DASHBOARD_VIEW_BY.REGION:
              return this.$t('dashboard.abnormalChange.viewBySingular.region');
            default:
              return this.$t('dashboard.abnormalChange.viewBySingular.account');
          }
        }
      },
      onCellClicked($event) {
        if(!this.enableDrillDown){
          return
        }
        let startDate = ''
        let endDate = ''
        let compareStartDate =''
        let compareEndDate =''
        let isCompare = true;
        let isCompareDateCustom = false;
        const latestSummarizedBillDate = _get(this.rawAbnormals, 'latestSummarizedBillDate')
        switch (this.widgetConfig.timeFrame) {
          case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE: {
            // startDate = dayjs.utc().subtract(4, 'day').format(DATE_FORMAT);
            // endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT);
            // compareStartDate= dayjs.utc().subtract(7, 'day').format(DATE_FORMAT);
            // compareEndDate = dayjs.utc().subtract(5, 'day').format(DATE_FORMAT);
            endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT);
            startDate = dayjs(latestSummarizedBillDate).subtract(2, 'day').format(DATE_FORMAT);
            compareStartDate = dayjs(latestSummarizedBillDate).subtract(5, 'day').format(DATE_FORMAT);
            compareEndDate = dayjs(latestSummarizedBillDate).subtract(3, 'day').format(DATE_FORMAT);
            break;
          }
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE: {
            // startDate = dayjs.utc().subtract(8, 'day').format(DATE_FORMAT);
            // endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT);
            // compareStartDate= dayjs.utc().subtract(15, 'day').format(DATE_FORMAT);
            // compareEndDate = dayjs.utc().subtract(9, 'day').format(DATE_FORMAT);
            endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT);
            startDate = dayjs(latestSummarizedBillDate).subtract(6, 'day').format(DATE_FORMAT);
            compareStartDate = dayjs(latestSummarizedBillDate).subtract(13, 'day').format(DATE_FORMAT);
            compareEndDate = dayjs(latestSummarizedBillDate).subtract(7, 'day').format(DATE_FORMAT);
            break;
          }
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS: {
            isCompare = false;
            const theLatestSummarizedBillDate = _get(this.rawAbnormals, 'latestSummarizedBillDate')
            if (theLatestSummarizedBillDate) {
              endDate = dayjs(theLatestSummarizedBillDate).format(DATE_FORMAT)
              startDate = dayjs(latestSummarizedBillDate).subtract(7, 'day').format(DATE_FORMAT)
            } else {
              endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT)
              startDate = dayjs.utc().subtract(9, 'day').format(DATE_FORMAT)
            }

            break;
          }
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH: {
            // startDate = dayjs.utc().startOf('month').format(DATE_FORMAT);
            // endDate = dayjs.utc().format(DATE_FORMAT);
            // compareStartDate= dayjs.utc().subtract(1, 'month').startOf('month').format(DATE_FORMAT);
            // compareEndDate = dayjs.utc().subtract(1, 'month').endOf('month').format(DATE_FORMAT);
            startDate = dayjs(latestSummarizedBillDate).startOf('month').format(DATE_FORMAT)
            endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT)
            compareStartDate =  dayjs(latestSummarizedBillDate).subtract(1,'month').startOf('month').format(DATE_FORMAT)
            compareEndDate = dayjs(latestSummarizedBillDate).subtract(1,'month').format(DATE_FORMAT)
            isCompareDateCustom = true;
            break;
          }
        }
        let payload = {
          selectedVendor : this.selectedVendor,
          widgetType: this.internalWidgetConfig.widgetType,
          viewBy: this.internalWidgetConfig.viewBy,
          startDate: startDate,
          endDate: endDate,
          compareStartDate: compareStartDate,
          compareEndDate: compareEndDate,
          isOthersClicked:true,
          isCompare: isCompare,
          isCompareDateCustom: isCompareDateCustom,
          // data: [{
          //   dataKey:$event.data.item,
          //   vendor:$event.data.vendor,
          // }]
          // data: this.standardizedAbnormals.map(item => {
          //   return {
          //     dataKey: item.item,
          //     vendor: item.vendor
          //   }
          // })
        };
        let fullPayload = {};
        if($event!=undefined){
          fullPayload = {
            ...payload,
            data: [{
              dataKey:$event.data.item,
              vendor:$event.data.vendor,
            }]
          };
        }else{
          fullPayload = {
            ...payload,
            data: this.standardizedAbnormals.map(item => {
              return {
                dataKey: item.item,
                vendor: item.vendor
              }
            })
          };
        }

        this.$emit('clickToAnalyze', fullPayload);

      },
      getAnomalyDetectSummary() {
        if (this.rawAbnormals.abnormalList.length > 0) {
          this.hasAnomalyDetect = true;
        } else {
          this.hasAnomalyDetect = false;
        }
      },
      setWidgetTitleArea() {
        this.$nextTick(() => {
          if(document.getElementById(this.widgetWrapperId) === null) {
            return;
          }
          let widgetWidth = document.getElementById(this.widgetWrapperId).offsetWidth;
          let headerLeftSide1Width = document.getElementById(this.headerLeftSide1).offsetWidth;
          let headerLeftSide2Width = document.getElementById(this.headerLeftSide2).offsetWidth;
          let headerRightSideWidth = document.getElementById(this.headerRightSide).offsetWidth;
          if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
            document.getElementById(this.widgetTitle).style.maxWidth = `${widgetWidth - HEADER_RIGHT_SIDE_WIDTH - headerLeftSide1Width - headerLeftSide2Width - DISTANCE_BETWEEN_OUTSIDE }px`
          } else {
            document.getElementById(this.widgetTitle).style.maxWidth = `${widgetWidth - headerRightSideWidth - headerLeftSide1Width - headerLeftSide2Width - DISTANCE_BETWEEN_OUTSIDE }px`
          }
          if (document.getElementById(this.widgetTitle).offsetWidth < document.getElementById(this.widgetTitle).scrollWidth) {
            this.isWidgetTitleTooltipEnabled = true;
          } else {
            this.isWidgetTitleTooltipEnabled = false;
          }
        });
      },
      changeHasAnomalyDetect(){
        //this.hasAnomalyDetect = false;
        this.onCellClicked();
      },
      goToAnomalyList() {
        const latestSummarizedBillDate = _get(this.rawAbnormals, 'latestSummarizedBillDate');
        let payload = {
          widgetIndex: this.widgetConfig.index,
          dashboardIndex: this.widgetConfig.dashboardIndex,
          widgetType: this.widgetConfig.widgetType,
          endDate: latestSummarizedBillDate
        }

        this.$router.push({
          name: ROUTE_NAME.ANOMALY_LIST,
          params: {
            ...payload
          }
        });
      }
    }
  };

  function serviceRender($vm) {
    return function(params) {
      return getDisplayItemWithVendorBaseOnViewBy(params.data, $vm.previewWidgetConfig.viewBy);
    }
  }

  function serviceGetter(params) {
    return `${params.data.itemAlias}(${params.data.item})`;
  }

  function lastMonthCostRenderCallback($vm) {
    return function(params) {
      return `${CURRENCY_SYMBOL[$vm.internalCommonUserInfo.selectedCurrency]}${formatCost(params.data.lastCost)}`;
    }
  }

  function currentMonthCostRenderCallback($vm) {
    return function(params) {
      return `${CURRENCY_SYMBOL[$vm.internalCommonUserInfo.selectedCurrency]}${formatCost(params.data.currentCost)}`;
    }
  }
  function formatLastCostValueGetter(params){
    return formatCost(params.data.lastCost)
  }

  function formatCurrentCostValueGetter(params){
    return formatCost(params.data.currentCost)
  }

  function increaseDecreaseRateGetter(params) {
    return formatPercentage(params.data.increaseDecreaseRate);
  }

  function increaseDecreaseRateGTooltip(params) {
    return `${formatPercentage(params.data.increaseDecreaseRate)}%`;
  }

  function increaseDecreaseRateRender(params) {
    if (!_isNil(params.data.increaseDecreaseRate)) {
      if (params.data.increaseDecreaseRate < 0) {

        return `
          <span>
            <span class="material-icons -color-green-1 -font-size-16 arrow-align">arrow_downward</span>
            <span >${formatPercentage(Math.abs(params.data.increaseDecreaseRate))}%</span>
          </span>
        `;
      } else {
        return `
          <span>
            <span class="material-icons -color-red-1 -font-size-16 arrow-align">arrow_upward</span>
            <span>${formatPercentage(params.data.increaseDecreaseRate)}%</span>
          </span>
        `;
      }
    }
  }

  function alarmLevelRender($vm) {
    return function(params) {
      switch (params.data.alarmLevel) {
        case 'Minor':
          return `<p class="-tag green-">` + $vm.$t('dashboard.abnormalChange.alarmLevels.minor') + `</p>`;
        case 'Critical':
          return `<p class="-tag red-">` + $vm.$t('dashboard.abnormalChange.alarmLevels.critical') + `</p>`;
        case 'Major':
        default:
          return `<p class="-tag yellow-">` + $vm.$t('dashboard.abnormalChange.alarmLevels.major') + `</p>`;
      }
    };
  }

  function alarmLevelGetter($vm) {
    return function (params) {
      switch (params.data.alarmLevel) {
        case 'Minor':
          return $vm.$t('dashboard.abnormalChange.alarmLevels.minor');
        case 'Critical':
          return $vm.$t('dashboard.abnormalChange.alarmLevels.critical');
        case 'Major':
        default:
          return $vm.$t('dashboard.abnormalChange.alarmLevels.major');
      }
    };
  }

  function changeComparator(valueA, valueB, nodeA, nodeB, isInverted) {
    // return Math.abs(valueA) - Math.abs(valueB);
    return valueA - valueB;
  }

  function customComparator(valueA, valueB) {
    return valueA.toLowerCase().localeCompare(valueB.toLowerCase());
  }

  function customAlarmComparator(valueA, valueB, nodeA, nodeB, isInverted) {
    return ABNORMAL_GRID_SORT_ALARM_LEVEL[nodeA.data.alarmLevel].toString().localeCompare(ABNORMAL_GRID_SORT_ALARM_LEVEL[nodeB.data.alarmLevel].toString());
  }

  // BackUp
  // function getProcessHeaderCallbackForExport(gridApi, widgetConfig, $vm) {
  //   const rangeTime = exportRangeTime(widgetConfig.timeFrame, getFullDateFormatByLocalization(), $vm)
  //   return function (params) {
  //     switch (params.column.getColDef().field) {
  //       case 'linkedAccountAlias':
  //         return $vm.$t(`dashboard.abnormalChange.download.${widgetConfig.viewBy}`);
  //       case 'currentCost':
  //         return rangeTime[widgetConfig.timeFrame].firstTime;
  //       case 'lastCost':
  //         return rangeTime[widgetConfig.timeFrame].lastTime;
  //       default:
  //         return params.column.getColDef().headerName
  //     }
  //   };
  // }

  function getProcessCellCallbackForExport() {
    return function (params) {
      if (params.column.getColDef().field === 'increaseDecreaseRate' ) {
        return `${params.value} %`
      } else {
        return params.value
      }
    };
  }
  function exportRangeTime(type, format, $vm) {
    const latestSummarizedBillDate = _get($vm.rawAbnormals, 'latestSummarizedBillDate')
    switch (type) {
      case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
        const latest3dayEnd = dayjs(latestSummarizedBillDate).format(format);
        const latest3dayStart = dayjs(latestSummarizedBillDate).subtract(2, 'day').format(format);
        const before3dayEnd = dayjs(latestSummarizedBillDate).subtract(5, 'day').format(format);
        const before3dayStart = dayjs(latestSummarizedBillDate).subtract(3, 'day').format(format);
        return {
          latest3dayEnd,
          latest3dayStart,
          before3dayEnd,
          before3dayStart,
          compare_last_3_days: {
            firstTime: `${latest3dayStart} ~ ${latest3dayEnd}`,
            lastTime: `${before3dayEnd} ~ ${before3dayStart}`,
          }
        }
      case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
        const latest7dayEnd = dayjs(latestSummarizedBillDate).format(format)
        const latest7dayStart = dayjs(latestSummarizedBillDate).subtract(6, 'day').format(format)
        const before7dayEnd = dayjs(latestSummarizedBillDate).subtract(13, 'day').format(format)
        const before7dayStart = dayjs(latestSummarizedBillDate).subtract(7, 'day').format(format)
        return {
          latest7dayEnd,
          latest7dayStart,
          before7dayEnd,
          before7dayStart,
          compare_last_7_days: {
            firstTime: `${latest7dayStart} ~ ${latest7dayEnd}`,
            lastTime: `${before7dayEnd} ~ ${before7dayStart}`,
          }
        };
      case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
        const latestDay = dayjs(latestSummarizedBillDate).subtract(1, 'day').format(format)
        const average7DayEnd = dayjs(latestSummarizedBillDate).subtract(7, 'day').format(format)
        let latestTotalCost = ''
        if (latestSummarizedBillDate) {
          latestTotalCost = dayjs(latestSummarizedBillDate).format(format)
        } else {
          latestTotalCost = dayjs.utc().subtract(2, 'day').format(format)
        }
        return {
          latestDay,
          average7DayEnd,
          latestTotalCost,
          compare_latest_day_vs_average_of_7_days: {
            firstTime: `${latestTotalCost}`,
            lastTime: `${average7DayEnd} ~ ${latestDay}`,
          }
        };
      case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
        const thisMonthStart = dayjs(latestSummarizedBillDate).startOf('month').format(format)
        const today = dayjs(latestSummarizedBillDate).format(format)
        const lastMonthStart =  dayjs(latestSummarizedBillDate).subtract(1,'month').startOf('month').format(format)
        const lastMonthFromToday = dayjs(latestSummarizedBillDate).subtract(1,'month').format(format)
        return {
          thisMonthStart,
          today,
          lastMonthStart,
          lastMonthFromToday,
          compare_this_month_vs_last_month: {
            firstTime: `${thisMonthStart} ~ ${today}`,
            lastTime: `${lastMonthStart} ~ ${lastMonthFromToday}`,
          }
        };
    }
  }

</script>
<style lang="scss">
  .dashboard-date {
    .tooltip-inner {
      font-size: 14px !important;
      font-family: NotoSansCJKkr-Regular !important;
      color: #fff !important;
      max-width: 100% !important;
      width: 100%;
      padding-left: 15px !important;
      padding-right: 15px !important;
      text-align: left;
    }
  }
  .abnormal-change-wrapper {
    padding: 0;
    border-radius: 4px;
    .abnormal-change-header-right {
      height: 24px;
      padding-top: 6px;
    }
    .abnormal-change-header-left {
      display: inline-block !important;
      .abnormal-change-view-by {
        display: flex;
        span {
          line-height: 26px;
        }
      }
    }
    .abnormal-change-table-wrapper {
      padding: 0;
      .abnormal-change-table {
        width: 100%;
      }
    }
    .abnormal-change-header-tooltip {
      .dashboard-date-range {
        font-size: 13.3px;
        height: 13.3px;
        cursor: pointer;
        position: relative;
        top: 2px;
        color: #b0b7bf;
      }
    }
    .abnormal-change-table {
      height: 370px;
      width: 100%;
    }
    .change-title {
      font-size: 12px;
      font-weight: 500;
      color: #7b8088;
      line-height: 2.2 !important;
    }

    .arrow-up {
      width: 0;
      height: 0;
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-bottom: 5px solid #ff475e;
      display: inline-block;
    }

    .arrow-down {
      width: 0;
      height: 0;
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-top: 5px solid #6cb41e;
      display: inline-block;
    }

    .arrow-right {
      border: solid #b5bdc4;
      border-width: 0 2px 2px 0;
      display: inline-block;
      padding: 3px;
      transform: rotate(-45deg);
      -webkit-transform: rotate(-45deg);
      float: right;
      margin-top: 15px;
      cursor: pointer;
    }

    .custom-abnormal-threshold-dropdown {
      .base-dropdown {
        button {
          padding-top: 0 !important;
          padding-right: 0 !important;
          padding-bottom: 0 !important;
          span {
            line-height: 2 !important;
          }
        }
        .dropdown-toggle {
          color: #8c9197 !important;
          font-size: 12px !important;
          padding-left: 0;
          padding-right: 0 !important;
          &:after {
            color: #0672FF !important;
            font-size: 12px !important;
          }
        }
        .dropdown-menu {
          min-width: 70px !important;
          li {
            a {
              padding-left: 10px !important;
              padding-right: 10px !important;
            }
          }
        }
      }
    }

    .custom-abnormal-view-by-dropdown {
      .base-dropdown {
        button {
          padding: 0 0 0 16px !important;
          span {
            line-height: 2 !important;
          }
        }
        .dropdown-menu {
          min-width: 70px !important;
          li {
            a {
              padding-left: 18px !important;
              padding-right: 18px !important;
            }
          }
        }
      }
    }

    .ag-row-hover {
      &:hover {
        .arrow-right {
          border: solid #0672ff;
          border-width: 0 2px 2px 0;
          display: inline-block;
          padding: 3px;
          transform: rotate(-45deg);
          -webkit-transform: rotate(-45deg);
          float: right;
          margin-top: 15px;
          cursor: pointer;
        }
      }
      background-color: #e6f1ff !important;
    }

    .sub-title {
      font-size: 12px;
      font-weight: normal;
      font-style: normal;
      font-stretch: normal;
      letter-spacing: normal;
      line-height: 16px;
      color: #7b8088;
      .material-icons {
        cursor: pointer;
      }
    }
    .ag-root ::-webkit-scrollbar {
      border: none;
      height: 6px;
      width: 6px;
    }

    .ag-root ::-webkit-scrollbar-thumb {
      background-clip: padding-box;
      background-color: #D5DAE0;
      border-radius: 2.5px;
      min-height: 50px;
    }

    .ag-root {
      overflow-x: hidden !important;
      .ag-body-viewport {
        background: white !important;
        /*.ag-center-cols-container {*/
        /*  cursor: pointer;  !*이상비용 드릴다운 포인터*!*/
        /*}*/
      }
    }

    .no-data {
      font-size: 14px;
      font-weight: normal;
      font-style: normal;
      font-stretch: normal;
      line-height: normal;
      letter-spacing: normal;
      color: #222222;
    }
    .change-filter {
      font-size: 12px;
      font-weight: normal;
      font-style: normal;
      font-stretch: normal;
      line-height: 1.5;
      letter-spacing: normal;
      text-align: center;
      color: #7b8088;
    }
    .custom-popover-abnormal-change {
      .popover {
        top: 0 !important;
        left: 14px !important;
        .arrow {
          left: 75% !important;
        }
        .arrow:before {
          border-bottom-color: #ffffff;
        }
      }
    }
    .anomaly-detect-text {
      text-align: left;
      padding-left: 10px;
      padding-right: 20px;
    }
    .anomaly-widget-footer{
      display:-ms-flexbox;
      display:flex;
      justify-content: flex-end;
      -ms-flex-align:center;
      align-items:center;
      color:#fff;
      //position:relative;
      padding:0 20px;
      height:35px;
      position: fixed;
      bottom: 0;
      .go-to-alert-list {
        color: #7B8088;
        display: inline-flex;
        padding: 0;
        margin-right: 6px;
      }
    }
    .cursor-default {
      cursor: default!important;
    }
  }
  .divider {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background-color: #000;
    z-index: 1;
  }

  .abnormal-change-wrapper .enable-drill-down {
    .ag-root {
      .ag-body-viewport {
        .ag-center-cols-container {
          cursor: pointer;  /*이상비용 드릴다운 포인터*/
        }
      }
    }
  }
  .edit-abnormal-form-wrapper {
    bottom: -6px;
  }
  .abnormal-change-header {
    padding: 5px 12px 5px 20px !important;
    height: 55px;
    /*.abnormal-change-header-main {*/
    /*  width: 100%;*/
    /*  justify-content: space-between;*/
    /*  margin-left: 1px;*/
    /*}*/
  }

  .arrow-align {
    vertical-align: middle;
  }

  @media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
    .abnormal-change-header {
      display: block!important;
      width: 100%;
      /*height: 56px;*/
      padding-right: 0px!important;
      .abnormal-change-header-left {
        margin-top: -6px;
        display: inline-block!important;
        float: left;
        width: 50%;
        height: 24px;
        .font-family-notosanscjkkr-medium {
          line-height: 8px;
        }
      }
      .abnormal-change-header-right {
        /*display: inline-block!important;*/
        /*float: right;*/
        /*width: 48%;*/
        /*text-align: right;*/
        height: 24px;
        padding-top: 6px;
        .change-title {
          display: inline-block;
          position: relative;
          top: 1px;
        }
        .custom-abnormal-threshold-dropdown {
          display: inline-block;
        }
      }
      .abnormal-change-header-tooltip {
        display: block;
        float: left;
        margin-top: -2px;
        .sub-title {
          line-height: 8px;
          display: inline-block;
          span {
            display: inline-block;
          }
        }
      }
    }
  }
.p-divider {
  border-top: solid rgba(29, 28, 209, 0.1) 2px;
}
</style>
