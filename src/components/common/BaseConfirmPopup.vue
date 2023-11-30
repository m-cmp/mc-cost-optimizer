<template>
  <b-modal
    :ref="popupRef"
    :title="title"
    :modal-class="modalClass"
    hide-footer
    @hidden="onCloseAction"
  >
    <div class="content-confirm-popup">
      <p
        v-html="content"/>
    </div>
    <div class="confirm-popup-footer font-family-notosanscjkkr-medium">
      <b-button
        :style="{background: cancelButtonColor + '!important', color: cancelButtonTextColor + '!important'}"
        class="text-gray-1 button-cancel"
        @click = "hide">
        {{ cancelText }}
      </b-button>
      <b-button
        :style="{ background: confirmButtonColor + '!important',
                  color: confirmButtonTextColor + '!important',
                  border: confirmButtonBorder + '!important',
                  height: confirmButtonHeight + 'px!important' }"
        class="text-gray-1 button-confirm"
        @click = "onConfirm">
        {{ confirmText }}
      </b-button>
    </div>
  </b-modal>
</template>

<script>
  import _isEmpty from 'lodash/isEmpty';

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
        default: "#0672FF",
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
      if (_isEmpty(this.cancelButtonText)) {
        this.cancelText = this.$t("dashboard.popup.cancel");
      } else {
        this.cancelText = this.cancelButtonText;
      }
      if (_isEmpty(this.confirmButtonText)) {
        this.confirmText = this.$t("dashboard.popup.confirm");
      } else {
        this.confirmText = this.confirmButtonText;
      }
    },
    methods: {
      show() {
        this.$refs[this.popupRef].show();
      },
      hide() {
        this.$refs[this.popupRef].hide();
      },
      onCloseAction(){
        this.$emit('onCloseAction', true)
      },
      onConfirm() {
        this.$emit('onConfirmAction', true)
        this.hide();
      }
    },
  }
</script>

<style lang="scss">
  .base-confirm-popup {
    .modal-header {
      text-align: center;
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
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%) !important;
    }
  }

  .button-cancel {
    outline: none;
    outline: 0;
    box-shadow: none !important;
    border-radius: 4px !important;
    margin-right: 25px !important;
    border: none;
  }

  .button-confirm {
    outline: none;
    outline: 0;
    box-shadow: none !important;
    border-radius: 4px !important;
  }

  .confirm-popup-footer {
    float: right;
    width: 100%;
    border-top: 1px solid rgb(230, 237, 249);
    text-align: right;
    height: 60px;
    padding-top: 12px;
    padding-right: 25px !important;
  }

</style>
