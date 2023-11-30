<template>
  <div class="base-time-frame">
    <div class="float-left custom-btn-base-time-frame">
      <BaseDropdown
        ref="dateTypeDropdown"
        :options="dateTypeOptions"
        :enabled-localization="true"
        :disabled="disabled"
        variant="default"
        @selectOption="onChangeDateType"/>
    </div>
    <div class="float-left custom-btn-base-time-frame">
      <BaseDropdown
        ref="timeFrameDropdown"
        :options="timeFrameOptions"
        :enabled-localization="true"
        :disabled="disabled"
        variant="default"
        @selectOption="onChangeTimeFrame"/>
    </div>
  </div>
</template>

<script>
  import BaseDropdown from "@/components/common/BaseDropdown";
  import {
    DASHBOARD_DATE_TYPE,
    DASHBOARD_DATE_TYPE_OPTIONS,
    MONTHLY_COST_TIME_FRAME,
    MONTHLY_COST_TIME_FRAME_OPTIONS,
    WEEKLY_COST_TIME_FRAME,
    WEEKLY_COST_TIME_FRAME_OPTIONS,
    SELECTED_VENDOR
  } from "@/constants/dashboardConstants";

  export default {
    name: 'BaseTimeFrame',
    components: {
      BaseDropdown
    },
    props: {
      disabled: {
        type: Boolean,
        default: false,
      },
      selectedVendorsByWidget:{
        type: Array,
        default() {
          return []
        }
      },
      // dateTypeOptions: {
      //   type: Array,
      //   default() {
      //     return DASHBOARD_DATE_TYPE_OPTIONS
      //   }
      // }
    },
    data() {
      return {
        selectedDateType: DASHBOARD_DATE_TYPE.MONTHLY,
        selectedTimeFrame: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
        dateTypeOptions: DASHBOARD_DATE_TYPE_OPTIONS,
        timeFrameOptions: MONTHLY_COST_TIME_FRAME_OPTIONS,
        DEFAULT_MONTHLY_TIME_FRAME: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
        DEFAULT_WEEKLY_TIME_FRAME: WEEKLY_COST_TIME_FRAME.LAST_8_WEEKS,
      }
    },
    mounted() {
      this.onReloadDateType(this.selectedVendorsByWidget)
      this.$emit('mounted');
    },
    methods: {
      onChangeDateType(selectedDateType) {
        if (this.selectedDateType === selectedDateType) {
          return;
        }
        this.selectedDateType = selectedDateType;
        switch (selectedDateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY: {
            this.timeFrameOptions = MONTHLY_COST_TIME_FRAME_OPTIONS.map(opt => {
              return {
                ...opt,
                isDefault: opt.value === this.DEFAULT_MONTHLY_TIME_FRAME
              }
            });
            this.selectedTimeFrame = this.DEFAULT_MONTHLY_TIME_FRAME;
            break;
          }
          case DASHBOARD_DATE_TYPE.WEEKLY: {
            this.timeFrameOptions = WEEKLY_COST_TIME_FRAME_OPTIONS.map(opt => {
              return {
                ...opt,
                isDefault: opt.value === this.DEFAULT_WEEKLY_TIME_FRAME
              }
            });
            this.selectedTimeFrame = this.DEFAULT_WEEKLY_TIME_FRAME;
            break;
          }
        }
        this.$emit('changeDateTypeAndTimeFrame', {dateType: this.selectedDateType, timeFrame: this.selectedTimeFrame});
      },
      onChangeTimeFrame(selectedTimeFrame) {
        if (this.selectedTimeFrame === selectedTimeFrame) {
          return;
        }
        this.selectedTimeFrame = selectedTimeFrame;
        // to remember default timeFrame idx when switching dateType around
        switch (this.selectedDateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY: {
            this.DEFAULT_MONTHLY_TIME_FRAME = selectedTimeFrame;
            break;
          }
          case DASHBOARD_DATE_TYPE.WEEKLY: {
            this.DEFAULT_WEEKLY_TIME_FRAME = selectedTimeFrame;
            break;
          }
        }
        this.$emit('changeDateTypeAndTimeFrame', {dateType: this.selectedDateType, timeFrame: this.selectedTimeFrame});
      },
      selectDateTypeAndTimeFrameExternally(selectedDateTypeAndTimeFrame) {
        // bind selected
        this.selectedDateType = selectedDateTypeAndTimeFrame.dateType;
        this.selectedTimeFrame = selectedDateTypeAndTimeFrame.timeFrame;

        // set date type text
        let selectedDateTypeOption = this.dateTypeOptions.find(opt => opt.value === selectedDateTypeAndTimeFrame.dateType);
        this.$refs.dateTypeDropdown.changeSelectedOptionText(selectedDateTypeOption.text);

        // set time frame text
        switch (selectedDateTypeAndTimeFrame.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY: {
            this.timeFrameOptions = MONTHLY_COST_TIME_FRAME_OPTIONS.map(opt => {
              return {
                ...opt,
                isDefault: opt.value === selectedDateTypeAndTimeFrame.timeFrame
              }
            });
            break;
          }
          case DASHBOARD_DATE_TYPE.WEEKLY: {
            this.timeFrameOptions = WEEKLY_COST_TIME_FRAME_OPTIONS.map(opt => {
              return {
                ...opt,
                isDefault: opt.value === selectedDateTypeAndTimeFrame.timeFrame
              }
            });
            break;
          }
        }
      },
      onReloadDateType(selectedVendorsByWidget){
        if(selectedVendorsByWidget.includes(SELECTED_VENDOR.NCP)){
          this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS.filter(option => option.value != DASHBOARD_DATE_TYPE.WEEKLY).map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
        }else{
          this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS
        }
      }
    }
  };
</script>
<style lang="scss">
  .custom-btn-base-time-frame {
    .base-dropdown {
      button {
        padding-top: 2px !important;
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
</style>
