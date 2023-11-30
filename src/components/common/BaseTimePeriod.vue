<template>
  <div class="time-period-wrapper">
    <p
      v-if="canDisplayTimePeriod"
      class="time-period-content">{{ formattedTimePeriod.startDate }}~{{ formattedTimePeriod.endDate }}</p>
  </div>
</template>

<script>
  import _isEmpty from 'lodash/isEmpty';
  import dayjs from 'dayjs';
  import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
  export default {
    name: 'BaseTimePeriod',
    props: {
      timePeriod: {
        type: Object,
        required: true
      },
    },
    computed: {
      canDisplayTimePeriod: function () {
        return !_isEmpty(this.timePeriod.startDate) && !_isEmpty(this.timePeriod.endDate);
      },
      formattedTimePeriod: function () {
        return {
          startDate: dayjs(this.timePeriod.startDate).format(getFullDateFormatByLocalization()),
          endDate: dayjs(this.timePeriod.endDate).format(getFullDateFormatByLocalization())
        }
      }
    }
  };
</script>

<style lang="scss">
  .time-period-wrapper {
    .time-period-content {
      font-size: 12px;
      color: #7b8088;
      font-family: "NotoSansCJKkr-Light", "Apple SD Gothic", sans-serif;
      margin-top: 1px;
    }
  }
</style>
