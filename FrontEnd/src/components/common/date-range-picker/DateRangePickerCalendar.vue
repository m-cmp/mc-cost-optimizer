<template>
  <div class="date-range-picker-calendar">
    <div class="d-flex align-items-center">
      <div class="p-1">
        <button
          type="button"
          class="btn btn-sm"
          @mousedown.prevent
          @click="goToPrevMonth">
          <base-material
            name="keyboard_arrow_left"
            color="gray-1"
          />
        </button>
      </div>
      <div
        class="p-1 col text-center color-gray-1 display-month"
      >
        <span class="color-black-1">{{ isEnLocalization ? displayMonth.format('MM') : displayMonth.format('YYYY') }}</span>
        /
        <span>{{ isEnLocalization ? displayMonth.format('YYYY') : displayMonth.format('MM') }}</span>
      </div>
      <div class="p-1">
        <button
          type="button"
          class="btn btn-sm"
          @mousedown.prevent
          @click="goToNextMonth">
          <base-material
            name="keyboard_arrow_right"
            color="gray-1"
          />
        </button>
      </div>
    </div>
    <div class="d-flex justify-content-center text-center date-range-picker-calendar-row">
      <div
        v-for="day in daysOfFirstWeek"
        :key="day.format('D')"
        class="col-day">{{ day.format('dd') }}</div>
    </div>
    <div class="d-flex flex-wrap justify-content-center text-center date-range-picker-calendar-row">
      <div
        v-for="day in days"
        :key="day.format('M-D')"
        :class="dayClass(day)"
        class="col-day"
        @mouseover="dayMouseOver(day)"
        @mousedown.prevent
        @click="dayClick(day)">
        {{ day.format('D') }}
      </div>
    </div>
  </div>
</template>

<script>

import {Trans} from "@/components/common/base-i18n/Translation";
import {SUPPORTED_LANGUAGE} from "@/constants/trans";
export default {
  name: 'DateRangePickerCalendar',
  filters: {},
  props: {
    calendarIndex: {
      type: Number,
      default: null
    },
    calendarCount: {
      type: Number,
      default: null
    },
    month: {
      type: [Object, String],
      default: null
    },
    startDate: {
      type: [Object, String],
      default: null
    },
    endDate: {
      type: [Object, String],
      default: null
    },
    compare: {
      type: Boolean,
      default: false
    },
    startDateCompare: {
      type: [Object, String],
      default: null
    },
    endDateCompare: {
      type: [Object, String],
      default: null
    },
    step: {
      type: String,
      default: null
    },
    previousStep: {
      type: String,
      default: null
    },
    rangeSelect: {
      type: String,
      default: null
    },
    rangeSelectCompare: {
      type: String,
      default: null
    },
    /**
     * 이전 날짜 선택 제약 여부
     */
    isBeforeLimit: {
      type: Boolean,
      default: true
    }
  },
  data: () => {
    return {}
  },
  computed: {
    isEnLocalization: function () {
      return Trans.currentLanguage === SUPPORTED_LANGUAGE.EN
    },
    displayMonth: function() {
      return this.$dayjs.utc(this.month).add(this.calendarIndex - 1, 'month')
    },
    days: function() {
      let startDay = this.$dayjs.utc(this.displayMonth).startOf('week')
      let endDay = this.$dayjs.utc(this.displayMonth).endOf('month').endOf('week').startOf('day').add(1, 'day')
      let nDays = (endDay.diff(startDay, 'days'));

      let days = []
      let day = 0
      while (day < nDays) {
        days.push(this.$dayjs.utc(startDay).add(day, 'day'))
        day++
      }
      return days
    },
    daysOfFirstWeek: function() {
      return this.days.slice(0, 7)
    }
  },
  watch: {},
  mounted() {

  },
  methods: {
    dayClass: function(day) {
      let classes = []
      // Hide days overflowing
      if(!this.isEnable(day)){
        classes.push('invisible')
      }
      // Class for days between startDate & endDate or is startDate (in case of startDate after endDate)
      if (day.isBetween(this.startDate, this.endDate, 'days', '[]') || day.isSame(this.startDate)) {
        classes.push('date-range-picker-range')
      }
      // Class for days between startDateCompare & endDateCompare or is startDateCompare (in case of startDateCompare after endDateCompare)
      if (this.compare && (day.isBetween(this.startDateCompare, this.endDateCompare, 'days', '[]') || day.isSame(this.startDateCompare))) {
        classes.push('date-range-picker-range-compare')
      }
      // Class for cursor if the step is selecting something
      if (this.step != null) {
        classes.push('date-range-picker-cursor-pointer')
      }

      // Class for cursor if the step is selecting something at the next time
      if (this.step === null && this.rangeSelect === 'custom' && this.previousStep === 'selectEndDate') {
        classes.push('date-range-picker-cursor-pointer')
      // Class for cursor if the step is selecting something at the next time (compare)
      } else if (this.step === null && this.rangeSelectCompare === 'custom' && this.previousStep === 'selectEndDateCompare') {
        classes.push('date-range-picker-cursor-pointer')
      }

      if (day.isSame(this.startDate) || day.isSame(this.startDate, 'day')) {
        classes.push('date-range-picker-start-date')
      }
      if (day.isSame(this.endDate) || day.isSame(this.endDate, 'day')) {
        classes.push('date-range-picker-end-date')
      }

      if (day.isSame(this.startDateCompare) || day.isSame(this.startDateCompare, 'day')) {
        if (this.compare) {
          classes.push('date-range-picker-compare-start-date')
        }
      }

      if (day.isSame(this.endDateCompare) || day.isSame(this.endDateCompare, 'day')) {
        if (this.compare) {
          classes.push('date-range-picker-compare-end-date')
        }
      }
      return classes.join(' ')
    },
    isEnable: function(day){
      if (!day.isBetween(this.displayMonth, this.$dayjs.utc(this.displayMonth).endOf('month'), 'days', '[]')) {
        return false;
      }
      //displayMonth 가 왜 한달 더 빠지는지 모르겠음....... 정상 6개월 데이터면 현재 월에서 -5을 해야 하는데 여기는 한달이 빠져서 -6을 함.
      if (this.displayMonth - this.$dayjs().utc().set('month', this.$dayjs().utc().month() - 6)  < 0 && this.isBeforeLimit ) {//TODO (2020.11.06) 릴리즈 이후 다시 복구예정 (날짜제한 제거 필요)
        return false;
      }
      return true;
    },
    dayMouseOver: function(day) {
      if (this.step != null &&this.isEnable(day)) {
        this.selectDate(day)
      }
    },
    dayClick: function(day) {
      if(this.isEnable(day)){
        this.nextStep(day)
      }
    },
    goToPrevMonth: function() {
      this.$emit('goToPrevMonth')
    },
    goToNextMonth: function() {
      this.$emit('goToNextMonth')
    },
    selectDate: function(date) {
      this.$emit('selectDate', date)
    },
    nextStep: function(day) {
      this.$emit('nextStep', day)
    }
  },
}
</script>

<style lang="scss">
  .date-range-picker-calendar {
    font-family: 'Montserrat-Regular';
    .display-month {
      font-size: 14px;
      font-weight: 500;
    }
  }
  .date-range-picker-calendar-row {
    font-size: 12px;
    /*max-width: 230px;*/
    .col-day {
      color: #222222;
      margin-bottom: 2px;
    }
  }

  .col-day {
    width: 14.28%;
    padding: 0.5rem 0;
    white-space: nowrap;
    cursor: default;
  }

  .date-range-picker-range {
    background-color: rgba(30,164,255,0.1) !important;
    color: #222222;
  }

  .date-range-picker-range-compare {
    background-color: rgba(255,124,0,0.1);
    color: #222222;
  }

  .date-range-picker-range {
    &.date-range-picker-range-compare {
     background: linear-gradient(0deg, rgba(30,164,255,0.1) 50%, rgba(255,124,0,0.1) 50%);
    }
  }

  .date-range-picker-cursor-pointer {
    cursor: pointer;
  }

  .date-range-picker-start-date {
    background-color: #1ea4ff !important;
    color: #ffffff !important;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
  }

  .date-range-picker-end-date {
    background-color: #1ea4ff !important;
    color: #ffffff !important;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
  }

  .date-range-picker-compare-start-date {
    background-color: #ff8660 !important;
    color: #ffffff;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
  }

  .date-range-picker-compare-end-date {
    background-color: #ff8660 !important;
    color: #ffffff;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
  }

  .invisible {
    visibility: inherit !important;
    background: #ffffff !important;
    color: #b0b7bf !important;
  }
</style>
