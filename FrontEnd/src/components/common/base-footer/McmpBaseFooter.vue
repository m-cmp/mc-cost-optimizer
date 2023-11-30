<template>
  <b-row
    id="footer"
    :class="[isSidebarOpened ? 'side-bar-opened' : '']"
    align-h="between"
    align-v="center"
    no-gutters
  >
    <b-row
      class="mr-auto"
      no-gutters>
      <b-dropdown
        ref="languagePopup"
        class="custom-dropdown"
        variant="transparent"
        dropup
        @toggle="onToggleLanguageDropdown"
      >
        <template slot="button-content">
          <span class="current-language">
            {{ currentLanguage.text }}
          </span>
        </template>
        <b-dropdown-form
          v-for="(language, index) in languageOptions"
          :key="index"
        >
          <b-row
            no-gutters
            @click="onSelectLanguage(language)"
          >
            {{ language.text }}
          </b-row>
        </b-dropdown-form>
      </b-dropdown>
    </b-row>
    <b-row
      no-gutters
      class="utils"
    />
  </b-row>
</template>
<script>
import {mapGetters} from "vuex";
import ENDPOINT from "@/api/endpoints";
import {LANGUAGE_OPTIONS} from '@/constants/trans'
import {setValueToStorageByKey, LOCAL_STORAGE_KEY} from '@/util/localStorage';
import Profile from '@/components/common/profile';
import {CLASSIC_VERSION_PAGE} from '@/constants/constants'
import {SUPPORTED_LANGUAGE} from "../../../constants/trans";
import axios from "axios";
import _ from "lodash";

const env = Profile.env === 'PROD' || Profile.env === 'CHINA' || Profile.env === 'LGE'? 'PRODUCTION' : 'DEV';

export default {
  name: 'McmpBaseFooter',
  components: { },
  props: {
    isSidebarOpened: {
      type: Boolean,
      default: false
    },
  },
  data () {
    return {
      languageOptions: LANGUAGE_OPTIONS,
      currentLanguage: null,
      CLASSIC_VERSION_PAGE: CLASSIC_VERSION_PAGE
    }
  },
  computed: {
    ...mapGetters({
      language: 'common/language',
    }) ,
    getLocale: function() {
      return this.$i18n.locale;
    },
    getSite: function() {
      return location.hostname;
    },
    showzeService: function () {
      return !_.isEmpty(this.$store.state.loginUser);
    }
  },
  watch: {
    language: {
      handler() {
        this.currentLanguage = this.languageOptions.find(lang => lang.value === this.getLocale)
        //디폴트 언어 설정
        if(this.currentLanguage == undefined) {
          this.currentLanguage = {value: SUPPORTED_LANGUAGE.EN, text: 'English'}
        }
      },
      immediate: true
    },
    getLocale () {

    }
  },
  mounted() {

  },
  created: function(){

  },
  methods:{
    onToggleLanguageDropdown() {
      this.$store.dispatch('costAnalytics/setIsCloseQuickFilterDropdown', true);
    },
    onSelectLanguage(lang) {
      if (this.currentLanguage.value !== lang.value) {
        this.$i18n.locale = lang.value
        setValueToStorageByKey(LOCAL_STORAGE_KEY.LANGUAGE, lang.value)
        this.$cookies.set(this.constants.LangCode, lang.value, "1y" , '/', typeof(this.$store.state.sub_domain) !== this.constants.UNDEFINED ? this.$store.state.sub_domain : '')
        this.$store.dispatch('common/setLang', lang.value);
      }
      this.$refs.languagePopup.hide()
    },
    moveOtherService(actionUrl){
      let url = this.urls.portalUrl;
      url = url  + actionUrl;
      location.href = url;
    },
    // FreshService로 이동
    moveSupport: function () {
      window.open(this.urls.supportUrl)
    },
  },
};
</script>

<style lang="scss">
</style>
