import _escapeRegExp from 'lodash/escapeRegExp';
import _isEmpty from 'lodash/isEmpty';
import _isNil from "lodash/isNil";

//Please refer this link to know more https://stackoverflow.com/questions/6555182/remove-all-special-characters-except-space-from-a-string-using-javascript
function removeAllSpecialCharacters(string, changedCharacter = '') {
  return string.replace(/[&\/\\#,+()$~%.'":*?<>{}]/g, changedCharacter);
}

function toStringWithMatchesHighlighted(string, searchValue, isCaseSensitive) {
  if (_isEmpty(string) || _isEmpty(searchValue)) {
    return string;
  }
  const searchValueFlags = isCaseSensitive ? 'g' : 'gi';
  const replaceValue = `<span style="font-weight: bold">$&</span>`;
  return string.replace(new RegExp(_escapeRegExp(searchValue), searchValueFlags), replaceValue);
}

function email_blur(email){
  let verify = /^\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/;
  return verify.test(email)
}

/**
 * null인 경우 예외 값 처리
 * @param val 원본
 * @param str null인 경우 대체값
 * @returns {*}
 */
function nvlStr(val, str) {

  // 숫자 유형인 경우 처리
  if(typeof val === "number") {
    if(_isNil(val) || isNaN(val)) {
      return str;
    }
  } else {
    // 숫자 이외의 처리는 모두 아래 형태로
    if(_isNil(val) || _isEmpty(val)) {
      return str;
    }
  }
  return val;
}

export {
  removeAllSpecialCharacters,
  toStringWithMatchesHighlighted,
  email_blur,
  nvlStr,
}
