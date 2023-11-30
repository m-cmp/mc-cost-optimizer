export const SUPPORTED_LANGUAGE = {
  EN: 'en',
  KO: 'ko',
  ZH: 'zh',
  JA: 'ja'
}
export const DEFAULT_LANGUAGE = SUPPORTED_LANGUAGE.EN
export const FALLBACK_LANGUAGE = SUPPORTED_LANGUAGE.EN
export const SUPPORTED_LANGUAGES = Object.values(SUPPORTED_LANGUAGE)
export const LANGUAGE_OPTIONS = [
  {value: SUPPORTED_LANGUAGE.KO, text: '한국어'},
  {value: SUPPORTED_LANGUAGE.EN, text: 'English'},
  {value: SUPPORTED_LANGUAGE.ZH, text: '简体中文'}
  // {value: SUPPORTED_LANGUAGE.JA, text: '日本語'}, 나중에 다시 오픈할것임 
]
