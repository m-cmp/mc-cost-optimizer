<template>
  <!-- fluid of b-container property full width -->
  <b-container
    id="wrapper"
    :class="{'base-welcome-popup in-billing' : welcomePopup.canShow}"
    class="wrapper-billing-layout"
    fluid>
    <BillingHeader
      :all-vendors="allVendors"
      :bill-list="billList"
      :common-user-info="commonUserInfo"
      :is-sidebar-active="isSidebarActive"
      :is-billing-charge-detail-visible="isBillingChargeDetailVisible"
      :active-month-idx="activeMonthIdx"
      :selected-vendor="selectedVendor"
      @setSelectedVendor="setSelectedVendor"
      @changeUserFilter="onChangeUserFilter"
      @clickBackBtn="onClickBackFromChargeDetail"
    />
    <v-tour
      :steps="steps"
      :callbacks="customCallBacks"
      name="billingTour">
      <template slot-scope="tour">
        <transition name="fade">
          <v-step
            v-for="(step, index) of tour.steps"
            v-if="tour.currentStep === index"
            :key="index"
            :step="step"
            :previous-step="tour.previousStep"
            :next-step="tour.nextStep"
            :stop="tour.stop"
            :is-first="tour.isFirst"
            :is-last="tour.isLast"
            :labels="tour.labels"
          >
            <template>
              <div slot="actions">
                <button
                  class="got-it-btn"
                  @click="tour.stop">{{ $t('billing.tourSteps.gotIt') }}</button>
              </div>
            </template>
          </v-step>
        </transition>
      </template>
    </v-tour>
    <b-row
      :class="{'active': isSidebarActive}"
      class="justify-content-md-center billing-main"
      no-gutters>
      <BaseLeftMenu @setSidebarActive="setSidebarActive">
        <template v-slot:mainRight>
          <div
            v-show="!isBillingChargeDetailVisible"
            class="px-10 billing-sidebar">
            <BillingSummary
              :bill-list="billList"
              :active-month-idx="activeMonthIdx"
              :current-step="currentStep"
              :selected-month-yaer="selectedMonthYear"
              :selected-vendor="selectedVendor"
              @setActiveMonthIdx="setActiveMonthIdx"
              @showHotspot="showHotspot"
            />
          </div>
          <div
            v-show="!isBillingChargeDetailVisible"
            class="px-10 billing-content">
            <MonthlyCostTrend
              :bill-list="billList"
              :active-month-idx="activeMonthIdx"
              :first-month-idx="firstMonthIdx"
              :last-month-idx="lastMonthIdx"
              :selected-vendor="selectedVendor"
              :is-enabled-prev-month-button="isEnabledPrevMonthButton"
              :is-enabled-next-month-button="isEnabledNextMonthButton"
              :browser="browser"
              @onClickPrev="onClickPrev"
              @onClickNext="onClickNext"
              @setActiveMonthIdx="setActiveMonthIdx"
            />
            <BillingDetailTable
              :active-month-idx="activeMonthIdx"
              :bill-list="billList"
              :charge-list="chargeList"
              :selected-vendor="selectedVendor"
              :selected-month-year="selectedMonthYear"
              :current-step="currentStep"
              :selected-tab-index="selectedTabIndex"
              @clickChargeListCell="onClickChargeListCell"
              @showHotspot="showHotspot"
            />
          </div>
        </template>
      </BaseLeftMenu>
    </b-row>
    <!--
    <BaseWelcomePopup
      :can-show="welcomePopup.canShow"
      :page-name="CLASSIC_VERSION_PAGE.BILLING"
      :new-update-options="welcomePopup.newUpdateOptions"
      :is-localization-enabled="true"
      :all-vendors="allVendors"
      :available-vendor="availableVendor"
      @clickDontShow="onClickWelcomePopupDontShow"
      @clickLetsDoIt="onClickWelcomePopupLetsDoIt"
      @selectNewUpdateOption="onSelectNewUpdateOption"
    />
    -->
    <!--<BaseSurveyModal
      ref="billingSurveyModal"
      :redirect-page="CLASSIC_VERSION_PAGE.BILLING"
      survey-ref="billingSurveyModal"/>
    <span
      class="go-top"
      @mouseover="delayDisplayText">
      <base-material
        :size="24"
        class="exit-to-old-version-icon"
        name="exit_to_app"
      />
      <p
        class="switch-old-version-content"
        @click="openSurveyModal">{{ backToClassicalVersionContent }}</p>
    </span>-->
  </b-container>
</template>

<script>
  import { mapGetters } from 'vuex';
  import BillingHeader from './billing-header/BillingHeader';
  import BillingSummary from './billing-summary/BillingSummary';
  import MonthlyCostTrend from './monthly-cost-trend/MonthlyCostTrend';
  import BillingDetailTable from './billing-detail-table/BillingDetailTable';
  import {
    getFirstMonthIdx,
    getLastMonthIdx,
    setFirstMonthIdxAfterClickPrev,
    setLastMonthIdxAfterClickPrev,
    setFirstMonthIdxAfterClickNext,
    setLastMonthIdxAfterClickNext,
    getSelectedMonthYear
  } from '@/util/montlyCostTrend';
  import {COST_ANALYTICS_ACTION_TYPE, CLASSIC_VERSION_PAGE, ROUTE_NAME, SEARCH_BAR_RESULT_GROUP, DEFAULT_VENDOR_OPTIONS} from "@/constants/constants";
  // import BaseWelcomePopup from "@/components/common/BaseWelcomePopup";
  import BaseLeftMenu from '@/components/common/base-left-menu/BaseLeftMenu';
  import { getValueFromStorageByKey, setValueToStorageByKey, LOCAL_STORAGE_KEY } from '@/util/localStorage';
  import { BILLING_NEW_UPDATE, BILLING_NEW_UPDATE_OPTIONS } from '@/constants/billingConstants';
  import _isEmpty from 'lodash/isEmpty';
  import {VENDOR_NAMES} from "../../../constants/billingConstants";

  /*const BaseSurveyModal = () => import("@/components/common/BaseSurveyModal");*/

  const BILLING_STEP = {
    SUMMARY: 0,
    CHARGE_LIST: 1,
    INVOICE_LIST: 2,
    CLOUD_BILL_DETAIL: 3
  }

  export default {
    name: 'BillingLayout',
    components: {
      BaseLeftMenu,
      // BaseWelcomePopup,
      BillingHeader,
      BillingSummary,
      MonthlyCostTrend,
      BillingDetailTable,
      /*BaseSurveyModal*/
    },
    data() {
      return {
        isSidebarActive: false,
        selectedMonthYear: {},
        welcomePopup: {
          // canShow: true,
          canShow:false,
          pageName: this.$t('onboarding.welcome.pageName.billing'),
          newUpdateOptions: BILLING_NEW_UPDATE_OPTIONS
        },
        currentStep: -1,
        steps: [
          {
            target: '#bill-summary-hotspot',
            content: this.$t('billing.tourSteps.content.billSummary'),
            header: {
              title : this.$t('billing.tourSteps.headerTitle.billSummary')
            },
            params: {
              enableScrolling: false
            }
          },
          {
            target: '#charge-list-hotspot',
            content: this.$t('billing.tourSteps.content.chargeList'),
            header: {
              title : this.$t('billing.tourSteps.headerTitle.chargeList')
            },
            params: {
              enableScrolling: false
            }
          },
          {
            target: '#invoice-list-hotspot',
            content: this.$t('billing.tourSteps.content.invoiceList'),
            header: {
              title : this.$t('billing.tourSteps.headerTitle.invoiceList')
            },
            params: {
              enableScrolling: false
            }
          },
          {
            target: '#cloud-bill-detail-hotspot',
            content: this.$t('billing.tourSteps.content.cloudBillDetail'),
            header: {
              title : this.$t('billing.tourSteps.headerTitle.cloudBillDetail')
            },
            params: {
              enableScrolling: false
            }
          },
          {
            target: '#invoice-insight-hotspot',
            content: this.$t('billing.tourSteps.content.invoiceInsight'),
            header: {
              title : this.$t('billing.tourSteps.headerTitle.invoiceInsight')
            },
            params: {
              enableScrolling: false
            }
          }
        ],
        customCallBacks: {
          onStop: this.myCustomStopCallback,
        },
        CLASSIC_VERSION_PAGE:  CLASSIC_VERSION_PAGE,
        backToClassicalVersionContent: '',
        isBillingChargeDetailVisible: false,
        selectedLinkedAccountId: null,
        canShowWelcomePopup: false,
      }
    },
    computed: {

      ...mapGetters({
        //allVendors: 'common/allVendors',
        submittedSurvey: 'common/submittedSurvey',
        browser: "common/browser",
        commonUserInfo: 'common/info',
        billList: 'billing/billList',
        chargeList: 'billing/chargeList',
        isChargeListLoading: 'billing/isChargeListLoading',
        activeMonthIdx: 'billing/activeMonthIdx',
        firstMonthIdx: 'billing/firstMonthIdx',
        lastMonthIdx: 'billing/lastMonthIdx',
        selectedTabIndex: 'billing/selectedTabIndex',
        selectedVendor: 'billing/selectedVendor',
        availableVendor: 'common/availableVendor'
      }),
      /**
       * We will enable previous month button if first month of monthly cost trend > first month of bill list
       */
      isEnabledPrevMonthButton: {
        cache: false,
        get() {
          return isEnabledPrevMonthButton(this.firstMonthIdx);
        }
      },
      /**
       * We will enable next month button if last month of monthly cost trend < last month of bill list
       */
      isEnabledNextMonthButton: {
        get() {
          return isEnabledNextMonthButton(this.lastMonthIdx, this.billList.length);
        }
      },
      isPrevMonthButtonFromChargeDetailChartEnabled: {
        cache: false,
        get() {
          return isPrevMonthButtonFromChargeDetailChartEnabled(this.activeMonthIdx)
        }
      },
      isNextMonthButtonFromChargeDetailChartEnabled: {
        cache: false,
        get() {
          return isNextMonthButtonFromChargeDetailChartEnabled(this.activeMonthIdx, this.billList.length)
        }
      },
      allVendors : function (){
        let curCmpnId = this.$store.state.loginUser.curCmpnId;
        let vendorInfo = this.$store.state.vendorInfo;
        let availableVendors;

        if(curCmpnId && vendorInfo && vendorInfo.length > 0){
          availableVendors = DEFAULT_VENDOR_OPTIONS.filter(option => vendorInfo.includes(option.value) && !option.disabled);

          if(curCmpnId == '6d85d2b3-e252-43c2-a2ff-6dd36429f2e3' && this.$store.state.loginUser.siteCd == 'HCMP'){
            availableVendors.push(DEFAULT_VENDOR_OPTIONS[7]);
          }

          if(availableVendors.length == 1){
            this.$store.dispatch('billing/setSelectedVendor', availableVendors[0].value);
          }else if(availableVendors.length == 0){
            this.$store.dispatch('billing/setSelectedVendor', ' ');
          }
          return availableVendors;
        }else{
          return [];
        }
      }
    },
    watch: {
      billList: function () {
        this.reloadBillingData();
      },
      selectedVendor: function () {
        this.reloadBillList();
        this.reloadChargeList();
      }
    },
    created() {
      this.$store.dispatch('common/getAvailableVendors');
      let defaultVendor;

      if( !_isEmpty(this.$route.query.selectedVendor)){ //URL로 벤더 정보가 넘어올 경우 해당 벤터를 조회 하도록 한다. ex) /billing?selectedVendor=gcp
        DEFAULT_VENDOR_OPTIONS.forEach( v =>{
            if(!v.disabled && v.value == this.$route.query.selectedVendor.toUpperCase()){
              this.$store.dispatch('billing/setSelectedVendor', v.value);
            }
          }
        )
      }

      if(_isEmpty(this.selectedVendor)) {
        defaultVendor = this.allVendors.includes(VENDOR_NAMES.AWS)? VENDOR_NAMES.AWS : this.allVendors[0];
        this.$store.dispatch('billing/setSelectedVendor', defaultVendor);
      }
      this.reloadBillList();
      this.currentStep = typeof getValueFromStorageByKey(LOCAL_STORAGE_KEY.BILLING_CURRENT_STEP) === 'number' ? parseInt(getValueFromStorageByKey(LOCAL_STORAGE_KEY.BILLING_CURRENT_STEP)) : -1;
      if (Object.values(BILLING_STEP).some(step => this.currentStep === step)) {
        this.currentStep = 5;
        // this.welcomePopup.canShow = true;
        return
      }
      // this.welcomePopup.canShow = (this.currentStep === -1);
    },
    mounted() {
      let defaultVendor = this.allVendors.includes(VENDOR_NAMES.AWS) ? VENDOR_NAMES.AWS : this.allVendors[0];
      if(_isEmpty(this.selectedVendor)) {
        this.$store.dispatch('billing/setSelectedVendor', defaultVendor);
      }

    },
    methods: {
      delayDisplayText() {
        let $vm = this;
        setTimeout(function () {
          $vm.backToClassicalVersionContent = $vm.$t('onboarding.survey.switchToClassicVersion');
        }, 150);
      },
      setSidebarActive(isSidebarActive) {
        this.isSidebarActive = isSidebarActive;
        this.$emit('toggleSidebar', isSidebarActive)
      },
      openSurveyModal() {
        if(this.submittedSurvey) {
          this.$refs.billingSurveyModal.redirectToClassicalVersion();
          return;
        }
        this.$refs.billingSurveyModal.show();
      },
      // myCustomStopCallback() {
      //   if (this.canShowWelcomePopup) {
      //     this.currentStep = 6;
      //     this.welcomePopup.canShow = true;
      //     this.canShowWelcomePopup = false;
      //     return;
      //   }
      //   this.currentStep = this.currentStep + 1;
      //   setValueToStorageByKey(LOCAL_STORAGE_KEY.BILLING_CURRENT_STEP, this.currentStep);
      // },
      showHotspot(selector) {
        const hotspotIndex = this.steps.findIndex(function(step) {
          return step.target === selector;
        });
        this.$tours['billingTour'].start(hotspotIndex);
      },
      setActiveMonthIdx(activeMonthIdx) {
        this.selectedMonthYear = getSelectedMonthYear(activeMonthIdx, this.billList)

        this.$store.dispatch('billing/setActiveMonthIdx', activeMonthIdx);
        this.$store.dispatch('billing/setFirstMonthIdx', getFirstMonthIdx(activeMonthIdx, this.billList.length, true));
        this.$store.dispatch('billing/setLastMonthIdx', getLastMonthIdx(activeMonthIdx, this.billList.length));
      },
      setSelectedVendor(activeVendor) {
        this.$store.dispatch('billing/setSelectedVendor', activeVendor);
      },
      onClickPrev() {
        const currentFirstMonthIdx = this.firstMonthIdx;
        const currentLastMonthIdx = this.lastMonthIdx;
        this.$store.dispatch('billing/setFirstMonthIdx', setFirstMonthIdxAfterClickPrev(currentFirstMonthIdx, currentLastMonthIdx));
        this.$store.dispatch('billing/setLastMonthIdx', setLastMonthIdxAfterClickPrev(currentFirstMonthIdx, currentLastMonthIdx));
      },
      onClickNext() {
        const currentFirstMonthIdx = this.firstMonthIdx;
        const currentLastMonthIdx = this.lastMonthIdx;
        this.$store.dispatch('billing/setFirstMonthIdx', setFirstMonthIdxAfterClickNext(currentFirstMonthIdx, currentLastMonthIdx));
        this.$store.dispatch('billing/setLastMonthIdx', setLastMonthIdxAfterClickNext(currentFirstMonthIdx, currentLastMonthIdx));
      },
      reloadBillingData() {
        this.selectedMonthYear = getSelectedMonthYear(this.billList.length - 1, this.billList);
      },
      reloadBillList(params) {
        let payload = {
          selectedVendor : this.selectedVendor == null ? 'AWS' : this.selectedVendor,
          defaultVendor : this.allVendors[0] || 'AWS'
        };
        if(params != null) {
          payload = Object.assign(payload, params);
        }

        this.$store.dispatch('billing/getBillList', payload);
      },
      reloadChargeList(params) {
        let payload = {
          selectedVendor : this.selectedVendor == null ? 'AWS' : this.selectedVendor,
          defaultVendor : this.allVendors[0] || 'AWS'
        };
        if(params != null) {
          payload = Object.assign(payload, params);
        }

        this.$store.dispatch('billing/getChargeList', payload);
      },
      onChangeUserFilter(userFilterIdx) {
        this.$router.push({
          name: ROUTE_NAME.COST_ANALYTICS,
          params: {
            actionType: COST_ANALYTICS_ACTION_TYPE.SEARCH_BAR,
            group: SEARCH_BAR_RESULT_GROUP.SAVED_FILTER,
            userFilterIdx: userFilterIdx
          }
        });
      },
      onClickWelcomePopupDontShow() {
        this.currentStep = 5;
        setValueToStorageByKey(LOCAL_STORAGE_KEY.BILLING_CURRENT_STEP, this.currentStep);
        this.welcomePopup.canShow = false
      },
      onClickWelcomePopupLetsDoIt() {
        this.currentStep = 0;
        this.welcomePopup.canShow = false
      },
      onClickSkipFowNow() {
        this.welcomePopup.canShow = false;
      },
      onSelectNewUpdateOption(newUpdateOption) {
        this.welcomePopup.canShow = false;
        // this.canShowWelcomePopup = true;
        switch (newUpdateOption) {
          case BILLING_NEW_UPDATE.BILL_SUMMARY: {
            this.currentStep = 0;
            document.getElementById('bill-summary-hotspot').click()
            break;
          }
          case BILLING_NEW_UPDATE.CHARGE_LIST: {
            this.currentStep = 1;
            document.getElementById('charge-list-hotspot').click()
            break;
          }
          case BILLING_NEW_UPDATE.CLOUD_INVOICE_LIST: {
            this.currentStep = 2;
            document.getElementById('invoice-list-hotspot').click()
            break;
          }
          case BILLING_NEW_UPDATE.CLOUD_BILL_DETAIL: {
            this.currentStep = 3;
            document.getElementById('cloud-bill-detail-hotspot').click()
            break;
          }
          case BILLING_NEW_UPDATE.CLOUD_INVOICE_INSIGHT: {
            this.currentStep = 4;
            document.getElementById('invoice-insight-hotspot').click()
            break;
          }
        }
      },
      onClickBackFromChargeDetail() {
        this.isBillingChargeDetailVisible = false;
      },
      onClickChargeListCell(params) {
        // currently support linkedAccountId only
        /* back_up
        if (params.column.colId !== 'linkedAccountId') {
          return;
        }*/
        if (params.column.columnKey !== 'linkedAccountId'){
          return ;
        }
        this.isBillingChargeDetailVisible = true;
        this.selectedLinkedAccountId = params.data.linkedAccountId;
      },
      onClickPrevMonthFromChargeDetailChart(activeMonthIdx) {
        this.setActiveMonthIdx(activeMonthIdx - 1);
      },
      onClickNextMonthFromChargeDetailChart(activeMonthIdx) {
        this.setActiveMonthIdx(activeMonthIdx + 1)
      },
      onSelectMonthFromChargeDetail(selectedMonthIdx) {
        this.setActiveMonthIdx(selectedMonthIdx);
      }
    }
  };
  function isEnabledPrevMonthButton(firstMonthIdx) {
    return firstMonthIdx > 0;
  }

  function isEnabledNextMonthButton(lastMonthIdx, billListLength) {
    return lastMonthIdx < billListLength - 1;
  }
  function isPrevMonthButtonFromChargeDetailChartEnabled(activeMonthIdx) {
    return activeMonthIdx > 0;
  }

  function isNextMonthButtonFromChargeDetailChartEnabled(activeMonthIdx, billListLength) {
    return activeMonthIdx < billListLength - 1;
  }
</script>
<style lang="scss" >
  @import "../../../assets/css/base/var";
  @import "../../../assets/css/base/function";
  #billing-detail-table-download-btn,
  #monthly-bill-trend-download-btn {
    padding-right: 0 !important;
    padding-left: 0 !important;
    margin-top: 3px;
    margin-right: 18px;
  }
  .tooltip {
    .tooltip-management {
      top: 100px !important;
      left: 11px!important;
    }
  }
  .add-menu-sidebar {
    position: relative;
  }
  #wrapper {
    .billing-main {
      margin-top: 20px;
    }
    &.wrapper-billing-layout {
      padding: 0 0.625rem 0 0;
      max-width: 1920px;
      padding-bottom: 0px!important;
      min-height: calc(100vh - 72px);
      .screen-notification-no-data {
        .material-icons {
          &.color-yellow-1 {
            display: flex;
            align-items: center;
          }
        }
      }
      .left-menu-main {
        display: flex;
        justify-content: flex-start!important;
        width: 100%;
        .main-left {
          display: flex;
          align-items: flex-start;
          .main-left-1 {
            width: 50px;
            height: auto;
          }

          .main-left-2 {
            background: #fff;
            width: 240px;
            height: 100vh;
            padding: 0px 12px;
            display: none;

            &.active {
              display: block;
            }
          }
        }
        .main-right {
          width: calc(100% - 51px);
          display: flex;
        }
        &.active {
          .main-right {
            width: calc(100% - 290px);
            display: flex;
          }
        }
      }
      .billing-sidebar {
        width: 340px;
      }
      .billing-content {
        width: calc(100% - 340px);
      }
    }
  }
  @media only screen and (min-width: 1921px) {
    #wrapper {
      &.wrapper-billing-layout {
        max-width: 1920px;
        float: left;
      }
    }
  }
  @media only screen and (max-width: 1024px) {
    #wrapper {
      &.wrapper-billing-layout {
        /*min-width: 100%;*/
        .left-menu-main {
          position: relative;
          &.active {
            &:before {
              content: '';
              top: 41px;
              left: 290px;
              width: 100%;
              height: 100vh;
              background: #000;
              z-index: 9999999;
              opacity: 0.2;
              position: fixed;
            }
          }
          .main-left {
            display: none;
          }
          .main-right {
            width: calc(100% - 51px) !important;
            margin-left: 50px;
          }
        }
      }
    }
  }

  @media only screen and (max-width: 1279px) {
    #wrapper {
      position: relative;
      &.wrapper-billing-layout {
        min-width: 1279px;
        padding-right: 0px;
        #header {
          position: fixed;
        }
        #title {
          position: absolute;
        }
      }
    }
  }
</style>
