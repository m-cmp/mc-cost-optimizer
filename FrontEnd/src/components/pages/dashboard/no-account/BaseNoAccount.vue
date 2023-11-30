<template>
  <div class="no-account-wrapper">
    <div class="no-account-inner">
      <p
        class="no-account-title">{{ $t('dashboard.noAccount.title') }}</p>
      <p class="text-normal">{{ $t('dashboard.noAccount.noAccountMessage') }}</p>
      <p class="text-normal please-add-account">{{ $t('dashboard.noAccount.pleaseAddAccount') }}</p>
    </div>
    <b-button
      class="button-background-primary">
      + {{ $t('dashboard.noAccount.addAccount') }}
    </b-button>
  </div>
</template>

<script>
  export default {
    name: 'BaseNoAccount',
  };
</script>

<style lang="scss">
  .no-account-wrapper {
    text-align: center;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    -ms-transform: translate(-50%, -50%); /* for IE 9 */
    -webkit-transform: translate(-50%, -50%); /* for Safari */
    width: 300px;
    .no-account-inner {
      justify-content: center;
    }

    .button-background-primary {
      background-color: #0672FF !important;
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
    }

    .no-account-title {
      font-size: 16px;
      font-weight: 500;
      font-style: normal;
      font-stretch: normal;
      line-height: normal;
      letter-spacing: normal;
      color: #222222;
      margin-bottom: 5px
    }

    .text-normal {
      font-size: 12px;
      font-weight: normal;
      font-style: normal;
      font-stretch: normal;
      line-height: 1.5;
      letter-spacing: normal;
      text-align: center;
      color: #7b8088;
    }
    .please-add-account {
      margin-bottom: 10px;
    }
  }
</style>
