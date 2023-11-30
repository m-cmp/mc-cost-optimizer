<template>
  <div class="custom-no-rows-overlay">
    <b-row class="no-rows-overlay-title">
      <base-material
        :size="16"
        color="yellow-1"
        name="error"
        class="error-icon"
      />
      <p>
        {{ enabledLocalization ? $t(title) : title }}
      </p>
    </b-row>
    <p
      v-show="contentDisplayed"
      class="no-rows-overlay-content"
    >
      {{ enabledLocalization ? $t(content) : content }}
    </p>
  </div>
</template>

<script>
  import Vue from 'vue';

  export default Vue.extend({
    name: 'CustomNoRowsOverlay',
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
  });
</script>

<style lang="scss">
  .custom-no-rows-overlay {
    font-size: 14px;
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    text-align: center;
    -ms-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    .no-rows-overlay-title {
      justify-content: center;
      .error-icon {
        padding-right: 5px;
      }
    }
    .no-rows-overlay-content {
      color: #7b8088;
      margin-top: 8px;
      font-size: 12px;
    }
  }
</style>
