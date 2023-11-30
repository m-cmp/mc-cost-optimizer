<template>
  <fragment>
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span>{{ $t('dashboard.dashboardEstimatedCost.title') }}</span>
      </p>
      <p class="-description -color-darkgray-1">{{ $t('dashboard.dashboardEstimatedCost.desc') }}</p>
    </b-row>
    <b-form
      v-if="show"
      class="edit-estimated-cost"
      @submit="onSubmit"
      @reset="onReset"
    >
      <b-form-group
        id="view-by-group"
        :label="$t('dashboard.dashboardEstimatedCost.cloudService')"
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
  import {VIEW_MODE, ESTIMATED_COST_VIEW_BY_VENDORS, DEFAULT_ESTIMATED_COST_WIDGET_CONFIG} from '@/constants/dashboardConstants';
  import { getSelectedVendorsByWidget, availableVendors } from "@/util/dashboardUtils";
  import _cloneDeep from "lodash/cloneDeep";
  import _isEqual from "lodash/isEqual";
  import _isNil from "lodash/isNil";
  import _isEmpty from "lodash/isEmpty";

  export default {
    name: "EditEstimatedCost",
    components: {
    },
    props: {
      commonUserInfo: {
        type: Object,
        required: true
      },
      widgetConfig: {
        type: Object,
        required: true,
        default() {
          return DEFAULT_ESTIMATED_COST_WIDGET_CONFIG
        }
      }
    },
    data() {
      return {
        draftWidgetConfig: {
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.widgetConfig, this, ESTIMATED_COST_VIEW_BY_VENDORS)]
        },
        vendorOptions: availableVendors(ESTIMATED_COST_VIEW_BY_VENDORS, this),
        show: true
      }
    },
    computed: {
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.draftWidgetConfig, this, ESTIMATED_COST_VIEW_BY_VENDORS);
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
        // this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [this.selectedVendor]);
        this.$emit('apply', this.draftWidgetConfig);
      },
      onReset(evt) {
        evt.preventDefault();
      },
      onClickCancelButton() {
        this.$emit('hideModal');
      },
      // availableVendors() {
      //   return ESTIMATED_COST_VIEW_BY_VENDORS.filter(option =>this.allVendors().includes(option.value)).map(vendor => {
      //     return {
      //       ...vendor,
      //       text: this.$t(vendor.text)
      //     };
      //   });
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
      // isSelectedVendorsNotEmpty($vm) {
      //   return !_isEmpty($vm.selectedVendorsByWidget) && !_isEmpty($vm.selectedVendorsByWidget[0]);
      // },
      // setSelectedVendors() {
      //   if (this.isSelectedVendorsNotEmpty(this.draftWidgetConfig)) {
      //     this.selectedVendor = this.draftWidgetConfig.selectedVendorsByWidget[0];
      //     return this.draftWidgetConfig.selectedVendorsByWidget[0];
      //   } else {
      //     this.selectedVendor = this.commonUserInfo.selectedVendors[0];
      //     return this.commonUserInfo.selectedVendors[0];
      //   }
      // }
    }
  }
</script>

<style lang="scss">
  .edit-estimated-cost {
    #view-by-group {
      #view-by-group__BV_label_ {
        font-family: 'NotoSansCJKkr-Medium';
      }
    }
  }
</style>
