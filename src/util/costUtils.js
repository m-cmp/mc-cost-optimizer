import numbro from 'numbro';
import _isNil from 'lodash/isNil';
import {CURRENCY, CURRENCY_SYMBOL, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
import store from '@/store';

/**
 * Designed for cost formatting, but can also used for other types by providing custom format options
 * @param cost the raw value
 * @param options {object} to override default format
 */
function formatCost(cost, options) {
  let DEFAULT_COST_FORMAT = {
    trimMantissa: false,
    optionalMantissa: false,
    thousandSeparated: true,
    mantissa: getMantissaByCurrency(store.getters['common/info'].selectedCurrency),
    forceSign: false,
    average: false,
  };
  let format = {
    ...DEFAULT_COST_FORMAT,
    ...options
  };

  return numbro(zeroIfNil(cost)).format(format);
}

function formatCostForExport(cost, options) {

  return zeroIfNil(cost);
}

function formatPercentage(percentage, options) {
  const DEFAULT_PERCENTAGE_FORMAT = {
    trimMantissa: false,
    optionalMantissa: false,
    thousandSeparated: true,
    mantissa: 2,
    forceSign: false,
    average: false,
  };
  let format = {
    ...DEFAULT_PERCENTAGE_FORMAT,
    ...options
  };
  return numbro(zeroIfNil(percentage)).format(format);
}

function zeroIfNil(cost) {
  return _isNil(cost) ? 0 : cost;
}

function getMantissaByCurrency(currency) {
  switch (currency) {
    case CURRENCY.KRW:
      return 0;
    default:
      return 2;
  }
}

function calculateCostByCurrency(costInUsd, currency, exchangeRate, isGCP = false) {
  const parsedCostInUsd = Number(costInUsd);
  if (_isNil(currency)) {
    return parsedCostInUsd;
  }
  if (_isNil(exchangeRate)) {
    return parsedCostInUsd * DEFAULT_EXCHANGE_RATE[currency];
  }

  let rate = exchangeRate[currency];
  if(currency === 'CNY'){
    rate = Math.round(rate*100) / 100; //[OIO-5358] 위안화 환율 반올림 원복
  }

  if(isGCP && currency === 'KRW') {
    if(parsedCostInUsd > 0) return Math.floor(parsedCostInUsd * rate);
    else return Math.floor(Math.abs(parsedCostInUsd) * rate) * -1;
    //return Math.floor(parsedCostInUsd * rate);
  }

  return Math.round((parsedCostInUsd * rate ) * 100) / 100;
}

function calculateCostByCurrencyForExport(costInUsd, currency, exchangeRate, isGCP) {
  const parsedCostInUsd = Number(costInUsd);
  if (_isNil(currency)) {
    return parsedCostInUsd;
  }
  if (_isNil(exchangeRate)) {
    return parsedCostInUsd * DEFAULT_EXCHANGE_RATE[currency];
  }

  let rate = exchangeRate[currency];
  if(currency === 'CNY'){
    rate = Math.round(rate*100) / 100;
  }

  if(isGCP && currency === 'KRW') {
    return Math.floor(parsedCostInUsd * rate);
  }

  return Math.round((parsedCostInUsd * rate ) * 100000000) / 100000000;
}

function formatDisplayNumber(number) {
  if (number >= 1000) {
    return Math.round((number / 1000)) + 'K';
  } else {
    return formatCost(number);
  }
}

function shortenDisplayNumber(number) {
  if (number >= 1000000) {
    return Math.round((number / 1000000)) + 'M';
  } else if (number >= 1000) {
    return Math.round((number / 1000)) + 'K';
  }
  return formatCost(number);
}

function roundNumberBaseOnCurrency(number, selectedCurrency) {
  // if (selectedCurrency !== CURRENCY.KRW) {
  //   return Math.round(number * 100) / 100;
  // }
  // return Math.round(number);
  return Math.round(number * 100) / 100;
}

function formatCostBaseOnSelectedCurrencySymbol(cost, selectedCurrencySymbol) {
  let mantissa = selectedCurrencySymbol === CURRENCY_SYMBOL.KRW ? 0 : 2;
  if (_isNil(cost)) {
    return formatCost(0, {mantissa: mantissa});
  }

  return formatCost(cost, {mantissa: mantissa});
}

function formatCostBaseOnSelectedCurrency(cost, selectedCurrency) {
  let mantissa = selectedCurrency === CURRENCY.KRW ? 0 : 2;
  if (_isNil(cost)) {
    return formatCost(0, {mantissa: mantissa});
  }

  return formatCost(cost, {mantissa: mantissa});
}

function formatCount(count) {
  const DEFAULT_COUNT_FORMAT = {
    thousandSeparated: true,
  };
  return numbro(zeroIfNil(count)).format(DEFAULT_COUNT_FORMAT);
}

/**
 * 현재 선택된 통화 심볼
 * @returns 통화 심볼
 */
function getSelectedCurrencySymbol() {
  return CURRENCY_SYMBOL[store.getters['common/info'].selectedCurrency];
}

// 소수점 8번째 자리까지
function exportCostsForNumber(cost){
  if (_isNil(cost)) {
    return Math.round(zeroIfNil(cost) * 100000000) / 100000000;
  }
  return Math.round(cost * 100000000) / 100000000;
}

// 소수점 8번째 자리까지
function exportUsagesForNumber(cost){
  if (_isNil(cost)) {
    return Math.round(zeroIfNil(cost) * 100000000) / 100000000;
  }
  return Math.round(cost * 100000000) / 100000000;
}

function customFormatterForDetailCosts(num){
  if( typeof (num) === 'number'){
    return (Math.round(Number(parseFloat(num)) * 100) / 100).toLocaleString('en');
  }else{
    return Number(num).toLocaleString('en');
  }
}

function customFormatterForDetailUsages(num){
  if( typeof (num) === 'number'){
    return (Math.round(Number(parseFloat(num)) * 100) / 100).toLocaleString('en');
  }else{
    return Number(num).toLocaleString('en');
  }
}

/**
 * 천자리 컴마 처리
 * @param val 숫자
 * @param fix 소수점 자리수
 * @returns {string} 포맷 숫자
 */
function numberComma(val, fix) {
  if(!val) return '0';
  let point = 2;

  if(fix && typeof fix) {
    point = fix;
  }

  // 숫자를 문자로 변환
  let str = point<0 ? String(Number.parseFloat(val)) : String(Number.parseFloat(val).toFixed(point));

  // 소수점인 경우 처리
  if(str.indexOf('.')>-1) {
    let p = str.split(".");
    let p1 = p[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    let p2 = p[1]?'.'+p[1]:'';

    while(p2.endsWith('0')) {
      p2 = p2.substring(0, p2.length-1);
    }

    return p1 + (p2==='.'?'':p2);
  }

  return str.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

export {
  formatCost,
  formatCostForExport,
  calculateCostByCurrency,
  calculateCostByCurrencyForExport,
  formatPercentage,
  formatDisplayNumber,
  roundNumberBaseOnCurrency,
  formatCostBaseOnSelectedCurrency,
  formatCostBaseOnSelectedCurrencySymbol,
  shortenDisplayNumber,
  formatCount,
  getSelectedCurrencySymbol,
  exportCostsForNumber,
  exportUsagesForNumber,
  customFormatterForDetailCosts,
  customFormatterForDetailUsages,
  numberComma
};
