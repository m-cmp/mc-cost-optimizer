<template>
  <header class="console-gnb new">
    <div class="logo-n-companies">
      <button
        id="home_login_gnb_logo"
        type="button"
        class="btn-mcmp font-weight-bold"
        @click="moveDashboard('/dashboard')">
        MCMP
      </button>

    </div>

    <BasePopup
      ref="AuthPopup"
      :modal-class="'none-auth base-confirm-popup'"
      :content="$t('noAuth.content')"
      :title="$t('noAuth.title')"
      :hide-header-close="true"
      :no-close-on-esc="true"
      :no-close-on-backdrop="true"
      :button-text="$t('common.button.ok')"
      popup-ref="openAuthPopup"
      @onConfirmAction="goDashboard()"
    />

  </header>
</template>
<script>
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';
import Multiselect from 'vue-multiselect'
import BasePopup from '@/components/common/BasePopup';
import axios from 'axios';
import ENDPOINT from "@/api/endpoints";
import {
  ROUTE_NAME,
  COST_ANALYTICS_ACTION_TYPE,
  CLASSIC_VERSION_PAGE,
  SEARCH_BAR_RESULT_GROUP,
  VENDOR,
} from "@/constants/constants";
import {SEARCH_BAR_SUGGESTION_VALUE, COST_TREND_BY} from "@/constants/costAnalyticsConstants";
import _get from 'lodash/get';
import _cloneDeep from 'lodash/cloneDeep';
import _isEmpty from 'lodash/isEmpty';
import {toStringWithMatchesHighlighted} from '@/util/stringUtils';
import Profile from '@/components/common/profile';
import {fetchUserFilters, fetchResultSearch} from '@/api/cost-analytics';
import {MAX_RECENT_SEARCH_COUNT} from '@/constants/constants';
import ClickOutside from 'vue-click-outside';
import {mapGetters} from "vuex";
import _toLower from 'lodash/toLower';
import _isNil from 'lodash/isNil';
import {getValueFromStorageByKey, setValueToStorageByKey, LOCAL_STORAGE_KEY} from '@/util/localStorage';
import _debounce from 'lodash/debounce';
import {Trans} from "@/components/common/base-i18n/Translation";
import {SUPPORTED_LANGUAGE} from "@/constants/trans";
import _ from 'lodash';
import BaseConfirmPopup from '@/components/common/popup/BaseConfirmPopup';
import { EventBus } from '../event-bus'

const gnb = require('./index')

const ICON_NAME = {
  STAR: 'star',
  HISTORY: 'history'
}

const ICON_COLOR = {
  GRAY: 'gray',
  ORANGE: 'orange'
}

const PROFILE = {
  DEV: 'DEV',
  PROD: 'PROD'
}

const DATE_FORMAT = 'YYYY-MM-DD';
const RESULTS_COUNT = {
  TEN: 10,
  ONE_THOUSAND: 1000
};

const SEARCH_DEBOUNCE_TIME = 200;

export default {
  name: 'McmpBaseHeader',
  components: { Multiselect, BaseConfirmPopup,BasePopup },
  directives: {
    ClickOutside
  },
  props: {
    userFilters: {
      type: Array,
      default() {
        return []
      }
    },
  },
  data () {
    return {
      profile: Profile,
      isActive: false,
      internalUserFilters: [],
      selectedCountries: [],
      options: [],
      searchText: '',
      url: '',
      isShowNothingFound: false,
      isLoading: false,
      redirectPage: CLASSIC_VERSION_PAGE.PROFILE,
      recentMenuList: [],
      // Submenu Variable
      menu: [],
      cmpnMenuAuth: null,
      securityMenuAuth: null,
      browserTimeZone: moment().format("Z"),
      userCompanyList : [],
      toastMessage:'test',
      selectStatus: false,
      ConcurrentLoginPopup: {
        content: this.$t(`common.popup.content.concurrent_login`),
        title: this.$t(`common.popup.title.concurrent_login`),
      },
      metringSvcMenuList: [
        {
          menuId: "MTV3010",
          menuNmEn: "Summary",
          menuNmKo: "Summary",
          menuNmZh: "Summary",
          menuNmJa: "Summary",
          menuDesc: null,
          menuUrl: "/dashboard",
          menuFullUrl: "/dashboard",
          authTypeCd: "AUTH_TYPE_010",
          authTypeNm: "EDIT",
          subYn: "N",
          menuNewYn: "N",
          menuBetaYn: "N",
          menuNm: {
            ko: "Summary",
            en: "Summary",
            zh: "Summary",
            ja: "Summary"
          },
          subMenuList: null
        },
        {
          menuId: "MTV3030",
          menuNmEn: "Billing Invoice",
          menuNmKo: "빌링 인보이스",
          menuNmZh: "Billing Invoice",
          menuNmJa: "帳單發票",
          menuDesc: null,
          menuUrl: "/billing",
          menuFullUrl: "/billing",
          authTypeCd: "AUTH_TYPE_010",
          authTypeNm: "EDIT",
          subYn: "N",
          menuNewYn: "N",
          menuBetaYn: "N",
          menuNm: {
            ko: "빌링 인보이스",
            en: "Billing Invoice",
            zh: "Billing Invoice",
            ja: "帳單發票"
          },
          subMenuList: null
        },
        {
          menuId: "MTV3060",
          menuNmEn: "Cost Anomaly Detection",
          menuNmKo: "이상 비용 탐지",
          menuNmZh: "费用异常检测",
          menuNmJa: "Cost Anomaly Detection",
          menuDesc: null,
          menuUrl: "/anomaly-detection",
          menuFullUrl: "/anomaly-detection",
          authTypeCd: "AUTH_TYPE_010",
          authTypeNm: "EDIT",
          subYn: "N",
          menuNewYn: "N",
          menuBetaYn: "N",
          menuNm: {
            ko: "이상 비용 탐지",
            en: "Cost Anomaly Detection",
            zh: "费用异常检测",
            ja: "Cost Anomaly Detection"
          },
          subMenuList: null
        }]
    }
  },
  computed: {
    ...mapGetters({
      homepageInfo: 'homepageInfo',
      isSurveySubmitted: 'common/submittedSurvey',
      isConcurrentLogin: 'isConcurrentLogin',
      portalUrl: 'portalUrl'
    }),
    defaultOptions: {
      cache: false,
      get() {
        let suggestionOptions = {
          title: {
            title: this.$t('header.searchBar.options.title.suggestion'),
            note: '',
            moreFilters: 'More filters',
            empty: this.$t('header.searchBar.nothingFound'),
          },
          details: [
            {
              name: this.$t('header.searchBar.options.suggestion.costOfThisMonth'),
              img: '/static/svg/icon_service_cost_management.svg',
              value: SEARCH_BAR_SUGGESTION_VALUE.COST_OF_THIS_MONTH,
              group: SEARCH_BAR_RESULT_GROUP.SUGGESTION
            },
            {
              name: this.$t('header.searchBar.options.suggestion.costOfThisMonthByAccount'),
              img: '/static/svg/icon_service_cost_management.svg',
              value: SEARCH_BAR_SUGGESTION_VALUE.COST_OF_THIS_MONTH_BY_ACCOUNT,
              group: SEARCH_BAR_RESULT_GROUP.SUGGESTION
            },
            { name: this.$t('header.searchBar.options.suggestion.CostOfThisMonthByProduct'),
              img: '/static/svg/icon_service_cost_management.svg',
              value: SEARCH_BAR_SUGGESTION_VALUE.COST_OF_THIS_MONTH_BY_PRODUCT,
              group: SEARCH_BAR_RESULT_GROUP.SUGGESTION
            },
          ]
        }
        let savedFiltersOptions = {
          title: {
            title: this.$t('header.searchBar.options.title.savedFilters'),
            note: ''
          },
          details: this.internalUserFilters.map(item => {
            return {
              name: item.filterName,
              index: item.index,
              iconName: ICON_NAME.STAR,
              iconColor: ICON_COLOR.ORANGE,
              group: SEARCH_BAR_RESULT_GROUP.SAVED_FILTER
            }
          })
        }
        const recentOptions = getValueFromStorageByKey(LOCAL_STORAGE_KEY.RECENT_SEARCH);
        let recentSearchOptions = {};
        if (!_isNil(recentOptions)) {
          recentOptions.reverse();
          recentSearchOptions = {
            title: {
              title: this.$t('header.searchBar.options.title.recentSearch'),
              note: ''
            },
            details: recentOptions.map(item => {
              return {
                name: item.name,
                value: item.value,
                vendor: item.vendor,
                viewBy: item.viewBy,
                iconName: ICON_NAME.HISTORY,
                iconColor: ICON_COLOR.GRAY,
                group: SEARCH_BAR_RESULT_GROUP.RECENT_SEARCH
              }
            })
          }
        }
        return [
          suggestionOptions,
          savedFiltersOptions,
          recentSearchOptions
        ]
      }
    },
    isBespin:{
      get(){
        let blntCmpnId = this.$store.state.loginUser.blntCmpnId; // 유저가 속한 회사 ( 베스핀 직원은 확인할 수 있도록 함 )
        if(blntCmpnId === '1'){
          return true;
        }else{
          return false;
        }
      }
    },
    isScmp:{
      get(){
        let siteId = this.$store.state.loginUser.siteCd; // WL 적용 전까지는 글로벌만 확인 가능
        if(siteId === 'SCMP'){
          return true;
        }else{
          return false;
        }
      }
    },
    isMea:{
      get(){
        let siteId = this.$store.state.loginUser.siteCd; // WL 적용 전까지는 글로벌만 확인 가능
        if(siteId === 'BGMEA'){
          return true;
        }else{
          return false;
        }
      }
    },
    isChina:{
      get(){
        if(Profile.env === 'CHINA'){
          return true;
        }else{
          return false;
        }
      }
    },
    canShowClearSearchInputBtn() {
      return !_isEmpty(this.searchText);
    },
    isSearchIconActive() {
      return !_isEmpty(this.searchText);
    },
    selectedCompany() {
      return this.$store.state.loginUser.curCmpnNm;
    },
    userName() {
      return this.$store.state.loginUser.userNm;
    },
    companyList(){
      return this.$store.state.cmpnList;
    },
    loginUserNm: function () {
      return this.$store.state.loginUser.userNm
    },
    getCurCmpnId: function () {
      return this.$store.state.loginUser.curCmpnId;
    },
    getLocale: function() {
      return this.$i18n.locale;
    },
  },
  watch: {
    userFilters: {
      handler() {
        this.internalUserFilters = _cloneDeep(this.userFilters);
      }
    },
    "$i18n.locale" : {
      handler() {
        this.getMenuPermission();
      }
    },
    // 회사 변경 시
    getCurCmpnId : function(){
      this.getUserCompanyList()
      // this.getHomepageInfo()
    },
    getLocale: function() {
      if(this.$store.state.svcMenuList!=null && this.$store.state.svcMenuList!=undefined){
        let svcMenuList = this.$store.state.svcMenuList;
        this.changeLocalizationTextForMenu(svcMenuList)//다국어 자동화 처리
        this.$store.commit('setSvcMenuList', {svcMenuList: svcMenuList})
      }
    },
    isConcurrentLogin: function() {
      if(this.isConcurrentLogin) {
        this.openConcurrentLoginPopup();
      }
    }
  },
  created() {
    this.getUserCompanyList()
  },
  mounted: function(){
    let self = this
    setTimeout(function(){
      gnb.ConsoleGNB.init();
    }, 1000);

    setTimeout(function(){
      let queryCmpnId = self.$route.query.cmpnId;
      if(!_isEmpty(queryCmpnId)){
        let cmpnObj = self.userCompanyList.find(item => item.cmpnId === queryCmpnId)
        if(!_isNil(cmpnObj) && self.$store.state.loginUser.curCmpnId!==cmpnObj.cmpnId){
          self.selectCompany(cmpnObj.cmpnId,cmpnObj.cmpnNm)
        }
      }
    }, 2000);

    if (this.isConcurrentLogin) {
      this.openConcurrentLoginPopup();
    }
  },
  methods:{
    logoutAllSession: function(withoutCurrent) {
    },
    openConcurrentLoginPopup: function() {
      this.$refs.ConcurrentLoginPopup.show();
    },
    goDashboard: function() {
      this.$router.push({
        name: ROUTE_NAME.DASHBOARD
      });
    },
    closePopup: function () {
      $.magnificPopup.close();
    },
    closeToast(){
      $('.layout-toast-message').toggleAttr("data-show");
    },
    // 로그인한 유저의 각 서비스별 메뉴 권한 조회
    getMenuPermission() {
      return Promise.resolve(mcmpAPIResponseDummyData.loadDashboard.menuPermission)
        .then(resp => {
            let companyInfo = resp.result.cmpnList[0];
            let metringSvc = companyInfo.service.find(item => item.svcId === "metering");
            if (metringSvc) {
              metringSvc.menuList = this.metringSvcMenuList // dummyData
            }
            let companyInfoSvc;
            companyInfo.service.forEach(function (element, index){
              if(element.svcId === "metering"){
                companyInfoSvc = index;
              }
            })
            this.setShortCutMenu(companyInfo.service.slice(companyInfoSvc, companyInfoSvc+1), companyInfo.cmpnId, true);
        })
    },
    // Service Portal 메뉴 이동 Function
    moveDashboard: function (menuUrl) {
      if (menuUrl === '/dashboard') {
        this.$router.push({
          name: ROUTE_NAME.DASHBOARD
        });
      }
    },
    moveRequest: function() {
      window.open(this.urls.srUrl);
    },
    // 로그인한 유저가 권한에 따라 사용 가능한 회사 목록 조회
    getUserCompanyList() {
      return Promise.resolve(mcmpAPIResponseDummyData.loadDashboard.companyList)
        .then(response => {
          this.userCompanyList = response.cmpnList
          this.$store.commit('setCompanyList', this.userCompanyList);

          gnb.GlobalMenu.setCompany();

          if(this.$store.state.loginUser.curCmpnId && _.find(this.userCompanyList, {cmpnId: this.$store.state.loginUser.curCmpnId}) ) {
            this.$store.commit('setVendorInfo', {vendorInfo:_.find(this.userCompanyList, {'cmpnId': this.getCurCmpnId}).vendor});
            this.getMenuPermission()
            // this.selectCompany(this.$store.state.loginUser.curCmpnId, this.$store.state.loginUser.curCmpnNm)
            return;
          } else if(this.userCompanyList.length > 0) {
            this.movePortalEachWL()
          }
        });
    },
    getTextByLocale(en,ko,zh,ja){
      let result = '';
      switch(this.getLocale){
        case 'en' :result = en; break;
        case 'ko' :result = ko; break;
        case 'zh' :result = zh; break;
        case 'ja' :result = ja; break;
      }
      return result;
    },
    movePortalEachWL: function() {
      window.location.href = this.urls.portalUrl
    },
    showGuide: function () {
      let siteId = this.$store.state.loginUser.siteCd;
        window.open(this.urls.srUrl);
    },
    showEvent: function () {
      $('#gnb_noti_button').toggleAttr('data-state', 'open')
    },
    hideEvent: function () {
      $('#gnb_noti_button').removeAttr('data-state')
    },
    changeLocalizationTextForMenu(serviceList){
      let self = this
      for(let svcItem of serviceList) {
        svcItem.svcNm = self.getTextByLocale(svcItem.svcNmEn,svcItem.svcNmKo,svcItem.svcNmZh,svcItem.svcNmJa)
        if(svcItem.menuList!==null){
          svcItem.menuList.forEach(menu => {
            // eslint-disable-next-line no-param-reassign
            menu.menuNm = self.getTextByLocale(menu.menuNmEn,menu.menuNmKo,menu.menuNmZh,menu.menuNmJa)
            if(menu.subMenuList!==null){
              menu.subMenuList.forEach(subMenu => {
                // eslint-disable-next-line no-param-reassign
                subMenu.menuNm = self.getTextByLocale(subMenu.menuNmEn,subMenu.menuNmKo,subMenu.menuNmZh,subMenu.menuNmJa)
              })
            }
          })
        }
      }
    },
    setShortCutMenu: function (serviceList, cmpnId, isReset) {
      let self = this;
      this.svcMenuList = []
      this.cmpnMenuAuth = ''
      this.securityMenuAuth = ''

      for(let svcItem of serviceList) {
        if(svcItem.svcId == 'metering') {
          //Stage에서 Billing 메뉴 테스트를 위한 임시 조치
          let curMenuUrl = '/' + this.$router.currentRoute.path.substr(1).split('/')[0];
          let urlCheck = !_.isNil(_.find(svcItem.menuList, {"menuUrl": curMenuUrl})) &&
            !_.isNil(
              _.find(svcItem.menuList, function(menu){
                  return menu.menuUrl === curMenuUrl &&
                    ((menu.authTypeCd === self.constants.AUTH_TYPE.VIEW) || (menu.authTypeCd === self.constants.AUTH_TYPE.EDIT))  &&
                    menu.menuId.startsWith(self.constants.MENU_PRE_CODE)
                }
              )
            )
          ;
          if(urlCheck){
            this.saveRecentMenu()
          }
          else {
            //'AUTH_TYPE_010': 수정권한, 'AUTH_TYPE_020': 조회권한, 'AUTH_TYPE_030': 권한없음
            //해당 메뉴의 권한이 없을때 다른 메뉴가 존재하면 다음메뉴로 이동

            this.openAuthPopup();
          }

          let meteringMenuList = _.find(svcItem.menuList ,function(menu){ return !(menu.authTypeCd.startsWith(self.constants.MENU_PRE_CODE)) && (menu.authTypeCd === "AUTH_TYPE_010" || menu.authTypeCd === "AUTH_TYPE_020" )});
          this.$store.commit('setKenobiMenuAuth', {KENOBI_MENU_AUTH: svcItem.menuList})
          if(!_.isNil(meteringMenuList)){
            this.$store.commit('setMeteringMenuAuth', {METERING_MENU_AUTH: meteringMenuList})
          }

          // this.settingSubMenu(svcItem);

          this.svcMenuList.push(svcItem);
        } else if(svcItem.svcId == 'portal') {
          for (let menuItem of svcItem.menuList) {
            if (menuItem.menuId == 'SP900') {
              this.cmpnMenuAuth = menuItem.authTypeCd;
            }
            if (menuItem.menuId == 'SP950') {
              this.securityMenuAuth = menuItem.authTypeCd;
            }
            svcItem.menuList = _.reject(svcItem.menuList, {menuId: 'SP900'});
            svcItem.menuList = _.reject(svcItem.menuList, {menuId: 'SP950'});
          }
          this.svcMenuList.push(svcItem);
        }else{
          this.svcMenuList.push(svcItem);
        }

      }

      this.changeLocalizationTextForMenu(this.svcMenuList)//다국어 자동화 처리
      this.$store.commit('setSvcMenuList', {svcMenuList: this.svcMenuList})
      if(this.$store.state.KENOBI_MENU_AUTH == null){
        if(this.$store.state.METERING_MENU_AUTH == null){
          window.location.href= this.urls.portalUrl
        }else{
          window.location.href= this.urls.meteringUrl
        }
      }
    },

    settingSubMenu(svcItem){

      for(let i=0; i<svcItem.menuList.length; i++) {

        // Budget 2Depth 메뉴 - 템플릿 하드코딩 우회로 여기서 셋팅함
        if(svcItem.menuList[i].menuId=== 'MT040'){

          let subMenuList = [];
          // Budget Overview
          subMenuList.push({
            menuUrl: 'budget/budget1',
            menuNm: 'header.costSubMenu.budget.overView',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: svcItem.menuList[i].menuFullUrl,
            isExternalService: true
          });
          // Budget Setting
          subMenuList.push({
            menuUrl: 'budget/mbudget',
            menuNm: 'header.costSubMenu.budget.setting',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: svcItem.menuList[i].menuFullUrl,
            isExternalService: true
          });
          // Alarm List
          subMenuList.push({
            menuUrl: 'budget/alarmlist',
            menuNm: 'header.costSubMenu.budget.alarmList',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: svcItem.menuList[i].menuFullUrl,
            isExternalService: true
          });
          // Alarm Recepient
          subMenuList.push({
            menuUrl: 'budget/receiver',
            menuNm: 'header.costSubMenu.budget.receiver',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: svcItem.menuList[i].menuFullUrl,
            isExternalService: true
          });


          // eslint-disable-next-line no-param-reassign
          svcItem.menuList[i].subMenuList = subMenuList;

        }
        // Payment & Discount 2Depth 메뉴 - 템플릿 하드코딩 우회로 여기서 셋팅함
        else if(svcItem.menuList[i].menuId== 'MT060'){
          let subMenuList = [];
          subMenuList.push({
            menuUrl: 'payment/discount',
            menuNm: 'header.costSubMenu.payment.discount',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: svcItem.menuList[i].menuFullUrl,
            isExternalService: true
          });
          // Credit Information
          subMenuList.push({
            menuUrl: 'payment/credit',
            menuNm: 'header.costSubMenu.payment.creditInfo',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: svcItem.menuList[i].menuFullUrl,
            isExternalService: true
          });

          // eslint-disable-next-line no-param-reassign
          svcItem.menuList[i]['subMenuList'] = subMenuList;

        }
        //커스텀 리포트 메뉴
        else if(svcItem.menuList[i].menuId== 'MT070'){ //report
          // TODO 일단 global 회사만 노출되도록 설정 (china, W/L 모두 제외) 빨리 하드코딩 떼어내고 싶음.
          if (!this.isChina && !this.isScmp && !this.isMea) {
            let subMenuList = [];
            // Static Report
            subMenuList.push({
              menuUrl: 'report',
              menuNm: 'header.costSubMenu.report.staticReport',
              menuId: svcItem.menuList[i].menuId,
              menuFullUrl: svcItem.menuList[i].menuFullUrl,
              isExternalService: false
            });
            // Customer Report Exporter
            subMenuList.push({
              menuUrl: 'cre',
              menuNm: 'header.costSubMenu.report.customReport',
              menuId: svcItem.menuList[i].menuId,
              menuFullUrl: svcItem.menuList[i].menuFullUrl,
              isExternalService: true
            });
            // eslint-disable-next-line no-param-reassign
            svcItem.menuList[i]['subMenuList'] = subMenuList;
          }
        }
        // Savings Plans
        else if(svcItem.menuList[i].menuId== 'MTV3040'){
          //alert(JSON.stringify(svcItem.menuList[i]));
          let subMenuList = [];
          // Utilization
          subMenuList.push({
            menuUrl: '/savings-plans/utilization',
            menuNm: 'header.costSubMenu.savingsPlans.utilization',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/savings-plans/utilization'
          });
          // Coverage
          subMenuList.push({
            menuUrl: '/savings-plans/coverage',
            menuNm: 'header.costSubMenu.savingsPlans.coverage',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/savings-plans/coverage'
          });
          // Inventory
          subMenuList.push({
            menuUrl: '/savings-plans/inventory',
            menuNm: 'header.costSubMenu.savingsPlans.inventory',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/savings-plans/inventory'
          });

          // eslint-disable-next-line no-param-reassign
          svcItem.menuList[i]['subMenuList'] = subMenuList;

        }
        // Reservations
        else if(svcItem.menuList[i].menuId== 'MTV3050'){
          //alert(JSON.stringify(svcItem.menuList[i]));
          let subMenuList = [];
          // Utilization
          subMenuList.push({
            menuUrl: '/reservations/utilization',
            menuNm: 'pageTitle.reserved.subMenu.utilization',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/reservations/utilization'
          });
          // Coverage
          subMenuList.push({
            menuUrl: '/reservations/coverage',
            menuNm: 'pageTitle.reserved.subMenu.coverage',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/reservations/coverage'
          });
          // Inventory
          subMenuList.push({
            menuUrl: '/reservations/inventory',
            menuNm: 'pageTitle.reserved.subMenu.inventory',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/reservations/inventory'
          });

          // eslint-disable-next-line no-param-reassign
          svcItem.menuList[i]['subMenuList'] = subMenuList;
          //Azure Reservations
        }else if(svcItem.menuList[i].menuId== 'MTV3080'){
          let subMenuList = [];
          // Utilization
          subMenuList.push({
            menuUrl: '/azure-reservations/utilization',
            menuNm: 'pageTitle.AzureReserved.subMenu.utilization',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/azure-reservations/utilization'
          });
          // Inventory
          subMenuList.push({
            menuUrl: '/azure-reservations/inventory',
            menuNm: 'pageTitle.AzureReserved.subMenu.inventory',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/azure-reservations/inventory'
          });
          // RI Recommend
          subMenuList.push({
            menuUrl: '/azure-reservations/rirecommend',
            menuNm: 'pageTitle.AzureReserved.subMenu.recommendations',
            menuId: svcItem.menuList[i].menuId,
            menuFullUrl: '/azure-reservations/rirecommend'
          });

          // eslint-disable-next-line no-param-reassign
          svcItem.menuList[i]['subMenuList'] = subMenuList;
        }
      }

      // eslint-disable-next-line no-param-reassign
      svcItem.menuList = _.reject(svcItem.menuList, {menuId: 'SP900'});
    },
    onClickAllResults(allResult) {
      let params = {
        searchText: this.searchText,
        viewBy: allResult.viewBy
      };
      if (this.isCurrentRouteNameEqualTo(ROUTE_NAME.COST_ANALYTICS)) {
        this.$emit('clickAllResult', params);
        this.$refs.searchBar.$refs.search.blur()
      } else {
        this.$store.dispatch('common/setSearchFrom', this.$route.fullPath);
        this.$router.push({
          name: ROUTE_NAME.COST_ANALYTICS,
          params: {
            actionType: COST_ANALYTICS_ACTION_TYPE.SEARCH_BAR,
            group: SEARCH_BAR_RESULT_GROUP.ALL_RESULT,
            ...params
          }
        });
      }
    },
    // 최근 사용 메뉴 가져오기
    getRecentMenu: function() {
      let url = ENDPOINT.GNB.RECENT_MENU + '/' + this.getLocale;

      axios.get(url)
        .then(resp => {
          if(resp.data.status == 'ok') {
            this.recentMenuList = resp.data.result;
          }
        });
    },
    // 최근 사용 메뉴 저장
    saveRecentMenu() {

      //같은 매뉴 내에서 이동 시 저장하지 않음
      if(this.watchedUrl == this.getCurMenuURL) return

      this.watchedUrl = this.getCurMenuURL

      //요 부분 라우터 실행시 태우는걸로 변경할꺼임
      let apiUrl = ENDPOINT.GNB.RECENT_MENU

      let menuInfo = {
        svcId : this.constants.SERVICE.METERING,
        menuId : ''
      };

      if(this.watchedUrl == '/dashboard') {
        menuInfo.menuId = 'MTV3010';
      }


      if(menuInfo.menuId != '') {
        axios.post(apiUrl, menuInfo)
          .then(response => {
            if(response.data.status == 'ok'){
              this.getRecentMenu();
            }
          });
      }
    },
    // FreshService로 이동
    moveSupport: function () {
      window.open(this.urls.supportUrl)
    },
    onMouseOver() {
      this.$nextTick(() => {
        let elMultiselectOptionHighlight = document.getElementsByClassName('multiselect__option--highlight');
        elMultiselectOptionHighlight[0].style.backgroundColor = '#ffffff';
        elMultiselectOptionHighlight[0].onmouseout  = function() {
          this.style.backgroundColor = '#ffffff';
        }
      })
    },
    onMouseOut() {
      this.$nextTick(() => {
        let elMultiselectOptionHighlight = document.getElementsByClassName('multiselect__option--highlight');
        elMultiselectOptionHighlight[0].onmouseover = function() {
          this.style.backgroundColor = '#f8f9fa';
        }
      })
    },
    onClickMoreFilters() {
      if (this.isCurrentRouteNameEqualTo(ROUTE_NAME.COST_ANALYTICS)) {
        this.$emit('clickMoreFilterBtn');
        this.$refs.searchBar.$refs.search.blur()
      } else {
        this.$store.dispatch('common/setSearchFrom', this.$route.fullPath);
        this.$router.push({
          name: ROUTE_NAME.COST_ANALYTICS,
          params: {
            actionType: COST_ANALYTICS_ACTION_TYPE.SEARCH_BAR,
            group: SEARCH_BAR_RESULT_GROUP.MORE_FILTERS,
          }
        });
      }
    },
    hide() {
      this.isActive = false;
    },
    toggle () {
      this.isActive = true;
    },
    clearAllSelectedCountries() {
      this.selectedCountries = []
    },

    getGroupsOfNormalOptions(apiRes) {
      if (_isEmpty(apiRes)) {
        return;
      }
      apiRes.linkedAccount.sort((a, b) => normalOptionComparator(`${a.item}(${a.itemAlias})`, `${b.item}(${b.itemAlias})`));
      apiRes.product.sort((a, b) => normalOptionComparator(a.itemAlias, b.itemAlias));
      apiRes.tags.sort((a, b) => normalOptionComparator(a.item, b.item));
      let accountSearchOptions = {
        title: {
          title: this.$t('header.searchBar.options.title.account'),
          allResults: this.getAllResultsText(apiRes.linkedAccountCount),
          viewBy: COST_TREND_BY.ACCOUNT
        },
        details: apiRes.linkedAccount.map(item => {
          return {
            name: `${item.item}(${item.itemAlias})`,
            value: item.item,
            vendor: item.vendor,
            img: this.getImageOptions(item.vendor),
            viewBy: COST_TREND_BY.ACCOUNT,
            group: SEARCH_BAR_RESULT_GROUP.NORMAL_OPTIONS
          }
        })
      };
      let productSearchOptions = {
        title: {
          title: this.$t('header.searchBar.options.title.product'),
          allResults: this.getAllResultsText(apiRes.productCount),
          viewBy: COST_TREND_BY.PRODUCT
        },
        details: apiRes.product.map(item => {
          return {
            name: item.itemAlias,
            value: item.item,
            vendor: item.vendor,
            img: this.getImageOptions(item.vendor),
            viewBy: COST_TREND_BY.PRODUCT,
            group: SEARCH_BAR_RESULT_GROUP.NORMAL_OPTIONS
          }
        })
      };
      let tagsSearchOptions = {
        title: {
          title: this.$t('header.searchBar.options.title.tag'),
          allResults: this.getAllResultsText(apiRes.tagsCount),
          viewBy: COST_TREND_BY.TAG
        },
        details: apiRes.tags.map(item => {
          return {
            name: item.item,
            value: item.item,
            vendor: item.vendor,
            img: this.getImageOptions(item.vendor),
            viewBy: COST_TREND_BY.TAG,
            group: SEARCH_BAR_RESULT_GROUP.NORMAL_OPTIONS,
          }
        })
      };
      return [
        accountSearchOptions,
        productSearchOptions,
        tagsSearchOptions,
      ]
    },
    getAllResultsText(resultsCount) {
      let allResultsText = '';
      if (resultsCount > RESULTS_COUNT.ONE_THOUSAND) {
        if (Trans.currentLanguage === SUPPORTED_LANGUAGE.ZH) {
          allResultsText = `${this.$t('header.searchBar.options.title.found')} ${RESULTS_COUNT.ONE_THOUSAND}+ ${this.$t('header.searchBar.options.title.results')}`
        } else {
          allResultsText = `${RESULTS_COUNT.ONE_THOUSAND}+ ${this.$t('header.searchBar.options.title.results')} ${this.$t('header.searchBar.options.title.found')}`
        }
      } else if (resultsCount > RESULTS_COUNT.TEN) {
        if (Trans.currentLanguage === SUPPORTED_LANGUAGE.ZH) {
          allResultsText = `${this.$t('header.searchBar.options.title.found')} ${resultsCount} ${this.$t('header.searchBar.options.title.results')}`
        } else {
          allResultsText = `${resultsCount} ${this.$t('header.searchBar.options.title.results')} ${this.$t('header.searchBar.options.title.found')}`
        }
      } else {
        allResultsText = ``
      }
      return allResultsText
    },
    getImageOptions(vendor) {
      switch (vendor) {
        case VENDOR.AWS:
          return '/static/svg/icon_vendor_aws.svg';
        case VENDOR.AZURE:
          return '/static/svg/icon_vendor_azure.svg';
        case VENDOR.ALI:
          return '/static/svg/icon_vendor_alibaba.svg';
        case VENDOR.GCP:
          return '/static/svg/icon_vendor_gcp.svg';
        default:
          return '/static/svg/icon_vendor_aws.svg';
      }
    },
    getRecentSearchName(item) {
      switch (item.viewBy) {
        case COST_TREND_BY.ACCOUNT:
          return `${this.$t('header.searchBar.options.recentSearch.account')}: ${item.name}`;
        case COST_TREND_BY.PRODUCT:
          return `${this.$t('header.searchBar.options.recentSearch.product')}: ${item.name}`;
        case COST_TREND_BY.REGION:
          return `${this.$t('header.searchBar.options.recentSearch.region')}: ${item.name}`;
        case COST_TREND_BY.TAG:
          return `${this.$t('header.searchBar.options.recentSearch.tag')}: ${item.name}`;
      }
    },

    onChangeSearchText: _debounce(function (searchText) {
      this.isLoading = true;
      this.isShowNothingFound = false;
      this.searchText = searchText;
      if (!_isEmpty(searchText)) {
        let filteredOption = [];
        fetchResultSearch({word: searchText})
          .then((result) => {
            let groupsOfNormalOptions = this.getGroupsOfNormalOptions(result);
            this.isShowNothingFound = true;
            this.isLoading = false;
            if (_isEmpty(groupsOfNormalOptions)) {
              return;
            }
            groupsOfNormalOptions.forEach(group => {
              let newGroup = null
              group.details.map(option => {
                if (option.name.toLowerCase().includes(searchText.trim().toLowerCase())) {
                  if (!newGroup) {
                    newGroup = {
                      title: group.title,
                      details: []
                    }
                  }
                  if (newGroup.details.length < 10) {
                    newGroup.details.push(option)
                  }
                }
              })
              if (newGroup) {
                filteredOption.push(newGroup)
              }
            })
            if (filteredOption.length) {
              this.options = filteredOption
            } else {
              this.options = this.defaultOptions
            }
          })
      } else {
        this.options = this.defaultOptions
        this.isLoading = false;
      }
    }, SEARCH_DEBOUNCE_TIME),

    isCurrentRouteNameEqualTo(routeName) {
      return _get(this.$router, 'currentRoute.name') === routeName;
    },
    navigateToCostAnalytics(costTrendBy) {
      this.$router.push({
        name: ROUTE_NAME.COST_ANALYTICS,
        params : {
          actionType: COST_ANALYTICS_ACTION_TYPE.SEARCH_BAR,
          group: SEARCH_BAR_RESULT_GROUP.SUGGESTION,
          costTrendBy: costTrendBy
        }
      })
    },
    onClickSuggestion(selectedOption) {
      if (this.isCurrentRouteNameEqualTo(ROUTE_NAME.COST_ANALYTICS)) {
        this.$emit('selectSuggestion', selectedOption.value)
      } else {
        switch (selectedOption.value) {
          case SEARCH_BAR_SUGGESTION_VALUE.COST_OF_THIS_MONTH:
          case SEARCH_BAR_SUGGESTION_VALUE.COST_OF_THIS_MONTH_BY_ACCOUNT:
            this.navigateToCostAnalytics(COST_TREND_BY.ACCOUNT)
            break;
          case SEARCH_BAR_SUGGESTION_VALUE.COST_OF_THIS_MONTH_BY_PRODUCT:
            this.navigateToCostAnalytics(COST_TREND_BY.PRODUCT)
            break;
          default:
            this.$router.push({
              name: ROUTE_NAME.COST_ANALYTICS,
              params : {
                searchBarOption: ''
              }
            })
        }
      }
    },
    onClickNormalAndRecentOption(selectedOption) {
      let payloadData = [
        {
          dataKey: selectedOption.value,
          vendor: selectedOption.vendor
        }
      ];
      let recentSearches = getValueFromStorageByKey(LOCAL_STORAGE_KEY.RECENT_SEARCH) || [];
      if (recentSearches.some(item => item.value === selectedOption.value)) {
        recentSearches = recentSearches.filter(item => item.value !== selectedOption.value);
      }
      if (selectedOption.group === SEARCH_BAR_RESULT_GROUP.NORMAL_OPTIONS) {
        // eslint-disable-next-line no-param-reassign
        selectedOption.name = this.getRecentSearchName(selectedOption)
      }
      recentSearches.push(selectedOption);
      if (recentSearches.length > MAX_RECENT_SEARCH_COUNT) {
        recentSearches.shift();
      }
      setValueToStorageByKey(LOCAL_STORAGE_KEY.RECENT_SEARCH, recentSearches);
      if (this.isCurrentRouteNameEqualTo(ROUTE_NAME.COST_ANALYTICS)) {
        this.$emit('selectNormalOption', {
          data: payloadData,
          viewBy: selectedOption.viewBy
        })
      } else {
        this.$router.push({
          name: ROUTE_NAME.COST_ANALYTICS,
          params: {
            actionType: COST_ANALYTICS_ACTION_TYPE.SEARCH_BAR,
            group: SEARCH_BAR_RESULT_GROUP.NORMAL_OPTIONS,
            viewBy: selectedOption.viewBy,
            startDate: this.$dayjs.utc().startOf('month').startOf('day').format(DATE_FORMAT),
            endDate: this.$dayjs.utc().startOf('day').format(DATE_FORMAT),
            data: payloadData,
          }
        });
      }
    },
    openSearchText() {
      this.options = this.defaultOptions
      this.$emit('openSearchBar');
    },
    formatOptionText(optionText) {
      return toStringWithMatchesHighlighted(optionText, this.searchText);
    },
    // Log Out
    logout(force) {
    },
    onToggleUserDropdown() {
      this.$emit('toggleUserDropdown');
      this.$store.dispatch('costAnalytics/setIsCloseQuickFilterDropdown', true);
    },
    moveConsole(menuUrl){
      location.href = `${this.urls.consoleUrl}/${menuUrl.slice(1)}`
    },
    openAuthPopup: function() {
      this.$refs.AuthPopup.show();
    }
  },
};

function normalOptionComparator(a, b) {
  const aOptText = _toLower(a);
  const bOptText = _toLower(b);
  if (aOptText > bOptText) {
    return 1;
  }
  if (aOptText < bOptText) {
    return -1;
  }
  return 0;
}
</script>

<style lang="scss">
// @import "../../../../static/styles/bsp_external_gv.css";
@import "../../../assets/css/base/var";
@import "../../../assets/css/base/function";
@import "../../../../node_modules/vue-multiselect/dist/vue-multiselect.min.css";
@import "../../../../static/styles/bsp_external_gnb.css";
@import "../../../../static/styles/ui_pack.css";
.tooltip-notice {
  top: 17px!important;
  .arrow {
    &:before {
      border-bottom-color: #fff;
      opacity: 0.1 !important;
    }
  }
  .tooltip-inner {
    font-size: 11px;
    font-family: NotoSansCJKkr-Regular;
    color: #fff;
    background: #333c66;
    opacity: 0.6 !important;
    padding: 3px 8.3px;
    height: 22px;
  }
}
#header {
  padding-left: 12px;
  padding-right: 12px;
  background:#fff;
  height:$header-height;
  width:100%;
  box-shadow:0 2px 8px 0 rgba(0,0,0,.15);
  position:absolute;
  top:0;
  left:0;
  right:0;
  z-index: 9999;
  .logo {
    margin-top: 8px;
    cursor: pointer;
  }
  .face-icon {
    color: #898d94;
  }
  .user-name {
    color: #7b8088;
  }
  .header-icon-right {
    >button {
      margin-left: 6px;
      margin-right: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 24px;
      height: 24px;
      &:hover {
        background: #d5dae0;
        border-radius: 3px;
      }
      &.header-icon-notice {
        img {
          width: 13.3px;
          height: 16.3px;
        }
      }
      &.header-icon-support {
        img {
          width: 16.7px;
          height: 16.7px;
        }
      }
      &.header-icon-use-guide {
        margin-right: 12px;
        img {
          width: 13.3px;
          height: 16.7px;
        }
      }
    }
    .header-right-back-link {
      min-width: 94px;
      height: 24px;
      position: relative;
      margin-left: 14px;
      display: flex;
      justify-content: space-around;
      >.b-dropdown {
        display: inline-flex;
        vertical-align: middle;
        min-width: 94px;
        height: 100%;
        justify-content: center;
        border-radius: 5px;
        button {
          padding-left: 25px;
          padding-right: 25px;
        }
      }
      >.material-icons {
        padding-right: 2px!important;
        padding-left: 3px!important;
        position: absolute;
        top: 5px;
        left: 0px;
        z-index: 9999999;
        pointer-events: none;
        &.color-gray-1 {
          top: 3px;
          right: 0px;
          left: auto;
        }
      }
      button {
        >.material-icons {
          padding-right: 3px;
          padding-left: 0px;
        }
        .font-family-notosanscjkkr-medium {
          color: #7b8088!important;
          font-size: 12px;
        }
      }
      &.active {
        background: #d5dae0!important;
        border-radius: 3px;
        min-width: 94px;

      }
      &:before {
        content: '';
        position: absolute;
        top: 0px;
        left: -11px;
        width: 1px;
        height: 24px;
        background: #e8ebef;
      }
      &:hover {
        background: #d5dae0;
        border-radius: 4px;
      }
      &:focus {
        background: #d5dae0!important;
        border-radius: 4px!important;
        outline: aliceblue;
      }
      &:active {
        background: #d5dae0;
        border-radius: 4px;
      }
      .material-icons {
        padding-right: 5px;
        padding-left: 5px;
      }
      .dropdown {
        .dropdown-menu {
          min-width: 178px;
          height:auto;
          box-shadow: 0 4px 16px 2px rgba(124, 129, 148, 0.25);
          border: 0px;
          border-radius: 4px;
          padding-top: 0px;
          padding-bottom: 0px;
          overflow:hidden;
          overflow-y:hidden;
          top: 2px!important;
          left: 0px!important;
          li {
            height: 40px;
            &:hover {
              background: #f5f6fa;
            }
            &:first-child {
              border-bottom: 1px solid #e8ebef;
              position: relative;
              &:before {
                content: '';
                position: absolute;
                top: 39px;
                left: 0px;
                width: 7px;
                height: 1px;
                background: #fff;
              }
              &:after {
                content: '';
                position: absolute;
                top: 39px;
                right: 0px;
                width: 7px;
                height: 1px;
                background: #fff;
              }
            }
            &:last-child {
              border-top: 1px solid #e8ebef;
              &:before {
                content: '';
                position: absolute;
                bottom: 39px;
                left: 0px;
                width: 7px;
                height: 1px;
                background: #fff;
              }
              &:after {
                content: '';
                position: absolute;
                bottom: 39px;
                right: 0px;
                width: 7px;
                height: 1px;
                background: #fff;
              }
            }
            a {
              font-family: NotoSansCJKkr-Regular;
              font-size: 14px;
              color: #222222;
              padding: 9.5px 10px;
              &:active {
                background: #f5f6fa;
              }
            }
          }
        }
        .dropdown-toggle {
          &:hover {
            background: #d5dae0;
          }
        }
      }
    }
  }
  .multiselect {
    min-height: 30px;
    .multiselect__clear {
      position: relative;
      .close {
        position: absolute;
        top: 7px;
        right: 40px;
        cursor: pointer;
        z-index: 999;
        border-radius: 50%;
        background: #7b8088!important;
        color: #fff;
        font-size: 13px!important;
        padding: 2px;
        font-weight: 400;
        text-shadow: none;
        opacity: 1;
        &:hover {
          background: #7b8088;
          opacity: 1;
        }
      }
    }

    .multiselect__select {
      display: none;
    }
    .multiselect__tags {
      min-height: 30px;
      display: flex;
      padding: 1px 38px 0 8px;
      border-radius: 5px;
      font-size: 14px;
      height: 30px;
      width: 600px;
      background-color: #e8ebef;
      margin-bottom: 0px;
      cursor: text;
      input {
        cursor: text;
      }
      .multiselect__tags-wrap {
        display: none !important;
        .multiselect__tag {
          display: none !important;
        }
      }
      .multiselect__input {
        top: 5px;
        &::placeholder {
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
    .multiselect__input:focus .multiselect__tags {
      border: 1px solid red!important;
    }
    .multiselect__content-wrapper {
      position: absolute;
      width: 100%;
      max-height: 600px;
      border-radius: 4px;
      -webkit-box-shadow: 0 5px 20px 3px rgba(124, 129, 148, 0.25);
      box-shadow: 0 5px 20px 3px rgba(124, 129, 148, 0.25);
      background: #fff;
      top: 32px;
      .multiselect__content {
        .see-more-div {
          background: #fff;
          color: #0672ff;
          font-size: 12px;
          font-weight: 500;
          height: 48px;
          display: flex;
          align-items: center;
          position: relative;
          .see-more {
            width: 100%;
            border-top: 1px solid #e8ebef;
            margin: 0 20px 0 20px;
            padding: 12px 0;
            .see-more-text {
              cursor: pointer;
              line-height: 23px;
            }
          }
          &:hover {
            background: #f8f9fa;
            color: #0672ff;
            font-size: 12px;
            font-weight: 500;
          }

        }
        .multiselect__element {
          .multiselect__option--group {
            background: #fff!important;
            font-size: 12px;
            color: #222!important;
            margin-top: 8px;
            margin-bottom: 3px;
            .search-filter-sub2 {
              .all-results {
                color: #0672ff;
                font-weight: 500;
                font-size: 12px;
                padding-left: 7px;
                pointer-events: auto;
                cursor: pointer;
              }
            }
          }
          .multiselect__option {
            .search-filter-sub2 {
              padding: 3px 18px;
              .search-empty {
                padding-top: 5px;
                padding-bottom: 15px;
                color: #7b8088;
                font-size: 14px;
              }
              label {
                margin-bottom: 0px;
                display: flex;
                -webkit-box-align: center;
                -ms-flex-align: center;
                align-items: center;
                cursor: pointer !important;
                .orange {
                  display: inline-block;
                  color: #ffc34c;
                  font-size: 23px;
                  margin-right: -3px;
                  margin-left: -1px;
                  &:hover {
                    color: #ffc34c;
                  }
                }
                .gray {
                  display: inline-block;
                  color: #7B8088;
                  font-size: 23px;
                  margin-right: -3px;
                  margin-left: -1px;
                  &:hover {
                    color: #7B8088;
                  }
                }
                img {
                  max-width: 16px;
                  margin-right: 10px;
                }
                span {
                  font-size: 14px;
                  color: #222;
                }
              }
            }
            &.multiselect__option--highlight {
              background-color: #f8f9fa;
              &.multiselect__option--highlight:after {
                content: '';
              }
            }
            min-height: auto;
            padding: 0px;
          }
        }
      }
    }
  }
  .multiselect,
  .multiselect__input,
  .multiselect__single {
    font-family: inherit;
    font-size: 12px;
    background: inherit;
  }
}
.dropdown-menu {
  height:300px;
  overflow:hidden;
  overflow-y:auto;
}
.search-filter-first {
  position: relative;
  >.material-icons {
    position: absolute;
    right: 13px;
    top: 6px;
    z-index: 9999999999999;
    font-size: 21px;
    opacity: 0.5;
    cursor: pointer;
  }
  .remove-text-button {
    position: absolute;
    width: 17px;
    height: 17px;
    top: 7px;
    right: 40px;
    cursor: pointer;
    z-index: 999;
    border-radius: 100%;
    background-color: #7b8088 !important;
    color: #fff;
    text-shadow: none;
    opacity: 1;
    .custom-remove-text-button {
      margin-left: 2px;
      margin-top: 2px;
    }
  }
  .custom-loading-search {
    position: absolute;
    top: 14px;
    right: 44px;
    z-index: 99;
    background-color: #e8ebef;
    .spinner-icon {
      background: radial-gradient(#e8ebef 35%, transparent 1px),
      conic-gradient(
          rgba(75,222,255,0.2),
          rgba(75,222,255,0.5) 30%,
          rgba(34,165,247,0.7) 55%,
          rgba(38, 187, 240, 1) 82%,
          rgba(38, 187, 240, 0) 80%
      );

    }
  }
}

.no-gutters.left {
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.search-filter {
  position: relative;
  .search-filter-first {
    position: relative;
    input {
      width: 600px;
      height: 30px;
      border-radius: 4px;
      border: solid 1px #0672ff;
      background-color: #e8ebef;
      padding-left: 15px;
      padding-right: 15px;
    }
    i {
      position: absolute;
      right: 12px;
      top: 6px;
      opacity: 0.5;
      font-size: 21px;
    }
  }

  .search-filter-sub1 {
    position: absolute;
    width: 100%;
    max-height: 600px;
    border-radius: 4px;
    box-shadow: 0 5px 20px 3px rgba(124, 129, 148, 0.25);
    background: #fff;
    padding-right: 18px;
    padding-left: 18px;
    >li {
      margin-top: 16px;
      label {
        font-size: 12px;
        color: #222;
      }
    }
  }
  .search-filter-sub2 {
    margin-top: 7px;
    li {
      label {
        display: flex;
        align-items: center;
        margin-bottom: 6px;
        img {
          max-width: 16px;
          margin-right: 10px;
        }
        span {
          font-size: 14px;
          color: #222;
        }
      }
    }

  }
}
.more-filters {
  border-top: 1px solid #e8ebef;
  color: #0672ff;
  font-size: 12px;
  font-weight: 500;
  padding-bottom: 16px;
  padding-top: 16px;
}
.change-color {
  color: #007bff;
}
.search-limit {
  .search-limit-first{
    padding-top: 0px;
    padding-bottom: 4px;
    color: #7b8088;
    font-size: 14px;
  }
  .search-limit-last{
    background: #fff;
    color: #0672ff;
    font-size: 12px;
    font-weight: 500;
    border-top: 1px solid #e8ebef;
    height: 40px;
    display: flex;
    align-items: center;
    margin-top: 10px;
    position: relative;
  }
}
.company-dropdown {
  .dropdown-menu {
    padding-top: 0;
    padding-bottom: 0;
    height: auto !important;
    min-width: 120px;
    .dropdown-item {
      padding: 0 15px;
    }
  }
}

.user-dropdown {
  .dropdown-menu {
    .dropdown-item {
      font-family: NotoSansCJKkr-Regular;
      font-size: 14px;
      color: #222222;
      padding: 9.5px 10px;
      outline: none;
      outline: 0;
      box-shadow: none !important;
      &:not(:disabled):not(.disabled):active {
        background-color: #ffffff;
      }
    }
  }
}
@media only screen and (max-width: 1080px) {
  #wrapper {
    .search-filter-first {
      .multiselect {
        .multiselect__tags {
          width: 400px;
        }
      }
    }
  }
}
@media only screen and (max-width: 1024px) {
  #app #header {
    position: absolute !important;
    top: -25px !important;
    left: -83px;
    width: calc(100% + 117px);
  }
  #wrapper {
    .search-filter-first {
      .multiselect {
        .multiselect__tags {
          width: 400px;
        }
      }
    }
  }
  .cost-analytics-page {
    .cost-analytics-page-wrapper {
      #header {
        left: -54px !important;
      }
    }
  }
}
@media only screen and (max-width: 1023px) {
  #app #header {
    width: calc(107% + 61px);
    top: -25px !important;
    left: -84px;
  }
}
@media only screen and (max-width: 900px) {
  #wrapper {
    .search-filter-first {
      .multiselect {
        .multiselect__tags {
          width: 300px;
        }
      }
    }
  }
}
@media only screen and (max-width: 768px) {
  .cost-analytics-page {
    .cost-analytics-page-wrapper {
      #header {
        left: -2px !important;
        width: calc(107% + 33px)!important;
      }
    }
  }
}
.none-auth {
  &.modal.show{
    .modal-dialog {
      height: 100%;
    }
  }
}

</style>
