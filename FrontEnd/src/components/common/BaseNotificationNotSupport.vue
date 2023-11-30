<template>
  <div class="screen-notification-no-data">
    <b-row class="no-data-content-wrapper">
      <base-material
        :size="16"
        class="no-data-content"
        color="yellow-1"
        name="error"
      />
      <p class="title">
        {{ enabledLocalization ? $t(title) : title }}
      </p>
    </b-row>
    <p
      v-show="contentDisplayed"
      class="content"
    >
      {{ enabledLocalization ? $t(content1) : content1 }}<br>
      {{ enabledLocalization ? $t(content2) : content2 }}
    </p>
  </div>
</template>

<script>
  import {getSupportVendorsString} from "@/util/Common";

  export default {
    name: 'BaseNotificationNotSupport',
    props: {
      contentDisplayed: {
        type: Boolean,
        default: true
      },
      enabledLocalization: {
        type: Boolean,
        default: true
      },
      supportVendors: {
        type: Array,
        required: true
      },
    },
    data() {
      return {
        title: '',
        content1: '',
        content2: ''
      }
    },
    watch: {
      '$i18n.locale': {
        handler() {
          this.getText();
        }
      }
    },
    mounted() {
      this.getText();
    },
    methods: {
      getText() {
        const supportVendors = getSupportVendorsString(this.supportVendors.map(v => v.value), this.$i18n.locale);
        this.title = this.$t('dashboard.abnormalChange.dontSupportVendorTitle');
        this.content1 = this.$t('dashboard.abnormalChange.dontSupportVendorContent1', {'supportVendors': supportVendors});
        this.content2 = this.$t('dashboard.abnormalChange.dontSupportVendorContent2');
      }
    }
  };
</script>

<style lang="scss">
  .screen-notification-no-data {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    text-align: center;
    -ms-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    .no-data-content-wrapper {
      justify-content: center;
      .no-data-content {
        padding-right: 5px;
      }
    }
    .title {
      font-size: 14px;
    }
    .content {
      color: #7b8088; margin-top: 8px; font-size: 12px;
    }
    .material-icons {
      &.color-yellow-1 {
        display: flex;
        align-items: center;
      }
    }
  }
</style>
