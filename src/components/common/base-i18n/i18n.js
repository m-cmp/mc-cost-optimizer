import VueI18n from 'vue-i18n'
import Vue from 'vue'
import en from '@/lang/en.json'
import ko from '@/lang/ko.json'
import zh from '@/lang/zh.json'
import ja from '@/lang/ja.json'
import VueCookies from "vue-cookies";
import GlobalConstants from "../../../constants/globalConstants";

Vue.use(VueI18n)

const languages = {
  en: en,
  ko: ko,
  zh: zh,
  ja: ja
}
let defaultLocale;

if (VueCookies.get(GlobalConstants.LangCode)) {
  defaultLocale = VueCookies.get(GlobalConstants.LangCode);
} else {
  if (navigator.appVersion.indexOf('MSIE') > -1) { // Explorer
    defaultLocale = navigator['userLanguage'].split('-')[0];
  } else {
    if(location.host.indexOf('mcmp.cn') > -1) {
      defaultLocale = GlobalConstants.CHINESE;
    } else {
      defaultLocale = navigator.language.split('-')[0];
    }
  }
}
defaultLocale = /(en|ko|zh|ja)/gi.test(defaultLocale) ? defaultLocale : GlobalConstants.ENGLISH;

const messages = Object.assign(languages)

export const i18n = new VueI18n({
  locale: defaultLocale, // set locale
  fallbackLocale: 'en',
  messages: messages // set locale messages
})
