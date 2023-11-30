import _isNil from "lodash/isNil";

/**
 * Set description of document
 *
 * @param description
 */
function setDocumentDescription(description) {
  document.head.querySelector('meta[name=description]').content = description
}

function closestElement(el, selector) {
  if (!el || !el.parentElement || !selector) {
    return null;
  }
  let matchesFn;

  ['matches','webkitMatchesSelector','mozMatchesSelector','msMatchesSelector','oMatchesSelector'].some(function(fn) {
    if (typeof document.body[fn] == 'function') {
      matchesFn = fn;
      return true;
    }
    return false;
  })
  for (let elParent = el.parentElement; !_isNil(elParent); elParent = elParent.parentElement) {
    if (elParent[matchesFn](selector)) {
      return elParent;
    }
  }
  return null
}

export {
  setDocumentDescription,
  closestElement
}
