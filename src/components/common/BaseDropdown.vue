<template>
  <div>
    <b-dropdown
      :class="[variant, 'base-dropdown']"
      :disabled="disabled"
      variant="none"
      @toggle="onToggle"
    >
      <template v-slot:button-content>
        <span>{{ enabledLocalization ? $t(selectedOptionText) : selectedOptionText }}</span>
      </template>
      <b-dropdown-item
        v-for="(option, i) in options"
        :key="i"
        class="bsp-dropdown-item"
        @click="handleClick(option)">
        <p class="bs-select-inline">
          {{ enabledLocalization ? $t(option.text) : option.text }}
        </p>
      </b-dropdown-item>
    </b-dropdown>
  </div>
</template>

<script>
  import _isEmpty from 'lodash/isEmpty';
  import _isEqual from 'lodash/isEqual';

  export default {
    name: 'BaseDropdown',
    props: {
      // element must be: {value: string, text: string}
      options: {
        type: Array,
        required: true,
      },
      enabledLocalization: {
        type: Boolean,
        required: false,
        default: false
      },
      variant: {
        type: String,
        required: false,
        default: 'primary'
      },
      disabled: {
        type: Boolean,
        required: false,
        default: false
      }
    },
    data() {
      return {
        selectedOptionText: '',
        isShow: false,
      }
    },
    computed: {
    },
    watch: {
      // triggered if options change. `isDefault` is used to display default selected option text
      options: {
        handler: function () {
          if (_isEmpty(this.options)) {
            return;
          }
          const isDefaultDefined = this.options.some(opt => {
            if (opt.isDefault) {
              this.selectedOptionText = opt.text;
              return true;
            }
          });
          if (!isDefaultDefined) {
            this.selectedOptionText = this.options[0].text;
          }
        },
        immediate: true
      }
    },
    mounted() {
      this.$emit('mounted');
      this.$root.$on('bv::dropdown::show', bvEvent => {
        this.isShow = true;
        this.$root.$emit('bv::hide::tooltip')
      })
      this.$root.$on('bv::dropdown::hide', bvEvent => {
        this.$root.$emit('bv::hide::tooltip');
        //Fix bug only one case after user handleClick to selectedOption
        const $vm = this;
        setTimeout(function() {
          $vm.isShow = false;
        }, 100);
      })
      this.$root.$on('bv::tooltip::show', bvEvent => {
        if (this.isShow) {
          this.$root.$emit('bv::hide::tooltip');
          if (_isEqual(bvEvent.target.id, 'dashboard-currency-tooltip')
           || _isEqual(bvEvent.target.id, 'cost-analytics-currency-tooltip')
          ) {
            bvEvent.preventDefault()
          }
        }
      })
    },
    methods: {
      handleClick: function (selectedOption) {
        this.selectedOptionText = selectedOption.text;
        this.$emit('selectOption', selectedOption.value)
      },
      // to change selected option text if options stay unchanged
      changeSelectedOptionText(selectedOptionText) {
        this.selectedOptionText = selectedOptionText;
      },
      onToggle() {
        this.$emit('toggle');
      },
    }
    ,
  }

</script>

<style lang="scss">
  .base-dropdown {
    .dropdown-toggle {
      font-size: 14px;
      font-weight: 500;
      font-style: normal;
      font-stretch: normal;
      line-height: normal;
      letter-spacing: normal;
    }

    .dropdown-toggle::after {
      color: #0672FF !important;
      margin-left: 10px !important;
    }

    .bs-select-checkbox {
      position: absolute;
      top: 6px;
    }

    .bs-select-inline {
      display: inline-block;
    }

    .dropdown-item {
      position: relative;
      font-size: 12.5px;
      height: 30px;
      padding-left: 13px;
    }

    .dropdown-menu {
      padding: 0 0 !important;
      border-radius: 3px;
      top: -3px !important;
      height: auto;
      max-height: 180px;
    }

    ::-webkit-scrollbar {
      background-color: #fff;
      width: 6px;
      height: 6px;
    }

    ::-webkit-scrollbar-track {
      background-color: #fff
    }

    ::-webkit-scrollbar-thumb {
      background-color: #babac0;
      border-radius: 6px;
    }
  }

  .primary {
    .dropdown-toggle {
      color: #0672FF !important;
      font-size: 12px;
      font-weight: 500;
      font-style: normal;
      font-stretch: normal;
      letter-spacing: normal;
    }
  }

  .default {
    .dropdown-toggle {
      color: #71808e !important;
      font-size: 12px !important;
    }
  }

  ::-webkit-scrollbar-button {
    display: none
  }

  .dropdown-toggle {
    box-shadow: none !important;
  }
</style>
