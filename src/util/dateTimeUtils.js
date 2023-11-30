import _isNil from 'lodash/isNil';
import dayjs from 'dayjs';
import {SUPPORTED_LANGUAGE} from "@/constants/trans";
import {Trans} from "@/components/common/base-i18n/Translation";

/**
 * Add '0' string to number less than 10
 * For example number is 9 -> '09'
 *
 * @param number
 * @returns {string}
 */
function add0StringToNumberLessThan10(number) {
  return ('0' + number).slice(-2);
}

function formatMonthYearBySlash(month, year) {
  return month + '/' + year;
}

function getMonthYearDashOfTodayBySlash() {
  const today = new Date();

  return formatMonthYearBySlash(add0StringToNumberLessThan10(today.getMonth() + 1), today.getFullYear())
}

function getFormattedLastMonthYear() {
  const today = new Date();

  return `${today.getFullYear()}${add0StringToNumberLessThan10(today.getMonth())}`;
}

function getFormattedCurrentMonthYear() {
  const today = new Date();
  return `${today.getFullYear()}${add0StringToNumberLessThan10(today.getMonth() + 1)}`;
}

function getFormattedCurrentWeek() {
  let formattedLastWeek = '';
  const today = new Date();
  const currentWeekNumber = dayjs().utc().week();
  if (currentWeekNumber === 1) {
    formattedLastWeek = `W52-${today.getFullYear() - 1}`;
  } else {
    formattedLastWeek = `W${(currentWeekNumber)}-${today.getFullYear()}`
  }

  return formattedLastWeek;
}

function getFormattedLastWeek() {
  let formattedLastWeek = '';
  const today = new Date();
  const currentWeekNumber = dayjs().utc().week();
  if (currentWeekNumber === 1) {
    formattedLastWeek = `W52-${today.getFullYear() - 1}`;
  } else {
    formattedLastWeek = `W${(currentWeekNumber - 1)}-${today.getFullYear()}`
  }

  return formattedLastWeek;
}

/**
 * Convert date to string YYYY-MM-DD
 *
 * @param date
 * @returns {string}
 */
function convertDateToString(date) {
  return date.getFullYear() + "-" + add0StringToNumberLessThan10(date.getMonth() + 1) + "-" + add0StringToNumberLessThan10(date.getDate());
}

/**
 * Check is future month base on year and month
 * Year > current year or year == current year and month > current month
 *
 * @param year
 * @param month
 * @returns {boolean}
 */
function isFutureMonth(year, month) {
  return (new Date().getFullYear() === parseInt(year) && new Date().getMonth() + 1 < parseInt(month) || new Date().getFullYear() < parseInt(year));
}

function splitYearMonthFromYYYYMM(yyyymm) {
  if (_isNil(yyyymm)) {
    return {};
  }
  return {
    year: yyyymm.substring(0, 4),
    month: yyyymm.substring(4, 6)
  };
}

/**
 * Split year month date from yyyy-mm-dd
 *
 * @param yyyymmdd (2019-08-25)
 * @returns {{}|{date: *, month: *, year: *}}
 */
function splitYearMonthDateFromYYYYMMDD(yyyymmdd) {
  if (_isNil(yyyymmdd)) {
    return {};
  }
  return {
    year: yyyymmdd.substring(0,4),
    month: yyyymmdd.substring(5,7),
    date: yyyymmdd.substring(8,10)
  };
}

/**
 * Split year month date from js date
 * @param jsDate
 * @returns {{}|{date: *, month: *, year: *}}
 */
function splitYearMonthDateFromJsDate(jsDate) {
  if (_isNil(jsDate)) {
    return {};
  }
  return {
    year: jsDate.getFullYear().toString(),
    month: add0StringToNumberLessThan10(jsDate.getMonth() + 1),
    date: add0StringToNumberLessThan10(jsDate.getDate())
  };
}

/**
 * Get week number by date
 * For example date = new Date('16-3-2019') -> Week number: 11
 *
 * @param {Date} date
 * @returns {number}
 */
function getWeekNumberByDate(date) {
  const dateTime = new Date(date.getTime());
  dateTime.setHours(0, 0, 0, 0);
  // Thursday in current week decides the year.
  dateTime.setDate(dateTime.getDate() + 3 - (dateTime.getDay() + 6) % 7);
  // January 4 is always in week 1.
  const weekOne = new Date(dateTime.getFullYear(), 0, 4);
  // Adjust to Thursday in week 1 and count number of weeks from date to week1.
  return 1 + Math.round(((dateTime.getTime() - weekOne.getTime()) / 86400000 - 3 + (weekOne.getDay() + 6) % 7) / 7);
}

/**
 * Get range of date by week number and year
 * For example weekNumber is 11, year is 2019 -> rangeOfDate is {startDate: '2019-03-11', endDate: '2019-03-17'}
 * Please refer this link to know more https://codepen.io/Venugopal46/pen/WrxdLY
 *
 * @param weekNumber
 * @param year
 * @returns {{startDate: string, endDate: string}}
 */
function getRangeOfDateByWeekNumber(weekNumber, year){
  const dateByYear = new Date('' + year + '');
  const numOfDaysPastSinceLastMonday = dateByYear.getDay() - 1;

  dateByYear.setDate(dateByYear.getDate() - numOfDaysPastSinceLastMonday);
  dateByYear.setDate(dateByYear.getDate() + (7 * (weekNumber - getWeekNumberByDate(dateByYear))));
  const startDate = (convertDateToString(dateByYear));

  dateByYear.setDate(dateByYear.getDate() + 6);
  const endDate = convertDateToString(dateByYear);

  return {
    startDate: startDate,
    endDate: endDate
  };
};

function getCurrentDateWithCurrentTimezone(dateFormat) {
  let date = new Date();
  // return dayjs(new Date(date.getTime() - date.getTimezoneOffset()*60000)).format(dateFormat ? dateFormat : 'YYYY-MM-DD');
  return dayjs().format(dateFormat ? dateFormat : 'YYYY-MM-DD');
}

function getWeekNumberOfDate(date) {
  let onejan = new Date(date.getFullYear(), 0, 1);
  return Math.ceil((((date - onejan) / 86400000) + onejan.getDay() + 1) / 7);
}

// Returns an array of dates between the two dates, please refer this link to know more https://stackoverflow.com/questions/4413590/javascript-get-array-of-dates-between-2-dates/50398144#50398144
function getDates(startDate, endDate, isMonthly=false) {
  let dates = [];
  if(isMonthly){
    for (let currentDate = startDate; currentDate <= endDate; currentDate.setMonth(currentDate.getMonth() + 1)) {
      if(dates.indexOf(dayjs(currentDate).format('YYYY-MM')) < 0){
        dates.push(dayjs(currentDate).format('YYYY-MM'));
      }
    }
  }else{
    for (let currentDate = startDate; currentDate <= endDate; currentDate.setDate(currentDate.getDate() + 1)) {
      dates.push(new Date(currentDate).toISOString().slice(0, 10));
    }
  }

  return dates;
}

function getFullDateFormatByLocalization(isMonthly = false) {
  if(isMonthly){
    if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
      return 'MM/YYYY';
    }
    return 'YYYY/MM';
  }else{
    if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
      return 'MM/DD/YYYY';
    }
    return 'YYYY/MM/DD';
  }
};

function getFullDateFormatByLocalizationWithSecond() {
  if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
    return 'MM/DD/YYYY hh:mm';
  }
  return 'YYYY/MM/DD hh:mm';
};


function getMonthYearDateFormatByLocalization() {
  if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
    return 'MM/YYYY';
  }
  return 'YYYY/MM';
};

function formatMonthYearByLocalization(month, year) {
  if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
    return `${month}/${year}`;
  }
  return `${year}/${month}`;
}

export {
  getRangeOfDateByWeekNumber,
  add0StringToNumberLessThan10,
  getMonthYearDashOfTodayBySlash,
  getFormattedLastWeek,
  getFormattedLastMonthYear,
  isFutureMonth,
  splitYearMonthDateFromJsDate,
  formatMonthYearBySlash,
  splitYearMonthFromYYYYMM,
  splitYearMonthDateFromYYYYMMDD,
  getCurrentDateWithCurrentTimezone,
  getFormattedCurrentMonthYear,
  getFormattedCurrentWeek,
  getWeekNumberOfDate,
  getDates,
  getFullDateFormatByLocalization,
  getFullDateFormatByLocalizationWithSecond,
  getMonthYearDateFormatByLocalization,
  formatMonthYearByLocalization
};
