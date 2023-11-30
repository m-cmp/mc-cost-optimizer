<template>
  <div class="vendor-dropdown-wrapper">
    <b-dropdown
      id="vendor-dropdown"
      :text="text"
      :class="variant"
      variant="none">
      <b-dropdown-item
        v-for="(option, i) in options"
        :key="i"
        class="bsp-dropdown-item"
        @click="handleClick($event, i)">
        <p
          class="bs-select-inline">
          {{ option.alias ? option.alias : option.text }}
        </p>
      </b-dropdown-item>
    </b-dropdown>
  </div>
</template>

<script>
  import _isEmpty from 'lodash/isEmpty';
  export default {
    name: 'VendorDropdown',
    props: {
      options: {
        type: Array,
        required: true
      },
      defaultSelectedOption: {
        type: '',
        required: true
      },
      currentVendorOption: {
        type: '',
        required: true
      },
      label: {
        type: String,
        default: ''
      },
      variant: {
        type: String,
        default: ''
      }
    },
    computed: {
      canShowLabel: function () {
        return !_isEmpty(this.label)
      },
      text: function() {
        if(this.options.length > 0){
          let selectedVndrObj =  this.options.find(option => option.value === this.currentVendorOption);
          return selectedVndrObj.text;
        }else {
          return this.currentVendorOption;
        }
      }
    },
    methods: {
      handleClick: function (event, selectedIndex) {
        this.$emit('selectedOption', {value: this.options[selectedIndex].value, idx: selectedIndex});
      }
    },
  };
</script>

<style lang="scss">
  .vendor-dropdown-wrapper {
    .vendor-label {
      font-family: 'NotoSansCJKkr-Bold', "Apple SD Gothic", sans-serif;
      font-size: 14px !important;
      color: #222222 !important;
      margin-right: 3px;
      margin-top: 4px;
    }
    #vendor-dropdown {
      width: 80px;
      font-weight: bold;
      padding-bottom: 1px;
      border-bottom: 1px solid #d5dae0;
      ul[role="menu"] {
        min-width: 100px;
      }
      .dropdown-toggle {
        &:hover {
          background-color: inherit !important;
        }
        font-family: 'NotoSansCJKkr-Bold', "Apple SD Gothic", sans-serif;
        font-size: 12px !important;
        color: #222222!important;
      }

      .dropdown-toggle::after {
        color: #222222;
        margin-left: 10px;
      }

      .bsp-dropdown-item {
        /*border-top: solid 0.2px #adad97;*/
        margin-left: -15px;
        margin-right: -15px;
        width: 130px;
      }

      .bs-select-checkbox {
        position: absolute;
        right: -123px;
        top: 6px;
      }

      .bs-select-inline {
        display: inline-block;
      }

      .dropdown-item {
        position: relative;
        font-size: 12.5px;
        height: 29px;
        &:active {
          background-color: #babac0;
        }
      }

      .dropdown-menu {
        height: auto !important;
        max-height: 150px;
        padding: 0 0 !important;
        border-radius: 3px;
        top: -3px !important;
        width: auto;
      }

      .dropdown-toggle {
        margin-left: var(--margin-left-by-vendor) !important;
        margin-bottom: -7px;
      }
    }
    .primary {
      .dropdown-toggle::after {
        color: #0672ff !important;
        margin-left: 10px;
        width: 10px;
        height: 5px;
      }
    }
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

  ::-webkit-scrollbar-button {
    display: none
  }
</style>
