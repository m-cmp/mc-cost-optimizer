/* eslint-disable no-param-reassign */

import store from '@/store'
import _isEmpty from "lodash/isEmpty";
import _ from "lodash";
import { MENU_URL } from '@/constants/constants';
import {DEFAULT_VENDOR_OPTIONS} from "../constants/constants";

function getSupportVendorsStringByNumberOfVendor(supportVendors, lang) {

  let vendors ='';
  if(lang == 'en') {
    if(supportVendors.length > 2) {
      supportVendors.forEach((vendor, index) => {
        if(supportVendors.length -1 !== index) {
          vendors += `${vendor}, `;
        } else {
          vendors += `and ${vendor}`;
        }
      })
    } else if(supportVendors.length > 1) {
      vendors = `${supportVendors[0]} and ${supportVendors[1]}`;
    } else {
      vendors = supportVendors;
    }
  } else if(lang == 'zh') {
    if(supportVendors.length > 1) {
      supportVendors.forEach((vendor, index) => {
        if(supportVendors.length -1 !== index) {
          vendors += `${vendor}、`
        } else {
          vendors += vendor
        }
      })
    } else {
      return supportVendors;
    }
  }else if(lang == 'ko') {
    if(supportVendors.length > 1) {
      supportVendors.forEach((vendor, index) => {
        if(supportVendors.length -1 !== index) {
          vendors += `${vendor}, `
        } else {
          vendors += vendor
        }
      })
    } else {
      return supportVendors;
    }
  }

  return vendors;
}

export function getSupportVendorsString(supportVendors, lang) {
  for (let i = 0; i < supportVendors.length; i++ ){
    if (supportVendors[i] === 'NCP'){
      supportVendors[i] = 'Ncloud';
    }
  }
  switch(lang) {
    case "ko":
      return getSupportVendorsStringByNumberOfVendor(supportVendors, 'ko');
    case "zh":
      return getSupportVendorsStringByNumberOfVendor(supportVendors, 'zh');
    case "en":
    case "ja":
      return getSupportVendorsStringByNumberOfVendor(supportVendors, 'en');
  }
}

export default{
  checkDouble: function (param){
      //not Double : return true
      //is Double :return false
      if(param===null || param==='') return false;
      param = param * 1;
      return !isNaN(param);
  },

  checkNum:function(param){
      //not Number : return true
      //is Number :return false
      if(('' + param).indexOf('.') !== -1 || param === '')
          return false;
      else{
          return this.checkDouble(param);
      }
  },

  formattedDate: function (dbDate) { //dbDate 는 millisecond 형태
      // 브라우저 api 로 timezone 세팅
      const localTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

      if ( localTimeZone ) {
          return moment.tz( dbDate, localTimeZone ).format("YYYY-MM-DD HH:mm:ss");
      }else { //timezone 계산이 안되는 브라우저의 경우
          return moment(dbDate).format("YYYY-MM-DD HH:mm:ss");
      }
  },
  uuid() {
      let lut = []; for (let i=0; i<256; i++) { lut[i] = (i<16?'0':'')+(i).toString(16); }
      let d0 = Math.random()*0xffffffff|0;
      let d1 = Math.random()*0xffffffff|0;
      let d2 = Math.random()*0xffffffff|0;
      let d3 = Math.random()*0xffffffff|0;
      return lut[d0&0xff]+lut[d0>>8&0xff]+lut[d0>>16&0xff]+lut[d0>>24&0xff]+'-'+
          lut[d1&0xff]+lut[d1>>8&0xff]+'-'+lut[d1>>16&0x0f|0x40]+lut[d1>>24&0xff]+'-'+
          lut[d2&0x3f|0x80]+lut[d2>>8&0xff]+'-'+lut[d2>>16&0xff]+lut[d2>>24&0xff]+
          lut[d3&0xff]+lut[d3>>8&0xff]+lut[d3>>16&0xff]+lut[d3>>24&0xff];
  },
  allVendors(){
      // return ['GCP' , 'AZURE'];
      let curCmpnId = store.state.loginUser.curCmpnId;
      let vendorInfo = store.state.vendorInfo;
      if(curCmpnId && vendorInfo &&vendorInfo.length > 0){
        return vendorInfo;
      }else{
        return [''];
      }
  },
  getDefaultVendorByCheckedAuth(vendor, availableVendors, isText){ // 권한 조회
      if(isText){
        //갑짜기 ncp 가 ncloud로 보여야 된다 그래서 땜빵 작업
        vendor = DEFAULT_VENDOR_OPTIONS.filter((vendorOption) => {
          return vendorOption.value === vendor
        })[0].text.toUpperCase()
        let upperCase = availableVendors.map(vendor => {return vendor.toUpperCase()});
        return !_isEmpty(upperCase) && upperCase.includes(vendor) && upperCase.indexOf(vendor) > -1
          ? availableVendors[upperCase.indexOf(vendor)] : availableVendors.filter(v=> v.toUpperCase() != vendor).map(v=>{return v})[0];
      }
      return !_isEmpty(availableVendors) && availableVendors.includes(vendor)
        ? vendor : availableVendors.filter(v=> v != vendor).map(v=>{return v})[0];
  },
  // getDefaultMultiVendorsByCheckedAuth(vendor, availableVendors, isText){ // 멀티 벤더 테스트
  //   if(isText){
  //     let upperCase = availableVendors.map(vendor => {return vendor.toUpperCase()});
  //     return !_isEmpty(upperCase)
  //     //&& upperCase.includes(vendor) && upperCase.indexOf(vendor) > -1 // Original
  //     &&vendor.filter(v => upperCase.includes(v) ).length  === vendor.length
  //       ? //availableVendors[upperCase.indexOf(vendor)] // Original
  //       vendor
  //       : availableVendors.filter(v=> v.toUpperCase() != vendor).map(v=>{return v});
  //   }
  //   return !_isEmpty(availableVendors) && vendor.filter(v => availableVendors.includes(v) ).length  === vendor.length
  //     ? vendor
  //     : availableVendors.filter(v=> vendor.indexOf(v.toUpperCase()) === -1  ).map(v=>{return v});
  // },
  checkVendorAvailableFromAllVendors(allVendors, availableVendors){
    return availableVendors.filter(v=>allVendors.includes(v.value)).length > 0
  },
  checkVendorAvailableFromSelectedVendor(vendor, availableVendors){
    return availableVendors.filter(v => v.value == vendor).length > 0
  },

  getDefaultMultiVendorsByCheckedAuth(vendor, availableVendors, isText){ // 멀티 벤더 테스트
    if(isText){
      let upperCase = availableVendors.map(vendor => {return vendor.toUpperCase()});
      return !_isEmpty(upperCase)
      //&& upperCase.includes(vendor) && upperCase.indexOf(vendor) > -1 // Original
      &&vendor.filter(v => upperCase.includes(v) ).length  === vendor.length
        ? //availableVendors[upperCase.indexOf(vendor)] // Original
        vendor
        : availableVendors.filter(v=> v.toUpperCase() != vendor).map(v=>{return v});
    }
    return !_isEmpty(availableVendors) && vendor.filter(v => availableVendors.includes(v) ).length  === vendor.length
      ? vendor : availableVendors.filter(v=> v != vendor).map(v=>{return v});
  },

  checkCostAnalyticsMenuAuth($vm){
    return !_.isNil(_.find(store.state.KENOBI_MENU_AUTH,function(menu){
      return menu.menuUrl === MENU_URL.COST_ANALYTICS &&
        ((menu.authTypeCd === $vm.constants.AUTH_TYPE.VIEW) || (menu.authTypeCd === $vm.constants.AUTH_TYPE.EDIT))  &&
        menu.menuId.startsWith($vm.constants.MENU_PRE_CODE)
    }));
  },

  checkCostAnomalyDetectionAuth($vm){
    return !_.isNil(_.find(store.state.KENOBI_MENU_AUTH,function(menu){
      return menu.menuUrl === MENU_URL.COST_ANOMALY_DETECTION &&
        ((menu.authTypeCd === $vm.constants.AUTH_TYPE.VIEW) || (menu.authTypeCd === $vm.constants.AUTH_TYPE.EDIT))  &&
        menu.menuId.startsWith($vm.constants.MENU_PRE_CODE)
    }));
  },

  convertUtcTimezoneToLocal: function(date){
        return  moment(moment.utc(date , 'YYYY-MM-DD HH:mm:ss').local()).format('YYYY-MM-DD HH:mm:ss');
    },
    convertUtcTimezoneToLocalByLang: function(date, lang){
        return  lang == 'en'
            ? moment(moment.utc(date , 'YYYY-MM-DD HH:mm:ss').local()).format('MM-DD-YYYY HH:mm:ss')
            : moment(moment.utc(date , 'YYYY-MM-DD HH:mm:ss').local()).format('YYYY-MM-DD HH:mm:ss');
    },
    convertDateDayByLang: function(date, lang){
        return  lang == 'en'
            ? moment(date).format('MM-DD-YYYY')
            : moment(date).format('YYYY-MM-DD');
    },
    viewPopup: (popupId) => {
        $.magnificPopup.open({
            items: {
                src: '#' + popupId
            },
        });
    },
    closePopup: () => {
        $.magnificPopup.close()
    },
    setAlert(title, content, callback) {
        let self = this;
        this.alert = $.confirm({
            title: title,
            content: content,
            buttons: {
                cancel: {
                    text: '확인',
                    btnClass : 'buttons',
                    action: function() {
                        // 취소 버튼 누를 때 callback 이 있는 경우 callback 실행
                        if(callback) {
                            return callback();
                        }
                    }
                }
            },
            onClose: function() {
                // 팝업 닫을 때 callback 있는 경우 callback 실행
                if(callback) {
                    return callback();
                }
            }
        });
    },
    setAlertBtnTranslate(title, content, btnText, url) {
        let self = this;
        this.alert = $.alert({
            animation: 'default',
            closeAnimation: 'default',
            closeIcon: true,
            title: title,
            //autoClose: 'cancel|3000',
            content: content,
            buttons: {
                cancel: {
                    text: btnText,
                    btnClass : 'buttons',
                    // action: function() {
                    //     // 취소 버튼 누를 때 callback 이 있는 경우 callback 실행
                    //     if(callback) {
                    //         return callback();
                    //     }
                    // }
                }
            },
            onClose : function(){
                if(url) {
                    location.href = url
                }
                // $.alert('Confirmed!');
                // // 팝업 닫을 때 callback 있는 경우 callback 실행
                // if(callback) {
                //     return callback();
                // }
            },
            onOpen: function() {
                setTimeout(()=>{
                    location.href = url
                    }, 3000);
            }
        });
    },
    setConfirmBtnCustom(title, content, leftBtnText, rightBtnText, callback) {
        let self = this;
        this.confirm = $.confirm({
            theme: 'theme-bsp',
            animation: 'default',
            closeAnimation: 'default',
            animationSpeed: 1000,
            container: 'body',
            containerFluid: false,
            useBootstrap: false,
            boxWidth: '500px',
            closeIcon: true,
            backgroundDismiss: false,
            backgroundDismissAnimation: 'none',
            title: title,
            content: content,
            buttons: {
                cancel: {
                    text: leftBtnText,
                    btnClass: 'buttons',
                    action: function() {
                        return callback(false);
                    }
                },
                confirm: {
                    text: rightBtnText,
                    btnClass: 'buttons type-warning',
                    action: function() {
                        return callback(true);
                    }
                }
            }
        });
    },

    /* 입력 관련 유효성 검사 Function */
    isEmptyText(text) {
        return text == null || typeof(text) == 'undefined' || text.replace(/ /g, '') == '';  // '　' ?z
    },
    isInUpperCaseLetter: (pwd) => {
        let upcase = /[A-Z]/; // 영문 소문자
        return upcase.test(pwd);
    },
    isInSmallLetter: (pwd) => {
        let low = /[a-z]/; // 영문 소문자
        return low.test(pwd);
    },
    isInSpecialCharacter: (pwd) => {
        let sp = /[^a-zA-Z0-9]/; // 영문 소,대문자, 숫자 외 문자
        return sp.test(pwd);
    },
    isInNumber: (pwd) => {
        let num = /[0-9]/; // 숫자
        return num.test(pwd);
    },
    validNumber: (pwd) => {
        let num = /[^0-9]/; // 숫자
        return num.test(pwd);
    },
    isInvalidEmail: (email) => {
        let isSuccess = false;

        // 1. @ 가 있는지 확인
        if(email.indexOf("@") > -1) {
            let local = email.substr(0, email.lastIndexOf("@"));
            let domain = email.substr(email.lastIndexOf("@") + 1, email.length);

            // 2. local 부분 검사
            /*
             * 첫 글자 공백 불가
              * 그 다음부터는 아래 내용대로 가능..
             *  */
            isSuccess = /^[\w\`0-9-=\[\]\\\;\',.\/~!@\#$%^&*\(\)+\{\}\|:"<>\?]+[\w\s\`0-9-=\[\]\\\;\',.\/~!@\#$%^&*\(\)+\{\}\|:"<>\?]*$/.test(local);

            // 3. domain 부분 길이 검사. 63자를 넘지않아야 함
            let domainLength = isSuccess && domain.substring(domain.lastIndexOf("@") + 1, domain.indexOf(".")).length;
            isSuccess =  isSuccess && ( domainLength < 1 ? false : domainLength < 63 );
            // 4. domain 부분 검사
            /*
             * 첫 글자 A-Z, a-z, 0-9 가능
             * 중간 A-Z, a-z, 0-9, -, _, . 가능
             * 마지막 A-Z, a-z, 0-9 가능
             */
            isSuccess = isSuccess && /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/.test(domain);
        }
        return !isSuccess;

    },
    clickTooltip(){
            $('[data-tirgger="tooltip"]').tooltipster({
                delay: 100,
                // trigger: 'click',
                animation: 'fade',
                updateAnimation: 'fade',
                side: 'right',
                arrow: true,
                contentAsHTML: true,
            });
    },

    round(i,fix){
        return i.toFixed(fix);
    },
    comma: (num, currency, digits) =>{

        if(digits === undefined){
            digits = 2;
        }

        // 2017.06.15 modified by sungho.hong : null 예외처리, return값이 String이기 때문에 Number Filtering은 하지 않습니다.
        if((num)) {
            var len, point, str, decimalPoint, decimalStr;

            num = Number(num).toFixed(digits);
            num = num + "";
            decimalPoint = digits > 0 ? num.indexOf('.') : num.length ;
            decimalStr = num.substring(decimalPoint, num.length);
            num = num.substring(0, decimalPoint);
            point = num.length % 3;
            len = num.length;

            str = num.substring(0, point);
            while (point < len) {
                if (str != "") str += ",";
                str += num.substring(point, point + 3);
                point += 3;
            }
            if(currency != '￦')
                str += decimalStr;
        } else {
            // 2017.06.15 modified by sungho.hong : null 일 경우 0으로 치환
            return 0;
        }

        return str;
    },
}
