<template>
  <b-modal
    :ref="popupRef"
    :title="title"
    :modal-class="modalClass"
    hide-footer
    @hidden="onConfirm">
    <div class="content-confirm-popup">
      <p
        v-html="content"/>
    </div>
    <div class="confirm-popup-footer font-family-notosanscjkkr-medium">
      <b-button
        :style="{background: buttonColor + '!important', color: buttonTextColor + '!important'}"
        class="text-gray-1 button-cancel"
        @click = "hide">
        {{ btnText }}
      </b-button>
    </div>
  </b-modal>
</template>

<script>
  import _isEmpty from 'lodash/isEmpty';

  export default {
    name: "BasePopup",
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
      buttonColor: {
        type: String,
        default: "#0672ff",
      },
      buttonText: {
        type: String,
        default: "",
      },
      buttonTextColor: {
        type: String,
        default: "#fff",
      },
      modalClass: {
        type: String,
        default: 'base-confirm-popup'
      }
    },
    data() {
      return {
        showModal: false,
        btnText: ""
      }
    },
    mounted(){
      if (_isEmpty(this.buttonText)) {
        this.btnText = this.$t("dashboard.popup.cancel");
      } else {
        this.btnText = this.buttonText;
      }
    },
    methods: {
      show() {
        this.$refs[this.popupRef].show();
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
