import {CURRENCY, CURRENCY_SYMBOL, KRW_CURRENCY_FIELDS, USD_CURRENCY_FIELDS} from '@/constants/constants';

/**
 * Get currency symbol by field
 *
 * @param field
 */
function getCurrencySymbolByField(field) {
  if (KRW_CURRENCY_FIELDS.includes(field)) {
    return CURRENCY_SYMBOL.KRW;
  }
  if (USD_CURRENCY_FIELDS.includes(field)) {
    return CURRENCY_SYMBOL.USD;
  }

  return '';
}

/**
 * Get currency by field
 *
 * @param field
 */
function getCurrencyByField(field, currnecy) {
  if (KRW_CURRENCY_FIELDS.includes(field) && ( currnecy === undefined || currnecy === CURRENCY.KRW)) {
    return CURRENCY.KRW;
  }else if (USD_CURRENCY_FIELDS.includes(field)) {
    return CURRENCY[currnecy];
  }else{
    return CURRENCY[currnecy];
  }

  return '';
}

export {
  getCurrencySymbolByField,
  getCurrencyByField,
}

