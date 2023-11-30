const VIEW_BY_ACCOUNT = 'account';

/**
 * Get display item base on view by
 * - viewBy: account -> displayItem = itemAlias (item)
 * - viewBy: product -> displayItem = itemAlias
 * - viewBy: region -> displayItem = itemAlias
 *
 * @param item
 * @param viewBy
 */
function getDisplayItemBaseOnViewBy(item, viewBy) {
  if (viewBy === VIEW_BY_ACCOUNT) {
    return `${item.itemAlias? item.itemAlias: item.item}(${item.item})`
  }

  return `${item.itemAlias ? item.itemAlias : item.item}`
}

function getDisplayItemBaseOnPortionViewBy(item, viewBy) {
  return `${getDisplayItemBaseOnViewBy(item, viewBy).replace("(","\n(")}`
}

/**
 * Get display item with vendor base on view by
 *
 * @param item
 * @param viewBy
 */
function getDisplayItemWithVendorBaseOnViewBy(item, viewBy) {
  return `${item.vendor} ${getDisplayItemBaseOnViewBy(item, viewBy)}`;
}

function getDisplayCompareItemWithVendorBaseOnViewBy(item, viewBy, compareLabel) {
  return `${item.vendor} ${getDisplayItemBaseOnViewBy(item, viewBy)} ${compareLabel}`;
}

function getDisplayProductPortionBaseOnProductFamily(item){

  if(item.includes("&")){
    return item.replace("&","\n&");
  }else{
    return item;
  }

}

// label 줄바꿈을 위한 2차원 배열 생성
function getDisplayProductPortionBaseOnProductFamilyForPrime(item) {
  if(item.includes("&")){
    let newArray = [];
    newArray = item.replaceAll('&', '_&').split('_').map((str) => [str]);
    return newArray;
  }else if(item.includes("for")){
    let newArray = [];
    newArray = item.replaceAll('for', 'for_').split('_ ').map((str) => [str]);
    return newArray;
  }
  return item;
}

export {
  getDisplayCompareItemWithVendorBaseOnViewBy,
  getDisplayItemBaseOnViewBy,
  getDisplayItemWithVendorBaseOnViewBy,
  getDisplayProductPortionBaseOnProductFamily,
  getDisplayItemBaseOnPortionViewBy,
  getDisplayProductPortionBaseOnProductFamilyForPrime
};
