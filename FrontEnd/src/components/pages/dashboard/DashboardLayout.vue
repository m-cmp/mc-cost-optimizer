<template>
  <!-- fluid of b-container property full width -->
  <b-container
    id="wrapper"
    class="wrapper-dashboard"
    fluid>
    <DashboardHeader
      :dashboard-data="dashboardData"
      :common-user-info="commonUserInfo"
      :current-dashboard="currentDashboard"
      :dashboard-view-mode="dashboardViewMode"
      :all-vendors="allVendors"
      :exchange-rate="exchangeRate"
      :is-sidebar-active="isSidebarActive"
      :widget-loading-state="widgetLoadingState"
      @selectVendors="onSelectVendors"
      @selectCurrency="onSelectCurrency"
      @selectDashboard="onSelectDashboard"
      @deleteDashboard="onDeleteDashboard"
      @clickCopyDashboard="onClickCopyDashboard"
      @clickEditDashboard="onClickEditDashboard"
      @cancelDashboard="onCancelDashboard"
      @saveDashboard="onSaveDashboard"
      @saveDashboardCopy="onSaveDashboardCopy"
      @submitSaveAs="onSubmitSaveAs"
      @addWidget="onAddWidget"
      @changeUserFilter="onChangeUserFilter"
      @showHotspot="showHotspot"
    />
    <!--    <v-tour-->
    <!--      :steps="steps"-->
    <!--      :callbacks="customCallBacks"-->
    <!--      name="dashboardTour">-->
    <!--      <template slot-scope="tour">-->
    <!--        <transition name="fade">-->
    <!--          <v-step-->
    <!--            v-for="(step, index) of tour.steps"-->
    <!--            v-if="tour.currentStep === index"-->
    <!--            :key="index"-->
    <!--            :step="step"-->
    <!--            :previous-step="tour.previousStep"-->
    <!--            :next-step="tour.nextStep"-->
    <!--            :stop="tour.stop"-->
    <!--            :is-first="tour.isFirst"-->
    <!--            :is-last="tour.isLast"-->
    <!--            :labels="tour.labels"-->
    <!--          >-->
    <!--            <template>-->
    <!--              <div slot="actions">-->
    <!--                <button-->
    <!--                  class="got-it-btn"-->
    <!--                  @click="tour.stop">{{ $t('dashboard.tourSteps.gotIt') }}</button>-->
    <!--              </div>-->
    <!--            </template>-->
    <!--          </v-step>-->
    <!--        </transition>-->
    <!--      </template>-->
    <!--    </v-tour>-->
    <LastUpdated
      :dashboard-view-mode="dashboardViewMode"
      :batch-time="batchTime"
    />
    <b-row
      class="mt-20"
      no-gutters/>
    <BaseLeftMenu @setSidebarActive="setSidebarActive">
      <template v-slot:mainRight>
        <DashboardWidgets
          v-if="hasAccount"
          :widgets="currentDashboard.widgets"
          :common-user-info="commonUserInfo"
          :exchange-rate="exchangeRate"
          :cost-month-to-date="costMonthToDate"
          :budget-data="budgetData"
          :total-saving="totalSaving"
          :estimated-cost="estimatedCost"
          :dashboard-view-mode="dashboardViewMode"
          :compare-cost-trend-guide-index="compareCostTrendGuideIndex"
          :current-dashboard="currentDashboard"
          :widget-loading-state="widgetLoadingState"
          :browser="browser"
          :is-sidebar-active="isSidebarActive"
          :all-vendors="allVendors"
          @deleteWidget="onDeleteWidget"
          @duplicateWidget="onDuplicateWidget"
          @saveWidget="onSaveWidget"
          @clickToAnalyze="onClickToAnalyze"
          @showHotspot="showHotspot"
        />
        <BaseNoAccount v-else/>
        <div id="dashboard-bottom" />
      </template>
    </BaseLeftMenu>
  </b-container>
</template>

<script>
  import _isEqual from 'lodash/isEqual'
  import _cloneDeep from 'lodash/cloneDeep';
  import _isEmpty from 'lodash/isEmpty'
  import _get from 'lodash/get';
  import _size from 'lodash/size';
  import _isNil from 'lodash/isNil';
  import {mapGetters} from 'vuex';
  import {
    DEFAULT_DASHBOARD,
    DEFAULT_DASHBOARD_INDEX,
    SAVE_WIDGET_REQUEST_MODEL,
    STATIC_WIDGETS,
    VIEW_MODE,
    DASHBOARD_WIDGET_TYPE,
    DASHBOARD_NEW_UPDATE,
  } from '@/constants/dashboardConstants';
  import {
    generateNameForCopyOfDefaultDashboard,
    getStandardizedWidgetsForRender,
    getStandardizedWidgetsForSave,
    getWidgetConfigsAfterDuplicated,
    getWidgetConfigWithFieldsInEditFormOnly,
  } from '@/util/dashboardUtils';
  import { getValueFromStorageByKey, setValueToStorageByKey, LOCAL_STORAGE_KEY } from '@/util/localStorage';
  const BaseNoAccount = () => import('@/components/pages/dashboard/no-account/BaseNoAccount');
  import {COST_ANALYTICS_ACTION_TYPE, CLASSIC_VERSION_PAGE, ROUTE_NAME, SEARCH_BAR_RESULT_GROUP} from "@/constants/constants";
  import BaseLeftMenu from '@/components/common/base-left-menu/BaseLeftMenu';

  const DashboardHeader = () => import('@/components/pages/dashboard/dashboard-header/DashboardHeader');
  const DashboardWidgets = () => import('@/components/pages/dashboard/DashboardWidgets');
  const LastUpdated = () => import('@/components/pages/dashboard/last-updated/LastUpdated');

  // don't know why it finishes scrolling in less than the duration provided
  const SCROLL_TO_NEW_WIDGET_DURATION = 1000;
  const FIRST_TIME_RENDER_TIMEOUT_OF_HEAVY_WIDGET = SCROLL_TO_NEW_WIDGET_DURATION * 3 / 4;
  const FIRST_TIME_RENDER_TIMEOUT_OF_LIGHT_WIDGET = 200;
  // const DASHBOARD_STEP = {
  //   EDIT_WIDGET: 0,
  //   MULTIPLE_DASHBOARD: 1,
  //   CREATE_DASHBOARD: 2,
  //   ADD_WIDGET: 3,
  //   EDIT_DUPLICATE_MOVE_DELETE: 4
  // }

  export default {
    name: 'DashboardLayout',
    components: {
      BaseNoAccount,
      LastUpdated,
      DashboardHeader,
      DashboardWidgets,
      BaseLeftMenu
    },
    inheritAttrs: false,
    props: {
    },
    data() {
      return {
        // this is current dashboard local state. Changes while in edit mode will be written directly to this.
        isSidebarActive: false,
        sharingClicked: false,
        toShareDashboardIndex: -1,
        currentDashboard: DEFAULT_DASHBOARD,
        VIEW_MODE: VIEW_MODE,
        // steps: [
        //   {
        //     target: '#edit-widget',
        //     content: this.$t('dashboard.tourSteps.content.editWidget'),
        //     header: {
        //       title : this.$t('dashboard.tourSteps.headerTitle.editWidget')
        //     },
        //     params: {
        //       enableScrolling: false
        //     }
        //   },
        //   {
        //     target: '#dashboard-template-selection',
        //     content: this.$t('dashboard.tourSteps.content.dashboardTemplateSelection'),
        //     header: {
        //       title : this.$t('dashboard.tourSteps.headerTitle.dashboardTemplateSelection')
        //     },
        //     params: {
        //       enableScrolling: false
        //     }
        //   },
        //   {
        //     target: '#create-dashboard',
        //     content: this.$t('dashboard.tourSteps.content.createDashboard'),
        //     header: {
        //       title : this.$t('dashboard.tourSteps.headerTitle.createDashboard')
        //     },
        //     params: {
        //       enableScrolling: false
        //     }
        //   },
        //   {
        //     target: '#add-widget-guide',
        //     content: this.$t('dashboard.tourSteps.content.addWidget'),
        //     header: {
        //       title : this.$t('dashboard.tourSteps.headerTitle.addWidget')
        //     },
        //     params: {
        //       enableScrolling: false
        //     }
        //   },
        //   {
        //     target: '#customize-widget',
        //     content: this.$t('dashboard.tourSteps.content.customizeWidget'),
        //     header: {
        //       title : this.$t('dashboard.tourSteps.headerTitle.customizeWidget')
        //     },
        //     params: {
        //       enableScrolling: false
        //     }
        //   },
        // ],
        // currentStep: -1,
        // customCallBacks: {
        //   onStop: this.myCustomStopCallback,
        // },
        backToClassicalVersionContent: '',
        compareCostTrendGuideIndex: -1,
        CLASSIC_VERSION_PAGE:  CLASSIC_VERSION_PAGE,
        widgetLoadingState: {},
        canShowWelcomePopup: false,
        queryDataLoaded:false,
      }
    },
    computed: {
      ...mapGetters({
        costMonthToDate: 'dashboard/costMonthToDate',
        estimatedCost: 'dashboard/estimatedCost',
        budgetData:'dashboard/budgetData',
        totalSaving:'dashboard/totalSaving',
        dashboardData: 'dashboard/dashboardData',
        dashboardViewMode: 'dashboard/dashboardViewMode',
        batchTime: 'dashboard/batchTime',
        commonUserInfo: 'common/info',
        exchangeRate: 'common/exchangeRate',
        // allVendors: 'common/allVendors',
        // isCostMonthToDateLoading: 'dashboard/isCostMonthToDateLoading',
        // isEstimatedCostLoading: 'dashboard/isEstimatedCostLoading',
        // isBudgetDataLoading: 'dashboard/isBudgetDataLoading',
        // isTotalSavingLoading: 'dashboard/isTotalSavingLoading',
        submittedSurvey: 'common/submittedSurvey',
        browser: "common/browser"
      }),
      hasAccount: function () {
        // out of scope
        return true;
      },
      //이용가능한 벤더 리스트
      allVendors : function (){
        // return ['GCP' , 'AZURE'];
        let curCmpnId = this.$store.state.loginUser.curCmpnId;
        let vendorInfo = this.$store.state.vendorInfo;
        if(curCmpnId && vendorInfo &&vendorInfo.length > 0){
          return vendorInfo;
        }else{
          return [];
        }
      }
    },
    watch: {
      dashboardData: {
        handler() {
          let currentDashboard = this.dashboardData.find(dashboard => dashboard.isDashboardSelected);
          if(!this.queryDataLoaded){
            const queryDashboardIndex = this.$route.query.dashboardIndex;
            let queryCurrentDashboard = this.dashboardData.find(dashboard => dashboard.index == queryDashboardIndex);
            if(!_isEmpty(queryCurrentDashboard)){
              currentDashboard = queryCurrentDashboard;
            }
            this.queryDataLoaded = true;
          }
          this.currentDashboard = {
            ..._cloneDeep(currentDashboard),
            widgets: getStandardizedWidgetsForRender(currentDashboard.widgets)
          };
        },
        immediate: true
      },
      'commonUserInfo.selectedVendors': {
        handler() {
          let requestPayload = {
            vendors: this.commonUserInfo.selectedVendors
          }

          // CO-145 누적비용 위젯 벤더 선택기능 추가
          // this.$store.dispatch('dashboard/getCostMonthToDate', requestPayload);
          // CO-144 예상비용 위젯 벤더 선택기능 추가
          // this.$store.dispatch('dashboard/getEstimatedCost', requestPayload);
          //2020-05-15 budget & total saving widget is removed.
          // this.$store.dispatch('dashboard/getBudgetData', requestPayload);
          // this.$store.dispatch('dashboard/getTotalSaving', requestPayload);
        },
        immediate: false
      },
      'currentDashboard.widgets': {
        handler() {
          // this.getCompareCostTrendGuideIndex()
          // update data.widgetLoadingState if widgets.length changes (remove widget/add widget/change dashboard...)
          if (_size(this.currentDashboard.widgets) !== _size(this.widgetLoadingState)) {
            let newWidgetLoadingState = {
              // [DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET]: this.isCostMonthToDateLoading,
              // [DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET]: this.isEstimatedCostLoading,
              // [DASHBOARD_WIDGET_TYPE.DASHBOARD_CLOUD_BUDGET_WIDGET]: this.isBudgetDataLoading,
              // [DASHBOARD_WIDGET_TYPE.DASHBOARD_TOTAL_SAVING_WIDGET]: this.isTotalSavingLoading,
            };
            this.currentDashboard.widgets.forEach(widget => {
              // if (STATIC_WIDGETS.includes(widget.widgetType)) {
              //   return;
              // }
              newWidgetLoadingState[widget.index] = _isNil(this.widgetLoadingState[widget.index])
                ? true
                : this.widgetLoadingState[widget.index];
            });
            this.widgetLoadingState = newWidgetLoadingState;
          }
        },
        immediate: true
      },
      allVendors:{
        handler() {
          this.$store.dispatch('dashboard/getDashboardData')
          this.$nextTick(() => {
            // this.welcomePopup.canShow = typeof getValueFromStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_WELCOME_POPUP) === 'boolean' ? getValueFromStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_WELCOME_POPUP) : true;
            // this.currentStep = typeof getValueFromStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_CURRENT_STEP) === 'number' ? parseInt(getValueFromStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_CURRENT_STEP)) : -1;
            // if (Object.values(DASHBOARD_STEP).some(step => this.currentStep === step)) {
            //   this.currentStep = -1;
            //   this.welcomePopup.canShow = true;
            //   return
            // }
            // this.welcomePopup.canShow = (this.currentStep === -1);
          })
        },
        immediate: false
      }
      // to sync data.widgetLoadingState
      // isCostMonthToDateLoading: {
      //   handler() {
      //     this.widgetLoadingState[DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET] = this.isCostMonthToDateLoading;
      //   },
      //   immediate: true
      // },
      // to sync data.widgetLoadingState
      // isEstimatedCostLoading: {
      //   handler() {
      //     this.widgetLoadingState[DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET] = this.isEstimatedCostLoading;
      //   },
      //   immediate: true
      // },
      //these widget removed
      // // to sync data.widgetLoadingState
      // isBudgetDataLoading: {
      //   handler() {
      //     this.widgetLoadingState[DASHBOARD_WIDGET_TYPE.DASHBOARD_CLOUD_BUDGET_WIDGET] = this.isBudgetDataLoading;
      //   },
      //   immediate: true
      // },
      // // to sync data.widgetLoadingState
      // isTotalSavingLoading: {
      //   handler() {
      //     this.widgetLoadingState[DASHBOARD_WIDGET_TYPE.DASHBOARD_TOTAL_SAVING_WIDGET] = this.isTotalSavingLoading;
      //   },
      //   immediate: true
      // },
    },
    mounted() {
      console.log('hi')
    },
    created() {
      //Reset view mode to default when user come back dashboard page by left menu bar
      if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
        this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.DEFAULT);
      }
    },
    methods: {
      setSidebarActive(isSidebarActive) {
        this.isSidebarActive = isSidebarActive;
        this.$emit('toggleSidebar', isSidebarActive)
      },
      openSurveyModal() {
        if (this.submittedSurvey) {
          this.$refs.dashboardSurveyModal.redirectToClassicalVersion();
        }
        this.$refs.dashboardSurveyModal.show();
      },
      delayDisplayText() {
        let $vm = this;
        setTimeout(function () {
          $vm.backToClassicalVersionContent = $vm.$t('onboarding.survey.switchToClassicVersion');
        }, 150);
      },
      onSelectCurrency(selectedCurrency) {
        if (_isEqual(this.commonUserInfo.selectedCurrency, selectedCurrency)) {
          return;
        }
        this.$store.dispatch('common/selectCurrency', selectedCurrency);
      },
      onSelectVendors(selectedVendors) {
        if (_isEqual(this.commonUserInfo.selectedVendors, selectedVendors)) {
          return;
        }
        this.$store.dispatch('common/selectVendors', selectedVendors);
      },
      onDeleteWidget(deletedWidgetIndex) {
        this.currentDashboard.widgets = this.currentDashboard.widgets.filter(widget => widget.index !== deletedWidgetIndex);
      },
      onDuplicateWidget(duplicatedWidgetIndex) {
        let duplicatedWidget = _cloneDeep(this.currentDashboard.widgets.find(widget => widget.index === duplicatedWidgetIndex));
        this.currentDashboard.widgets = getWidgetConfigsAfterDuplicated(this.currentDashboard.widgets, duplicatedWidget);
      },
      onSaveWidget(submittedWidgetConfig) {
        const widgetArrayIdx = this.currentDashboard.widgets.findIndex(widget => widget.index === submittedWidgetConfig.index);
        if (this.dashboardViewMode === VIEW_MODE.DEFAULT) {
          let saveWidgetConfigPayload = {
            ...SAVE_WIDGET_REQUEST_MODEL,
            widget: submittedWidgetConfig,
            dashboardIndex: this.currentDashboard.index
          };
          this.$store.dispatch('dashboard/saveWidget', saveWidgetConfigPayload)
            .then(res => {
              this.currentDashboard.widgets[widgetArrayIdx].isEditFormVisible = false;
              let newSubmittedWidgetConfig = {
                ...submittedWidgetConfig,
                isEditFormVisible: false
              };
              // this.$set(this.currentDashboard.widgets, widgetArrayIdx, submittedWidgetConfig);
              this.$set(this.currentDashboard.widgets, widgetArrayIdx, newSubmittedWidgetConfig);
              const payload = {
                dashboardIndex: this.currentDashboard.index,
                widgetIndex: widgetArrayIdx,
                updatedWidget: submittedWidgetConfig
              }
              this.$store.dispatch('dashboard/syncUpdatedDashboardWidget', payload);
            });
        } else {
          this.currentDashboard.widgets[widgetArrayIdx].isEditFormVisible = false;
          const newWidgetInEditMode = {
            ...this.currentDashboard.widgets[widgetArrayIdx],
            ...getWidgetConfigWithFieldsInEditFormOnly(submittedWidgetConfig)
          };
          this.$set(this.currentDashboard.widgets, widgetArrayIdx, newWidgetInEditMode);
        }
      },
      onSelectDashboard(selectedDashboardIndex) {
        this.$store.dispatch('dashboard/selectDashboard', selectedDashboardIndex);
        let selectedDashboard = this.findOriginalDashboardByIndex(selectedDashboardIndex);
        this.currentDashboard = {
          ..._cloneDeep(selectedDashboard),
          widgets: getStandardizedWidgetsForRender(selectedDashboard.widgets),
        };
      },
      onDeleteDashboard(deletedDashboardIndex) {
        this.$store.dispatch('dashboard/deleteDashboard', deletedDashboardIndex)
          .then((res) => {
            return this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.DEFAULT);
            //return this.$store.dispatch('dashboard/selectDashboard', DEFAULT_DASHBOARD_INDEX);
          })
          .then((res) => {
            this.$store.dispatch('dashboard/getDashboardData');
          });
      },
      // enter copy mode, no API call here
      onClickCopyDashboard(originalDashboardIndex) {
        // this.currentStep = this.currentStep < 2 && !this.welcomePopup.canShow ? 2 : this.currentStep;
        const originalDashboard = this.findOriginalDashboardByIndex(originalDashboardIndex);
        this.currentDashboard = {
          ..._cloneDeep(originalDashboard),
          widgets: getStandardizedWidgetsForRender(originalDashboard.widgets),
          dashboardName: generateNameForCopyOfDefaultDashboard(this.dashboardData)
        };
        this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.COPY);
      },
      // // enter edit mode, no API call here
      onClickEditDashboard(editedDashboardIndex) {
        // this.currentStep = this.currentStep < 2 && !this.welcomePopup.canShow ? 2 : this.currentStep;
        this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.EDIT);
        if (this.currentDashboard.index === editedDashboardIndex) {
          return;
        }
        this.$store.dispatch('dashboard/selectDashboard', editedDashboardIndex);
        const editedDashboard = this.findOriginalDashboardByIndex(editedDashboardIndex);
        this.currentDashboard = {
          ..._cloneDeep(editedDashboard),
          widgets: getStandardizedWidgetsForRender(editedDashboard.widgets),
        };
      },
      onCancelDashboard() {
        const originalDashboard = this.findOriginalDashboardByIndex(this.currentDashboard.index);
        this.currentDashboard = {
          ..._cloneDeep(originalDashboard),
          widgets: getStandardizedWidgetsForRender(originalDashboard.widgets),
        };
        this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.DEFAULT);
      },
      onSaveDashboard() {
        const dashboard = {
          ...this.currentDashboard,
          widgets: getStandardizedWidgetsForSave(this.currentDashboard.widgets)
        };
        this.$store.dispatch('dashboard/saveDashboard', dashboard)
          .then(res => {
            this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.DEFAULT);
            return this.$store.dispatch('dashboard/getDashboardData');
          });
      },
      onSaveDashboardCopy() {
        const newDashboard = {
          ...this.currentDashboard,
          widgets: getStandardizedWidgetsForSave(this.currentDashboard.widgets)
        };
        this.$store.dispatch('dashboard/saveAsDashboard', newDashboard)
          .then(savedDashboard => {
            this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.DEFAULT);
            return this.$store.dispatch('dashboard/selectDashboard', savedDashboard.index);
          })
          .then(res => {
            return this.$store.dispatch('dashboard/getDashboardData');
          });
      },
      onSubmitSaveAs(submittedDashboardName) {
        let newDashboard = {
          ...this.currentDashboard,
          dashboardName: submittedDashboardName
        };
        this.$store.dispatch('dashboard/saveAsDashboard', newDashboard)
          .then(savedDashboard => {
            this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.DEFAULT);
            return this.$store.dispatch('dashboard/selectDashboard', savedDashboard.index);
          })
          .then(res => {
            return this.$store.dispatch('dashboard/getDashboardData');
          });
      },
      onAddWidget(newWidget) {
        let newWidgets = [
          ...this.currentDashboard.widgets,
          {
            ...newWidget,
            firstTimeRenderTimeout: getNewWidgetFirstTimeRenderTimeout(newWidget.widgetType)
          }
        ];

        this.currentDashboard.widgets = getStandardizedWidgetsForRender(newWidgets);
        if(this.currentDashboard.widgets.filter(widget=>{
          return widget.y != 0;
        }).length>0){ //[IT03CM-302] 위젯이 추가될 때 스크롤이 올라가서 짤리는 현상 방지. 위젯들의 위치정보가 0이면 다음 로직을 타지 않음
          this.$scrollTo('#dashboard-bottom', SCROLL_TO_NEW_WIDGET_DURATION, {container: '.dashboard-page'});
        }
      },
      // this returns directly a dashboard from the state, so if u need to modify it, don't modify it directly, but via a clone of it
      findOriginalDashboardByIndex(dashboardIndex) {
        return this.dashboardData.find(dashboard => dashboard.index === dashboardIndex);
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
      onClickToAnalyze(payload) {
        if (this.dashboardViewMode === VIEW_MODE.DEFAULT) {
          this.$store.dispatch('common/setIsLoading', true);
          this.$store.dispatch('common/setSearchFrom', this.$route.fullPath);
          this.$router.push({
            name: ROUTE_NAME.COST_ANALYTICS,
            params: {
              selectedVendor: payload.selectedVendor,
              actionType: COST_ANALYTICS_ACTION_TYPE.CLICK_TO_ANALYZE,
              widgetType: payload.widgetType,
              viewBy: payload.viewBy,
              startDate: payload.startDate,
              endDate: payload.endDate,
              compareStartDate: payload.compareStartDate,
              compareEndDate: payload.compareEndDate,
              data: payload.data,
              startDateOfOtherItem: payload.startDateOfOtherItem,
              endDateOfOtherItem: payload.endDateOfOtherItem,
              isCompare:payload.isCompare,
              isCompareDateCustom:payload.isCompareDateCustom,
              //isOthersClicked:payload.isOthersClicked === undefined ? false  : payload.isOthersClicked
            }
          });
        }
      },
      onClickWelcomePopupDontShow() {
        setValueToStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_WELCOME_POPUP, false);
        // setValueToStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_CURRENT_STEP, 5);
        this.welcomePopup.canShow = false
      },
      onClickWelcomePopupLetsDoIt() {
        // this.changeCurrentStepByViewMode()
        // if (this.currentStep < 1) {
        //   this.currentStep = this.compareCostTrendGuideIndex === -1 ? 1 : 0
        // }
        // if (this.dashboardViewMode === VIEW_MODE.EDIT || this.dashboardViewMode === VIEW_MODE.COPY) {
        //   document.getElementById('cancel-dashboard-header-button').click()
        // }
        // let guideId = this.getNameWrapperById(this.steps[this.currentStep].target.substring(1))
        // let guideElement = document.getElementById(guideId)
        // if (guideElement) {
        //   guideElement.scrollIntoView({behavior: "smooth", block: "center", inline: "nearest"});
        // }
        setValueToStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_WELCOME_POPUP, true);
        this.welcomePopup.canShow = false;
      },
      onClickSharingCancel() {
        this.sharingClicked = false;
      },
      onClickCopySharingUrl() {
        this.sharingClicked = false;
      },
      // myCustomStopCallback() {
      //    if (this.canShowWelcomePopup) {
      //     this.currentStep = -1;
      //     this.welcomePopup.canShow = true;
      //     this.canShowWelcomePopup = false;
      //     return;
      //   }
      //   this.currentStep = this.currentStep + 1;
      //   if (this.currentStep === DASHBOARD_STEP.CREATE_DASHBOARD) {
      //     if (this.currentDashboard.isTemplate) {
      //       this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.COPY);
      //     } else {
      //       this.$store.dispatch('dashboard/setDashboardViewMode', VIEW_MODE.EDIT);
      //     }
      //   }
      //   if (this.compareCostTrendGuideIndex === -1 && (this.currentStep === DASHBOARD_STEP.EDIT_WIDGET || this.currentStep === DASHBOARD_STEP.EDIT_DUPLICATE_MOVE_DELETE)) {
      //     this.currentStep++
      //     setValueToStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_CURRENT_STEP, this.currentStep);
      //     return
      //   }
      //   setValueToStorageByKey(LOCAL_STORAGE_KEY.DASHBOARD_CURRENT_STEP, this.currentStep);
      //   if (_get(this.steps, this.currentStep + '.target')) {
      //     let guideId = this.getNameWrapperById(this.steps[this.currentStep].target.substring(1))
      //     switch (this.dashboardViewMode) {
      //       case VIEW_MODE.DEFAULT:
      //         if (this.currentStep > -1 && this.currentStep < 2) {
      //           let guideElement = document.getElementById(guideId)
      //           if (guideElement) {
      //             guideElement.scrollIntoView({behavior: "smooth", block: "center", inline: "nearest"});
      //           }
      //         }
      //         break;
      //       case VIEW_MODE.EDIT:
      //       case VIEW_MODE.COPY:
      //         if (this.currentStep > 1 && this.currentStep < 5) {
      //           let guideElement = document.getElementById(guideId)
      //           if (guideElement) {
      //             guideElement.scrollIntoView({behavior: "smooth", block: "center", inline: "nearest"});
      //           }
      //         }
      //         break;
      //     }
      //   }
      // },
      // onClickSkipFowNow() {
      //   this.welcomePopup.canShow = false;
      //   this.currentStep = 9
      // },
      // onSelectNewUpdateOption(newUpdateOption) {
      //   this.welcomePopup.canShow = false;
      //   this.canShowWelcomePopup = true;
      //   if (this.dashboardViewMode === VIEW_MODE.EDIT || this.dashboardViewMode === VIEW_MODE.COPY) {
      //     document.getElementById('cancel-dashboard-header-button').click()
      //   }
      //   switch (newUpdateOption) {
      //     case DASHBOARD_NEW_UPDATE.EDIT_WIDGET: {
      //       this.currentStep = 0;
      //       setTimeout(() => {
      //         document.getElementById('edit-widget').click()
      //       }, 50)
      //       break;
      //     }
      //     case DASHBOARD_NEW_UPDATE.DASHBOARD_TEMPLATE_SELECTION: {
      //       if (this.dashboardViewMode === VIEW_MODE.EDIT || this.dashboardViewMode === VIEW_MODE.COPY) {
      //         document.getElementById('cancel-dashboard-header-button').click()
      //       }
      //       this.currentStep = 1;
      //       setTimeout(() => {
      //         document.getElementById('dashboard-template-selection').click()
      //       }, 50)
      //       break;
      //     }
      //     case DASHBOARD_NEW_UPDATE.CREATE_DASHBOARD: {
      //       if (this.currentDashboard.isTemplate) {
      //         this.onClickCopyDashboard(this.currentDashboard.index)
      //       } else {
      //         this.onClickEditDashboard(this.currentDashboard.index)
      //       }
      //       this.currentStep = 2;
      //       setTimeout(() => {
      //         document.getElementById('create-dashboard-wrapper').click()
      //       }, 50)
      //       break;
      //     }
      //     case DASHBOARD_NEW_UPDATE.ADD_WIDGET_GUIDE: {
      //       if (this.currentDashboard.isTemplate) {
      //         this.onClickCopyDashboard(this.currentDashboard.index)
      //       } else {
      //         this.onClickEditDashboard(this.currentDashboard.index)
      //       }
      //       this.currentStep = 3;
      //       setTimeout(() => {
      //         document.getElementById('add-widget-guide').click()
      //       }, 50)
      //       break;
      //     }
      //     case DASHBOARD_NEW_UPDATE.CUSTOMIZE_WIDGET: {
      //       if (this.currentDashboard.isTemplate) {
      //         this.onClickCopyDashboard(this.currentDashboard.index)
      //       } else {
      //         this.onClickEditDashboard(this.currentDashboard.index)
      //       }
      //       this.currentStep = 4;
      //       setTimeout(() => {
      //         document.getElementById('customize-widget').click()
      //       }, 100)
      //       break;
      //     }
      //   }
      //   setTimeout(() => {
      //     let guideId = this.getNameWrapperById(this.steps[this.currentStep].target.substring(1))
      //     let guideElement = document.getElementById(guideId)
      //     if (guideElement) {
      //       guideElement.scrollIntoView({behavior: "smooth", block: "center", inline: "nearest"});
      //     }
      //   }, 150)
      // },
      showHotspot(selector) {
        const hotspotIndex = this.steps.findIndex(function(step) {
          return step.target === selector;
        });
        this.$tours['dashboardTour'].start(hotspotIndex);
      },
      // changeCurrentStepByViewMode() {
      //   this.welcomePopup.canShow = true;
      //   if (this.currentStep < 2 && (this.dashboardViewMode === VIEW_MODE.EDIT || this.dashboardViewMode === VIEW_MODE.COPY)) {
      //     this.currentStep = 0;
      //   }
      // },
      // getNameWrapperById(name) {
      //   if (this.currentStep === 0 || this.currentStep === 4) {
      //     return `${name}-wrapper-${this.compareCostTrendGuideIndex}`
      //   } else {
      //     return `${name}-wrapper`
      //   }
      // },
      // getCompareCostTrendGuideIndex() {
      //   if (_isEmpty(this.currentDashboard.widgets)) {
      //     return
      //   }
      //   let trendWidgets = this.currentDashboard.widgets.filter(widget => widget.widgetType === DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET)
      //   if (_isEmpty(trendWidgets)) {
      //     return
      //   }
      //   let minYTrendWidget = trendWidgets[0]
      //   for(let i = 1; i < trendWidgets.length; i++) {
      //     if (trendWidgets[i].y < minYTrendWidget.y) {
      //       minYTrendWidget = trendWidgets[i]
      //     }
      //   }
      //   this.compareCostTrendGuideIndex = minYTrendWidget.index;
      // }
    }
  };

  function getNewWidgetFirstTimeRenderTimeout(widgetType) {
    switch (widgetType) {
      case DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET:
      case DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET:
      case DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET:
        return FIRST_TIME_RENDER_TIMEOUT_OF_HEAVY_WIDGET;
    }
    return FIRST_TIME_RENDER_TIMEOUT_OF_LIGHT_WIDGET;
  }
</script>

<style lang="scss">
  @import "../../../assets/css/base/var";
  @import "../../../assets/css/base/function";
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
    padding-left: 0px;
    padding-bottom: 55px !important;
    padding-right: 0px;
    &.wrapper-dashboard {
      padding-bottom: 80px !important;
      margin-bottom: -12px;
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
          height: 100vh;
        }

        .main-left-2 {
          background: transparent;
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
      }
      &.active {
        .main-right {
          width: calc(100% - 290px);
          .custom-dashboard-common-item {
            padding-top: 8px !important;
            padding-left: 0 !important;
            padding-right: 0 !important;
            .last-month {
              padding: 0 20px !important;
            }
          }
        }
      }
    }
  }
  .new-portion-widget-wrapper {
    .new-portion-widget-header {
      display: flex;
      align-content: center;
      /*flex-wrap: nowrap;*/
      padding-right: 0px !important;
      height: 55px;
      width: 100%;
      .integrated-portion-widget-header-left {
        display: flex;
        margin-top: 1px;
        font-size: 1rem;
        flex: 1 1 auto;
        &.eng {
          font-size: 0.9rem !important;
        }
        &.expand {
          font-size: 0.9rem !important;
          flex-direction: column;
        }
        .title-area {
          margin-right: 6px;
        }
        .dropdown-area {
          display: inline-flex;
        }
        .dropdown-area-category-widget {
          display: inline-flex;
          align-items: center;
          .dropdown-toggle {
            padding-left: 0px !important;
          }
        }
      }
      .integrated-portion-widget-header-right {
        display: inline-flex;
        align-items: center;
        flex : 0 0;
      }
      .period-area {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        .period-select-space {
          display: flex;
          flex-direction: row;
        }
      }
      .edit-btn {
        .edit-button {
          padding-left: 10px !important;
        }
      }
      .custom-view-by-dropdown {
        margin-left : 0 !important;
        margin-right : 6px;
        &.service-group-dropdown {
          .base-dropdown {
            width: 100%;
            button {
              width: 100%;
              span {
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }
            }
          }
        }
      }
    }
    .portion-widget-chart-wrapper {
      height: 360px;
      padding-left: 5px;
      padding-right: 5px;
    }
    .custom-popover-portion-widget {
      .popover {
        top: 0 !important;
        left: 16px !important;
        .arrow {
          left: 75% !important;
        }
        .arrow:before {
          border-bottom-color: #ffffff;
        }
      }
    }

    .custom-btn-portion-time-frame-dropdown {
      .base-dropdown {
        button {
          padding-top: 0 !important;
          padding-right: 0 !important;
          padding-bottom: 0 !important;
        }
      }
      .dropdown-menu {
        min-width: 110px !important;
        li {
          a {
            padding-left: 4px !important;
            padding-right: 10px !important;
          }
        }
      }
    }
    .custom-btn-portion-date-type-dropdown {
      .base-dropdown {
        button {
          padding: 0 0 0 16px !important;
        }
        .dropdown-menu {
          min-width: 70px !important;
          li {
            a {
              padding-left: 18px !important;
              padding-right: 18px !important;
            }
          }
        }
      }
    }
  }

  .portion-widget-wrapper {
    .portion-widget-chart-wrapper {
      height: 360px;
      padding-left: 5px;
      padding-right: 5px;
    }
    .custom-popover-portion-widget {
      .popover {
        top: 0 !important;
        left: 16px !important;
        .arrow {
          left: 75% !important;
        }
        .arrow:before {
          border-bottom-color: #ffffff;
        }
      }
    }
    .ellipsis {
      white-space: nowrap;
      width: 50%;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .custom-btn-portion-time-frame-dropdown {
      .base-dropdown {
        button {
          padding-top: 0 !important;
          padding-right: 0 !important;
          padding-bottom: 0 !important;
        }
      }
      .dropdown-menu {
        min-width: 110px !important;
        li {
          a {
            padding-left: 4px !important;
            padding-right: 10px !important;
          }
        }
      }
    }
    .custom-btn-portion-date-type-dropdown {
      .base-dropdown {
        button {
          padding: 0 0 0 16px !important;
        }
        .dropdown-menu {
          min-width: 70px !important;
          li {
            a {
              padding-left: 18px !important;
              padding-right: 18px !important;
            }
          }
        }
      }
    }
  }
  .edit-portion-by-account-form {
    .portion-widget-chart {
      height:240px;
      &.edit-form {
        position: relative;
      }
    }
  }
  .edit-portion-by-account-form,
  #edit-dashboard-cost-form,
  .edit-abnormal-form{
    .tag-key-options,
    .view-by-options,
    .date-type-options,
    .time-frame-options,
    .sort-by-options,
    .sort-type-options{
      width: 48%;
      display: inline-block;
    }
    .view-by-options,
    .date-type-options,
    .sort-by-options{
      margin-right: 3%;
    }
  }
  #edit-dashboard-multi-vendor-cost-by-form,
  .edit-abnormal-form{
    .tag-key-options,
    .view-by-options,
    .date-type-options,
    .time-frame-options,
    .sort-by-options,
    .sort-type-options{
      width: 48%;
      display: inline-block;
    }
    .view-by-options,
    .date-type-options,
    .sort-by-options{
      margin-right: 3%;
    }
  }
  .edit-abnormal-user-form{
    .tag-key-options,
    .view-by-options,
    .date-type-options,
    .time-frame-options,
    .sort-by-options,
    .sort-type-options{
      width: 48%;
      display: inline-block;
    }
    .view-by-options,
    .date-type-options,
    .sort-by-options{
      margin-right: 3%;
    }
  }
  .edit-abnormal-ai-form{
    .tag-key-options,
    .view-by-options,
    .date-type-options,
    .time-frame-options,
    .sort-by-options,
    .sort-type-options{
      width: 48%;
      display: inline-block;
    }
    .view-by-options,
    .date-type-options,
    .sort-by-options{
      margin-right: 3%;
    }
  }
  .dashboard-common-item {
    text-align: center;
    .custom-dashboard-common-item {
      .coming-soon-badge {
        font-size: 12px;
        font-family: NotoSansCJKkr-Medium;
        background-color: #e0f3ff !important;
        position: absolute;
        top: 65%;
        left: 50%;
        border-radius: 4px;
        padding-top: 0 !important;
        -ms-transform: translate(-50%, -50%);
        transform: translate(-50%, -50%);
        .coming-soon-text {
          color: #056eff !important;
        }
      }
    }
    .dashboard-cloud-budget-title , .dashboard-total-saving-title {
      justify-content: center;
    }
    .forecast-this-month-title {
      padding-left: 5px;
    }
    #forecast-this-month-icon {
      padding-right: 5px;
      cursor: default;
    }
    .estimated-cost-loading, .cost-month-to-date-loading, .cost-budget-loading, .cost-total-saving-loading, .year-cost-fcst-loading  {
      height: 78px;
    }
    .currency-text {
      font-family: Montserrat-Regular;
      font-size: 24px;
      color: #000;
    }
    p {
      display: flex;
      align-items: center;
      justify-content: center;
      .exchange-rate-icon {
        display: flex;
        align-items: center;
      }
      .currency-text-small {
        font-size: 12px;
        color: #000;
        font-family: Montserrat-Regular;
      }
    }
    .custom-notification-no-data {
      position: relative;
    }
    .col-height {
      height: 110px;
    }
    .no-data {
      top: 65%;
      .material-icons {
        &.color-yellow-1 {
          display: flex;
          align-items: center;
        }
      }
    }
    .custom-label {
      color: #222222;
    }
    .last-month {
      color: #7b8088;
      display: flex;
      justify-content: center;
      align-items: center;
      .color-red-1,
      .color-green-1,
      .color-gray-1 {
        padding-right: 5px;
      }
    }
  }
  .vue-grid-item {
    border-radius: 4px;
    transition-duration: 0s!important;
    &.portion-by-widget {
      z-index: 1;
    }
    &:not(.vue-grid-placeholder) {
      background: white;
      border: none;
    }
    .dashboard-widget-height {
      height: 360px;
      &.compare-cost-trend {
        height: 366px;
      }
    }
    .portion-widget-header {
      padding: 0 12px 6px 20px !important;
      height: 55px
    }
    .portion-time-period-pos {
      position: absolute;
      top: 32px;
      left: 20px;
    }
    .portion-right-header-mr-top {
      margin-top: 10px;
      .dropdown-button {
        margin-bottom: 5px;
      }
    }
    .dashboard-widget-header {
      padding: 12px 12px 12px 20px !important;
      &.compare-cost-trend {
        padding-right: 0;
      }
    }
    .resizing {
      opacity: 0.9;
    }
    .text {
      font-size: 24px;
      text-align: center;
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      margin: auto;
      height: 100%;
      width: 100%;
    }
    .no-drag {
      height: 100%;
      width: 100%;
    }
    .minMax {
      font-size: 12px;
    }
    .add {
      cursor: pointer;
    }
    &.vue-grid-placeholder {
      background-color: #D3EAFF !important;
    }
  }

  .vue-draggable-handle {
    position: absolute;
    width: 20px;
    height: 20px;
    top: 0;
    left: 0;
    background-color: #d3eaff;
    background-position: bottom right;
    padding: 0 8px 8px 0;
    background-repeat: no-repeat;
    background-origin: content-box;
    box-sizing: border-box;
    cursor: pointer;
  }

  .custom-view-by-dropdown {
    &.tag-keys-dropdown,
    &.accounts-dropdown {
      margin-left: 9px !important;
      .base-dropdown {
        width: 100%;
        button {
          width: 100%;
          span {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }
    }
  }

  .custom-view-by-dropdown {
    &.accounts-dropdown {
      .base-dropdown {
        button {
          padding-left: 0px !important;
        }
      }
    }
  }

  .custom-tag-key-tooltip,
  .custom-accountOptions-tooltip {
    .tooltip-inner {
      padding-left: 15px !important;
      padding-right: 15px !important;
      font-size: 14px !important;
      text-align: left !important;
      max-width: 100% !important;
      width: 100% !important;
    }
  }
  .icon-arrow-up,
  .icon-arrow-down,
  .icon-arrow-center {
    height: 8px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding-right: 0px!important;
  }

  @media only screen and (max-width: 1343px) {
    .dashboard-common-item {
      .custom-dashboard-common-item {
        padding-top: 8px !important;
        padding-bottom: 5px !important;
        .last-month {
          padding: 0 20px !important;
        }
      }
    }
  }
  @media only screen and (max-width: 1279px) {
    #wrapper {
      position: relative;
      &.wrapper-dashboard {
        min-width: 1274px;
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
  @media only screen and (max-width: 1392px) {
    .left-menu-main {
      &.active {
        .main-right {
          .custom-dashboard-common-item {
            padding: 8px 10px 8px 10px !important;
            .last-month {
              padding: 0 20px !important;
            }
            .current-month-estimated-cost {
              margin-top: 0 !important;
            }
            .current-month-cost {
              margin-top: 0 !important;
            }
            .current-budget-data{
              margin-top: 0 !important;
            }
            .current-total-saving{
              margin-top: 0 !important;
            }
            p {
              padding: 0 !important;
            }
          }
        }
      }
    }
  }
  @media only screen and (max-width: 1440px) and (min-width: 1281px) {
    #wrapper {
      &.wrapper-dashboard {
        min-width: auto;
        width: 100%;
        padding-right: 0px;
        .left-menu-main {
          .main-right {
            width: calc(100% - 51px) !important;
            margin: auto;
            margin-left: 52px;
          }
        }
      }
    }
  }
  @media only screen and (min-width: 1440px) {
    #wrapper {
      &.wrapper-dashboard {
        min-width: 1400px;
      }
    }
  }
  @media only screen and (min-width: 1532px) {
      #wrapper {
          &.wrapper-dashboard {
              min-width: 1531px;
          }
      }
  }
  @media only screen and (max-width: 1024px) {
    .wrapper-dashboard {
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
          display: none!important;
        }
        .main-right {
          width: calc(100% - 51px) !important;
          margin-left: 50px;
          margin-top: 28px;
        }
      }
    }
  }
  @media screen and (max-width: 1279px),
  screen and (max-width: 1439px) and (min-width: 1280px),
  screen and (max-width: 1024px) {
    .base-welcome-popup {
      &:before {
        width: 1440px;
        margin-left: 0px !important;
      }
    }
  }

  @media only screen and (min-width: 1921px) {
    #wrapper {
      &.wrapper-dashboard {
        .left-menu-main {
          position: unset!important;
        }
      }
    }
  }
</style>
