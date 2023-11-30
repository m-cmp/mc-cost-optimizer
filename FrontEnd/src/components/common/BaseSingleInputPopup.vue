<template>
  <b-modal
    :ref="popupRef"
    :title="title"
    modal-class="base-single-input-popup"
    hide-footer>
    <div class="content-single-input-popup">
      <p class="input-label">{{ inputLabel }}</p>
      <b-form-input
        v-model="inputValue"
        :class="inputInvalid ? 'input-invalid' : ''"
        :placeholder = "placeHolder"
        :maxlength="inputMaxLength"
        required/>
      <p
        v-if="inputInvalid"
        class="input-warning-message"> {{ inputWarningMessage }}</p>
    </div>
    <br>
    <div class="single-input-popup-footer">
      <b-button
        :style="{background: cancelButtonColor + '!important', color: cancelButtonTextColor + '!important'}"
        class="text-gray-1 button-cancel"
        @click = "hide">
        {{ cancelText }}
      </b-button>
      <b-button
        :style="{background: confirmButtonColor + '!important', color: confirmButtonTextColor + '!important'}"
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
    name: "BaseSingleInputPopup",
    props: {
      popupRef: {
        type: String,
        required: true,
      },
      placeHolder: {
        type: String,
        default: "dasdasd"
      },
      inputLabel: {
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
      inputInvalid: {
        type: Boolean,
        default: false,
      },
      inputWarningMessage: {
        type: String,
        default: "",
      },
      inputMaxLength: {
        type: Number,
        default: 32
      }
    },
    data() {
      return {
        showModal: false,
        cancelText: "",
        confirmText: "",
        inputValue: "",
      }
    },
    mounted(){
      if (_isEmpty(this.cancelButtonText)) {
        this.cancelText = this.$t("dashboard.popup.cancel")
      }
      if (_isEmpty(this.confirmButtonText)) {
        this.confirmText = this.$t("dashboard.dashboardHeader.saveAs")
      }
    },
    methods: {
      show() {
        this.$refs[this.popupRef].show();
      },
      hide() {
        this.$refs[this.popupRef].hide();
        this.reset();
      },
      reset() {
        this.inputValue = "";
      },
      onConfirm() {
        this.$emit('onConfirmAction', this.inputValue);
      }
    },
  }
</script>

<style lang="scss">
  .base-single-input-popup {
    .modal-header {
      text-align: center;
      .modal-title {
        position: absolute;
        left: 50%;
        margin-left: -17%;
        font-size: 16px;
      }
    }

    .modal-body {
      padding: 0 !important;
      .content-single-input-popup {
        padding: 30px 50px 30px 50px;
        input {
          cursor: text;
        }
      }

      ::-webkit-input-placeholder { /* Chrome/Opera/Safari */
        color: #e4e0e8;
      }
      ::-moz-placeholder { /* Firefox 19+ */
        color: #e4e0e8;
      }
      :-ms-input-placeholder { /* IE 10+ */
        color: #e4e0e8;
      }
      :-moz-placeholder { /* Firefox 18- */
        color: #e4e0e8;
      }
    }

    .modal-content {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%) !important;
    }

    .form-control {
      border: 1px solid #e4e0e8;
      border-radius: 7px;
      height: 36px;
      outline: 0;
    }

    .form-control:focus {
      -webkit-box-shadow: none;
      -moz-box-shadow: none;
      box-shadow: none;
    }

    .input-label {
      padding-bottom: 10px;
    }

    .input-invalid {
      border: 1px solid red;
    }

    .input-warning-message {
      color: red;
    }

    .button-confirm {
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
    }

    .button-cancel {
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
      margin-right: 25px !important;
      border: none;
    }

    .single-input-popup-footer {
      float: right;
      width: 100%;
      border-top: 1px solid rgb(230, 237, 249);
      text-align: right;
      height: 60px;
      padding-top: 12px;
      padding-right: 25px !important;
    }
  }

</style>
