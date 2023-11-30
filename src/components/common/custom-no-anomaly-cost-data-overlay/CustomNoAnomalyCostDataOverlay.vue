<template>
  <div class="custom-no-anomaly-cost-data-overlay">
    <!--    <b-row class="no-rows-overlay-title">-->
    <!--      <base-material-->
    <!--        :size="16"-->
    <!--        color="yellow-1"-->
    <!--        name="error"-->
    <!--        class="error-icon"-->
    <!--      />-->
    <!--      <p>-->
    <!--        {{ enabledLocalization ? $t(title) : title }}-->
    <!--      </p>-->
    <!--    </b-row>-->
    <p
      v-show="contentDisplayed"
      class="no-rows-overlay-content"
    >
      <span class="material-icons">done</span>
      {{ enabledLocalization ? $t(content) : content }}
    </p>
  </div>
</template>

<script>
import Vue from 'vue';

export default Vue.extend({
  name: 'CustomNoAnomalyCostDataOverlay',
  props: {
    content: {
      type: String,
      default: 'dashboard.abnormalChange.noAnomalyData'
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
.custom-no-anomaly-cost-data-overlay {
  font-size: 14px;
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  text-align: center;
  -ms-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  .no-rows-overlay-content {
    color: #7b8088;
    margin-top: 8px;
    font-size: 12px;
    .material-icons {
      font-size: 12px !important;
      color: #02975b !important;
      font-weight: bold;
      vertical-align: middle;
    }
  }
}
</style>
