<template>
  <div class="ml-abnormal-user-detection">
    <b-card
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
                <span class="-tag gray-">
                  <span class="material-icons">person</span>
                  <p>User</p>
                </span>
                <span class="-tag gray-">
                  <base-icon
                    :original="true"
                    name="icon_ai" />
                  <p>User</p>
                </span>
                {{ selectedVendor }} {{ $t('dashboard.abnormalChange.abnormalChangeBy.#1') }}
              </b-row>
              <BaseDropdown
                ref="abnormalChangeViewByDropdown"
                :options="selectedVendor == 'GCP' ? abnormalChangeViewByOptionsNoRegion : abnormalChangeViewByOptions"
                :enabled-localization="true"
                :disabled="widgetLoadingState[widgetConfig.index]"
                class="custom-view-by-dropdown"
                @selectOption="onSelectOptionViewBy"
                @mounted="onAbnormalChangeViewByDropdownMounted"
              />
              <span>{{ $t('dashboard.abnormalChange.abnormalChangeBy.#2') }}</span>
            </div>
          </b-col>
        </b-row>
        <b-row
          class="font-family-notosanscjkkr-medium abnormal-change-header-right"
        >
          <p class="change-title custom-abnormal-threshold-dropdown">{{ $t('dashboard.abnormalChange.change') }}</p>
          <base-dropdown
            ref="abnormalThresholdDropdown"
            :options="thresholdOptions"
            :disabled="widgetLoadingState[widgetConfig.index]"
            class="custom-abnormal-threshold-dropdown"
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
      <div v-show="!widgetLoadingState[widgetConfig.index]">
        <b-row
          no-gutters
          class="-inline-notification with-icon- gray- w-100 px-0">
          <b-button class="-bg-transparent -with-btn-vue w-100 h-100">
            <span class="material-icons">warning</span>
            <p>Warning message </p>
            <p class="-icon-wrapper">
              <span class="material-icons -font-size-24">keyboard_arrow_right</span>
            </p>
          </b-button>

        </b-row>
        <b-col class="abnormal-change-table-wrapper">
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
            class="ag-theme-bootstrap abnormal-change-table"
            @grid-ready="onGridReady"
            @column-moved="columnMoved"
          />
          <!--Get rid of "Drill down"-->
          <!--<ag-grid-vue @cellClicked="onCellClicked">-->
        </b-col>
      </div>
      <b-modal
        ref="edit-abnormal-user-form"
        v-model="widgetConfig.isEditFormVisible"
        :title="$t('dashboard.abnormalChange.editAbnormalChangeByWidget')"
        modal-class="right-wing"
        hide-footer
        hide-backdrop
      >
        <div v-if="widgetConfig.isEditFormVisible">
          <EditAbnormalUserForm
            :common-user-info="internalCommonUserInfo"
            :exchange-rate="internalExchangeRate"
            :dashboard-view-mode="dashboardViewMode"
            :widget-config="internalWidgetConfig"
            class="edit-abnormal-user-form-wrapper"
            @hideModal="hideModal"
            @save="onSaveEditAbnormalUserForm"
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
  import _isNil from 'lodash/isNil';
  import {calculateCostByCurrency, formatCost, formatPercentage} from '@/util/costUtils';
  import BaseDropdown from '@/components/common/BaseDropdown';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import { CURRENCY_SYMBOL, DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE } from '@/constants/constants';
  import {
    ABNORMAL_THRESHOLD,
    ABNORMAL_TIME_FRAME,
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
    DEFAULT_WIDGET_DATA,
    EXPORT_FORMAT_DATE,
    THRESHOLD_OPTIONS,
    VIEW_MODE,
    DASHBOARD_VIEW_BY_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS_NO_REGION,
    DASHBOARD_VIEW_BY,
  } from '@/constants/dashboardConstants';
  import dayjs from 'dayjs';
  import _isEmpty from 'lodash/isEmpty';
  import _isEqual from 'lodash/isEqual';
  import _cloneDeep from 'lodash/cloneDeep';
  import _get from 'lodash/get';
  import { isAbnormalChangeWidgetDataConfigChanged, getSelectedVendorsByWidget } from '@/util/dashboardUtils';
  import { getDisplayItemWithVendorBaseOnViewBy } from '@/util/formatAccountUtils';
  import ShowEditFormModalMixin from '@/mixins/ShowEditFormModalMixin';
  import { fetchDashboardMLAbnormalUser } from '@/api/dashboard';

  const EditAbnormalUserForm = () => import('@/components/pages/dashboard/ml-abnormal-detection/EditMLAbnormalUserForm');
  import CustomLoadingOverlay from '@/components/common/custom-loading-overlay/CustomLoadingOverlay';
  import CustomNoRowsOverlay from '@/components/common/custom-no-rows-overlay/CustomNoRowsOverlay';
  import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
  import {ABNORMAL_VIEW_BY_VENDORS, COST_MONTH_TO_DATE_VIEW_BY_VENDORS} from "../../../../constants/dashboardConstants";
  const DATE_FORMAT = 'YYYY-MM-DD'

  export default {
    name: 'DashboardMLAbnormalUserDetection',
    components: {
      BaseDropdown,
      EditAbnormalUserForm,
      BaseLoadingIndicator
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
      }
    },
    data() {
      return {
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
          selectedVendorsByWidget: []
        },
        gridOptions: null,
        columnDefs: [],
        thresholdOptions: THRESHOLD_OPTIONS,
        containerCustomPopover: `abnormal-custom-popover-${this.widgetConfig.index}`,
        abnormalDropdown: `abnormal-dropdown-${this.widgetConfig.index}`,
        dashboardDateRange: `dashboard-date-range-${this.widgetConfig.index}`,
        dashboardDate: `dashboard-date-${this.widgetConfig.index}`,
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
        abnormalChangeViewByOptions: DASHBOARD_VIEW_BY_OPTIONS,
        abnormalChangeViewByOptionsNoRegion: DASHBOARD_VIEW_BY_OPTIONS_NO_REGION,
        widgetCurrency : this.$store.state.common.info.currencies.KRW,
      };
    },
    computed: {
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
    },
    watch: {
      currentDashboard: {
        handler() {
          this.gridColumnApi.resetColumnState()
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
              }
            } else {
              this.hideModal();
            }
            return;
          }
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
        },
        immediate: true
      },
      'commonUserInfo.selectedCurrency': {
        handler(newSelectedCurrency) {
          if (this.internalCommonUserInfo.selectedCurrency === newSelectedCurrency) {
            return;
          }
          this.internalCommonUserInfo.selectedCurrency = this.commonUserInfo.selectedCurrency;
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
      }
    },
    created() {
      const $vm = this;
      this.gridOptions = {
        localeText: {
          noRowsToShow: this.$t('billing.chargeList.noResultsFound')
        },
        processCellForClipboard: params => {
          if (params.column.colId && ['currentCost', 'lastCost', 'increaseDecreaseRate'].includes(params.column.colDef.field)) {
            return _get(params, `node.data.${params.column.colDef.field}`);
          }
          return params.value;
        }
      };
      this.columnDefs = [
        {
          headerName: $vm.$t('dashboard.abnormalChange.service'),
          headerTooltip: $vm.$t('dashboard.abnormalChange.service'),
          field: 'linkedAccountAlias',
          cellRenderer: serviceRender($vm),
          tooltipValueGetter: serviceRender($vm),
          valueGetter: serviceGetter,
          width: 160,
          comparator: customComparator,
        },
        {
          headerName: $vm.$t('dashboard.abnormalChange.thisMonth'),
          headerTooltip: $vm.$t('dashboard.abnormalChange.thisMonth'),
          field: 'currentCost',
          cellStyle: { textAlign: 'right' },
          cellRenderer: currentMonthCostRenderCallback($vm),
          tooltipValueGetter: currentMonthCostRenderCallback($vm),
          valueFormatter: formatCurrentCostValueGetter,
          width: 180,
        },
        {
          headerName: $vm.$t('dashboard.abnormalChange.lastMonth'),
          headerTooltip: $vm.$t('dashboard.abnormalChange.lastMonth'),
          field: 'lastCost',
          cellRenderer: lastMonthCostRenderCallback($vm),
          tooltipValueGetter: lastMonthCostRenderCallback($vm),
          cellStyle: { textAlign: 'right' },
          valueFormatter: formatLastCostValueGetter,
          width: 180,
        },
        {
          headerName: $vm.$t('dashboard.abnormalChange.change'),
          headerTooltip: $vm.$t('dashboard.abnormalChange.change'),
          field: 'increaseDecreaseRate',
          cellStyle: { textAlign: 'right' },
          width: 110,
          tooltipValueGetter: increaseDecreaseRateGTooltip,
          cellRenderer: increaseDecreaseRateRender,
          valueFormatter: increaseDecreaseRateGetter,
          comparator: (valueA, valueB, nodeA, nodeB, isInverted) => changeComparator(valueA, valueB, nodeA, nodeB, isInverted)
        },
        {
          headerName: this.$t('dashboard.abnormalChange.vendor'),
          field: 'vendor',
          hide: true,
        },
      ];
      this.defaultColDef = {
        sortable: true, suppressMenu: true, resizable: true, unSortIcon: true, suppressHorizontalScroll: true
      };
      this.sortingOrder = [null, 'desc', 'asc',];
      this.frameworkComponents = {
        customLoadingOverlay: CustomLoadingOverlay,
        customNoRowsOverlay: CustomNoRowsOverlay
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
      this.gridApi = this.gridOptions.api;
      this.gridColumnApi = this.gridOptions.columnApi;
      // this.genderHeaderColumnsTime(this);
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setAbnormalTableOption();
      this.gridOptions.api.showLoadingOverlay();
    },
    methods: {
      onAbnormalChangeViewByDropdownMounted() {
        const selectedAbnormalChangeViewByOpt = this.abnormalChangeViewByOptions.find(opt => opt.value === this.internalWidgetConfig.viewBy);
        this.$refs.abnormalChangeViewByDropdown.changeSelectedOptionText(selectedAbnormalChangeViewByOpt.text);
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
      },
      makeSizeColumnsToFit() {
        const $vm = this;
        setTimeout(function() {
          //We have to use timeout & $nextTick to make sizeColumnsToFit workable
          $vm.gridApi.sizeColumnsToFit();
        }, 0);
        this.$nextTick();
      },
      updatePreviewWidgetConfig() {
        this.previewWidgetConfig = {
          viewBy: this.internalWidgetConfig.viewBy,
          threshold: this.internalWidgetConfig.threshold,
          selectedVendorsByWidget: getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS)
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
        if (!_isEmpty(this.columnDefs) && !_isNil(this.gridOptions)) {
          this.genderHeaderColumnsTime(this);
        }
      },
      loadAbnormal(widgetConfig) {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;
        let payload = {
          threshold: widgetConfig.threshold,
          timeFrame: widgetConfig.timeFrame,
          viewBy: widgetConfig.viewBy,
          // vendors: this.internalCommonUserInfo.selectedVendors,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS)],
          widgetCurrency: this.widgetCurrency
        };
        fetchDashboardMLAbnormalUser(payload)
          .then(res => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.rawAbnormals = res;
            this.genderHeaderColumnsTime(this);
            this.getRenderTimeFrameLabelWithTime(this);
            this.getRenderTimeFrameLabel(this);
            this.setSortModel();
            this.onAbnormalChangeViewByDropdownMounted();
          })
          .catch(err => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            console.error(err);
            this.rawAbnormals = null;
            this.onAbnormalChangeViewByDropdownMounted();
          })
      },
      setAbnormalTableOption() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.abnormalTableOption.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.abnormalTableOption.options = DASHBOARD_DROPDOWN_OPTIONS
        }
      },
      onGridReady(params) {
        this.api = params.api;
        let sortDefault = [
          {
            colId: "increaseDecreaseRate",
            sort: "desc"
          }
        ];
        this.gridApi.setSortModel(sortDefault);
        this.gridApi.sizeColumnsToFit();
      },
      columnMoved() {
        if (this.internalWidgetConfig.isColumnSave) {
          this.onSaveEditAbnormalUserForm(this.internalWidgetConfig);
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
      },
      onSelectOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            this.exportAbnormalList();
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
            // this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.latest3DayTotal');
            // this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.threeDaysBeforeThat');
            this.columnDefs[1].headerName = `${rangeTime.latest3dayStart} ~ ${rangeTime.latest3dayEnd}`;
            this.columnDefs[2].headerName = `${rangeTime.before3dayEnd} ~ ${rangeTime.before3dayStart}`;
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.latest3DayTotal');
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.threeDaysBeforeThat');
            break;
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
            // this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.latest7DaysTotal');
            // this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.sevenDaysBeforeThat');
            this.columnDefs[1].headerName = `${rangeTime.latest7dayStart} ~ ${rangeTime.latest7dayEnd}`;
            this.columnDefs[2].headerName = `${rangeTime.before7dayEnd} ~ ${rangeTime.before7dayStart}`;
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.latest7DaysTotal');
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.sevenDaysBeforeThat');
            break;
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.latestTotalCost');
            this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.averageCostOfLatest7Days');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.latestTotalCost');
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.averageCostOfLatest7Days');
            break;
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
            this.columnDefs[1].headerName = this.$t('dashboard.abnormalChange.thisMonthSoFar');
            this.columnDefs[2].headerName = this.$t('dashboard.abnormalChange.samePeriodOfLastMonth');
            this.columnDefs[1].headerTooltip = this.$t('dashboard.abnormalChange.thisMonthSoFar');
            this.columnDefs[2].headerTooltip = this.$t('dashboard.abnormalChange.samePeriodOfLastMonth');
            break;
          default:
            break;
        }
        this.gridOptions.api.setColumnDefs(this.columnDefs);
      },
      onSaveEditAbnormalUserForm(widgetConfigForm) {
        let widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        if (widgetConfig.isColumnSave) {
          this.$set(widgetConfig, 'columnState', JSON.stringify(this.gridColumnApi.getColumnState()));
        } else {
          this.$set(widgetConfig, 'columnState', '');
        }
        this.$emit('save', widgetConfig);
      },
      onThresholdDropdownMounted() {
        const selectedThresholdOpt = this.thresholdOptions.find(opt => opt.value === this.internalWidgetConfig.threshold);
        this.$refs.abnormalThresholdDropdown.changeSelectedOptionText(selectedThresholdOpt.text);
      },
      exportAbnormalList(){
        const params = {
          fileName: `AbnormalChange${this.i18nViewBy}(${this.internalCommonUserInfo.selectedCurrency})`,
          processHeaderCallback: getProcessHeaderCallbackForExport(this.gridApi, this.internalWidgetConfig, this),
          processCellCallback: getProcessCellCallbackForExport(),
          columnKeys: ['linkedAccountAlias', 'vendor', 'currentCost', 'lastCost', 'increaseDecreaseRate']
        }
        this.gridApi.exportDataAsCsv(params);
      },
      setSortModel() {
        if (!_isEmpty(this.internalWidgetConfig.colId) && !_isEmpty(this.internalWidgetConfig.sortType)) {
          this.gridApi.setSortModel([
            {
              colId: this.internalWidgetConfig.colId,
              sort: this.internalWidgetConfig.sortType
            }
          ]);
        }
        if (this.internalWidgetConfig.isColumnSave) {
          this.gridColumnApi.setColumnState(JSON.parse(this.internalWidgetConfig.columnState));
        } else {
          this.gridColumnApi.resetColumnState();
        }
        this.gridApi.sizeColumnsToFit();
      },
      // allVendors(){
      //   // return ['AWS', 'GCP' , 'AZURE'];
      //   let curCmpnId = this.$store.state.loginUser.curCmpnId;
      //   let vendorInfo = this.$store.state.vendorInfo;
      //   if(curCmpnId && vendorInfo &&vendorInfo.length > 0){
      //     return vendorInfo.map(option => {
      //       //return this.$t(option.cloudVndrId).toUpperCase();
      //       return option;
      //     });
      //   }else{
      //     return [''];
      //   }
      // },
      // getDefaultVendorByCheckedAuth(vendor){
      //   let rslt = !_isEmpty(this.allVendors()) && this.allVendors().includes(vendor) ? vendor :this.allVendors().filter(v=> v != vendor).map(v=>{return v})[0];
      //   return rslt;
      // },
      // isSelectedVendorsNotEmpty($vm) {
      //   return !_isEmpty($vm.selectedVendorsByWidget) && !_isEmpty($vm.selectedVendorsByWidget[0]);
      // },
      // isSelectedVendorsInAllVendors() {
      //   return this.allVendors().includes(this.previewWidgetConfig.selectedVendorsByWidget[0]);
      // }
      // onCellClicked() {
      //   let startDate = ''
      //   let endDate = ''
      //   switch (this.widgetConfig.timeFrame) {
      //     case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE: {
      //       startDate = dayjs.utc().subtract(4, 'day').format(DATE_FORMAT);
      //       endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT);
      //       break;
      //     }
      //     case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE: {
      //       startDate = dayjs.utc().subtract(8, 'day').format(DATE_FORMAT);
      //       endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT);
      //       break;
      //     }
      //     case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS: {
      //       const theLatestSummarizedBillDate = _get(this.rawAbnormals, 'latestSummarizedBillDate')
      //       if (theLatestSummarizedBillDate) {
      //         startDate = endDate = dayjs(theLatestSummarizedBillDate).format(DATE_FORMAT)
      //       } else {
      //         startDate = endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT)
      //       }
      //       break;
      //     }
      //     case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH: {
      //       startDate = dayjs.utc().startOf('month').format(DATE_FORMAT);
      //       endDate = dayjs.utc().format(DATE_FORMAT);
      //       break;
      //     }
      //   }
      //   let payload = {
      //     widgetType: this.widgetConfig.widgetType,
      //     viewBy: this.widgetConfig.viewBy,
      //     startDate: startDate,
      //     endDate: endDate,
      //     data: this.standardizedAbnormals.map(item => {
      //       return {
      //         dataKey: item.item,
      //         vendor: item.vendor
      //       }
      //     })
      //   };
      //   this.$emit('clickToAnalyze', payload);
      // }
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
          <p class="-wrapper-v-center h-100">
            <span class="material-icons -color-green-1 -font-size-16">arrow_downward</span>
            <span >${formatPercentage(Math.abs(params.data.increaseDecreaseRate))}%</span>
          </p>
        `;
      } else {
        return `
          <p class="-wrapper-v-center h-100">
            <span class="material-icons -color-red-1 -font-size-16">arrow_upward</span>
            <span>${formatPercentage(params.data.increaseDecreaseRate)}%</span>
          </p>
        `;
      }
    }
  }

  function changeComparator(valueA, valueB, nodeA, nodeB, isInverted) {
    // return Math.abs(valueA) - Math.abs(valueB);
    return valueA - valueB;
  }

  function customComparator(valueA, valueB) {
    return valueA.toLowerCase().localeCompare(valueB.toLowerCase());
  }

  function getProcessHeaderCallbackForExport(gridApi, widgetConfig, $vm) {
    const rangeTime = exportRangeTime(widgetConfig.timeFrame, getFullDateFormatByLocalization(), $vm)
    return function (params) {
      switch (params.column.getColDef().field) {
        case 'linkedAccountAlias':
          return $vm.$t(`dashboard.abnormalChange.download.${widgetConfig.viewBy}`);
        case 'currentCost':
          return rangeTime[widgetConfig.timeFrame].firstTime;
        case 'lastCost':
          return rangeTime[widgetConfig.timeFrame].lastTime;
        default:
          return params.column.getColDef().headerName
      }
    };
  }

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
  .ml-abnormal-user-detection {
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
        height: 370px;
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
      line-height: 30px;
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
      cursor: pointer;
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
        .ag-center-cols-container {
          cursor: default; // Halt the Drill-Down pointer
        }
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
  }
  .edit-abnormal-user-form-wrapper {
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
</style>
