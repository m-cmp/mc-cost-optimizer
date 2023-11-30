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
      {{ enabledLocalization ? $t(content) : content }}
    </p>
  </div>
</template>

<script>
  export default {
    name: 'BaseNotificationNoData',
    props: {
      title: {
        type: String,
        default: 'noData.title'
      },
      content: {
        type: String,
        default: 'noData.content'
      },
      contentDisplayed: {
        type: Boolean,
        default: true
      },
      enabledLocalization: {
        type: Boolean,
        default: true
      }
    },
    data() {
      return {}
    },
    methods: {
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
