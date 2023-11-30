<template>
  <div class="dashboard-common-item">
    <b-col
      class="equal-element bg-white base-wrapper-radius py-16 custom-notification-no-data col-height custom-dashboard-common-item">
      <p
        class="font-14 custom-label font-family-notosanscjkkr-regular"
        style="display: inline-block!important;">
        {{ $t('dashboard.dashboardCommon.costMonthToDate') }} {{ getDateRange }} {{ '('+selectedVendor+')' }}
      </p>
      <b-row
        slot="header"
        style="display: inline-block!important; float: right;">
        <b-button
          :id="detailTableOption.detailTableOptionBtnId"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="dropdown-button in-cost-month-to-date"
          variant="transparent">
          <base-material
            :size="24"
            name="more_vert"
            color="gray-1"/>
        </b-button>
        <div
          :id="containerCustomPopover"
          class="custom-popover-cost-month-to-date"
        />
        <BasePopoverDropdown
          ref="costMonthToDatePopover"
          :target="detailTableOption.detailTableOptionBtnId"
          :placement="detailTableOption.placement"
          :options="detailTableOption.options"
          :show-popover="detailTableOption.showPopover"
          :enabled-localization="detailTableOption.enabledLocalization"
          :container-custom-popover="containerCustomPopover"
          @selectOption="onSelectOption"
        />
      </b-row>
      <b-modal
        ref="edit-cost-month-to-date"
        v-model="widgetConfig.isEditFormVisible"
        :title="$t('dashboard.editWidget')"
        modal-class="right-wing"
        hide-footer
        hide-backdrop>
        <div v-if="widgetConfig.isEditFormVisible">
          <EditCostMonthToDate
            :common-user-info="commonUserInfo"
            :widget-config="widgetConfig"
            :dashboard-view-mode="dashboardViewMode"
            @apply="applySaveWidget"
            @hideModal="hideModal"/>
        </div>
      </b-modal>
      <div v-show="widgetLoadingState[widgetConfig.index]">
        <b-col
          class="cost-month-to-date-loading"
        >
          <BaseLoadingIndicator :loading-height="78"/>
        </b-col>
      </div>
      <div v-if="hasCostMonthToDateData && !widgetLoadingState[widgetConfig.index]">
        <b-row
          class="base-font-special mt-10 ml-0 mr-0 current-month-cost"
          align-h="center"
          align-v="center">
          <span class="font-24 currency-text">
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(costMonthToDate.currentMonthCost)) }}
          </span>
          <p>
            <span class="exchange-rate-icon">
              <base-material
                v-if="costMonthToDate.currentMonthCost < costMonthToDate.lastMonthCost"
                :size="18"
                color="green-1"
                name="arrow_drop_down"
                class="icon-arrow-down"
              />
              <base-material
                v-else-if="costMonthToDate.currentMonthCost > costMonthToDate.lastMonthCost"
                :size="18"
                color="red-1"
                name="arrow_drop_up"
                class="icon-arrow-up"
              />
              <base-material
                v-else
                :size="18"
                color="gray-1"
                name="remove"
                class="icon-arrow-center"
              />
            </span>
            <span class="currency-text-small">
              {{ formatPercentage(costMonthToDateChangePercentage) }}%
            </span>
          </p>
        </b-row>
        <p
          v-if="costMonthToDate.currentMonthCost < costMonthToDate.lastMonthCost"
          class="font-12 color-gray-1 last-month"
        >
          {{ $t('dashboard.dashboardCommon.overLastMonth.#1') }}
          <base-material
            :size="18"
            color="green-1"
            name="arrow_drop_down"
            class="icon-arrow-down"
          />
          <span class="color-green-1">
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(Math.abs(costMonthToDate.lastMonthCost - costMonthToDate.currentMonthCost))) }}
          </span>
          {{ $t('dashboard.dashboardCommon.overLastMonth.#2') }}
        </p>
        <p
          v-else-if="costMonthToDate.currentMonthCost > costMonthToDate.lastMonthCost"
          class="font-12 color-gray-1 last-month"
        >
          {{ $t('dashboard.dashboardCommon.overLastMonth.#1') }}
          <base-material
            :size="18"
            color="red-1"
            name="arrow_drop_up"
            class="icon-arrow-up"
          />
          <span class="color-red-1">
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(Math.abs(costMonthToDate.lastMonthCost - costMonthToDate.currentMonthCost))) }}
          </span>
          {{ $t('dashboard.dashboardCommon.overLastMonth.#2') }}
        </p>
        <p
          v-else
          class="font-12 color-gray-1 last-month"
        >
          <base-material
            :size="18"
            color="gray-1"
            name="remove"
            class="icon-arrow-center"
          />
          {{ $t('dashboard.dashboardCommon.equalsLastMonth') }}
        </p>
      </div>
      <BaseNotificationNoData
        v-if="typeof selectedVendor != 'undefined' && !widgetLoadingState[widgetConfig.index] && !hasCostMonthToDateData"
        :content-displayed="noDataContentDisplayed"
        class="no-data"
      />
      <BaseNotificationNotSupport
        v-if="typeof selectedVendor == 'undefined' && !widgetLoadingState[widgetConfig.index]"
        :support-vendors="supportVendors"
        class="no-data"
      />
    </b-col>
  </div>
</template>

<script>
  import DashboardCommonMixin from '@/mixins/DashboardCommonMixin';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNotSupport from '@/components/common/BaseNotificationNotSupport';
  import { DEFAULT_CURRENCY, VENDOR } from '@/constants/constants';
  import {
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTION_EDIT,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DEFAULT_WIDGET_DATA,
    VIEW_MODE
  } from '@/constants/dashboardConstants';
  import _cloneDeep from 'lodash/cloneDeep';
  import { fetchCostMonthToDate }  from '@/api/dashboard';
  import { isCostMonthToDateWidgetDataConfigChanged,getSelectedVendorsByWidget } from '@/util/dashboardUtils';
  import { DEFAULT_EXCHANGE_RATE } from '@/constants/constants';
  import _isNil from "lodash/isNil";
  import _isEqual from "lodash/isEqual";
  import _isEmpty from "lodash/isEmpty";
  import {COST_MONTH_TO_DATE_VIEW_BY_VENDORS} from "../../../../constants/dashboardConstants";

  const EditCostMonthToDate = () => import('@/components/pages/dashboard/dashboard-common/EditCostMonthToDate');

  export default {
    name: 'DashboardMonthToDate',
    components: {
      BaseLoadingIndicator,
      EditCostMonthToDate,
      BaseNotificationNotSupport
    },
    mixins: [DashboardCommonMixin],
    props: {
      allVendors: {
        type: Array,
        default() {
          return []
        }
      },
      widgetConfig: {
        type: Object,
        required: true,
        default: null
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      dashboardViewMode: {
        type: String,
        require: true,
        default: ''
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      }
    },
    data() {
      return {
        legendPaddingLeft: 30,
        legendPaddingBottom: 1,
        legendPaddingRight: 20,
        legendPaddingTop: 10,
        markerWidth: 10,
        markerHeight: 10,
        categoryField: 'time',
        categoryMinGridDistance: 10,
        containerCustomPopover: `cost-month-to-date-custom-popover-${this.widgetConfig.index}`,
        detailTableOption: {
          options: DASHBOARD_DROPDOWN_OPTION_EDIT,
          detailTableOptionBtnId: `detail-table-option-btn-${this.widgetConfig.index}`,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        internalWidgetConfig: {},
        // previewWidgetConfig: {
        //   vendors: [COST_MONTH_TO_DATE_VENDOR_OPTION.AWS]
        // },
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendors: []
        },
        costMonthToDate: DEFAULT_WIDGET_DATA.COST_MONTH_TO_DATE,
        supportVendors: COST_MONTH_TO_DATE_VIEW_BY_VENDORS
      };
    },
    computed: {
      getDateRange() {
        if (!this.costMonthToDate || !this.costMonthToDate.currentMonthDate) {
          return '';
        }
        const dateArr = this.costMonthToDate.currentMonthDate.split('-');
        return `(${dateArr[1]}/01 ~ ${dateArr[1]}/${dateArr[2]})`;
      },
      valueAxisNumberFormat: function () {
        return `${this.currencySymbol}#a`;
      },
      selectedVendor: {
        get() {
          return getSelectedVendorsByWidget(this.widgetConfig, this, COST_MONTH_TO_DATE_VIEW_BY_VENDORS, true)
        }
      },
      hasCostMonthToDateData() {
        return !_isEmpty(this.costMonthToDate);
      },
    },
    watch: {
      allVendors: {
        handler() {
          if(_isNil(this.widgetConfig)) {
            return;
          }

          // if(this.widgetConfig.selectedVendorsByWidget == null) {
          //   this.widgetConfig.selectedVendorsByWidget = this.internalWidgetConfig.selectedVendorsByWidget;
          // }

          // this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
          this.loadCostMonthToDate(this.widgetConfig);
        },
        immediate: false
      },
      'commonUserInfo.selectedVendors': {
        handler(newSelectedVendors) {
          if (_isEqual(this.internalCommonUserInfo.selectedVendors, newSelectedVendors)) {
            return;
          }
          this.internalCommonUserInfo.selectedVendors = _cloneDeep(this.commonUserInfo.selectedVendors);
        },
        immediate: true
      },
      dashboardViewMode: function () {
        this.setDetailTableOption();
      },
      widgetConfig: {
        handler(newValue, oldValue) {
          if (!isCostMonthToDateWidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if(_isEmpty(this.allVendors)) {
            return;
          }

          // if(_isEmpty(this.widgetConfig.selectedVendorsByWidget[0]) || _isEmpty(this.widgetConfig.selectedVendorsByWidget || _isNil(this.widgetConfig.selectedVendorsByWidget))) {
          //   this.widgetConfig.selectedVendorsByWidget = this.internalWidgetConfig.selectedVendorsByWidget;
          // }

          if(isCostMonthToDateWidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
          }

        },
        immediate: true
      },
      internalWidgetConfig: {
        handler() {
          if (_isNil(this.internalWidgetConfig)) {
            return;
          }
          this.loadCostMonthToDate(this.internalWidgetConfig);
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
      // 'internalCommonUserInfo.selectedVendors': {
      //   handler() {
      //     const widgetConfig = {
      //       ...this.internalWidgetConfig,
      //       // ...this.previewWidgetConfig
      //     };
      //     this.loadCostMonthToDate(widgetConfig);
      //   },
      //   immediate: false
      // }
    },
    mounted(){
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setDetailTableOption();
    },
    methods: {
      setDetailTableOption() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.detailTableOption.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.detailTableOption.options = DASHBOARD_DROPDOWN_OPTION_EDIT;
        }
      },
      loadCostMonthToDate(widgetConfig) {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;
        let payload = {
          ...widgetConfig,
          vendors: this.allVendors.filter(vendor => this.internalCommonUserInfo.selectedVendors),
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.widgetConfig, this, COST_MONTH_TO_DATE_VIEW_BY_VENDORS)]
        };
        fetchCostMonthToDate(payload)
          .then((res) => {
            this.costMonthToDate = res;
            this.widgetLoadingState[this.widgetConfig.index] = false;
          })
          .catch((err) => {
            console.error(err);
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.costMonthToDate = DEFAULT_WIDGET_DATA.COST_MONTH_TO_DATE;
          });
      },
      onSelectOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
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
        this.$refs.costMonthToDatePopover.close();
      },
      applySaveWidget(widgetConfigForm) {
        if(_isEqual(this.widgetConfig.selectedVendorsByWidget, widgetConfigForm.selectedVendorsByWidget)){
          this.hideModal();
          return;
        }
        const widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        this.$emit('save', widgetConfig);
      },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      }
    }
  };
</script>

<style lang="scss">
  .custom-popover-cost-month-to-date {
    .popover {
      top: 0 !important;
      left: 16px !important;
      .arrow {
        left: 75% !important;
      }
      .arrow:before {
        border-bottom-color: #ffffff;
      }
    }
  }
</style>
