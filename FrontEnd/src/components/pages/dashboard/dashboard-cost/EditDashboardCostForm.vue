<template>
  <fragment>
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span>{{ $t('dashboard.dashboardCost.title') }}</span>
      </p>
      <div class="-pb-1">
        <p class="-description -color-darkgray-1">{{ $t('dashboard.dashboardCost.desc1') }}</p>
        <p class="-description -color-darkgray-1">{{ $t('dashboard.dashboardCost.desc2') }}</p>
      </div>
      <div>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.dashboardCost.descCommon') }}</p>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.dashboardCost.descAWS') }}</p>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.dashboardCost.descGCP') }}</p>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.dashboardCost.descAzure') }}</p>
        <p class="-description -color-darkgray-1">{{ $t('dashboard.dashboardCost.descTencent') }}</p>
      </div>
    </b-row>
    <b-form
      v-if="show"
      id="edit-dashboard-cost-form"
      @submit="onSubmit"
      @reset="onReset"
    >
      <!--      <b-form-group-->
      <!--        id="preview-group"-->
      <!--        :label="$t('dashboard.dashboardCost.editWidgetForm.previews')"-->
      <!--        label-for="preview">-->
      <!--        <PreviewDashboardCost-->
      <!--          :common-user-info="commonUserInfo"-->
      <!--          :exchange-rate="exchangeRate"-->
      <!--          :view-by="draftWidgetConfig.viewBy"-->
      <!--          :date-type-and-time-frame="dateTypeAndTimeFrame"-->
      <!--          :chart-type="draftWidgetConfig.chartType"-->
      <!--          :scale="draftWidgetConfig.scale"-->
      <!--          :filter="draftWidgetConfig.filter"-->
      <!--          :custom-filter="draftWidgetConfig.customFilter"-->
      <!--          @changeDashboardCost="onChangePreviewDashboardCost"-->
      <!--        />-->
      <!--      </b-form-group>-->
      <b-form-group
        id="cloud-service-group"
        :label="$t('dashboard.dashboardCost.editWidgetForm.cloudService')"
        label-for="selectVendor">
        <b-form-select
          id="selectVendor"
          v-model="selectedVendorByOption"
          :options="vendorOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          @change="refreshDataWhenChangeOption"
        />
      </b-form-group>
      <b-form-group
        id="view-by-group"
        :label="$t('dashboard.dashboardCost.editWidgetForm.viewBy')"
        label-for="view-by">
        <b-form-select
          id="view-by"
          v-model="internalViewBy"
          :options="viewByOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          required
        />
      </b-form-group>
      <b-form-group id="time-frame-group">
        <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.dashboardCost.editWidgetForm.dateRange') }}</div>
        <b-form-select
          :value="internalDateType"
          :options="dateTypeOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          class="date-type-options"
          @change="onChangeDateType"
        />
        <b-form-select
          :value="internalTimeFrame"
          :options="timeFrameOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          class="time-frame-options"
          @change="onChangeTimeFrame"
        />
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.dashboardCost.editWidgetForm.chartType') }}</div>
      <b-button-group class="mr-8 mb-3">
        <b-button
          id="equalize-btn-id"
          :pressed="draftWidgetConfig.chartType === CHART_TYPE.STACK"
          :disabled="vendorOptions.length > 0 ? false : true"
          variant="outline-gray-4"
          class="custom-color blue-1 icon-only box-shadow-none chart-type-button"
          @click="onChangeChartType(CHART_TYPE.STACK)"
        >
          <base-icon
            :original="true"
            :color="draftWidgetConfig.chartType !== CHART_TYPE.STACK ? '#7b8088' : ''"
            name="icon_material_equalize_custom"
            width="12"
            height="12"/>
          <span class="ml-1">{{ $t('dashboard.chartTypeOption.stack') }}</span>
        </b-button>
        <b-button
          :pressed="draftWidgetConfig.chartType === CHART_TYPE.LINE"
          :disabled="vendorOptions.length > 0 ? false : true"
          variant="outline-gray-4"
          class="custom-color blue-1 icon-only box-shadow-none chart-type-button"
          @click="onChangeChartType(CHART_TYPE.LINE)"
        >
          <base-material name="timeline"/>
          <span class="ml-1">{{ $t('dashboard.chartTypeOption.line') }}</span>
        </b-button>
      </b-button-group>
      <b-form-group
        v-if="draftWidgetConfig.chartType === CHART_TYPE.STACK"
        class="mr-8"
        size="sm"
      >
        <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.dashboardCost.editWidgetForm.setScale') }}</div>
        <b-form-radio-group
          id="scale-radio-group"
          v-model="draftWidgetConfig.scale"
          :disabled="vendorOptions.length > 0 ? false : true"
          stacked
        >
          <!--백분율, 값 선택 부분입니다.-->
          <b-form-radio
            :value="SCALE.VALUE"
            class="mb-1"
          >
            <span class="option-value-label">{{ $t('dashboard.scaleOption.value') }}</span>
          </b-form-radio>
          <b-form-radio
            :value="SCALE.PERCENTAGE"
            class="mb-1"
          >
            <span class="option-value-label">{{ $t('dashboard.scaleOption.percentage') }}</span>
          </b-form-radio>
        </b-form-radio-group>
      </b-form-group>
      <b-form-group
        class="mr-8"
        size="sm"
      >
        <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.dashboardCost.editWidgetForm.setFilter') }}</div>
        <b-form-radio-group
          id="filter-radio-group"
          v-model="draftWidgetConfig.filter"
          :disabled="vendorOptions.length > 0 ? false : true"
          stacked
        >
          <b-form-radio
            :value="FILTER.TOP_10_BY_COST"
            class="mb-1"
          >
            <span class="option-value-label">{{ $t('dashboard.filterOption.top10ByCost') }}</span>
          </b-form-radio>
          <b-form-radio
            :value="FILTER.CUSTOM"
            class="mb-1"
          >
            <span class="option-value-label">{{ $t('dashboard.filterOption.custom') }} ({{ internalCustomFilter!=undefined?internalCustomFilter.length:0 }}/{{ MAX_CUSTOM_FILTER_SIZE }})</span>
          </b-form-radio>
        </b-form-radio-group>
        <MultiSelect
          v-click-outside="onClickOutsideCustomFilterMultiselect"
          :ref="customFilterMultiselectRef"
          v-model="internalCustomFilter"
          :multiple="true"
          :options="customFilterOptions"
          :disabled="(draftWidgetConfig.filter === FILTER.TOP_10_BY_COST) || (vendorOptions.length > 0 ? false : true)"
          :placeholder="customFilterPlaceholder"
          :max="MAX_CUSTOM_FILTER_SIZE"
          :hide-selected="true"
          :custom-label="customFilterOptionText"
          :show-labels="false"
          :close-on-select="false"
          track-by="item"
          @open="onOpenCustomFilter"
          @close="onCloseCustomFilter"
        >
          <!--@mousedown.prevent fixes the "flicker" bug when clicking tags-->
          <!--but still does not fix when clicking space around tags (no slot for it)-->
          <!--potentially library issue. Reference https://github.com/shentao/vue-multiselect/issues/826-->
          <template
            slot="tag"
            slot-scope="{ option, remove }"
          >
            <span
              class="custom-filter-tag"
              @mousedown.prevent="onClickSelectedCustomFilter"
            >
              <span>{{ customFilterOptionText(option) }}</span>
              <span
                class="custom-filter-tag-icon"
                @mousedown.prevent="onRemoveCustomFilterMultiselectOption(option, remove)"
              />
            </span>
          </template>
          <template
            slot="caret"
            slot-scope="{ toggle }"
          >
            <div
              class="multiselect__select"
              @mousedown.prevent.stop="onClickCustomFilterMultiselectCaretBtn(toggle)"
            />
          </template>
          <template slot="maxElements">
            {{ '' }}
          </template>
          <template slot="noResult">
            {{ '' }}
          </template>
        </MultiSelect>
        <div
          class="float-right control-buttons">
          <b-button
            variant="outline-secondary"
            class="cancel-button font-family-notosanscjkkr-medium"
            @click="onClickCancelButton"
          >
            {{ $t('dashboard.topCost.cancel') }}
          </b-button>
          <span>
            <b-button
              :disabled="(vendorOptions.length > 0 ? false : true) || isCustomFilterEmpty"
              type="submit"
              variant="primary"
              class="font-family-notosanscjkkr-medium"
            >
              {{ saveButtonLocalization }}
            </b-button>
          </span>
        </div>
      </b-form-group>
    </b-form>
  </fragment>
</template>

<script>
  import {
    DASHBOARD_COST_MAX_CUSTOM_FILTER_SIZE,
    DASHBOARD_DATE_TYPE,
    DASHBOARD_DATE_TYPE_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR,
    DEFAULT_COST_BY_WIDGET_CONFIG,
    FILTER,
    MONTHLY_COST_TIME_FRAME,
    MONTHLY_COST_TIME_FRAME_OPTIONS,
    SCALE,
    WEEKLY_COST_TIME_FRAME,
    WEEKLY_COST_TIME_FRAME_OPTIONS,
    VIEW_MODE,
    DASHBOARD_VIEW_BY,
    DASHBOARD_WIDGET_TYPE,
    COST_BY_WIDGET_VENDORS,
    SELECTED_VENDOR
  } from '@/constants/dashboardConstants';
  import { getSelectedVendorsByWidget, availableVendors } from "@/util/dashboardUtils";
  import MultiSelect from 'vue-multiselect';
  import 'vue-multiselect/dist/vue-multiselect.min.css';
  import {CHART_TYPE, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import {fetchDashboardCost} from '@/api/dashboard';
  import ClickOutside from 'vue-click-outside';
  import _cloneDeep from 'lodash/cloneDeep';
  import _isNil from 'lodash/isNil';
  import _isEmpty from 'lodash/isEmpty';
  import _get from 'lodash/get';
  import _isEqual from 'lodash/isEqual';
  import _toLower from 'lodash/toLower';

  export default {
    name: 'EditDashboardCostForm',
    components: {
      MultiSelect,
    },
    directives: {
      ClickOutside,
    },
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
        default() {
          return DEFAULT_COST_BY_WIDGET_CONFIG
        }
      },
      dashboardViewMode: {
        type: String,
        required: true,
        default: VIEW_MODE.DEFAULT
      }
    },
    data() {
      return {
        vendorOptions: availableVendors(COST_BY_WIDGET_VENDORS, this),
        draftWidgetConfig: DEFAULT_COST_BY_WIDGET_CONFIG,
        viewByOptions: DASHBOARD_VIEW_BY_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        dateTypeOptions: DASHBOARD_DATE_TYPE_OPTIONS.map(opt => {
          return {
            ...opt,
            text: this.$t(opt.text)
          };
        }),
        timeFrameOptions: MONTHLY_COST_TIME_FRAME_OPTIONS.map(opt => {
          return {
            ...opt,
            text: this.$t(opt.text)
          };
        }),
        DEFAULT_MONTHLY_TIME_FRAME: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
        DEFAULT_WEEKLY_TIME_FRAME: WEEKLY_COST_TIME_FRAME.LAST_8_WEEKS,
        //selectedVendorByOption : '',
        customFilterOptions: [],
        show: true,
        CHART_TYPE: CHART_TYPE,
        SCALE: SCALE,
        FILTER: FILTER,
        MAX_CUSTOM_FILTER_SIZE: DASHBOARD_COST_MAX_CUSTOM_FILTER_SIZE,
        isCustomFilterOpened: false,
        draftSelectedCustomFilter: [],
        customFilterMultiselectRef: 'customFilterMultiselect',
      }
    },
    computed: {
      dateTypeAndTimeFrame: function() {
        return {
          dateType: this.draftWidgetConfig.dateType,
          timeFrame: this.draftWidgetConfig.timeFrame
        }
      },
      customFilterPlaceholder() {
        if (!_isEmpty(this.internalCustomFilter) && this.internalCustomFilter.length >= this.MAX_CUSTOM_FILTER_SIZE) {
          return this.$t('dashboard.dashboardCost.editWidgetForm.customFilter.placeholder.reachedMax', {max: this.MAX_CUSTOM_FILTER_SIZE});
        }
        if (this.isCustomFilterOpened) {
          return this.$t('dashboard.dashboardCost.editWidgetForm.customFilter.placeholder.searchAnything');
        }
        return this.$t('dashboard.dashboardCost.editWidgetForm.customFilter.placeholder.selectItem');
      },
      itemVendorToCustomFilterOpt() {
        let map = {};
        this.customFilterOptions.forEach(cf => {
          map[getCustomFilterKey(cf)] = cf;
        });
        return map;
      },
      internalCustomFilter: {
        get() {
          if (_isEmpty(this.draftSelectedCustomFilter)) {
            return [];
          }
          return this.draftSelectedCustomFilter.map(cf => {
            return {
              ...cf,
              itemAlias: _isEmpty(this.itemVendorToCustomFilterOpt) || _isNil(this.itemVendorToCustomFilterOpt[getCustomFilterKey(cf)])
                ? ''
                : this.itemVendorToCustomFilterOpt[getCustomFilterKey(cf)].itemAlias
            };
          });
        },
        set(customFilters) {
          this.draftSelectedCustomFilter = customFilters;
        }
      },
      internalViewBy: {
        get() {
          if(_isEqual(this.draftWidgetConfig.viewBy, "project") || _isEqual(this.draftWidgetConfig.viewBy, "account")) {
            if(this.draftWidgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP)) {
              return "project";
            } else {
              return "account";
            }
          } else {
            return this.draftWidgetConfig.viewBy;
          }
        },
        set(selectedViewBy) {
          this.$set(this.draftWidgetConfig, 'viewBy', selectedViewBy);
          this.$set(this.draftWidgetConfig, 'customFilter', []);
        }
      },
      internalDateType: {
        get() {
          return this.draftWidgetConfig.dateType;
        },
        set(selectedDateType) {
        }
      },
      internalTimeFrame: {
        get() {
          return this.draftWidgetConfig.timeFrame;
        },
        set(selectedDateType) {
        }
      },
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      dashboardCostRequestParams: function() {
        return {
          selectedVendorsByWidget: this.draftWidgetConfig.selectedVendorsByWidget,
          viewBy: this.draftWidgetConfig.viewBy,
          dateType: this.dateTypeAndTimeFrame.dateType,
          timeFrame: this.dateTypeAndTimeFrame.timeFrame,
          filter: this.draftWidgetConfig.filter,
          customFilter: this.draftWidgetConfig.customFilter,
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
        };
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.draftWidgetConfig, this, COST_BY_WIDGET_VENDORS);
        },
        set(selectedVendorByOption) {
          this.refreshDataWhenChangeOption();
          this.$set(this.draftWidgetConfig, 'customFilter', []);
          this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [selectedVendorByOption]);
        }
      },
      isCustomFilterEmpty: {
        get(){
          if(this.draftWidgetConfig.filter === FILTER.CUSTOM && _isEmpty(this.internalCustomFilter)){
            return true;
          }else{
            return false;
          }
        }
      }
    },
    watch: {
      widgetConfig: {
        handler: function() {
          this.draftWidgetConfig = _cloneDeep(this.widgetConfig);
          this.initDateTypeAndTimeFrame();
        },
        immediate: true
      },
      'draftWidgetConfig.customFilter': {
        handler() {
          this.draftSelectedCustomFilter = _cloneDeep(this.draftWidgetConfig.customFilter);
        },
        immediate: true,
      },
      'draftWidgetConfig.filter': {
        handler: function() {
          if (this.draftWidgetConfig.filter === 'custom') {
            this.fetchDashboardCostData(this.dashboardCostRequestParams);
          }
        },
        immediate: true
      },
      'draftWidgetConfig.viewBy': {
        handler: function() {
          this.fetchDashboardCostData(this.dashboardCostRequestParams);
          //this.refreshViewByVendor();
        },
        immediate: true
      },
      'draftWidgetConfig.selectedVendorsByWidget': {
        handler: function() {
          this.refreshViewByVendor()
        },
        immediate: true
      }
    },
    created() {
      if(this.widgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.NCP)){
        this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS.filter(option => option.value != DASHBOARD_DATE_TYPE.WEEKLY).map(opt => {
          return {
            ...opt,
            text: this.$t(opt.text)
          };
        });
        this.widgetConfig.dateType = DASHBOARD_DATE_TYPE.MONTHLY;
        //this.onChangeDateType(this.widgetConfig.dateType);
      }else{
        this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS.map(opt => {
          return {
            ...opt,
            text: this.$t(opt.text)
          };
        });
      }
    },
    methods: {
      refreshViewByVendor(){
        if(this.draftWidgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP) && this.draftWidgetConfig.viewBy === DASHBOARD_VIEW_BY.REGION){
          this.draftWidgetConfig.viewBy = DASHBOARD_VIEW_BY.ACCOUNT;
        }
        this.viewByOptions = this.funcCostByWidgetViewByOptions(this.selectedVendorByOption)
        this.fetchDashboardCostData(this.dashboardCostRequestParams);
      },
      onSubmit(evt) {
        evt.preventDefault();
        this.$emit('save', this.draftWidgetConfig);
      },
      onReset(evt){
        evt.preventDefault();
      },
      onClickCancelButton() {
        this.$emit('hideModal');
      },
      initDateTypeAndTimeFrame() {
        this.onChangeDateType(this.widgetConfig.dateType);
        this.onChangeTimeFrame(this.widgetConfig.timeFrame);
      },
      onChangeChartType(type) {
        this.draftWidgetConfig.chartType = type
      },
      customFilterOptionText(opt) {
        if (this.draftWidgetConfig.viewBy === DASHBOARD_VIEW_BY.ACCOUNT) {
          return _isEmpty(opt.itemAlias) ? opt.item : `${opt.itemAlias} (${opt.item})`;
        }
        return _isEmpty(opt.itemAlias) ? '' : opt.itemAlias;
      },
      onChangePreviewDashboardCost(previewDashboardCost) {
        const newCustomFilterOptions = _get(previewDashboardCost, 'customFilters') || [];
        if (_isEqual(this.customFilterOptions, newCustomFilterOptions)) {
          return;
        }
        this.customFilterOptions = _cloneDeep(newCustomFilterOptions);
        this.customFilterOptions.sort((a,b) => {
          const aOptText = _toLower(this.customFilterOptionText(a));
          const bOptText = _toLower(this.customFilterOptionText(b));
          if (aOptText > bOptText) {
            return 1
          }
          if (aOptText < bOptText) {
            return -1;
          }
          return 0;
        });
      },
      onChangeDateType(selectedDateType) {
        this.draftWidgetConfig.dateType = selectedDateType;
        switch (selectedDateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY: {
            this.timeFrameOptions = MONTHLY_COST_TIME_FRAME_OPTIONS.map(opt => {
              return {
                ...opt,
                text: this.$t(opt.text)
              };
            });
            this.draftWidgetConfig.timeFrame = this.DEFAULT_MONTHLY_TIME_FRAME;
            break;
          }
          case DASHBOARD_DATE_TYPE.WEEKLY: {
            this.timeFrameOptions = WEEKLY_COST_TIME_FRAME_OPTIONS.map(opt => {
              return {
                ...opt,
                text: this.$t(opt.text)
              };
            });
            this.draftWidgetConfig.timeFrame = this.DEFAULT_WEEKLY_TIME_FRAME;
            break;
          }
        }
      },
      onChangeTimeFrame(selectedTimeFrame) {
        this.draftWidgetConfig.timeFrame = selectedTimeFrame;
        switch (this.draftWidgetConfig.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY: {
            this.DEFAULT_MONTHLY_TIME_FRAME = selectedTimeFrame;
            break;
          }
          case DASHBOARD_DATE_TYPE.WEEKLY: {
            this.DEFAULT_WEEKLY_TIME_FRAME = selectedTimeFrame;
            break;
          }
        }
      },
      // the "flicker" bug is actually "close and open happens in a short time"
      // -> should only allow "apply draft" on some expected close events (caret close, click outside)
      applyDraftSelectedCustomFilter() {
        this.draftWidgetConfig.customFilter = this.draftSelectedCustomFilter;
      },
      onClickOutsideCustomFilterMultiselect() {
        this.applyDraftSelectedCustomFilter();
      },
      // if this is a "close" action -> apply the draft
      onClickCustomFilterMultiselectCaretBtn(toggleCallback) {
        toggleCallback();
        if (!this.isCustomFilterOpened) {
          this.applyDraftSelectedCustomFilter();
        }
      },
      onRemoveCustomFilterMultiselectOption(option, removeCallback) {
        removeCallback(option);
      },
      onClickSelectedCustomFilter() {
        this.$refs[this.customFilterMultiselectRef].activate();
      },
      onOpenCustomFilter() {
        this.isCustomFilterOpened = true;
      },
      onCloseCustomFilter() {
        this.isCustomFilterOpened = false;
      },
      fetchDashboardCostData(payload) {
        this.isLoading = true;
        let customFilterPayload = null;
        if (_isNil(payload.customFilter)) {
          customFilterPayload = {
            ...payload,
            customFilter: []
          }
        } else {
          customFilterPayload = payload;
        }
        fetchDashboardCost(customFilterPayload)
          .then((res) => {
            this.isLoading = false;
            this.dashboardCost = res;
            this.onChangePreviewDashboardCost(res);
          })
          .catch((err) => {
            this.isLoading = false;
            console.error(err);
          })
      },
      funcCostByWidgetViewByOptions(selectedVendorsByWidget) {
        if(!_isEmpty(DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget])){
          return DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget].map(option => {
            return {
              ...option,
              text: this.$t(option.text)
            }
          })
        }else{
          return DASHBOARD_VIEW_BY_OPTIONS.map(option => {
            return {
              ...option,
              text: this.$t(option.text)
            };
          })
        }
      },
      refreshDataWhenChangeOption() {
        if(this.draftWidgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.NCP)){
          this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS.filter(option => option.value != DASHBOARD_DATE_TYPE.WEEKLY).map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
          this.timeFrameOptions = MONTHLY_COST_TIME_FRAME_OPTIONS.map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
          this.draftWidgetConfig.dateType = DASHBOARD_DATE_TYPE.MONTHLY;
          this.draftWidgetConfig.timeFrame = this.DEFAULT_MONTHLY_TIME_FRAME;
          this.onChangeDateType(this.draftWidgetConfig.dateType);
        }else{
          this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS.map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
        }
      },
    },
  };

  function getCustomFilterKey(cf) {
    return `${cf.item}-${cf.vendor}`;
  }
</script>
<style lang="scss">
  #edit-dashboard-cost-form {
    #preview-group {
      #preview-group__BV_label_ {
        font-family: 'NotoSansCJKkr-Medium';
      }
    }
    #view-by-group {
      #view-by-group__BV_label_ {
        font-family: 'NotoSansCJKkr-Medium';
      }
    }
    #equalize-btn-id, .chart-type-button {
      border-bottom-left-radius: 4px;
      border-top-left-radius: 4px;
      &:hover {
        path {
          fill: #ffffff;
        }
      }
    }
    #filter-radio-group {
      margin-bottom: 8px;
    }
    .control-buttons {
      margin-top: 20px;
    }
    #view-by-group {
      position: relative;
      #view-by {
        &:focus {
          box-shadow: none !important;
        }
      }
    }
    .cancel-button {
      margin-right: 6px;
    }
    .option-value-label {
      color: #7b8088
    }
    .custom-filter-tag {
      position: relative;
      display: inline-flex;
      padding: 1px 26px 1px 10px;
      border-radius: 4px;
      margin-right: 4px;
      margin-bottom: 2px;
      color: #6c7994;
      line-height: 1;
      background: #e9ebf5;
      white-space: nowrap;
      overflow: hidden;
      max-width: 100%;
      text-overflow: ellipsis;
      font-size: 12px;
    }

    .custom-filter-tag-icon {
      cursor: pointer;
      margin-left: 7px;
      position: absolute;
      right: 0;
      top: 2px;
      bottom: 0;
      font-weight: 700;
      font-style: normal;
      width: 22px;
      text-align: center;
      line-height: 15px;
      transition: all .2s ease;
      border-radius: 5px
    }
    .custom-filter-tag-icon:after {
      content: "\D7";
      color: #6c7994;
      font-size: 14px
    }
    .custom-filter-tag-icon:focus, .custom-filter-tag-icon:hover {
      background: #e9ebf5
    }
    .custom-filter-tag-icon:focus:after, .custom-filter-tag-icon:hover:after {
      color: #6c7994
    }
    .multiselect__option {
      font-size: 14px;
      display: block;
      padding: 13px;
      min-height: 40px;
      line-height: 16px;
      text-decoration: none;
      text-transform: none;
      vertical-align: middle;
      position: relative;
      cursor: pointer;
      white-space: nowrap;
      width: 96% !important;
    }
    .multiselect__option--highlight {
      background: #f5f6fa;
      outline: none;
      color: #222222
    }
    .multiselect--active {
      .multiselect__select {
        padding: 4px 10px 0 0 !important;
      }
    }
    .multiselect {
      width: 96%;
      margin-left: 22px;
      min-height: 32px !important;
      border-radius: 4px !important;
      .multiselect__select {
        padding: 0 0 4px 10px;
      }
      .multiselect__tags {
        padding: 4px 30px 3px 6px !important;
        min-height: 32px !important;
        overflow: auto !important;
        pointer-events: none;
        .multiselect__tags-wrap {
          pointer-events: auto;
        }
      }
    }
    .multiselect--disabled {
      height: 32px !important;
      border-radius: 4px !important;
      .multiselect__select {
        height: 100%;
        padding-top: 4px !important;
        padding-bottom: 0 !important;
        padding-right: 0;
        background-color: #f2f4f6;
        border-top: 1px solid #e8e8e8;
        border-right: 1px solid #e8e8e8;
        border-bottom: 1px solid #e8e8e8;
      }
      .multiselect__tags {
        background-color: #f2f4f6;
        height: 100% !important;
        min-height: 0 !important;
        padding: 0 0 0 12px;
      }
    }
    .multiselect__input {
      line-height: 1px !important;
    }
    .multiselect__input::placeholder {
      font-size: 14px;
      color: #b0b7bf;
      margin: 0 !important;
    }
    .multiselect__placeholder {
      font-size: 14px;
      padding-left: 5px;
      padding-top: 4px !important;
      font-weight: 100;
      margin-bottom: 0 !important;
    }
  }
</style>
