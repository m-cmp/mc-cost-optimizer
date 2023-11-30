<template>
  <div class="d-flex date-range-picker-row">
    <!-- Calendars -->
    <div
      v-for="calendarIndex in calendarCount"
      :key="calendarIndex"
      class="date-range-picker-calendar-col">
      <date-range-picker-calendar
        :calendar-index="calendarIndex"
        :calendar-count="calendarCount"
        :month="month"
        :start-date="startDate"
        :end-date="endDate"
        :compare="compare"
        :start-date-compare="startDateCompare"
        :end-date-compare="endDateCompare"
        :step="step"
        :previous-step="previousStep"
        @goToPrevMonth="goToPrevMonth"
        @goToNextMonth="goToNextMonth"
        @selectDate="selectDate"
        @nextStep="nextStep"
      />
    </div>
    <div class="form-group form-inline justify-content-end mb-0 custom-btn-group">
      <!-- // Hidden Items -->
      <input
        ref="startDate"
        :value="dateFormat(startDate,'startDate')"
        type="text"
        class="transparent-field"
        @focus="step = 'selectStartDate'"
        @blur="inputDate"
        @change="onChangeDate"
      >
      <input
        ref="endDate"
        :value="dateFormat(endDate, 'endDate')"
        type="text"
        class="transparent-field"
        @focus="step = 'selectEndDate'"
        @blur="inputDate"
        @change="onChangeDate"
      >
      <!-- // Hidden Items -->
      <button
        type="button"
        class="btn btn-light font-family-notosanscjkkr-medium"
        @click="cancel">{{ cancelLabel }}</button>
      <button
        type="button"
        class="btn btn-primary ml-2 font-family-notosanscjkkr-medium"
        @click="submit">{{ submitLabel }}</button>
    </div>
  </div>
</template>

<script>
import DateRangePickerCalendar from '../date-range-picker/DateRangePickerCalendar'
import { getFullDateFormatByLocalization } from "@/util/dateTimeUtils";
const STEP = {
  SELECT_START_DATE: 'selectStartDate',
  SELECT_END_DATE: 'selectEndDate'
}
const RANGE_SELECT = {
  CUSTOM: 'custom'
}
const DATE_FORMAT = {
  YYYYMMDD: 'YYYY/MM/DD',
  MMDDYYYY: 'MM/DD/YYYY',
}
export default {
  name: "DatePicker",
  components: { DateRangePickerCalendar },
  props: {
    filterSettings: {
      type: Object,
      default: null
    },
    calendarCount: {
      type: Number,
      default: 2
    },
    cancelLabel: {
      type: String,
      default: 'Cancel'
    },
    submitLabel: {
      type: String,
      default: 'Cancel'
    },
    defaultRangeSelect: {
      type: String,
      default: 'custom'
    },
    ranges: {
      type: Object,
      default: function() {
        return {
          currentMonth: {
            label: 'Current month',
            startDate: this.$dayjs.utc().startOf('month'),
            endDate: this.$dayjs.utc().endOf('month').startOf('day')
          },
          lastMonth: {
            label: 'Last month',
            startDate: this.$dayjs.utc().subtract(1, 'month').startOf('month'),
            endDate: this.$dayjs.utc().subtract(1, 'month').endOf('month').startOf('day')
          }
        }
      }
    },
  },
  data() {
    return {
      startDate: null,
      endDate: null,
      startDateCompare: '',
      endDateCompare: '',
      compareCostType: null,
      compare: false,
      month: this.$dayjs.utc().startOf('month'),
      step: null,
      previousStep: null,
      rangeSelect: null
    }
  },
  computed: {
    // isSubmitButtonEnabled() {
    //   return this.isStartDateLessThanOrEqualToEndDate
    //     && this.isValidDuration
    //     && this.isCompareStartDateLessThanOrEqualToCompareEndDate
    //     && this.isCompareDurationValid
    //     && this.isSingleDurationValid
    // }
  },
  watch: {
    rangeSelect: function() {
      this.selectRange(this.rangeSelect)
    },
    endDate: {
      handler() {
        if (this.step === STEP.SELECT_END_DATE) {
          this.validateSelectCalendar();
        }
      },
      immediate: true
    },
  },
  created: function() {
    // Initialize ranges
    this.rangeSelect = this.defaultRangeSelect;
    if (this.rangeSelect === RANGE_SELECT.CUSTOM) {
      // this.startDate = this.$dayjs.utc().startOf('month');
      this.startDate = this.filterSettings.startDate;
      if (this.defaultStartDate) {
        this.startDate = this.$dayjs.utc(this.defaultStartDate, DATE_FORMAT.YYYYMMDD);
      }
      // this.endDate = this.$dayjs.utc().endOf('month').startOf('day');
      this.endDate = this.filterSettings.endDate;
      if (this.defaultEndDate) {
        this.endDate = this.$dayjs.utc(this.defaultEndDate, DATE_FORMAT.YYYYMMDD);
      }
    }
  },
  mounted() {
    this.refreshViewpointMonth();

    if (this.step === null && this.rangeSelect === RANGE_SELECT.CUSTOM && this.previousStep === STEP.SELECT_END_DATE) {
      this.step = STEP.SELECT_END_DATE;
    }
  },
  methods: {
    dateFormat: function(value, type) {
      return value ? this.$dayjs.utc(value).format(getFullDateFormatByLocalization()) : ''
    },
    refreshViewpointMonth() {
      const durationMonth = this.$dayjs.utc().diff(this.startDate, 'month')
      this.month = this.$dayjs.utc().subtract(durationMonth + 1, 'month').startOf('month')
    },
    goToPrevMonth() {
      this.month = this.$dayjs.utc(this.month).subtract(1, 'month');
    },
    goToNextMonth() {
      this.month = this.$dayjs.utc(this.month).add(1, 'month');
    },
    selectRange: function(rangeKey) {
      let predefinedRange = false;

      // Custom range
      if (!predefinedRange && this.step == null) {
        this.step = STEP.SELECT_START_DATE;
        this.previousStep = STEP.SELECT_START_DATE;
        const $vm = this;
        setTimeout(function() {
          $vm.$refs.startDate && $vm.$refs.startDate.focus();
        }, 0)
      } else if (this.step !== null) {
        //Fix bug change range select after save custom range (focus input date)
        this.step = null;
      }
      this.refreshViewpointMonth();
    },
    selectDate(date) {
      if (this.step === STEP.SELECT_START_DATE) {
        this.startDate = date;
      } else if (this.step === STEP.SELECT_END_DATE) {
        this.endDate = date;
      }
    },
    nextStep(day) {
      //In case when user reselect start date for range select
      if (day && this.step === null && this.rangeSelect === RANGE_SELECT.CUSTOM && this.previousStep === STEP.SELECT_END_DATE) {
        this.startDate = day;
        this.step = STEP.SELECT_END_DATE;
        this.$refs.endDate.focus();
        //In case when user reselect start date for range select compare
      } else if (day && this.step === null) {
        return;
      } else if (this.step === STEP.SELECT_START_DATE) {
        this.step = STEP.SELECT_END_DATE;
        this.$refs.endDate.focus();
      } else if (this.step === STEP.SELECT_END_DATE) {
        this.step = null;
        this.$refs.endDate.blur();
      }
      if (this.step !== null) {
        this.$set(this, 'previousStep', this.step);
      }
    },
    inputDate(input) {
      //This is stricky here, convert utc with hour is 00:00:00 is wrong, it always return previous day
      let date = this.$dayjs.utc(input.target.value, getFullDateFormatByLocalization()).add(-new Date().getTimezoneOffset(), 'minutes'); // add 8 로 하니까 하루씩 밀림
      // this.message.single.endDateIsLessThanStartDate = '';
      // this.message.compare.endDateIsLessThanStartDate = '';
      if (date.isValid()) {
        this.selectDate(date)
        // this.validateCustomTimeFrame()
        this.validateEndDateThanStartDate();
      }
      this.nextStep()
    },
    onChangeDate(input) {
      let date = this.$dayjs.utc(input.target.value, getFullDateFormatByLocalization()).add(-new Date().getTimezoneOffset(), 'minutes');
      //TODO (2020.11.06) 릴리즈 이후 다시 복구예정 (날짜제한 제거 필요)
      if(date - this.$dayjs().utc().set('month', this.$dayjs().utc().month() - 6)  < 0){
        date = this.$dayjs().utc().set('month', this.$dayjs().utc().month() - 6).startOf('month');
      }
      this.selectDate(date);
      this.refreshViewpointMonth();
    },
    validateEndDateThanStartDate() {
      const newStartDate = new Date(this.startDate.year(), this.startDate.month(), this.startDate.date());
      const newEndDate = new Date(this.endDate.year(), this.endDate.month(), this.endDate.date());

      if (newEndDate < newStartDate) {
        setTimeout(() => {
          this.startDate = this.endDate
          this.step = STEP.SELECT_END_DATE
          this.$refs.endDate.focus()
        }, 2)
        if (this.step === STEP.SELECT_START_DATE || this.step === STEP.SELECT_END_DATE) {
          setTimeout(() => {
            // this.message.single.endDateIsLessThanStartDate = this.$t('costAnalytics.header.endDateIsLessThanStartDateWarningMessage');
            this.endDate = this.startDate;
            this.step = STEP.SELECT_END_DATE;
            this.$refs.endDate.focus();
          }, 1)
        }
      }
    },
    validateSelectCalendar() {
      if (this.checkDatePeriodGreaterThanOneYear(this.startDate, this.endDate) && this.step === STEP.SELECT_END_DATE) {
        this.$refs.endDate.blur();
        this.step = STEP.SELECT_END_DATE;
        this.$refs.endDate.focus();
      }
    },
    checkDatePeriodGreaterThanOneYear(startDate, endDate) {
      const newStartDate = new Date(startDate.year(), startDate.month(), startDate.date());
      const previousEndDate = new Date(endDate.year() - 1, endDate.month(), endDate.date());
      return newStartDate < previousEndDate;
    },
    submit() {
      // if (!this.isSubmitButtonEnabled) {
      //   return;
      // }
      this.$emit('submit', {
        startDate: this.startDate,
        endDate: this.endDate
        // compare: this.compare,
        // rangeSelect: this.rangeSelect,
        // rangeSelectCompare: this.rangeSelectCompare,
        // startDateCompare: this.$dayjs.utc(this.startDateCompare).format('YYYY-MM-DD'),
        // endDateCompare: this.$dayjs.utc(this.endDateCompare).format('YYYY-MM-DD'),
        // compareCostType: this.compareCostType
      })
    },
    cancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style lang="scss">
  /* Custom row */
  .date-range-picker-row {
    border-bottom: 1px solid #D5DAE0;
  }
  .date-range-picker-calendar-col {
    width: 220px;
    padding: 0 10px 8px 10px;
    flex-basis: 100%;
  }
  .set-range-select-title {
    margin-bottom: 8px;
    margin-top: 11px;
  }

  /* Select menus border */
  .date-range-picker-range-border {
    &:focus {
      box-shadow: none !important;
      border-color: #D5DAE0 !important;
    }
    &.compare {
      &:focus {
        box-shadow: none !important;
        border-color: #D5DAE0 !important;
      }
    }
  }

  .custom-btn-group {
    position: absolute;
    right: 0;
    bottom: 0;
  }

  .warning-modal {
    .modal-dialog {
      width: 400px;
      .modal-header {
        text-align: center;
        .modal-title {
          position: absolute;
          left: 42%;
          font-size: 16px;
        }
      }
      .modal-body {
        padding: 0 !important;
        .warning-popup-header {
          font-weight: bold;
          font-size: 14px;
          text-align: center;
          border-bottom: 0.5px solid #D5DAE0;
          padding: 10px 0;
        }
        .warning-popup-content {
          padding: 20px;
          border-bottom: 0.5px solid #D5DAE0;
        }
        .warning-popup-footer {
          .btn-action {
            float: right;
            margin: 20px;
            background-color: #0672FF;
            border-radius: 4px;
            border-color: #0672FF !important;
            &:focus {
              box-shadow: none !important;
              background-color: #0672FF !important;
            }
          }
        }
      }
    }
  }
  .transparent-field {
    color: transparent;
  }
</style>
