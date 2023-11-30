<template>
  <div :style="{height: `${loadingHeight}px`}">
    <div class="loading-data-wrapper">
      <div
        class="loading-wrapper">
        <div
          :style="{right: `calc(${loadingItemRightByPercent}% + ${loadingItemRightPx}px)`}"
          class="loading-items">
          <div class="spinner-icon"/>
          <div class="text">{{ $t('loadingData') }}...</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'BaseLoadingIndicator',
    props: {
      loadingHeight: {
        type: Number,
        default: 366
      },
      loadingItemRightByPercent: {
        type: Number,
        default: 50
      },
      loadingItemRightPx: {
        type: Number,
        default: 33
      },
    },
  };
</script>

<style lang="scss">
  .loading-data-wrapper {
    width: 100%;
    height: 100%;
    position: relative;
    .loading-wrapper {
      width: 100%;
      height: 100%;
      position: absolute;
    }
  }
</style>
