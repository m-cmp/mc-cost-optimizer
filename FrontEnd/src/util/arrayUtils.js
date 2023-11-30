import _isEmpty from 'lodash/isEmpty';
import _cloneDeep from 'lodash/cloneDeep';

function toggleArrayItem(array, item) {
  const index = array.indexOf(item);
  if (index === -1)
    array.push(item);
  else
    array.splice(index,1);
}

/**
 * @returns {Array}
 */
function getElementsInAAndNotInB(arrA, arrB) {
  if (_isEmpty(arrA)) {
    return [];
  }
  if (_isEmpty(arrB)) {
    return _cloneDeep(arrA);
  }
  let setB = new Set(arrB);
  return arrA.filter(el => !setB.has(el));
}

/**
 * @returns {Set<string>}
 */
function getElementsSetInAAndNotInB(arrA, arrB) {
  return new Set(getElementsInAAndNotInB(arrA, arrB));
}

export {
  getElementsInAAndNotInB,
  getElementsSetInAAndNotInB,
  toggleArrayItem
}
