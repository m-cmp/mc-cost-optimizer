/* eslint-disable no-undef */
(function (root, factory) {
    if (typeof define === "function" && define.amd) {
        define([], factory);
    } else if (typeof module === "object" && module.exports) {
        module.exports = factory();
    } else {
        root.ps = factory();
    }
    })(typeof self !== "undefined" ? self : this, function () {
        var ps = {
        //배포된 버전입니다.
        version: "0.0.1",
        //키보드에서 입력시 실제로 입력되는 값을 의미합니다.
        //예를 들어 event.keycode 함수로 유저가 입력한 키보드 값을 알 수 있습니다.
        //이때 if (event.keycode = ps.str.keycode.ENTER)
        //라는 함수가 true가 나올 경우 유저는 Enter를 클릭한 것으로 알수 있습니다.
        keyCode: {
            BACKSPACE: 8,
            COMMA: 188,
            DELETE: 46,
            DOWN: 40,
            END: 35,
            ENTER: 13,
            ESCAPE: 27,
            HOME: 36,
            LEFT: 37,
            PAGE_DOWN: 34,
            PAGE_UP: 33,
            PERIOD: 190,
            RIGHT: 39,
            SPACE: 32,
            TAB: 9,
            UP: 38,
        },
        //날자포멧
        sDateFormat: "yyyymmdd",
        //시간 포멧
        sTimeFormat: "hh:mi:ss",
        //문자열에 대한 공통 함수 시작
        str: {
            //nullcheck 부분, param이 null일경우 true값을 반환
            isNull: function (sVal) {
                return (
                    sVal === undefined ||
                    sVal === null ||
                    sVal === "null" ||
                    sVal === "&nbsp;" ||
                    sVal.length === 0
                );
            },
            //첫번째 param이 null인경우 두번째 param으로 값을 치환해줌
            nvl: function (sVal, sNullVal) {
                return ps.str.isNull(sVal) ? sNullVal : sVal;
            },
            //param에 해당하는 값을 문자열로 치환함
            addQuote: function (sVal) {
                if (ps.str.isNull(sVal)) return "''";
    
                return sVal.indexOf("'") > -1 ? '"' + sVal + '"' : "'" + sVal + "'";
            },
            //param이 문자열로 따옴표/쌍따옴표가 붙을 경우 삭제함
            stripQuote: function (sVal) {
                if (ps.str.isNull(sVal)) return sVal;
        
                return (sVal.charAt(0) === "'" || sVal.charAt(0) === '"') &&
                    (sVal.charAt(sVal.length - 1) === "'" ||
                    sVal.charAt(sVal.length - 1) === '"')
                    ? sVal.substring(1, sVal.length - 1)
                    : sVal;
            },
            //첫번째 param안에 두번째 param을 나눈뒤 세번째 param으로 바꿈
            replaceAll: function (sVal, sOriVal, sChangeVal) {
                if (ps.str.isNull(sVal)) return sVal;
        
                return sVal.split(sOriVal).join(sChangeVal);
            },
            //두번째 Param이 첫번째 param의 문자열 길이보다 큰 만큼 세번째 Param을 왼쪽에 반복함
            padLeft: function (sVal, nLength, sChar) {
                sVal = ps.str.isNull(sVal) ? "" : sVal;
                sChar = ps.str.isNull(sChar) ? " " : sChar;
        
                var sRetVal = "";
        
                for (var i = 0; i < nLength - sVal.length; i++) sRetVal += sChar;
        
                return sRetVal + sVal;
            },
            //두번째 Param이 첫번째 param의 문자열 길이보다 큰 만큼 세번째 Param을 오른쪽에 반복함
            padRight: function (sVal, nLength, sChar) {
                sVal = ps.str.isNull(sVal) ? "" : sVal;
                sChar = ps.str.isNull(sChar) ? " " : sChar;
        
                var sRetVal = "";
        
                for (var i = 0; i < nLength - sVal.length; i++) sRetVal += sChar;
        
                return sVal + sRetVal;
            },
            //첫번째 Param에서 두번째 param에 작성된 구분자를 모두 삭제함
            removeDelimiter: function (sVal, sDelimiter) {
                if (ps.str.isNull(sVal)) return sVal;
        
                return ps.str.replaceAll(sVal, sDelimiter, "");
            },
            //첫번째 param의 문자열을 두번째 param의 길이만큼 나눠 세번째 param에 해당하는 구분자를 넣는다.
            //두번째 Param은 "3,2,4" 이런식으로 넣어야 하며, 1234567890을 앞의 param으로 나눈다면, 123-45-6789 로 나눠진다. 4를 넘는 문자열은 삭제한다.
            appendDelimiter: function (sVal, sTokenSize, sDelimiter) {
                if (ps.str.isNull(sVal)) return sVal;
        
                var sEmpty = (sFormat = sRetVal = "");
                var arrToken = (arrTemp = null);
                var nTokenCnt = (nIndex = 0);
        
                if (!isNaN(Number(ps.str.removeDelimiter(sTokenSize, ",")))) {
                    arrToken = sTokenSize.split(",");
                    nTokenCnt = arrToken.length;
                    if (nTokenCnt > 0) {
                    sFormat = ps.str.padLeft(sEmpty, arrToken[0], "@");
                    for (var i = 1; i < nTokenCnt; i++) {
                        sFormat +=
                        sDelimiter + ps.str.padLeft(sEmpty, Number(arrToken[i]), "@");
                    }
                    }
                }
        
                if (sFormat.length === 0) {
                    sRetVal = sVal;
                } else {
                    var nIndex = 0;
                    var arrTemp = sFormat.split(sDelimiter);
        
                    for (var j = 0; j < arrTemp.length; j++) {
                    if (j == 0) sRetVal += sVal.substr(nIndex, arrTemp[j].length);
                    else sRetVal += sDelimiter + sVal.substr(nIndex, arrTemp[j].length);
        
                    nIndex = nIndex + arrTemp[j].length;
                    }
                }
        
                return sRetVal;
            },
            //첫번째 param의 특수문자를 모두 제거한다.
            removeSpecialChar: function (sVal) {
                if (ps.str.isNull(sVal)) return sVal;
        
                var sSpecial = "~!@#$%^&*-+:;,./=_`{|}()[]\\?<>";
                var sRetValue = "";
        
                for (var i = 0; i < sVal.length; i++) {
                    if (sSpecial.indexOf(sVal.charAt(i)) == -1)
                    sRetValue += sVal.charAt(i);
                }
        
                return sRetValue;
            },
            //첫번째 param의 숫자를 화폐단위로 변환하고 두번째 param의 숫자 만큼 소숫점으로 나타낸다.
            //소숫점 나머지는 버림으로 처리한다.
            toFixedLocaleString: function (nNum, iFractionDigit) {
                if (ps.str.isNull(nNum)) return nNum;
                if (ps.str.isNull(iFractionDigit)) iFractionDigit = 0;
        
                var aNum = String(nNum).split(".");
        
                return (
                    Number(aNum[0]).toLocaleString() +
                    (aNum.length > 1 ? "." + aNum[1].substr(0, iFractionDigit) : "")
                );
            },
            //
            lengthB: function (sVal) {
                var nBytes = 0,
                    nLen = 0;
        
                for (var i = 0; i < sVal.length; i++) {
                    nLen = escape(sVal.charAt(i)).length;
                    nBytes += nLen > 3 ? nLen / 3 : 1;
                }
        
                return nBytes;
            },
            substrB: function (sVal, nStart, nLength) {
                var nBytes = 0,
                    nCharLen = 0,
                    nRtnBytes = 0;
                var sRtn = "";
        
                for (var i = 0; i < sVal.length; i++) {
                    nCharLen = escape(sVal.charAt(i)).length;
                    nBytes += nCharLen > 3 ? nCharLen / 3 : 1;
        
                    if (nBytes > nStart) {
                    nRtnBytes += nCharLen > 3 ? nCharLen / 3 : 1;
        
                    if (nRtnBytes > nLength) {
                        return sRtn;
                    }
                    sRtn += sVal.charAt(i);
                    }
                }
        
                return sRtn;
            },
            toFormatString: function (sVal, sMask) {
                if (ps.str.isNull(sVal) || ps.str.isNull(sMask)) return sVal;
        
                var sRetVal = "";
                var sUnit;
        
                sVal = String(sVal);
        
                for (var i = 0; i < sMask.length; i++) {
                    sUnit = sMask.charAt(i);
                    if (sUnit == "@") {
                    sRetVal += sVal.charAt(0);
                    sVal = sVal.substr(1);
                    } else if (sUnit == "*") {
                    sRetVal += sUnit;
                    sVal = sVal.substr(1);
                    } else {
                    sRetVal += sUnit;
                    }
                }
        
                return sRetVal;
            },
            toDate: function (sDate) {
                var oDate = new Date();
                if (!ps.str.isNull(sDate)) {
                    sDate = ps.str.removeSpecialChar(sDate);
                    sDate = ps.str.replaceAll(String(sDate), " ", "");
        
                    switch (sDate.length) {
                    case 4:
                        oDate.setFullYear(Number(sDate.substr(0, 4)), 0, 1);
                        break;
                    case 6:
                        oDate.setFullYear(
                        Number(sDate.substr(0, 4)),
                        Number(sDate.substr(4, 2)) - 1,
                        1
                        );
                        break;
                    case 8:
                        oDate.setFullYear(
                        Number(sDate.substr(0, 4)),
                        Number(sDate.substr(4, 2)) - 1,
                        Number(sDate.substr(6, 2))
                        );
                        break;
                    case 10:
                        oDate.setFullYear(
                        Number(sDate.substr(0, 4)),
                        Number(sDate.substr(4, 2)) - 1,
                        Number(sDate.substr(6, 2))
                        );
                        oDate.setHours(Number(sDate.substr(8, 2)), 0, 0, 0);
                        break;
                    case 12:
                        oDate.setFullYear(
                        Number(sDate.substr(0, 4)),
                        Number(sDate.substr(4, 2)) - 1,
                        Number(sDate.substr(6, 2))
                        );
                        oDate.setHours(
                        Number(sDate.substr(8, 2)),
                        Number(sDate.substr(10, 2)),
                        0,
                        0
                        );
                        break;
                    case 14:
                        oDate.setFullYear(
                        Number(sDate.substr(0, 4)),
                        Number(sDate.substr(4, 2)) - 1,
                        Number(sDate.substr(6, 2))
                        );
                        oDate.setHours(
                        Number(sDate.substr(8, 2)),
                        Number(sDate.substr(10, 2)),
                        Number(sDate.substr(12, 2)),
                        0
                        );
                        break;
                    case 17:
                        oDate.setFullYear(
                        Number(sDate.substr(0, 4)),
                        Number(sDate.substr(4, 2)) - 1,
                        Number(sDate.substr(6, 2))
                        );
                        oDate.setHours(
                        Number(sDate.substr(8, 2)),
                        Number(sDate.substr(10, 2)),
                        Number(sDate.substr(12, 2)),
                        Number(sDate.substr(14, 3))
                        );
                        break;
                    }
                }
        
                return oDate;
            },
        },
        date: {
            isNull: function(oDate) {
                if (oDate === 'undefined') return true;
                if (oDate === null) return true;
                if (oDate.valueOf() === '') return true;

                return false;
            },
            toFormatString: function(oDate, sFormat) {
                if (oDate == null || !oDate.valueOf()) oDate = new Date();
                if (!(oDate instanceof Date)) {
                    oDate = ps.str.toDate(String(oDate));
                }
                if (ps.str.isNull(sFormat)) sFormat = ps.sDateFormat + ' :: ' + ps.sTimeFormat;

                return sFormat.replace(/(yyyy|mm|dd|hh|mi|sssss|sss|ss)/gi,
                    function($1) {
                        switch ($1.toLowerCase()) {
                            case 'yyyy':
                                return oDate.getFullYear();
                            case 'mm':
                                return ps.str.padLeft(String(oDate.getMonth() + 1), 2, 0);
                            case 'dd':
                                return ps.str.padLeft(String(oDate.getDate()), 2, 0);
                            case 'hh':
                                return ps.str.padLeft(String(oDate.getHours()), 2, 0);
                            case 'mi':
                                return ps.str.padLeft(String(oDate.getMinutes()), 2, 0);
                            case 'sssss':
                                return ps.str.padLeft(String(oDate.getSeconds()), 2, 0) + oDate.getMilliseconds();
                            case 'sss':
                                return oDate.getMilliseconds();
                            case 'ss':
                                return ps.str.padLeft(String(oDate.getSeconds()), 2, 0);
                        }
                    }
                );
            },
            diffDate: function(oStartDate, oEndDate) {
                if (oStartDate == null || !oStartDate.valueOf() || oEndDate == null || !oEndDate.valueOf()) return 0;

                if (!(oStartDate instanceof Date)) {
                    oStartDate = ps.str.toDate(String(oStartDate));
                }

                if (!(oEndDate instanceof Date)) {
                    oEndDate = ps.str.toDate(String(oEndDate));
                }
                return oEndDate.getTime() - oStartDate.getTime();
            },
            calcDate: function(oDate, sType, nOffset) {
                if (oDate == null || !oDate.valueOf()) oDate = new Date();
                if (!(oDate instanceof Date)) {
                    oDate = ps.str.toDate(String(oDate));
                }
                
                var rDate = oDate;

                if (ps.str.isNull(sType)) sType = 'dd';
                
                nOffset = Number(nOffset);

                switch (sType.toLowerCase()) {
                    case 'yyyy':
                        rDate.setFullYear(oDate.getFullYear() + nOffset);
                        break;
                    case 'mm':
                        rDate.setMonth(oDate.getMonth() + nOffset);
                        break;
                    case 'dd':
                        rDate.setDate(oDate.getDate() + Number(nOffset));
                        break;
                    case 'hh':
                        rDate.setHours(oDate.getHours() + nOffset);
                        break;
                    case 'mi':
                        rDate.setMinutes(oDate.getMinutes() + nOffset);
                        break;
                    case 'ss':
                        rDate.setSeconds(oDate.getSeconds() + nOffset);
                        break;
                    case 'sss':
                        rDate.setMilliseconds(oDate.getMilliseconds() + nOffset);
                        break;
                    default:
                        rDate.setDate(oDate.getDate() + nOffset);
                        break;
                }

                return rDate;
            },
            isDate: function(sDate) {
                if (ps.str.isNull(sDate)) return false;

                if (sDate instanceof Date) return true;

                sDate = ps.str.removeSpecialChar(String(sDate));
                sDate = ps.str.replaceAll(String(sDate), ' ', '');

                try {
                    var objDate = ps.str.toDate(sDate);

                    if (ps.date.isNull(objDate)) return false;

                    switch (sDate.length) {
                        case 8:
                            if (ps.date.toFormatString(objDate, "yyyy/mm/dd") !== ps.str.toFormatString(sDate, '@@@@/@@/@@')) {
                                return false;
                            }
                            break;
                        case 10:
                            if (ps.date.toFormatString(objDate, "yyyy/mm/dd hh") !== ps.str.toFormatString(sDate, '@@@@/@@/@@ @@')) {
                                return false;
                            }
                            break;
                        case 12:
                            if (ps.date.toFormatString(objDate, "yyyy/mm/dd hh:mi") !== ps.str.toFormatString(sDate, '@@@@/@@/@@ @@:@@')) {
                                return false;
                            }
                            break;
                        case 14:
                            if (ps.date.toFormatString(objDate, "yyyy/mm/dd hh:mi:ss") !== ps.str.toFormatString(sDate, '@@@@/@@/@@ @@:@@:@@')) {
                                return false;
                            }
                            break;
                        case 17:
                            if (ps.date.toFormatString(objDate, "yyyy/mm/dd hh:mi:ss.sss") !== ps.str.toFormatString(sDate, '@@@@/@@/@@ @@:@@:@@.@@@')) {
                                return false;
                            }
                            break;
                        default:
                            return false;
                    }
                } catch (e) {
                    return false;
                }

                return true;
            },
            today: function(sFormat) {
                if (ps.str.isNull(sFormat)) sFormat = ps.sDateFormat;

                return ps.date.toFormatString(new Date(), sFormat);
            },
            nowTime: function(sFormat) {
                if (ps.str.isNull(sFormat)) sFormat = ps.sTimeFormat;

                return ps.date.toFormatString(new Date(), sFormat);
            },
            todayTime: function(sFormat) {
                if (ps.str.isNull(sFormat)) sFormat = ps.sDateFormat + ' ' + ps.sTimeFormat;

                return ps.date.toFormatString(new Date(), sFormat);
            },
            yesterday: function(sFormat) {
                if (ps.str.isNull(sFormat)) sFormat = ps.sDateFormat;

                return ps.date.toFormatString(ps.date.calcDate(new Date(), 'dd', -1), sFormat);
            },
            yesterdayTime: function(sFormat) {
                if (ps.str.isNull(sFormat)) sFormat = ps.sDateFormat + ' ' + ps.sTimeFormat;

                return ps.date.toFormatString(ps.date.calcDate(new Date(), 'dd', -1), sFormat);
            },
            addYear: function(oDate, nOffset) {
                return ps.date.calcDate(oDate, 'yyyy', nOffset);
            },
            addMonth: function(oDate, nOffset) {
                return ps.date.calcDate(oDate, 'mm', nOffset);
            },
            addDay: function(oDate, nOffset) {
                return ps.date.calcDate(oDate, 'dd', nOffset);
            },
            addHour: function(oDate, nOffset) {
                return ps.date.calcDate(oDate, 'hh', nOffset);
            },
            addMinute: function(oDate, nOffset) {
                return ps.date.calcDate(oDate, 'mi', nOffset);
            },
            addSecond: function(oDate, nOffset) {
                return ps.date.calcDate(oDate, 'ss', nOffset);
            },
            toString: function(oDate, sFormat) {
                if (ps.str.isNull(sFormat)) sFormat = ps.sDateFormat;

                return ps.date.toFormatString(oDate, sFormat);
            }
        }
    };
    return ps; 
});