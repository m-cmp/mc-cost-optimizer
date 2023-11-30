<template>
  <fragment>
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span>{{ $t('dashboard.topCost.title') }}</span>
      </p>
      <div class="-pb-1">
        <p class="-description -color-darkgray-1">{{ $t('dashboard.topCost.desc1') }}</p>
        <p class="-description -color-darkgray-1">{{ $t('dashboard.topCost.desc2') }}</p>
      </div>
      <div>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.topCost.descAWS') }}</p>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.topCost.descGCP') }}</p>
        <p class="-description -color-darkgray-1 -pb-1">{{ $t('dashboard.dashboardCost.descAzure') }}</p>
        <p class="-description -color-darkgray-1">{{ $t('dashboard.dashboardCost.descTencent') }}</p>
      </div>
    </b-row>
    <b-form
      v-if="show"
      @submit="onSubmit"
      @reset="onReset"
    >
      <!--      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.topCost.previews') }}</div>-->
      <!--      <b-form-group-->
      <!--        id="preview-group"-->
      <!--        label-for="preview">-->
      <!--        <PreviewTopCost-->
      <!--          :view-by="draftWidgetConfig.viewBy"-->
      <!--          :time-frame="draftWidgetConfig.timeFrame"-->
      <!--          :common-user-info="commonUserInfo"-->
      <!--          :exchange-rate="exchangeRate"-->
      <!--        />-->
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
          @change="onSelectedVendors"
        />
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.topCost.viewBy') }}</div>
      <b-form-group
        id="view-by-group"
        label-for="view-by">
        <b-form-select
          id="view-by"
          v-model="internalViewBy"
          :options="viewByOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          required
        />
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.topCost.dateRange') }}</div>
      <b-form-group
        id="time-frame-group"
        label-for="time-frame">
        <b-form-select
          id="set-time-frame"
          v-model="draftWidgetConfig.timeFrame"
          :options="timeFrameOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          required
        />
      </b-form-group>

      <div class="float-right">
        <b-button
          variant="outline-secondary"
          class="cancel-button font-family-notosanscjkkr-medium"
          @click="onClickCancelButton"
        >{{ $t('dashboard.topCost.cancel') }}</b-button>
        <b-button
          :disabled="vendorOptions.length > 0 ? false : true"
          type="submit"
          variant="primary"
          class="font-family-notosanscjkkr-medium"
        >{{ saveButtonLocalization }}</b-button>
      </div>
    </b-form>
  </fragment>
</template>

<script>
  import {
    DASHBOARD_VIEW_BY_OPTIONS,
    DEFAULT_TOP_COST_WIDGET_CONFIG,
    TOP_5_TIME_FRAME_OPTIONS,
    VIEW_MODE,
    TOP_5_VIEW_BY_VENDORS,
    DASHBOARD_VIEW_BY
  } from '@/constants/dashboardConstants';
  import PreviewTopCost from './PreviewTopCost';
  import {DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import _cloneDeep from 'lodash/cloneDeep';
  import _isEqual from "lodash/isEqual";
  import _isEmpty from "lodash/isEmpty"
  import {availableVendors, getSelectedVendorsByWidget} from "@/util/dashboardUtils";
  import {DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR, SELECTED_VENDOR} from "../../../../constants/dashboardConstants";

  export default {
    name: 'EditTopCostForm',
    components: {
      PreviewTopCost
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
          return DEFAULT_TOP_COST_WIDGET_CONFIG
        }
      },
      // allVendors: {
      //   type: Array,
      //   required: true,
      //   default() {
      //     return []
      //   }
      // },
      dashboardViewMode: {
        type: String,
        required: true,
        default: VIEW_MODE.DEFAULT
      }
    },
    data() {
      return {
        vendorOptions: availableVendors(TOP_5_VIEW_BY_VENDORS, this),
        draftWidgetConfig: DEFAULT_TOP_COST_WIDGET_CONFIG,
        viewByOptions: DASHBOARD_VIEW_BY_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        timeFrameOptions: TOP_5_TIME_FRAME_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        show: true
      }
    },
    computed: {
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.draftWidgetConfig, this, TOP_5_VIEW_BY_VENDORS);
        },
        set(selectedVendorByOption) {
          this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [selectedVendorByOption]);
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
    },
    watch: {
      widgetConfig: {
        handler: function() {
          this.draftWidgetConfig = _cloneDeep(this.widgetConfig);
          this.draftWidgetConfig.selectedVendorsByWidget = [getSelectedVendorsByWidget(this.draftWidgetConfig, this, TOP_5_VIEW_BY_VENDORS)];
          this.onSelectedVendors();
        },
        immediate: true
      }
    },
    // created() {
    //   this.viewByOptions = this.funcPortionByWidgetViewByOptions(this.internalVendor)
    //   console.log(this.viewByOptions)
    // },
    mounted() {
    },
    methods: {
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
      onSelectedVendors(){
        this.viewByOptions = this.funcPortionByWidgetViewByOptions(this.draftWidgetConfig.selectedVendorsByWidget[0])
        if(_isEqual(this.draftWidgetConfig.selectedVendorsByWidget[0], SELECTED_VENDOR.GCP) && (this.draftWidgetConfig.viewBy == DASHBOARD_VIEW_BY.REGION
          || this.draftWidgetConfig.viewBy == DASHBOARD_VIEW_BY.ACCOUNT)){
          this.draftWidgetConfig.viewBy = DASHBOARD_VIEW_BY.PROJECT;
        }
      },
      funcPortionByWidgetViewByOptions(selectedVendorsByWidget) {
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
</style>
