<template>
  <div id="mlAbnormalDatePicker">
    <DatePicker
      :set-range-select-label="this.$t('costAnalytics.header.dateRange')"
      :compare-label="this.$t('costAnalytics.header.compare')"
      :submit-label="this.$t('costAnalytics.header.ok')"
      :cancel-label="this.$t('costAnalytics.header.cancel')"
      :date-format="DATE_FORMAT"
      :filter-settings="filterSettings"
      :allow-compare="true"
      @submit="onSaveDateRangePicker"
      @cancel="onCancelDateRangePicker"
    />
  </div>
</template>

<script>
  import DatePicker from '@/components/common/date-picker/DatePicker'
  import {
    DATE_FORMAT
  } from '@/constants/costAnalyticsConstants'
  export default {
    name: "MLAbnormalDatePicker",
    components: {
      DatePicker,
    },
    props: {
      filterSettings: {
        type: Object,
        default: null
      }
    },
    data() {
      return {
        DATE_FORMAT: DATE_FORMAT
      }
    },
    methods: {
      onCancelDateRangePicker() {
        this.$emit('closeDatePicker');
      },
      onSaveDateRangePicker(timeFrame) {
        let payload = Object.assign({}, timeFrame, {
          startDate: timeFrame && timeFrame.startDate ? timeFrame.startDate : '',
          endDate: timeFrame && timeFrame.endDate ? timeFrame.endDate : ''
        });
        this.$emit('submit', payload);
      },
    }
  }
</script>

<style lang="scss">
  #mlAbnormalDatePicker {
    -webkit-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    -moz-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    background-color: #ffffff;
    position: absolute;
    z-index: 9999;
    padding: 15px 10px 55px 10px;
    width: 570px;
    border-radius: 4px;
    &.sidebar-active {
      left: 360px !important;
    }
    .custom-checkbox {
      height: 19px;
      margin-top: 8px;
      padding-left: 16px;
      label {
        cursor: pointer;
      }
      [type="checkbox"]:checked + label:before,
      [type="checkbox"]:not(:checked) + label:before {
        top: 3px;
        width: 12px;
        height: 12px;
        margin-left: 8px;
        box-shadow: none;
      }
      [type="checkbox"]:not(:checked) + label:before {
        border-color: #D5DAE0 !important;
      }
      [type="checkbox"]:checked + label:after,
      [type="checkbox"]:not(:checked) + label:after {
        top: 1px;
        width: 13px;
        margin-left: 8px;
        box-shadow: none;
      }
    }
    .form-group {
      margin-bottom: 8px !important;
    }
    .custom-btn-group {
      bottom: 5px !important;
      right: 20px !important;
    }
    .date-range-picker-date-input {
      border: 1px solid #D5DAE0;
      font-size: 14px;
      height: 32px;
      margin-bottom: 0 !important;
      cursor: text;
      &:disabled {
        cursor: default;
      }
    }
    .btn-light {
      border: 1px solid #B8BFCA !important;
      color: #898D94 !important;
      background-color: transparent;
      &:focus {
        box-shadow: none !important;
      }
    }
    .btn-primary {
      &:hover {
        background-color: #007bff !important;
        border-color: #007bff !important;
      }
      &:focus {
        box-shadow: none !important;
      }
    }
    .date-range-picker-date-input {
      margin-bottom: 0 !important;
    }
    .custom-radio {
      padding-left: 16px;
      label {
        cursor: pointer;
        margin-left: 6px !important;
        &:before {
          width: 12px !important;
          height: 12px !important;
          margin-top: 2px !important;
          margin-left: 2px !important;
          box-shadow: none !important;
        }
        &:after {
          width: 12px !important;
          height: 12px !important;
          margin-top: 2px !important;
          margin-left: 2px !important;
          box-shadow: none !important;
        }
      }
      .custom-control-input:disabled ~ .custom-control-label {
        color: #7b8088 !important;
        &:before {
          background-color: #e8ebef !important;
          border-color: #d5dae0 !important;
          border-radius: 100% !important;
        }
        &:after {
          border-radius: 100% !important;
        }
      }
    }
    .date-range-picker-calendar-row {
      color: #6c7994 !important;
      margin-top: 8px !important;
    }
    .col-day {
      width: 32px !important;
      height: 32px !important;
      line-height: 15px !important;
    }
    .align-items-center {
      border-bottom: 1px solid #D5DAE0 ;
      padding-bottom: 8px !important;
    }
    .flex-wrap {
      margin-top: 0 !important;
    }
    .date-range-picker-col {
      button {
        border: none !important;
      }
    }

    .custom-select-time-frame {
      .custom-select {
        font-size: 14px;
        height: 32px;
        &:disabled {
          background-color: #f2f4f6;
          color: #b0b7bf;
        }
        &:focus {
          box-shadow: none !important;
        }
      }
      select {
        border-radius: 4px;
        &:hover {
          cursor: pointer !important;
        }
        background-image: url("data:image/svg+xml,%3Csvg%20xmlns%3D%27http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%27%20width%3D%27100%27%20height%3D%27100%27%20fill%3D%27gray%27%3E%3Cpolygon%20points%3D%270%2C0%20100%2C0%2050%2C50%27%2F%3E%3C%2Fsvg%3E");
        background-position: right 10px top 12px;
        background-repeat: no-repeat;
        background-size: 10px;
      }
    }
  }
</style>
