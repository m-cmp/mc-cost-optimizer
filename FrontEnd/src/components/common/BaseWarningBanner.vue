<template>
  <div
    v-if="opened"
    class="body-banner">
    <p class="banner-text">
      <base-material
        :size="20"
        name="error_outline"
        class="info-icon"/>
      {{ message }}
    </p>
    <button @click="closeBanner">
      <base-material
        :size="20"
        name="close"
        class="close-icon"/>
    </button>
  </div>
</template>

<script>
  export default {
    name: 'BaseWarningBanner',
    props: {
      message: {
        type: '',
        required: true
      },
      opened: {
        type: Boolean,
        default: false
      }
    },
    methods: {
      closeBanner() {
        this.$emit('onCloseBanner');
        this.opened = !this.opened;
      }
    },
  };
</script>

<style lang="css" scoped>
  .body-banner {
    width: 100%;
    height: 48px;
    background-color: #535a80;
    position: relative;
  }

  .banner-text {
    height: 24px;
    font-size: 14px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.71;
    letter-spacing: normal;
    color: #ffffff;
    padding-top: 9px;
    padding-left: 22px;
  }

  .close-icon {
    position: absolute;
    font-size: 24px;
    right: 18px;
    color: white;
    bottom: 15px;
    cursor: pointer;
  }
  .info-icon {
    position: relative;
    top: 4px;
    right: 5px;
  }
</style>
