<template>
  <b-modal
    id="concurrent-login-confirm-popup"
    :ref="popupRef"
    :title="$t(title)"
    :modal-class="modalClass"
    :hide-header-close="hideHeaderClose"
    :no-close-on-esc="noCloseOnEsc"
    :no-close-on-backdrop="noCloseOnBackdrop"
    hide-footer>
    <div class="content-confirm-popup">
      <p
        v-html="$t(content)"/>
    </div>
    <div class="confirm-popup-footer font-family-notosanscjkkr-medium">
      <b-button
        :style="{background: cancelButtonColor + '!important', color: cancelButtonTextColor + '!important'}"
        class="text-gray-1"
        variant="outline-secondary"
        @click = "cancel">
        {{ $t(cancelText) }}
      </b-button>
      <b-button
        :style="{ background: confirmButtonColor + '!important',
                  color: confirmButtonTextColor + '!important',
                  border: confirmButtonBorder + '!important',
                  height: confirmButtonHeight + 'px!important' }"
        class="text-gray-1"
        variant="warning"
        @click = "onConfirm">
        {{ $t(confirmText) }}
      </b-button>
    </div>
  </b-modal>
</template>

<script>

  export default {
    name: "BaseConfirmPopup",
    props: {
      popupRef: {
        type: String,
        required: true,
      },
      content: {
        type: String,
        default: ""
      },
      title: {
        type: String,
        default: ""
      },
      cancelButtonColor: {
        type: String,
        default: "#fff",
      },
      cancelButtonText: {
        type: String,
        default: "",
      },
      cancelButtonTextColor: {
        type: String,
        default: "#0672FF",
      },
      confirmButtonColor: {
        type: String,
        default: "#dc3545",
      },
      confirmButtonText: {
        type: String,
        default: "",
      },
      confirmButtonTextColor: {
        type: String,
        default: "#fff",
      },
      confirmButtonBorder: {
        type: String,
        default: "none",
      },
      confirmButtonHeight: {
        type: String,
        default: "32",
      },
      modalClass: {
        type: String,
        default: 'base-confirm-popup'
      },
      hideHeaderClose: {
        type: Boolean,
        default: false
      },
      noCloseOnEsc: {
        type: Boolean,
        default: false
      },
      noCloseOnBackdrop: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        showModal: false,
        cancelText: "",
        confirmText: "",
      }
    },
    mounted(){
      if (_.isEmpty(this.cancelButtonText)) {
        this.cancelText = "common.button.cancel";
      } else {
        this.cancelText = this.cancelButtonText
      }
      if (_.isEmpty(this.confirmButtonText)) {
        this.confirmText = "common.button.ok";
      } else {
        this.confirmText = this.confirmButtonText;
      }
    },
    methods: {
      show() {
        this.$refs[this.popupRef].show();
      },
      cancel() {
        this.$emit('onCancel')
        this.$refs[this.popupRef].onCancel();
      },
      hide() {
        this.$refs[this.popupRef].hide();
      },
      onConfirm() {
        this.$emit('onConfirmAction', true)
        this.hide();
      }
    },
  }
</script>

<style lang="scss">
  #concurrent-login-confirm-popup {
    &.content-confirm-popup {
      width: 400px;
      min-height: 72px;
      font-family: NotoSansCJKkr;
      font-size: 14px;
      font-weight: normal;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.71;
      letter-spacing: normal;
      color: #222222;

    }
    &.base-confirm-popup {
      .modal-dialog {
        top: 15%;
      }
      .modal-header {
        text-align: center;
        white-space: nowrap;
        height: 48px;
        padding: 0 1rem;
        align-items: center;
        .modal-title {
          position: absolute;
          left: 50%;
          margin-left: -13%;
          font-size: 16px;
        }
      }

      .modal-body {
        padding: 0 !important;
        .content-confirm-popup {
          padding: 30px;
          p {
            font-size: 14px;
          }
        }
      }

      .modal-content {
        position: absolute;
        top: 300px;
        left: 50%;
        transform: translate(-50%, -50%) !important;
      }
    }

    &.button-cancel {
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
      margin-right: 25px !important;
      border: none;
    }

    &.button-confirm {
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
    }

    &.confirm-popup-footer {
      float: right;
      width: 100%;
      border-top: 1px solid rgb(230, 237, 249);
      text-align: right;
      padding-top: 12px;
    }
  }
</style>
