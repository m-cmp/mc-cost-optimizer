<template>
  <div class="month-year-dropdown-wrapper">
    <p
      v-show="canShowLabel"
      class="inline-block month-year-label">{{ label }}</p>
    <b-dropdown
      id="month-year-dropdown"
      :text="options[optionIndex]"
      :class="variant"
      variant="none"
      @shown="handleDropdownClick($event)">
      <b-dropdown-item
        v-for="(option, i) in options"
        :key="i"
        class="bsp-dropdown-item"
        @click="handleClick($event, i)">
        <p
          class="bs-select-inline">
          {{ option }}</p>
        <p
          v-if="option === options[optionIndex]"
          class="bs-select-inline">({{ $t('billing.billingSummary.selected') }})</p>
      </b-dropdown-item>
    </b-dropdown>
  </div>
</template>

<script>
  import _isEmpty from 'lodash/isEmpty';
  export default {
    name: 'MonthYearDropdown',
    props: {
      options: {
        type: Array,
        required: true,
      },
      defaultSelectedOption: {
        type: '',
        required: true,
      },
      currentMonthOption: {
        type: '',
        required: true,
      },
      optionIndex: {
        type: Number,
        required: true,
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
      }
    },
    methods: {
      handleClick: function (event, selectedIndex) {
        const selectedOptionValue = this.options[selectedIndex];
        if (selectedOptionValue === this.defaultSelectedOption) {
          this.$emit('selectedOption', {value: this.currentMonthOption, idx: selectedIndex})
        } else {
          this.$emit('selectedOption', {value: selectedOptionValue, idx: selectedIndex})
        }
      },
      handleDropdownClick: function () {
        const ulTagElement = document.getElementById("month-year-dropdown").getElementsByTagName("UL")[0];
        const currentLi = ulTagElement.getElementsByTagName("LI")[this.optionIndex];
        currentLi.parentNode.scrollTop = currentLi.offsetTop;
      }
    },
  };
</script>

<style lang="scss">
  .month-year-dropdown-wrapper {
    .month-year-label {
      font-family: 'NotoSansCJKkr-Bold', "Apple SD Gothic", sans-serif;
      font-size: 14px !important;
      color: #222222 !important;
      margin-right: 3px;
      margin-top: 4px;
    }
    #month-year-dropdown {
      width: 80px;
      font-weight: bold;
      padding-bottom: 1px;
      border-bottom: 1px solid #d5dae0;
      .dropdown-toggle {
        &:hover {
          background-color: inherit !important;
        }
        font-family: 'NotoSansCJKkr-Bold', "Apple SD Gothic", sans-serif;
        font-size: 14px !important;
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
        width: 190px;
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
      }

      .dropdown-toggle {
        margin-left: -13px !important;
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
