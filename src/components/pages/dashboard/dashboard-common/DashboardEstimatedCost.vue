<template>
  <div class="dashboard-common-item">
    <b-col
      class="equal-element bg-white base-wrapper-radius py-16 custom-notification-no-data col-height custom-dashboard-common-item">
      <b-row
        align-v="center"
        align-h="center"
        class="estimated-cost-title">
        <span
          class="font-14 mr-8 custom-label font-family-notosanscjkkr-regular forecast-this-month-title">
          {{ $t('dashboard.dashboardCommon.forecastThisMonth') }}
          {{ '(' + selectedVendor + ')' }}
        </span>
        <base-material
          :id="forecastThisMonthIcon"
          :size="16"
          name="info"
        />
        <b-tooltip
          :target="forecastThisMonthIcon"
        >
          <span class="font-12">
            {{ $t('dashboard.dashboardCommon.forecastThisMonthTooltip') }}
          </span>
        </b-tooltip>
      </b-row>
      <b-row
        style="display: inline!important; float: right;">
        <b-button
          :id="detailTableOption.detailTableOptionBtnId"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="dropdown-button in-estimated-cost"
          variant="transparent"
          style="float: right;">
          <base-material
            :size="24"
            name="more_vert"
            color="gray-1"/>
        </b-button>
        <div
          :id="containerCustomPopover"
          class="custom-popover-estimated-cost"
        />
        <BasePopoverDropdown
          ref="estimatedCostPopover"
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
        ref="edit-estimated-cost"
        v-model="widgetConfig.isEditFormVisible"
        :title="$t('dashboard.editWidget')"
        modal-class="right-wing"
        hide-footer
        hide-backdrop
      >
        <div v-if="widgetConfig.isEditFormVisible">
          <EditEstimatedCost
            :common-user-info="internalCommonUserInfo"
            :exchange-rate="internalExchangeRate"
            :widget-config="internalWidgetConfig"
            :dashboard-view-mode="dashboardViewMode"
            @apply="applySaveWidget"
            @hideModal="hideModal"/>
        </div>
      </b-modal>
      <div v-show="widgetLoadingState[widgetConfig.index]">
        <b-col
          class="estimated-cost-loading"
        >
          <BaseLoadingIndicator :loading-height="78"/>
        </b-col>
      </div>
      <div
        v-if="hasEstimatedCostData && !widgetLoadingState[widgetConfig.index]"
        v-show="!widgetLoadingState[widgetConfig.index]">
        <b-row
          class="base-font-special mt-10 mt-10 ml-0 mr-0 current-month-estimated-cost"
          align-h="center"
          align-v="center">
          <span
            class="font-24 currency-text"
          >
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(dashboardEstimatedCost.currentMonthEstimatedCost)) }}
          </span>
          <p>
            <span class="exchange-rate-icon">
              <base-material
                v-if="dashboardEstimatedCost.currentMonthEstimatedCost < dashboardEstimatedCost.lastMonthTotalCost"
                :size="18"
                color="green-1"
                name="arrow_drop_down"
                class="icon-arrow-down"
              />
              <base-material
                v-else-if="dashboardEstimatedCost.currentMonthEstimatedCost > dashboardEstimatedCost.lastMonthTotalCost"
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
              {{ formatPercentage(estimatedCostChangePercentage) }}%
            </span>
          </p>
        </b-row>
        <p
          v-if="dashboardEstimatedCost.currentMonthEstimatedCost < dashboardEstimatedCost.lastMonthTotalCost"
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
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(Math.abs(dashboardEstimatedCost.lastMonthTotalCost - dashboardEstimatedCost.currentMonthEstimatedCost))) }}
          </span>
          {{ $t('dashboard.dashboardCommon.overLastMonth.#2') }}
        </p>
        <p
          v-else-if="dashboardEstimatedCost.currentMonthEstimatedCost > dashboardEstimatedCost.lastMonthTotalCost"
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
            {{ currencySymbol }}{{ formatCost(calculateCostByCurrency(Math.abs(dashboardEstimatedCost.lastMonthTotalCost - dashboardEstimatedCost.currentMonthEstimatedCost))) }}
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
        v-if="!hasEstimatedCostData && !widgetLoadingState[widgetConfig.index] && typeof selectedVendor != 'undefined'"
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

  import { mapGetters } from  'vuex';
  import {
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTION_EDIT,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    VIEW_MODE,
    DEFAULT_ESTIMATED_COST_WIDGET_CONFIG,
    VENDORS,
    DEFAULT_WIDGET_DATA
  } from '@/constants/dashboardConstants';
  import {DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import { isEstimatedCostWidgetDataConfigChanged, getSelectedVendorsByWidget } from '@/util/dashboardUtils';
  import {fetchEstimatedCost} from '@/api/dashboard';
  import _cloneDeep from "lodash/cloneDeep";
  import _isEmpty from "lodash/isEmpty";
  import _isNil from "lodash/isNil";
  import _isEqual from "lodash/isEqual";
  import {
    COST_MONTH_TO_DATE_VIEW_BY_VENDORS,
    ESTIMATED_COST_VIEW_BY_VENDORS
  } from "../../../../constants/dashboardConstants";

  const EditEstimatedCost = () => import('@/components/pages/dashboard/dashboard-common/EditEstimatedCost');

  export default {
    name: 'DashboardEstimatedCost',
    components: {
      EditEstimatedCost,
      BaseLoadingIndicator,
      BaseNotificationNotSupport
    },
    mixins: [DashboardCommonMixin],
    props: {
      widgetConfig: {
        type: Object,
        required: true,
        default: null
      },
      estimatedCost: {
        type: Object,
        required: true
      },
      dashboardViewMode: {
        type: String,
        require: true,
        default: ''
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      }
    },
    data() {
      return {
        forecastThisMonthIcon: `forecast-this-month-icon-${this.widgetConfig.index}`,
        containerCustomPopover: `estimated-cost-custom-popover-${this.widgetConfig.index}`,
        detailTableOption: {
          options: DASHBOARD_DROPDOWN_OPTION_EDIT,
          detailTableOptionBtnId: `detail-table-option-btn-${this.widgetConfig.index}`,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendorsByWidget: []
        },
        internalExchangeRate: DEFAULT_EXCHANGE_RATE,
        internalWidgetConfig: DEFAULT_ESTIMATED_COST_WIDGET_CONFIG,
        previewWidgetConfig: {
          selectedVendorsByWidget: []
        },
        dashboardEstimatedCost: {
          selectedVendorsByWidget: []
        },
        supportVendors: ESTIMATED_COST_VIEW_BY_VENDORS
      };
    },
    computed: {
      ...mapGetters({
        isEstimatedCostLoading: 'dashboard/isEstimatedCostLoading',
      }),
      hasEstimatedCostData() {
        return !_isEmpty(this.dashboardEstimatedCost);
      },
      // selectedVendor: {
      //   get() {
      //     return this.isSelectedVendorsNotEmpty(this.internalWidgetConfig)
      //       ? this.internalWidgetConfig.selectedVendorsByWidget[0]
      //       : !_isEmpty(this.commonUserInfo.selectedVendors[0])
      //         ? this.commonUserInfo.selectedVendors[0] : this.allVendors()[0];
      //   }
      // },
      selectedVendor: {
        get() {
          return getSelectedVendorsByWidget(this.internalWidgetConfig, this, ESTIMATED_COST_VIEW_BY_VENDORS, true);
        }
      }
    },
    watch: {
      dashboardViewMode: function () {
        this.setDetailTableOption();
      },
      widgetConfig: {
        handler(newValue, oldValue) {
          if (!isEstimatedCostWidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if (!isEstimatedCostWidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            //In case user change preview widget config in dashboard page & click to open edit form modal but make no change and then click save button
            // -> we have to update preview widget config like edit form modal
            if (isEstimatedCostWidgetDataConfigChanged(this.previewWidgetConfig, this.widgetConfig, true)) {
              this.updatePreviewWidgetConfig();
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
      'commonUserInfo.selectedVendors': {
        handler(newSelectedVendors) {
          if (_isEqual(this.internalCommonUserInfo.selectedVendors, newSelectedVendors)) {
            return;
          }
          this.internalCommonUserInfo.selectedVendors = _cloneDeep(this.commonUserInfo.selectedVendors);
        },
        immediate: true
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
    },
    created() {
    },
    mounted(){
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setDetailTableOption();
    },
    methods: {
      updatePreviewWidgetConfig() {
        this.previewWidgetConfig = {
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, ESTIMATED_COST_VIEW_BY_VENDORS)]
        };
        this.loadDashboardEstimatedCost(this.previewWidgetConfig);
      },
      setDetailTableOption() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.detailTableOption.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.detailTableOption.options = DASHBOARD_DROPDOWN_OPTION_EDIT;
        }
      },
      loadDashboardEstimatedCost(widgetConfig) {
        const $vm = this;
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;
        let payload = {
          ...widgetConfig,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, ESTIMATED_COST_VIEW_BY_VENDORS)]
        }
        fetchEstimatedCost(payload)
          .then((res) => {
            this.dashboardEstimatedCost = res;
            this.widgetLoadingState[this.widgetConfig.index] = false;
            // $vm.selectedVendor = this.dashboardEstimatedCost.selectedVendor;
            // $vm.internalWidgetConfig.selectedVendor = this.dashboardEstimatedCost.selectedVendor;
          })
          .catch((err) => {
            console.error(err);
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.dashboardEstimatedCost = null;
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
        this.$refs.estimatedCostPopover.close();
      },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      },
      applySaveWidget(widgetConfigForm) {
        const widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        this.$emit('save', widgetConfig);
      },
      // isSelectedVendorsNotEmpty($vm) {
      //   return !_isEmpty($vm.selectedVendorsByWidget) && !_isEmpty($vm.selectedVendorsByWidget[0]);
      // },
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
      // isSelectedVendorsInAllVendors() {
      //   return this.allVendors().includes(this.previewWidgetConfig.selectedVendorsByWidget[0]);
      // },
      // getDefaultVendorByCheckedAuth(vendor){
      //   let rslt = !_isEmpty(this.allVendors()) && this.allVendors().includes(vendor) ? vendor :this.allVendors().filter(v=> v != vendor).map(v=>{return v})[0];
      //   return rslt;
      // },
    }
  };
</script>

<style lang="scss">
  .custom-popover-estimated-cost {
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
  .estimated-cost-title {
    display: inline-block!important;
    .material-icons {
      position: relative;
      top: 2.5px;
      color: #b0b7bf;
      cursor: pointer;
    }
  }

</style>
