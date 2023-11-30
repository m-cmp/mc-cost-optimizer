<template>
  <div class="pagination-container">
    <div
      class="pr-1 text-right p-per-page line-height-30 font-family-notosanscjkkr-medium">{{ this.$t('pagination.perPage') }} :</div>
    <div class="custom-div-select-per-page">
      <b-form-select
        :options="optionsPerPage"
        v-model="pageLimit"
        @change="updatePerPage()"/>
    </div>
    <div class="pr-0 col-sm-1 text-center line-height-30 custom-btn-first-page">
      <button @click="onBtnFirst">
        <base-material
          :size="12"
          color="paging-btn"
          name="first_page"
          class="first-button"/>
      </button>
    </div>
    <div class="px-0 text-center line-height-30 custom-btn-chevron-left">
      <button @click="onBtnPrevious">
        <base-material
          :size="12"
          color="paging-btn"
          name="chevron_left"
          class="previous-button"/>
      </button>
    </div>
    <div class="px-0 col-sm-1 line-height-30">
      <b-form-input
        id="page-index"
        v-model="pageNumber"
        class="current-page-number-input"
        type="number"
        @keyup.enter="updatePageChange()"/>
    </div>
    <div class="px-0 text-center p-range line-height-30">
      {{ this.$t('pagination.of') }} {{ totalPage }}
    </div>
    <div class="px-0 text-center line-height-30 custom-btn-chevron-right">
      <button @click="onBtnNext">
        <base-material
          :size="12"
          color="paging-btn"
          name="chevron_right"
          class="next-button"/>
      </button>
    </div>
    <div class="pl-0 text-center line-height-30 custom-btn-last-page">
      <button
        id="btLast"
        @click="onBtnLast">
        <base-material
          :size="12"
          color="paging-btn"
          name="last_page"
          class="last-button"/>
      </button>
    </div>
  </div>
</template>

<script>
  import { DEFAULT_OPTION_PER_PAGE, DEFAULT_PAGE_SIZE } from '@/constants/constants';
  import _isEmpty from 'lodash/isEmpty';

  export default {
    name: 'Pagination',
    props: {
      currentPage: {
        type: Number,
        default: 0,
      },
      totalCount: {
        type: Number,
        default: 0,
        required: true,
      },
      totalPage: {
        type: Number,
        default: 1,
        required: true,
      },
      perPage: {
        type: Number,
        default: 10,
        required: true,
      },
      onBtFirst: {
        type: Function,
        default: null,
      },
      onBtLast: {
        type: Function,
        default: null
      },
      onBtPrevious: {
        type: Function,
        default: null
      },
      onBtNext: {
        type: Function,
        default: null
      },
      onPageChange: {
        type: Function,
        default: null
      },
      onPerPageChange: {
        type: Function,
        default: null
      },
    },
    data() {
      return {
        pageLimit: DEFAULT_PAGE_SIZE,
        optionsPerPage: DEFAULT_OPTION_PER_PAGE,
        pageNumber: this.currentPage + 1,
      };
    },
    computed: {},
    watch: {
      currentPage: function () {
        this.pageNumber = this.currentPage + 1;
      }
    },
    methods: {
      updatePageChange() {
        if (_isEmpty(this.pageNumber)) {
          this.pageNumber = 1;
          return;
        }
        this.pageNumber = parseInt(this.pageNumber);
        if (this.pageNumber <= 0) {
          this.pageNumber = 1;
        }
        if (this.pageNumber > this.totalPage) {
          this.pageNumber = this.totalPage;
        }
        this.onPageChange(this.pageNumber - 1);
      },
      updatePerPage() {
        this.onPerPageChange(this.pageLimit);
      },
      onBtnNext() {
        let max = parseInt(this.totalCount / this.perPage) + 1;
        if (this.pageNumber < this.totalPage) {
          this.pageNumber = this.pageNumber + 1;
          this.onBtNext();
        }
      },
      onBtnLast() {
        this.pageNumber = parseInt(this.totalCount / this.perPage) + 1;
        this.onBtLast();
      },

      onBtnPrevious() {
        if (this.pageNumber >= 2) {
          this.pageNumber = this.pageNumber - 1;
          this.onBtPrevious();
        }
      },
      onBtnFirst() {
        this.pageNumber = 1;
        this.onBtFirst();
      }
    }
  };
</script>

<style lang="scss" scoped>
  .pagination-container {
    white-space: nowrap;
    display: inline-flex;
    float: right;
    margin-top: 4px;
    .custom-btn-first-page {
      padding-left: 16px !important;
      border-left: 1px solid #d5dae0;
      position: relative;
      height: 17px;
      margin-top: 8px;
      button {
        position: absolute;
        left: 16px;
        bottom: -7px;
        &:focus {
          outline: 0 !important;
        }
        .first-button {
          font-weight: bold;
        }
      }
    }
    .custom-btn-chevron-left {
      margin-left: 15px;
      margin-right: 17px;
      button {
        &:focus {
          outline: 0 !important;
        }
        .previous-button {
          font-weight: bold;
          margin-top: 10px;
        }
      }
    }
    .custom-btn-chevron-right {
      margin-left: 17px;
      margin-right: 20px;
      button {
        &:focus {
          outline: 0 !important;
        }
        .next-button {
          font-weight: bold;
          margin-top: 10px;
        }
      }
    }
    .custom-div-select-per-page {
      width: 55px;
      margin-right: 40px;
      height: 24px;
      margin-top: 4px;
      select {
        padding: 0;
        padding-left: 5px;
        border: none;
        height: 24px;
        border-radius: 4px;
        &:focus {
          box-shadow: none;
        }
      }
    }
    .custom-btn-last-page {
      button {
        &:focus {
          outline: 0 !important;
        }
        .last-button {
          font-weight: bold;
          margin-top: 10px;
        }
      }
    }
    .p-per-page {
      font-size: 12px;
      font-weight: 500;
      font-style: normal;
      font-stretch: normal;
      line-height: 1.33;
      letter-spacing: normal;
      color: #222222;
    }
    .p-range {
      font-size: 12px;
      font-weight: 500;
      font-style: normal;
      font-stretch: normal;
      line-height: 1.33;
      letter-spacing: normal;
      margin-left: 8px;
      color: #7b8088;
    }
    .current-page-number-input {
      border: 1px solid #d5dae0;
      border-radius: 4px !important;
      height: 24px;
      margin-top: 3.3px;
      padding: 0;
      text-align: center;
      cursor: text !important;
      &:focus {
        box-shadow: none;
      }
    }
    #page-index::-webkit-outer-spin-button,
    #page-index::-webkit-inner-spin-button {
      /* display: none; <- Crashes Chrome on hover */
      -webkit-appearance: none;
      margin: 0; /* <-- Apparently some margin are still there even though it's hidden */
    }
    .line-height-30 {
      line-height: 30px;
    }
    #page-index[type=number] {
      -moz-appearance: textfield; /* Firefox */
    }
  }

  @media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
    .pagination-container {
      width: 410px;
      display: inline-flex;
      float: right;
      margin-top: 4px;
    }
  }
</style>
