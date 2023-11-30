<template>
  <div
    v-show="canShow"
    :class="{'font-family-notosanscjkkr-medium welcome-wrapper-ko': lang==='ko', 'font-family-notosanscjkkr-medium welcome-wrapper-en': lang==='en' || lang==='zh'}"
  >
    <b-row
      class="welcome-content font-family-notosanscjkkr-regular"
      no-gutters
    >
      <div
        class="welcome-main-content"
      >
        <p
          class="welcome-title"
          v-html="$t('onboarding.welcome.welcomeTitle.anomalyAlarm')"/>
        <p
          class="welcome-title2"
          v-html="$t('onboarding.welcome.welcomeTitle.anomalyAlarm2')"/>

        <template
          v-if="($store.state.loginUser.siteCd !== null) && ($store.state.loginUser.siteCd !== 'SBCK')"
        >
          <div>
            <base-icon
              :original="true"
              class="welcome-sub-title"
              width="18"
              height="18"
              name="icon_ai_blue" />
            <p
              class="welcome-sub-title"
              v-html="$t('onboarding.welcome.anomalyAlarm.subtitle.ai')"/>
            <p
              class="welcome-sub-content"
              v-html="$t('onboarding.welcome.anomalyAlarm.aiDescription')"/>
            <br>
            <p
              class="welcome-sub-content"
              v-html="$t('onboarding.welcome.anomalyAlarm.aiContent')"/>
          </div>
          <br>
        </template>
        <div>
          <base-material
            :size="20"
            class="welcome-sub-title"
            color="blue-1"
            name="person"
          />
          <p
            class="welcome-sub-title"
            v-html="$t('onboarding.welcome.anomalyAlarm.subtitle.userRule')"
          />
          <p
            class="welcome-sub-content"
            v-html="$t('onboarding.welcome.anomalyAlarm.userRuleDescription')"/>
          <br>
          <p
            class="welcome-sub-content"
            v-html="$t('onboarding.welcome.anomalyAlarm.userRuleContent')"/>
        </div>
        <div class="image-abnormal-welcome"/>
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
        {{ $t('onboarding.welcome.buttonText.gotIt') }}
      </b-button>
    </div>
  </div>
</template>

<script>
  import {CLASSIC_VERSION_PAGE} from "@/constants/constants";

  const FULL_PATH = {
    BILLING: '/billing',
    DASHBOARD: '/dashboard',
    COST_ANALYTICS: '/cost-analytics'
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
    },
    data() {
      return {
        lang: null
      }
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
      }
    },
    created() {
      this.lang = this.$i18n._vm.locale;
    },
    methods: {
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
  .welcome-wrapper-ko {
    position: fixed;
    width: 620px;
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
      height: 460px;
      color: #222222;
      background-color: #EFF8FB;
      border-bottom: 1px solid rgba(124, 129, 148, 0.25);
      .welcome-main-content {
        width: 620px;
        color: #7b8088;
        padding: 30px 40px 0px 30px;
        position: relative;
        .welcome-title {
          font-size: 24px;
          margin-bottom: 4px !important;
          color: #222222;
          font-family: NotoSansCJKkr-Medium;
        }
        .welcome-title2 {
          font-size: 20px;
          margin-bottom: 16px;
          color: #222222;
        }
        .welcome-sub-title {
          color: #0672FF;
          font-weight: 900;
          float: left;
        }
        .welcome-sub-content {
          clear:both;
        }
        .image-abnormal-welcome {
          position: absolute;
          bottom: 0;
          right: 38px;
          display: inline-block;
          width: 180px;
          height: 180px;
          background-repeat: no-repeat !important;
          background-size: 100% 100% !important;
          background-image: url("../../assets/images/abnormalWelcome.png");
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
        padding-left: 0px !important;
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
        justify-content: center;
        background-color: #0672ff;
        border-color: #ffffff;
        border-radius: 4px;
        /*padding-left: 16px !important;*/
        &:focus {
          box-shadow: none;
        }
      }
    }
  }
  .welcome-wrapper-en {
    position: fixed;
    width: 750px;
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
      height: 460px;
      color: #222222;
      background-color: #EFF8FB;
      border-bottom: 1px solid rgba(124, 129, 148, 0.25);
      .welcome-main-content {
        width: 750px;
        color: #7b8088;
        padding: 30px 40px 0px 30px;
        position: relative;
        .welcome-title {
          width: 660px;
          font-size: 24px;
          margin-bottom: 4px !important;
          color: #222222;
          font-family: NotoSansCJKkr-Medium;
        }
        .welcome-title2 {
          width: 750px;
          font-size: 20px;
          margin-bottom: 16px;
          color: #222222;
        }
        .welcome-sub-title {
          color: #0672FF;
          font-weight: 900;
          float: left;
        }
        .welcome-sub-content {
          clear:both;
        }
        .image-abnormal-welcome {
          position: absolute;
          bottom: 0;
          right: 38px;
          display: inline-block;
          width: 180px;
          height: 180px;
          background-repeat: no-repeat !important;
          background-size: 100% 100% !important;
          background-image: url("../../assets/images/abnormalWelcome.png");
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
        padding-left: 0px !important;
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
        justify-content: center;
        background-color: #0672ff;
        border-color: #ffffff;
        border-radius: 4px;
        /*padding-left: 16px !important;*/
        &:focus {
          box-shadow: none;
        }
      }
    }
  }
</style>
