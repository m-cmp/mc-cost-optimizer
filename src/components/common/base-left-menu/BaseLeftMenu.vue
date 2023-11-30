<template>
  <div
    :class="{'active': isSidebarActive}"
    class="left-menu-main"
    no-gutters>
    <nav
      :class="{'active': isSidebarActive}"
      class="menu-wrapper height-bottom scrolling">
      <div
        class="menu-scroll"
        style="height: calc(100% - 132px);">
        <ul>
          <template v-for="svc of getMenuList">
            <li
              v-if="svc.svcId !='whatap'"
              :id="'svc' + '_' + svc.svcId"
              :key = "svc.svcId"
              :class="svc.svcId =='metering' ? 'selected' : ''">
              <button
                :data-service="svc.svcId"
                :title="svc.svcNm"
                type="button"
                name="button"
                @click="toggleSidebarOpen"><span>{{ svc.svcNm }}</span>
                <em
                  v-if="svc.menuNewYn === 'Y'"
                  class="icon-version new "><strong class="montserrat-r ">N</strong></em>
                <em
                  v-if="svc.menuBetaYn === 'Y'"
                  class="icon-version"><strong class="montserrat-r ">B</strong></em>
                <em
                  v-if="svc.svcId === 'saas'"
                  class="ci ci-service-saas"/>
                <em
                  v-if="svc.svcId === 'cms'"
                  class="ci ci-service-cms"/>
                <em
                  v-if="svc.svcId === 'multicdn'"
                  class="ci ci-service-multicdn medium-"/>
                <em
                  v-if="svc.svcId === 'autosavings'"
                  class="ci ci-service-360-auto_savings medium-"/>
              </button>
            </li>
          </template>
          <!--이건 어디다?-->
          <!--<li class="no-service-list">-->
          <!--<button type="button" name="button" data-service="add" class="hover-btn"><span>menu add</span></button>-->
          <!--</li>-->
        </ul>
      </div>
      <div class="service-fold">
        <!--        <button-->
        <!--          type="button"-->
        <!--          name="button"-->
        <!--          data-icon="closelist"-->
        <!--          class="hover-btn"><span>close</span></button>-->
        <!--        <div-->
        <!--          data-service="recently"-->
        <!--          hidden>-->
        <!--          <h3>{{ $t('header.tooltipTitle.submenu.recent_history') }}&lt;!&ndash;최근 방문 기록&ndash;&gt;</h3>-->
        <!--          <ul>-->
        <!--            <li-->
        <!--              v-for="(recent,i) of recentMenuList"-->
        <!--              :key = "i"-->
        <!--            >-->
        <!--              <a-->
        <!--                href="#"-->
        <!--                onclick="return false;"-->
        <!--                @click="moveRecentMenu(recent)" >-->
        <!--                <span class="service">{{ recent.svcNm }}</span>-->
        <!--                {{ recent.menuNm }}-->
        <!--              </a>-->
        <!--            </li>-->
        <!--          </ul>-->
        <!--        </div>-->
        <template
          v-for="svc of getMenuList"
        >
          <div
            v-if="svc.svcAuthTypeCd !== 'AUTH_TYPE_ROLE_030'"
            :id="'menu' + '_' + svc.svcId"
            :key = "svc.svcId"
            :data-service="svc.svcId"
            :class="{'selected' : svc.svcId == 'metering' ,'whaTap' : svc.svcId == 'whatap'}"
            hidden>
            <h3
              class="font-family-notosanscjkkr-Bold">{{ svc.svcNm }}</h3>
            <ul class="service-submenus">
              <template v-for="(menu,i) of svc.menuList">
                <li
                  v-if="!(menu.authTypeCd == 'AUTH_TYPE_030' || menu.menuId =='SP900' || menu.menuId =='SP950')"
                  :key="i"
                  data-depth="off"
                  class="one">
                  <a
                    :class="{'selected' : getCurMenuURL.substr(1).split('/')[0] === menu.menuUrl.substr(1).split('/')[0] && menu.menuId.startsWith(constants.MENU_PRE_CODE)}"
                    href="#"
                    onclick="return false;"
                    @click="moveServiceMenu(svc.svcId, menu.menuId, menu.menuUrl, menu.menuFullUrl)">
                    {{ menu.menuNm }}
                    <em
                      v-if="menu.menuNewYn === 'Y'"
                      class="icon-version new "><strong class="montserrat-r ">New</strong></em>
                    <em
                      v-if="menu.menuBetaYn === 'Y'"
                      class="icon-version"><strong class="montserrat-r ">Beta</strong></em>
                    <em
                      v-if="menu.subMenuList"
                      onclick="return false;"
                      class="arrow"/>
                  </a>
                  <ul
                    v-for="(subMenu,j) of menu.subMenuList"
                    :key="j">
                    <li
                      data-depth="off">
                      <a
                        href="#"
                        onclick="return false;"
                        @click="moveServiceMenu(svc.svcId, subMenu.menuId, subMenu.menuPath, subMenu.menuFullUrl, menu.subYn === 'Y')">

                        {{ $t(subMenu.menuNm) }}</a>
                        <!-- 하위 Depth 가 더 필요하다면? 아래 형식으로 계속 추가-->
                        <!--                    <ul>-->
                        <!--                      <li><a href="#">3depth</a></li>-->
                        <!--                      <li><a href="#">3depth</a></li>-->
                        <!--                      <li><a href="#">3depth</a></li>-->
                        <!--                    </ul>-->
                    </li>
                  </ul>
                </li>
              </template>
            </ul>
          </div>
        </template>

      </div>
    </nav>
    <!-- <div class="gnb-left-menu"> -->
    <!-- <div class="gnb-left">
        <b-col>
          <b-row
            class="add-menu-sidebar"
            no-gutters
            align-h="center"
          >
            <b-button
              v-click-outside="hide"
              :pressed="true"
              :class="{'active': isSidebarActive}"
              variant="transparent"
              class="pseudo-color blue-1 background-color"
              data-service="recently"
              @click="toggleSidebarActive">
              <base-icon
                :original="true"
                name="icon_service_cost_management"
                color="#5d8bf8"/>
            </b-button>
          </b-row>
        </b-col>
      </div> -->
    <!-- <div
        :class="{'active': isSidebarActive}"
        class="main-sidebar">
        <ul class="main-sidebar-sub1">
          <li>
            <span>{{ $t('sidebarMenu.costManagement') }}</span>
            <i
              class="material-icons"
              @click="toggleSidebarActive">first_page</i>
          </li>
          <li @click="removeSearchFromState">
            <router-link to="/dashboard">{{ $t('sidebarMenu.dashboard') }}</router-link>
          </li>
          <li @click="removeSearchFromState">
            <router-link to="/cost-analytics">{{ $t('sidebarMenu.costAnalytics') }}</router-link>
          </li>
          <li @click="removeSearchFromState">
            <router-link to="/billing">{{ $t('sidebarMenu.billing') }}</router-link>
          </li>
        </ul>
      </div> -->
    <!-- </div> -->
    <div
      v-show="!canShowAdvancedFilter"
      class="main-left">
      <div class="main-left-1"/>
      <div
        :class="{'active': isSidebarActive}"
        class="main-left-2"/>
    </div>
    <div
      v-show="!canShowAdvancedFilter"
      class="main-right">
      <slot name="mainRight"/>
    </div>
  </div>
</template>

<script>
  import ClickOutside from 'vue-click-outside';
  import ENDPOINT from "@/api/endpoints";
  import { EventBus } from '../event-bus'
  import axios from 'axios';
  import constants from '../../../constants/globalConstants';
  import _ from "lodash";
  const lnb = require('./index')
  export default {
    name: "BaseLeftMenu",
    directives: {
      ClickOutside
    },
    props: {
      canShowAdvancedFilter: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        isSidebarActive: false,
        recentMenuList: [],
        watchedUrl: '',
      }
    },
    computed: {
        getLocale: function() {
            return this.$i18n.locale;
        },
        getMenuList: function(){
            return this.$store.state.svcMenuList
        },
        getCurMenuURL: function() {
            return '/' + this.$route.path.substr(1).split('/')[0]
        }
    },
    watch:{
      getMenuList: function(){
        setTimeout(function(){
          lnb.LNB.init()
        }, 200);
          // lnb.LNB.init()
      },
    },
    created: function (){
      // this.getRecentMenu()
    },
    mounted: function(){
      let self = this;
      // this.getRecentMenu();
      //   EventBus.$on("changeLocale", e => {
      //       this.getRecentMenu()
      //   });

      //LNB 마우스 오버/클릭 시 발생하는 이벤트 처리
      // let $menuWrapper = $('.menu-wrapper');
      // $menuWrapper.on('mouseleave',function(){
      //   self.isSidebarActive = false;
      //   self.$emit('setSidebarActive', self.isSidebarActive);
      // });
      // let $menuScroll = $menuWrapper.find('.menu-scroll');
      // let $menuSelect = $menuWrapper.find('.menu-scroll').find('>ul>li').not('.no-service-list');
      // $menuScroll.on('mouseenter',function(){
      //   window.innerWidth < 1105 ? self.isSidebarActive = false : self.isSidebarActive = true;
      //   // self.isSidebarActive = true;
      //   self.$emit('setSidebarActive', self.isSidebarActive);
      // });
      // $.map($menuSelect,function(value){
      //   $(value).on('mouseenter',function(){
      //     self.isSidebarActive = true;
      //     self.$emit('setSidebarActive', self.isSidebarActive);
      //   })
      // });

    },
    methods: {
      exceptMenuCheck(inputMenuId){
        return this.constants.MENU_EXCEPT.includes(inputMenuId)
      },
      checkMenuRender(subMenu) {
        if(!_.isEqual(this.profile.env, "DEV") && !_.isEqual(this.profile.env, "STAGE") && _.isEqual(subMenu.menuNm, "pageTitle.AzureReserved.subMenu.recommendations")) {
          if(_.isEqual(this.$store.state.loginUser.siteCd, "SBCK")) {
            return true;
          } else {
            return false;
          }
        }
        return true;
      },
      removeSearchFromState() {
        this.$store.dispatch('common/setSearchFrom', '');
      },
      hide() {
        if (window.innerWidth < 1025) {
          this.isSidebarActive = false;
          this.$emit('setSidebarActive', false);
        }
      },
      toggleSidebarActive() {
        //일단 사용 안함
        this.isSidebarActive = !this.isSidebarActive;
        this.$emit('setSidebarActive', this.isSidebarActive);
      },

      toggleSidebarOpen() {
        // if(window.innerWidth < 1025) {
        //   return;
        // }
        // if(!this.isSidebarActive) {
        //   this.isSidebarActive = true;
        //   this.$emit('setSidebarActive', this.isSidebarActive);
        // }
      },
      toggleSidebarClose() {
        if(this.isSidebarActive){
          this.isSidebarActive = false;
          this.$emit('setSidebarActive', this.isSidebarActive);
        }
      },
      moveRecentMenu: function (menu) {
          let url
          if (menu.svcId == 'metering') {
              this.$router.push(menu.menuUrl);
          } else if (menu.svcId == 'metering') {
              url = menu.actnUrl + '#' + menu.menuUrl
              location.href = url
          } else {
              url = menu.actnUrl + menu.menuUrl.substring(1)
              location.href = url
          }
        // this.toggleSidebarActive();
      },
      // Service Portal 메뉴 이동 Function
      movePortal: function (menuUrl) {
          if (!`${this.urls.portalUrl}`.includes('nuricloud')) {
              location.href = `${this.urls.portalUrl}/${menuUrl.substr(1)}`
          } else {
              window.open('https://www.nuricloud.com')
          }
      },
      moveService: function(svcId, svcActnUrl) {
          if (svcId == 'metering') {
              this._route.navigate(['/dashboard'])
          } else if (svcId == 'whatap') {
              window.open(svcActnUrl)
          } else {
              location.href = svcActnUrl
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
      saveRecentMenu: function() {

        //같은 매뉴 내에서 이동 시 저장하지 않음
        if(this.watchedUrl == this.getCurMenuURL) return

        this.watchedUrl = this.getCurMenuURL

        //요 부분 라우터 실행시 태우는걸로 변경할꺼임
        let apiUrl = ENDPOINT.GNB.RECENT_MENU;

        let menuInfo = {
          svcId : this.constants.SERVICE.METERING,
          menuId : ''
        };

        if(this.watchedUrl == '/userDashboard') {
          menuInfo.menuId = 'MT210';
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
      moveServiceMenu: function(svcId, menuId ,menuUrl, menuFullUrl, isExternalService) {
        if ((svcId == this.constants.SERVICE.METERING || svcId == this.constants.SERVICE.MAIN)
          && menuId.startsWith(this.constants.MENU_PRE_CODE)
          && !this.exceptMenuCheck(menuId) ) {
          this.$router.push(menuUrl);
        }
        else {
          if(isExternalService === true){
          //서브메뉴가 있는 메뉴들은 menuFullUrl이 제일 마지막 부분만 뺴고 들어오고 있어 menuUrl에서 마지막 부분만 붙여주면 특정 메뉴로 잘 이동됨
          if(menuUrl === 'cre'){
            location.href = menuFullUrl.split('report')[0] + 'cre/'
          } else {
            location.href = menuFullUrl
          }
        }else{
          location.href = menuFullUrl
        }

        }
      }
    }
  }
</script>

<style scoped lang="scss">
  @import "src/assets/css/base/var";
  @import "src/assets/css/base/function";
  .gnb-left-menu {
    background: #fff;
    top: $header-height;
    bottom: 0;
    border: 0px;
    border-right-width: 1px;
    border-style: solid;
    border-color: #EAECEF;
    z-index: z('gnb');
    display: flex;
    margin-top: -95px;
    position: fixed;
    top: 135px!important;
    left: 0px;
    .gnb-left {
      width: 52px;
      border-right: 1px solid #e8ebef;
      position: relative;
    }
    .main-sidebar {
      background: #fff;
      width: 240px;
      height: 100vh;
      padding: 0px 12px;
      display: none;
      &.active {
        display: block;
        border-right: 1px solid #e8ebef;
      }
      .main-sidebar-sub1 {
        .router-link-exact-active {
          color: #222222;
          font-family: NotoSansCJKkr-Bold;
        }
        li:first-child {
          height: 60px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          border-bottom: 1px solid #e8ebef;
          padding-left: 8px;
          padding-right: 0px;
          margin-bottom: 8px;
          position: relative;
          &:before {
            content: '';
            position: absolute;
            top: 0px;
            left: -13px;
            background: #fff;
            width: 1px;
            height: 60px;
          }
          &:after {
            content: '';
            position: absolute;
            bottom: -1px;
            left: -62px;
            background: #e8ebef;
            width: 50px;
            height: 1px;
          }
          &:hover {
            background: #ffffff;
          }
          >span {
            font-size: 16px;
            font-weight: 500;
            color: #222222;
            font-family: 'Montserrat-Medium';
          }
          >i {
            width: 32px;
            height: 32px;
            font-size: 18px;
            color: #898d94;
            display: flex;
            align-items: center;
            justify-content: center;
            &:hover {
              border-radius: 4px;
              background: #d5dae0;
              cursor: pointer;
            }
          }
        }
        li {
          height: 40px;
          display: flex;
          align-items: center;
          &:hover {
            background: #e9ebf5;
          }
          a {
            color: #777777;
            font-family: NotoSansCJKkr-Regular;
            font-size: 12px;
            padding-left: 8px;
            width: 100%;
            &:active {
              font-family: NotoSansCJKkr-Bold;
              color: #222222;
            }
            &:hover {
              text-decoration: none;
            }
          }
        }
      }
    }
    .setting {
      position:absolute;
      bottom:20px;
      left:0;
      .btn {
        height:auto;
        &:before,
        &:after {
          content:none;
        }
      }
    }
    .col {
      padding:0;
    }
    .btn {
      height:60px;
      width:100%;
      display:block;
      padding:0;
      position:relative;
      &.active {
        &:before{
          content:'';
          position:absolute;
          left:0;
          top:0;
          bottom:0;
          width:4px;
        }
      }
    }
  }
</style>
