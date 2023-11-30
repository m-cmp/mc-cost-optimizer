<template>
  <fragment >
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span>{{ $t('dashboard.dashboardCostMonthToDate.title') }}</span>
      </p>
      <p class="-description -color-darkgray-1">{{ $t('dashboard.dashboardCostMonthToDate.desc') }}</p>
    </b-row>
    <b-form
      v-if="show"
      class="edit-cost-month-to-date-form"
      @submit="onSubmit"
      @reset="onReset"
    >

      <b-form-group
        id="cloud-service-group"
        :label="$t('dashboard.dashboardCostMonthToDate.cloudService')"
        label-for="selectVendor">
        <b-form-select
          id="selectVendor"
          v-model="selectedVendorByOption"
          :options="vendorOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          required/>
      </b-form-group>


      <div class="float-right">
        <b-button
          variant="outline-secondary"
          class="cancel-button font-family-notosanscjkkr-medium"
          @click="onClickCancelButton"
        >{{ $t('dashboard.dashboardHeader.cancel') }}</b-button>
        <b-button
          :disabled="vendorOptions.length > 0 ? false : true"
          type="submit"
          variant="primary"
          class="font-family-notosanscjkkr-medium">{{ saveButtonLocalization }}</b-button>
      </div>
    </b-form>
  </fragment>
</template>

<script>
  import {
    VIEW_MODE,
    COST_MONTH_TO_DATE_VIEW_BY_VENDORS
  } from '@/constants/dashboardConstants';
  import _cloneDeep from 'lodash/cloneDeep';
  import _isNil from 'lodash/isNil';
  import _isEqual from 'lodash/isEqual';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import {getSelectedVendorsByWidget,availableVendors} from "../../../../util/dashboardUtils";

  export default {
    name: 'EditCostMonthToDate',
    components: {
      BaseLoadingIndicator,
      BaseNotificationNoData
    },
    props: {
      hideModal: {
        type: Function,
        require: false,
        default: null
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      widgetConfig: {
        type: Object,
        required: true,
        default: null
      },
      dashboardViewMode: {
        type: String,
        required: true,
        default: VIEW_MODE.DEFAULT
      },
    },
    data() {
      return {
        vendorOptions: availableVendors(COST_MONTH_TO_DATE_VIEW_BY_VENDORS, this),
        draftWidgetConfig: {
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.widgetConfig, this, COST_MONTH_TO_DATE_VIEW_BY_VENDORS)]
        },
        show: true,
        isLoading: true,
      };
    },
    computed: {
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.draftWidgetConfig, this, COST_MONTH_TO_DATE_VIEW_BY_VENDORS);
        },
        set(selectedVendorByOption) {
          this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [selectedVendorByOption]);
        }
      }
    },
    watch: {
      widgetConfig: {
        handler: function() {
          this.draftWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: true
      },
      draftWidgetConfig: {
        handler(newVal, oldVal) {
          if (_isEqual(newVal, oldVal)) {
            return;
          }
          if (_isNil(this.draftWidgetConfig)) {
            return;
          }
        },
        immediate: true
      },
    },
    methods: {
      onSubmit(evt) {
        evt.preventDefault();
        let submitConfig = {
          ...this.draftWidgetConfig,
        }
        this.$emit('apply', submitConfig);
      },
      onReset(evt) {
        evt.preventDefault();
      },
      onClickCancelButton() {
        this.$emit('hideModal');
      }
    }
  };
</script>

<style lang="scss">
  .lb-preview {
    font-size: 12px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.33;
    letter-spacing: normal;
    color: #222222;
  }
  .edit-cost-month-to-date-form {
    #view-by-group {
      #view-by-group__BV_label_ {
        font-family: 'NotoSansCJKkr-Medium';
      }
    }
  }
</style>
