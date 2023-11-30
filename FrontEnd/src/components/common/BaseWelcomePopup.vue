<template>
  <div
    v-show="canShow"
    id="welcome-wrapper"
    class="font-family-notosanscjkkr-medium"
  >
    <b-row
      class="welcome-content font-family-notosanscjkkr-regular"
      no-gutters
    >
      <div
        class="welcome-main-left"
      >
        <p
          class="welcome-title"
          v-html="welcomeTitle"/>
        <p v-html="$t('onboarding.welcome.welcomeContent', {'serviceName': serviceNameForContent})"/>
        <br>
        <p
          v-html="$t('onboarding.welcome.awsAvailablePrepareForOthers', {'serviceName': serviceNameForAvailableVendors, 'availableVendors': availableVendorsText})"/>
        <div class="image-bit-map"/>
      </div>
      <div
        class="welcome-main-right"
      >
        <p class="new-updates-text">{{ $t('onboarding.welcome.newUpdatesText') }}</p>
        <ul
          v-for="(option, i) in updateList"
          :key="i"
          class="welcome-new-updates"
        >
          <li
            class="new-update-item"
            @click="onClickNewUpdatedOption(option)"
          >
            <base-material
              :size="17"
              name="bookmark"
              class="bookmark-icon"
              color="#1155cb"/>
            <span>{{ isLocalizationEnabled ? $t(option.text) : option.text }}</span>
          </li>
        </ul>
      </div>
    </b-row>
    <div class="welcome-wrapper-footer">
      <b-button
        class="close-button text-gray-1"
        variant="transparent"
        @click="onClickDontShowItAgain"
      >
        {{ $t('onboarding.welcome.buttonText.dontShowItAgain') }}
      </b-button>
      <b-button
        class="do-it-button"
        @click="onClickLetsDoIt"
      >
        <p>{{ $t('onboarding.welcome.buttonText.gotIt') }}</p>
      </b-button>
    </div>
  </div>
</template>

<script>
  import {CLASSIC_VERSION_PAGE, VENDOR} from "@/constants/constants";
  import { TAB_INDEX, BILLING_NEW_UPDATE, AVAILABLE_VENDORS_IN_BILLING_INVOICE } from "@/constants/billingConstants";
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from "../../constants/costAnalyticsConstants";

  const FULL_PATH = {
    BILLING: '/billing',
    DASHBOARD: '/dashboard',
    COST_ANALYTICS: '/cost-analytics',
    SAVINGS_PLANS: '/savings-plans'
  }

  export default {
    name: "BaseWelcomePopup",
    props: {
      canShow: {
        type: Boolean,
        default: true
      },
      pageName: {
        type: String,
        required: true,
        default: null
      },
      newUpdateOptions: {
        type: Array,
        default() {
          return []
        }
      },
      isLocalizationEnabled: {
        type: Boolean,
        required: false,
        default: false
      },
      allVendors: {
        type: Array,
        default() {
          return []
        }
      },
      availableVendor: {
        type: Array,
        default() {
          return []
        }
      }
    },
    data() {
      return {
        updateList: [],
        availableVendorsText: '',
        serviceNameForContent: '',
        serviceNameForAvailableVendors: '',
        isKor: this.$i18n.locale == "ko" ? true : false
      }
    },
    computed: {
      welcomeTitle() {
        switch (this.pageName) {
          case CLASSIC_VERSION_PAGE.BILLING:
            return this.$t('onboarding.welcome.welcomeTitle.billing')
          case CLASSIC_VERSION_PAGE.DASHBOARD:
            return this.$t('onboarding.welcome.welcomeTitle.dashboard')
          case CLASSIC_VERSION_PAGE.COST_ANALYTICS:
            return this.$t('onboarding.welcome.welcomeTitle.costAnalytics')
          case CLASSIC_VERSION_PAGE.SAVINGS_PLANS:
            return this.$t('onboarding.welcome.welcomeTitle.savingsPlans')
        }
      },
    },
    watch: {
      canShow: {
        handler() {
          if (this.canShow) {
            this.disableScroll()
          } else {
            this.enableScroll()
          }
        },
        immediate: true
      },
      allVendors: {
        handler() {
          if(_.isEmpty(this.allVendors) || !_.isEqual(this.pageName, CLASSIC_VERSION_PAGE.BILLING)) {
            return;
          }
          this.setBillingInvoiceUpdateList();
        }
      },
      availableVendor: {
        handler() {
          if(_.isEmpty(this.availableVendor)) {
            return;
          }
          this.availableVendorsText = this.setBillingInvoiceAvailableVendors();
        }
      },
      pageName: {
        handler() {
          switch(this.pageName) {
            case CLASSIC_VERSION_PAGE.BILLING:
              this.serviceNameForContent = this.$t('header.billingTitle') + (this.isKor ? "를" : "");
              this.serviceNameForAvailableVendors = this.$t('header.billingTitle') + (this.isKor ? "는" : "");
              this.availableVendorsText = this.setBillingInvoiceAvailableVendors();
              break;
            case CLASSIC_VERSION_PAGE.COST_ANALYTICS:
            default:
              this.serviceNameForContent = this.$t('pageTitle.costAnalytics') + (this.isKor ? "을" : "");
              this.serviceNameForAvailableVendors = this.$t('pageTitle.costAnalytics') + (this.isKor ? "은" : "");
              this.availableVendorsText = this.setCostAnalyticsAvailableVendors();
              this.updateList = this.newUpdateOptions ;
              break;
          }
        },
        immediate: true
      }
    },
    methods: {
      setCostAnalyticsAvailableVendors() {
        let vendors = '';
        COST_ANALYTICS_VIEW_BY_VENDORS.filter(vendor => {
          if(_.isEqual(this.profile.env, "CHINA")) {
           return  vendor.value != VENDOR.GCP;
          }
          else return true;
        }).map(v => v.value)
          .forEach(item => vendors += `${item}, `);
        return vendors.substr(0, vendors.length-2);
      },
      setBillingInvoiceAvailableVendors() {
        let vendors = '';
        let vendorList = this.availableVendor.map( vendor => vendor.mspVndrId.toUpperCase());           // portal에서 받아온 클라우드 계정 벤더 데이터 대문자화
        vendorList = _.remove(vendorList, function(vendor) {                                   // 빌링 인보이스에서 지원하지 않는 벤더 제거
          return Object.keys(AVAILABLE_VENDORS_IN_BILLING_INVOICE).includes(vendor);
        })
        vendorList.forEach(vendor => vendors += `${AVAILABLE_VENDORS_IN_BILLING_INVOICE[vendor]}, `)

        return vendors.substr(0, vendors.length-2);
      },
      setBillingInvoiceUpdateList() {
        let max = []
        let list = [];
        this.allVendors.forEach(item => {
          for (let key in TAB_INDEX[item.value]) {
            max.push(key)
          }
        });

        list.push(this.newUpdateOptions[0])
        Array.from(new Set(max)).forEach(item => {
          if (this.newUpdateOptions.find(row => row.value === BILLING_NEW_UPDATE[item])) list.push(this.newUpdateOptions.find(row => row.value === BILLING_NEW_UPDATE[item]))
        })
        this.updateList = list;
      },
      disableScroll() {
        this.$nextTick(()=> {
          let scrollTop = window.pageYOffset || document.documentElement.scrollTop;
          let scrollLeft = window.pageXOffset || document.documentElement.scrollLeft;
          switch (this.$route.fullPath) {
            case FULL_PATH.COST_ANALYTICS: {
              document.querySelector('.cost-analytics-page').onscroll = function() {
                document.querySelector('.cost-analytics-page').scrollTo(scrollLeft, scrollTop);
              };
              break;
            }
            case FULL_PATH.DASHBOARD: {
              document.querySelector('.dashboard-page').onscroll = function() {
                document.querySelector('.dashboard-page').scrollTo(scrollLeft, scrollTop);
              };
              break;
            }
          }
        })
      },
      enableScroll() {
        this.$nextTick(()=> {
          switch (this.$route.fullPath) {
            case FULL_PATH.COST_ANALYTICS: {
              document.querySelector('.cost-analytics-page').onscroll = function() {};
              break;
            }
            case FULL_PATH.DASHBOARD: {
              document.querySelector('.dashboard-page').onscroll = function() {};
              break;
            }
          }
        })
      },
      onClickDontShowItAgain() {
        this.$emit('clickDontShow')
      },
      onClickLetsDoIt() {
        this.$emit('clickLetsDoIt')
      },
      onClickSkipFowNow() {
        this.$emit('clickSkipFowNow')
      },
      onClickNewUpdatedOption: function (selectedOption) {
        this.$emit('selectNewUpdateOption', selectedOption.value)
      },
    }
  }
</script>

<style lang="scss">
  .stop-scrolling {
    height: 100%;
    overflow: hidden;
  }
  #welcome-wrapper {
    position: fixed;
    width: 660px;
    top: 50%;
    right: 50%;
    transform: translate(50%, -50%);
    background-color: #ffffff;
    color: #ffffff;
    z-index: 999999;
    -webkit-box-shadow: 0 3px 12px 2px rgba(124, 129, 148, 0.25);
    -moz-box-shadow: 0 3px 12px 2px rgba(124, 129, 148, 0.25);
    box-shadow: 0 3px 12px 2px rgba(124, 129, 148, 0.25);
    .welcome-content {
      height: 368px;
      color: #222222;
      border-bottom: 1px solid rgba(124, 129, 148, 0.25);
      .welcome-main-left {
        width: 380px;
        color: #7b8088;
        padding: 38px 40px 0px 30px;
        position: relative;
        .welcome-title {
          font-size: 22px;
          margin-bottom: 16px;
          color: #222222;
          font-family: NotoSansCJKkr-Medium;
        }
        .image-bit-map {
          position: absolute;
          bottom: 0;
          right: 38px;
          display: inline-block;
          width: 115px;
          height: 96px;
          background-repeat: no-repeat !important;
          background-size: 100% 100% !important;
          background-image: url("../../assets/images/bitmap@2x.png");
        }
      }
      .welcome-main-right {
        width: 280px;
        padding: 40px 13px 0px 24px;
        background-color: #f5f6fa;
        .new-updates-text {
          color: #7b8088;
          margin-left: 8px;
          margin-bottom: 16px;
        }
        .welcome-new-updates {
          font-size: 14px;
          color: #1155cb;
          font-family: NotoSansCJKkr-Medium;
          margin: 0 !important;
          .new-update-item {
            display: inline-flex;
            margin-bottom: 12px;
            padding: 4px 0;
            width: 100%;
            cursor: pointer;
            &:hover {
              background-color: #e9ebf5;
              border-radius: 4px;
            }
            .bookmark-icon {
              padding: 2.5px 5.3px 0px 9.3px;
            }
          }
        }
      }
    }
    .welcome-wrapper-footer {
      padding: 20px 20px 20px 35.1px;
      .close-button {
        height: 32px;
        &:focus {
          box-shadow: none !important;
        }
      }
      .skip-for-now-button {
        float: right;
        height: 32px;
        margin-right: 30px;
        &:focus {
          box-shadow: none !important;
          border: none !important;
          outline: none !important;
        }
      }
      .do-it-button {
        float: right;
        width: 69px;
        background-color: #0672ff;
        border-color: #ffffff;
        border-radius: 4px;
        //horizontal-align: center;
        vertical-align: center;
        &:focus {
          box-shadow: none;
        }
        p {
          margin-left : auto;
          margin-right : auto;
        }
      }
    }
  }
</style>
