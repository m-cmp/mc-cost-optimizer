<template>
  <div
    v-if="$store.state.loginUser.curCmpnId !== null"
    id="app">
    <router-view/>
  </div>
</template>

<script>
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';
import './components/icons';
import {EventBus} from './components/common/event-bus';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import _isNil from 'lodash/isNil';
import {BROWSER} from "@/constants/constants";
import ENDPOINT from "@/api/endpoints";
import {getValueFromStorageByKey, setValueToStorageByKey, LOCAL_STORAGE_KEY} from '@/util/localStorage';
import {SUPPORTED_LANGUAGE} from '@/constants/trans'


export default {
  name: 'App',
  data: function() {
    return {
      tokenKey:'',
      LoginInfoCheckedFlag: false
    }
  },
  computed: {
    getLocale: function() {
      return this.$i18n.locale;
    }
  },
  watch: {
    getLocale: function() {
      this.broadcastingLocaleChanged()
    },
  },
  created() {
    this.init();
    this.$store.dispatch('common/setBrowser', this.getBrowser());
  },
  methods: {
    init(){
      let self = this;
      return Promise.resolve(mcmpAPIResponseDummyData.loadDashboard.currentLoggedIn.result)
        .then(json => {
          self.$store.commit('setLoginUser', json);
        })
    },
    getBrowser() {
      if (/Trident\/|MSIE/.test(window.navigator.userAgent)) {
        return BROWSER.IE
      }
      return BROWSER.CHROME
    },


    // login(pathname) {
    //   const url = `${this.urls.ssoUrl}/oauth/clientId`;
    //
    //   let client_id = '';
    //   // let hash = window.location.hash;
    //   // let redirect_url = (hash == '/' || hash == '#/') ? '%23/dashboard' : hash.replace('#', '%23');
    //   axios.get(url).then((result) => {
    //     if (result.data.client_id) {
    //       client_id = result.data.client_id
    //       location.href = `${this.urls.ssoUrl}/oauth/authorize/sitecode/token?response_type=token&client_id=${client_id}&redirect_uri=${this.urls.kenobiUrl + pathname}&scope=all&state=`
    //     }
    //   })
    // },
    getQueryParams(param) {

      var result = window.location.search.match(
        new RegExp("(\\?|&)" + param + "(\\[\\])?=([^&]*)")
      )
      return result ? result[3] : false
    },
    getWhiteLabelDomain(hostname) {
      return '.' + hostname.split('.').slice(1).join('.')
    },
    broadcastingLocaleChanged: function(){
      EventBus.$emit('changeLocale', this.getLocale);
    }
  }
};
</script>

<style lang="scss">
  @import "assets/css/base/index";
  @import '../node_modules/material-icons/iconfont/material-icons.css';
  body {
    scroll-behavior: smooth;
    #app{
      background:#F2F4F6;
      height: calc(100vh - 35px);
      overflow: auto;
      #header {
        position: fixed;
        top: 0px;
      }
      #title {
        position: fixed;
      }
    }
  }
  // Stack Chart 아이콘 z-index 전역처리
  .btn-group > .btn:focus, .btn-group > .btn:active, .btn-group > .btn.active, .btn-group-vertical > .btn:focus, .btn-group-vertical > .btn:active, .btn-group-vertical > .btn.active {
    z-index: 0;
  }
</style>
