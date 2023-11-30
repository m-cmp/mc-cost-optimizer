<template>
  <b-row class="billing-detail-table-view-by-wrapper">
    <div class="view-by-text">
      <span>{{ $t("billing.viewByText") }}:</span>
    </div>
    <div class="view-by-options">
      <base-dropdown
        :options="viewByOptions"
        :enabled-localization="true"
        @selectOption="onSelectOption"
      />
    </div>
    <div
      class="tag-dropdown-wrapper">
      <base-dropdown
        v-show="canDisplayTagDropdown"
        :options="tagOptions"
        @selectOption="onSelectTag"
      />
      <b-dropdown
        v-if="canDisplayNonTagDropdown"
        variant="none">
        <!--:text="$t('billing.viewBy.tagKey')"-->
        <b-dropdown-item>
          <p
            class="bs-select-inline no-tag-content">
            {{ $t("billing.viewBy.thereIsNoTagKey") }}
          </p>
        </b-dropdown-item>
      </b-dropdown>
    </div>
    <div
      class="tag-dropdown-wrapper">
      <base-dropdown
        v-show="canDisplayServiceGroupDropdown"
        :options="serviceGroupOptions"
        @selectOption="onSelectServiceGroup"
      />
      <b-dropdown
        v-if="canDisplayNonServiceGroupDropdown"
        variant="none">
        <!--:text="$t('billing.viewBy.serviceGroup')"-->
        <b-dropdown-item>
          <p
            class="bs-select-inline no-tag-content">
            {{ $t("billing.viewBy.thereIsNoServiceGroupSet") }}
          </p>
        </b-dropdown-item>
      </b-dropdown>
    </div>
    <div class="-wrapper vertical-center -pb-3 -pl-1 -mt-2">
      <b-button
        class="-btn primary- small-"
        @click="onSelectGridLayerOption"
      >
        <span v-if="layerOption === false">  {{ $t('billing.cloudBillDetails.expandAll') }} </span>
        <span v-if="layerOption === true">   {{ $t('billing.cloudBillDetails.collapseAll') }} </span>
      </b-button>
    </div>
  </b-row>
</template>

<script>
  import BaseDropdown from "@/components/common/BaseDropdown";
  import {VIEW_BY_OPTIONS, VIEW_BY_OPTION_VALUE} from '@/constants/billingConstants';
  import { VENDOR } from '@/constants/constants';
  import { mapGetters } from "vuex";
  import _isEmpty from 'lodash/isEmpty';
  import _cloneDeep from "lodash/cloneDeep";
  import _ from 'lodash';
  export default {
    name: 'BillingDetailTableViewBy',
    components: {BaseDropdown},
    props: {
      selectedVendor: {
        type: String,
        required: true
      },
      selectedMonthYear: {
        type: Object,
        required: true
      },
      selectedViewByOption: {
        type: String,
        required: true
      },
    },
    data() {
      return {
        viewByOptions : VIEW_BY_OPTIONS,
        selectedOption: VIEW_BY_OPTION_VALUE.ACCOUNT,
        layerOption: false,
      }
    },
    computed: {
      ...mapGetters({
        chargeTableState: 'billing/chargeTableState',
        tagKeys: 'billing/cloudBilDetailsTagKeys',
        serviceGroupSets: 'billing/cloudBilDetailsSvgSets'
      }),
      tagOptions: function () {
        return this.tagKeys.map(tag => {
          return {
            text: tag,
            value: tag
          }
        })
      },
      serviceGroupOptions: function () {
        return this.serviceGroupSets.map(svg => {
          return {
            text: svg,
            value: svg
          }
        })
      },
      canDisplayTagDropdown: function () {
        return this.selectedOption === VIEW_BY_OPTION_VALUE.TAG && !_isEmpty(this.tagOptions)
      },
      canDisplayNonTagDropdown: function () {
        return this.selectedOption === VIEW_BY_OPTION_VALUE.TAG && _isEmpty(this.tagOptions);
      },
      canDisplayServiceGroupDropdown: function () {
        return this.selectedOption === VIEW_BY_OPTION_VALUE.SERVICEGROUP && !_isEmpty(this.serviceGroupOptions);
      },
      canDisplayNonServiceGroupDropdown: function () {
        return this.selectedOption === VIEW_BY_OPTION_VALUE.SERVICEGROUP && _isEmpty(this.serviceGroupOptions);
      }
    },
    watch: {
      selectedOption: {
        handler(){
          if(this.selectedOption === VIEW_BY_OPTION_VALUE.TAG) {
            this.getTagOptions()
          }
          if(this.selectedOption === VIEW_BY_OPTION_VALUE.SERVICEGROUP) {
            this.getServiceGroupOptions()
          }
        }
      },
      selectedVendor: {
        handler() {
          this.setViewByOptions();
        }
      }
    },
    created () {
      this.setViewByOptions();
    },
    methods: {
      setViewByOptions() {
        this.viewByOptions = VIEW_BY_OPTIONS;
        switch (this.selectedVendor) {
          case VENDOR.AWS:
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = 'billing.billingSummary.account';

            if(this.selectedOption !== '') { //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption('');
            }
            break;
          case VENDOR.AZURE:
            this.viewByOptions = this.viewByOptions
              .filter(option => ![VIEW_BY_OPTION_VALUE.TAG, VIEW_BY_OPTION_VALUE.SERVICEGROUP].includes(option.value) )
              .map(option => {
                return {
                  ...option
                }
              });
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = 'billing.billingSummary.subscription';

            if(this.selectedOption !== '') { //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption('');
            }
            break;
          case VENDOR.GCP:
            this.viewByOptions = this.viewByOptions
              .filter(option => [VIEW_BY_OPTION_VALUE.ACCOUNT].includes(option.value) )
              .map(option => {
                return {
                  ...option
                }
              });
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = 'billing.billingSummary.project';

            if(this.selectedOption !== '') { //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption('');
            }
            break;
          case VENDOR.OCI: // 계정, 인보이스, 리전 3개 View만 보여주기
            this.viewByOptions = this.viewByOptions
              .filter(option => ![VIEW_BY_OPTION_VALUE.TAG, VIEW_BY_OPTION_VALUE.SERVICEGROUP].includes(option.value))
              .map(option => {
                return {
                  ...option
                }
              });
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = 'billing.billingSummary.compartment';

            if(this.selectedOption !== ""){ //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption("");
            }
            break;
          case VENDOR.NCP:
            this.viewByOptions = this.viewByOptions
              .map(option => {
                return {
                  ...option
                }
              });
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = 'billing.billingSummary.account';
            if(this.selectedOption !== ""){ //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption("");
            }
            break;
          case VENDOR.TENCENT:
            this.viewByOptions = this.viewByOptions.
              filter(options => options.value !== VIEW_BY_OPTION_VALUE.TAG && options.value !== VIEW_BY_OPTION_VALUE.SERVICEGROUP)
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = 'billing.billingSummary.account';
            if(this.selectedOption !== ""){ //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption("");
            }
            break;
          case VENDOR.OPENSTACK:
            this.viewByOptions = this.viewByOptions.
              filter(options => options.value == VIEW_BY_OPTION_VALUE.ACCOUNT)
            this.viewByOptions
              .find(option => option.value === VIEW_BY_OPTION_VALUE.ACCOUNT).text = '서비스';
            if(this.selectedOption !== ""){ //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption("");
            }
            break;
          default:
            if(this.selectedOption !== "") { //벤더 변경할 때 selectedOption를 ""(공백)으로 디폴트 처리
              this.onSelectOption(this.selectedViewByOption);
            }
            break;
        }
      },
      onSelectOption(selectedOption) {
        if (this.selectedOption === selectedOption) {
          this.$store.dispatch('billing/setSelectedViewByOption', selectedOption);
          return;
        }
        // if(this.selectedOption !== selectedOption){
        //   console.log('change viewBy')
        // } // 보기옵션 변경 상태와 펼치기 버튼 상태의 동기화 작업시 boolean 조건 이용하기

        this.selectedOption = selectedOption;
        this.$store.dispatch('billing/setSelectedViewByOption', selectedOption);
      },
      onSelectTag(selectedTag) {
        this.$store.dispatch('billing/setSelectedTag', selectedTag);
      },
      onSelectServiceGroup(selectedServiceGroup) {
        this.$store.dispatch('billing/setSelectedServiceGroupSet', selectedServiceGroup);
      },
      onSelectGridLayerOption(){
        if(!this.layerOption){
          this.layerOption = true
        } else {
          this.layerOption = false
        }
        this.$store.dispatch('billing/setGridLayerCondition', this.layerOption)
      },
      getTagOptions(){
        this.$store.dispatch('billing/getTagOptions', {
          selectedVendor: this.selectedVendor,
          yearMonth: `${this.selectedMonthYear.chargeYear}-${this.selectedMonthYear.chargeMonth}`
        });
      },
      getServiceGroupOptions(){
        this.$store.dispatch('billing/getServiceGroupSetOptions', {
          selectedVendor: this.selectedVendor,
          yearMonth: `${this.selectedMonthYear.chargeYear}-${this.selectedMonthYear.chargeMonth}`
        });
      }

    }
  };
</script>
<style lang="scss">
  .billing-detail-table-view-by-wrapper {
    font-weight:normal !important;
    font-size: 14px !important;
    height: 40px;
    .view-by-text {
      padding-top: 11px;
      padding-left: 35px;
      color: #6c7994;
    }
    .view-by-options {
      display: inline-block;
      margin-top: 4px;
      .dropdown-toggle {
        &:after {
          color: #7b8088 !important;
        }
      }
      .base-dropdown {
        button {
          font-size: 14px !important;
          padding-left: 8px !important;
          font-family: "NotoSansCJKkr-Regular";
        }
        .dropdown-menu {
          min-width: 70px !important;
          font-family: "NotoSansCJKkr-Regular";
          li {
            a {
              padding-left: 18px !important;
              padding-right: 18px !important;
            }
          }
        }
      }
    }
    .tag-dropdown-wrapper {
      font-size: 14px !important;
      display: inline-block;
      margin-top: 4px;
      .dropdown-menu {
        height: auto !important;
        padding-top: 0;
        padding-bottom: 0;
        margin-top: -5px;
        background: #FFFFFF;
        box-shadow: 0 3px 12px 2px rgba(124,129,148,0.25);
        border-radius: 4px;
      }

      .dropdown-toggle {
        color: #0672FF !important;
        font-size: 14px !important;

        &:after {
          color: #0672FF !important;
          margin-left: 10px !important;
        }
      }

      .show {
        .dropdown-toggle {
          &:after {
            border-top: 0 !important;
            border-bottom: 4px dashed !important;
          }
        }
      }

      .no-tag-content {
        margin-left: -10px;
        margin-top: 5px;
        margin-bottom: 5px;
        color: #babac0;
        font-family: "NotoSansCJKkr-Light", "Apple SD Gothic", sans-serif;
        font-weight: 300 !important;
        font-size: 12px;
      }
    }
  }
</style>
