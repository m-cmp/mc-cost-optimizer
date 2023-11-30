<template>
  <div
    v-if="dashboardViewMode === viewMode.DEFAULT"
    class="ui-last-updated"
  >
    <span>{{ $t('dashboard.lastUpdated.lastUpdated') }}:</span>
    <span
      v-for="(item, index) in vendorSecondsAgo"
      :key="index">
      <span>{{ item.vendor }}</span>
      <span>
        {{ getTimeAgoLabel(item.secondsAgo) }}
        <span
          v-if="index !== vendorSecondsAgo.length - 1"
          class="comma-text"
        >
          ,
        </span>
      </span>
    </span>
  </div>
</template>

<script>
  import {VIEW_MODE} from "@/constants/dashboardConstants";
  import {DEFAULT_VENDOR_OPTIONS} from "@/constants/constants"
  import dayjs from 'dayjs';
  import _orderBy from 'lodash/orderBy';

  export default {
    name: 'LastUpdated',
    components: {
    },
    props: {
      batchTime: {
        type: Object,
        require: true,
        default: function() {
          return {};
        }
      },
      dashboardViewMode: {
        type: String,
        require: true,
        default: ''
      },
    },
    data() {
      return {
        viewMode: VIEW_MODE
      }
    },
    computed: {
      vendorSecondsAgo: {
        cache: true,
        get() {
          const batchTime =  Object.keys(this.batchTime).map(key => {
            return {
              vendor: getVendorText(key),
              secondsAgo: getSecondsAgo(this.batchTime[key])
            }
          })
          return _orderBy(batchTime, 'vendor')
        }
      }
    },
    watch: {
    },
    mounted() {
    },
    methods: {
      getTimeAgoLabel(secondsAgo) {
        let minute = secondsAgo / 60
        let hour = minute / 60
        let day = hour / 24
        if (Math.round(hour) < 24) {
          if (Math.round(hour) === 1) {
            return `${Math.round(hour)}${this.$t('dashboard.lastUpdated.hourAgo')}`
          }
          return `${Math.round(hour)}${this.$t('dashboard.lastUpdated.hoursAgo')}`
        }
        if (Math.floor(day) === 1) {
          return `${Math.floor(day)}${this.$t('dashboard.lastUpdated.dayAgo')}`
        }
        return `${Math.floor(day)}${this.$t('dashboard.lastUpdated.daysAgo')}`
      }
    }
  }

  function getVendorText(key) {
    return DEFAULT_VENDOR_OPTIONS.filter(vendor => vendor.value === key).map(vnd => {return vnd.text})[0]
  }

  function getSecondsAgo(batchTime) {
    const currentTimeUnixSeconds = dayjs().unix()
    const batchTimeUnixSeconds = dayjs.utc(batchTime).unix()
    return currentTimeUnixSeconds - batchTimeUnixSeconds
  }
</script>
<style lang="scss">
  .ui-last-updated {
    float: right;
    margin-top: 15px;
    margin-right: 20px;
    color: #898D94;
    .comma-text {
      margin-right: 2px;
      margin-left: -2px;
    }
  }
</style>
