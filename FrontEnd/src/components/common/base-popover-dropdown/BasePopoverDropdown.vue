<template>
  <b-popover
    ref="popover"
    :target="target"
    :placement="placement"
    :show="showPopover"
    :container="containerCustomPopover"
    :custom-class="customClass"
    triggers="focus">
    <div
      v-for="(option, i) in options"
      :key="i"
      @click="onSelect(option)"
    >
      <b-button
        class="mb-1 text-gray-1 custom-btn-attachment"
        variant="transparent">
        <base-material
          :size="24"
          :name="(option.icon != null) ? option.icon : 'attachment'"
          class="custom-icon-attachment"
        />
        <p
          class="font-family-notosanscjkkr-medium"
        >
          {{ enabledLocalization ? $t(option.text) : option.text }}
        </p>
      </b-button>
    </div>
  </b-popover>
</template>

<script>
  export default {
    name: 'BasePopoverDropdown',
    props: {
      target: {
        type: String,
        default: null
      },
      placement: {
        type: String,
        default: null
      },
      options: {
        type: Array,
        required: true,
      },
      enabledLocalization: {
        type: Boolean,
        required: false,
        default: false
      },
      showPopover: {
        type: Boolean,
        default: false
      },
      containerCustomPopover: {
        type: String,
        default: null
      },
      customClass: {
        type: String,
        default: ''
      }
    },
    methods: {
      onSelect(selectedOption) {
        this.$emit('selectOption', selectedOption.value);
      },
      open() {
        this.$refs.popover.$emit('open')
      },
      close() {
        this.$refs.popover.$emit('close')
      }
    }
  };
</script>

<style lang="scss">
  .custom-btn-attachment {
    padding: 0!important;
  }
  .custom-icon-attachment {
    padding-right: 3px !important;
  }
  .popover {
    -webkit-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    -moz-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    border: none !important;
    max-width: 30% !important;
    top: 0 !important;
    left: 16px;
    .popover-body {
      padding: 0 !important;
      div {
        padding: 2px 12px !important;
        &:hover {
          background-color: #F2F4F6;
        }
      }
    }
    .arrow {
      left: 86.5% !important;
    }
    .arrow:before {
      border-bottom-color: #ffffff;
    }
  }
</style>
