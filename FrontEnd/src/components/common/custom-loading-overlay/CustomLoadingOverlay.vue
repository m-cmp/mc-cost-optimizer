<template>
  <div id="loading-wrapper">
    <div
      class="loading-wrapper">
      <div
        class="loading-items -wrapper-column custom-items">
        <div class="text">
          <span class="spinner-icon"/>
          <span
            class="-ml-1"
            style="margin-top: 6px">
            {{ $t('loadingTable') }}...
          </span>
        </div>
        <span
          v-if="delayMessageAction"
          class="-description -mt-2">
          {{ delayLoadingMessage }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue';

  export default Vue.extend({
    name: 'CustomLoadingOverlay',
    props: {
    },
    data() {
      return {
        delayMessageAction: false,
        delayLoadingMessage: this.$t('delayLoadingMessage')
      };
    },
    mounted() {
      setTimeout(() => {
        this.delayMessageAction = true
      }, 5000)
    }
  });
</script>

<style lang="scss">
  #loading-wrapper {
    width: 100%;
    height: 100%;
    position: relative;
    .loading-wrapper {
      width: 100%;
      height: 100%;
      position: absolute;
      .loading-items{
        &.custom-items {
          align-items: center;
        }
      }
    }
  }
</style>
