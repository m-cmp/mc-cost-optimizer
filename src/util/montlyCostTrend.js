import {TOTAL_MONTH_IN_YEAR} from '../constants/constants';
import {DEFAULT_NUMBER_OF_NEXT_MONTHS, DEFAULT_NUMBER_OF_PREVIOUS_MONTHS} from '@/constants/billingConstants';

function getFirstMonthIdx(activeMonthIdx, billListLength, isChangingMonth) {
  // when user change month by select column or select from dropdown
  if (isChangingMonth) {
    return activeMonthIdx >= DEFAULT_NUMBER_OF_PREVIOUS_MONTHS
      ? activeMonthIdx - DEFAULT_NUMBER_OF_PREVIOUS_MONTHS
      : 0;
  }
  // on first visit page
  // later if default activeMonthIdx changes, flow will go into this case
  return activeMonthIdx >= TOTAL_MONTH_IN_YEAR - 1
    ? activeMonthIdx - (TOTAL_MONTH_IN_YEAR - 1)
    : 0;
}

function getLastMonthIdx(activeMonthIdx, billListLength) {
  return activeMonthIdx + DEFAULT_NUMBER_OF_NEXT_MONTHS < billListLength
    ? activeMonthIdx + DEFAULT_NUMBER_OF_NEXT_MONTHS
    : billListLength - 1;
}

function setFirstMonthIdxAfterClickPrev(firstMonthIdx, lastMonthIdx) {
  return firstMonthIdx - 1
}

function setLastMonthIdxAfterClickPrev(firstMonthIdx, lastMonthIdx) {
  if (lastMonthIdx - firstMonthIdx < 11) {
    return lastMonthIdx;
  }
  return lastMonthIdx - 1
}

function setFirstMonthIdxAfterClickNext(firstMonthIdx, lastMonthIdx) {
  if (lastMonthIdx - firstMonthIdx < 11) {
    return firstMonthIdx;
  }
  return firstMonthIdx + 1
}

function setLastMonthIdxAfterClickNext(firstMonthIdx, lastMonthIdx) {
  return lastMonthIdx + 1
}

function getSelectedMonthYear(activeMonthIdx, billList) {
  return {
    chargeMonth: billList[activeMonthIdx] && billList[activeMonthIdx].chargeMonth,
    chargeYear: billList[activeMonthIdx] && billList[activeMonthIdx].chargeYear
  }
}

function isLastMonthOfBillList(activeMonthIdx, billListLength) {
  return activeMonthIdx === billListLength - 1;
}

function getStartAndEndLocationByTargetData(targetDataColumnChart, categoryAxisStartLocation, categoryAxisEndLocation) {
  let lengthSeries = targetDataColumnChart.data.length;
  let categoryStartLocation = categoryAxisStartLocation
  let categoryEndLocation = categoryAxisEndLocation
  for (let i = 0; i < targetDataColumnChart.data.length; i++) {
    const targetData = targetDataColumnChart.data[i]
    let activeMonth = targetData.activeMonthIdx
    let firstMonth = targetData.firstMonthIdx
    let lastMonth = targetData.lastMonthIdx
    if (isLastMonthOfBillList(targetData.idx, targetData.billListLength)) {
      if (lengthSeries === 11) {
        categoryStartLocation = 0
        categoryEndLocation = 2
        if (firstMonth === 0 && activeMonth === 4) {
          categoryStartLocation = -1
          categoryEndLocation = 1
        }
      } else if (lengthSeries === 10) {
        categoryStartLocation = 0
        categoryEndLocation = 3
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 5: {
              categoryStartLocation = 0
              categoryEndLocation = 3
              break
            }
            case 4: {
              categoryStartLocation = -1
              categoryEndLocation = 2
              break
            }
            case 3: {
              categoryStartLocation = -2
              categoryEndLocation = 1
            }
          }
        }
      } else if (lengthSeries === 9) {
        categoryStartLocation = 0
        categoryEndLocation = 4
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 4: {
              categoryStartLocation = -1
              categoryEndLocation = 3
              break
            }
            case 3: {
              categoryStartLocation = -2
              categoryEndLocation = 2
              break
            }
            case 2: {
              categoryStartLocation = -3
              categoryEndLocation = 1
            }
          }
        }
      } else if (lengthSeries === 8) {
        categoryStartLocation = 0
        categoryEndLocation = 5
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 4: {
              categoryStartLocation = -1
              categoryEndLocation = 4
              break
            }
            case 3: {
              categoryStartLocation = -2
              categoryEndLocation = 3
              break
            }
            case 2: {
              categoryStartLocation = -3
              categoryEndLocation = 2
              break
            }
            case 1: {
              categoryStartLocation = -4
              categoryEndLocation = 1
            }
          }
        }
      } else if (lengthSeries === 7) {
        categoryStartLocation = 0
        categoryEndLocation = 6
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 4: {
              categoryStartLocation = -1
              categoryEndLocation = 5
              break
            }
            case 3: {
              categoryStartLocation = -2
              categoryEndLocation = 4
              break
            }
            case 2: {
              categoryStartLocation = -3
              categoryEndLocation = 3
              break
            }
            case 1: {
              categoryStartLocation = -4
              categoryEndLocation = 2
              break
            }
            case 0: {
              categoryStartLocation = -5
              categoryEndLocation = 1
            }
          }
        }
      } else if (lengthSeries === 6) {
        categoryStartLocation = 0
        categoryEndLocation = 7
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 4: {
              categoryStartLocation = -2
              categoryEndLocation = 6
              break
            }
            case 3: {
              categoryStartLocation = -3
              categoryEndLocation = 5
              break
            }
            case 2: {
              categoryStartLocation = -4
              categoryEndLocation = 4
              break
            }
            case 1: {
              categoryStartLocation = -5
              categoryEndLocation = 3
              break
            }
            case 0: {
              categoryStartLocation = -6
              categoryEndLocation = 2
            }
          }
        }
      } else if (lengthSeries === 5) {
        categoryStartLocation = 0
        categoryEndLocation = 8
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 3: {
              categoryStartLocation = -2
              categoryEndLocation = 6
              break
            }
            case 2: {
              categoryStartLocation = -3
              categoryEndLocation = 5
              break
            }
            case 1: {
              categoryStartLocation = -4
              categoryEndLocation = 4
              break
            }
            case 0: {
              categoryStartLocation = -5
              categoryEndLocation = 3
            }
          }
        }
      } else if (lengthSeries === 4) {
        categoryStartLocation = 0
        categoryEndLocation = 9
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 2: {
              categoryStartLocation = -3
              categoryEndLocation = 6
              break
            }
            case 1: {
              categoryStartLocation = -4
              categoryEndLocation = 5
              break
            }
            case 0: {
              categoryStartLocation = -5
              categoryEndLocation = 4
            }
          }
        }
      } else if (lengthSeries === 3) {
        categoryStartLocation = 0
        categoryEndLocation = 10
        if (firstMonth === 0) {
          switch (activeMonth) {
            case 1: {
              categoryStartLocation = -4
              categoryEndLocation = 6
              break
            }
            case 0: {
              categoryStartLocation = -5
              categoryEndLocation = 5
            }
          }
        }
      } else if (lengthSeries === 2) {
        categoryStartLocation = 0
        categoryEndLocation = 11
        if (firstMonth === 0) {
          if (activeMonth === 0) {
            categoryStartLocation = -5
            categoryEndLocation = 6
          }
        }
      } else if (lengthSeries === 1) {
        categoryStartLocation = 0
        categoryEndLocation = 12
      }
    } else {
      if (firstMonth === 0) {
        if (activeMonth === 0) {
          categoryStartLocation = -5
          categoryEndLocation = 1
          switch (lastMonth) {
            case 7: {
              categoryStartLocation = -4
              categoryEndLocation = 1
              break
            }
            case 8: {
              categoryStartLocation = -3
              categoryEndLocation = 1
              break
            }
            case 9: {
              categoryStartLocation = -2
              categoryEndLocation = 1
              break
            }
            case 10: {
              categoryStartLocation = -1
              categoryEndLocation = 1
              break
            }
            case 11: {
              categoryStartLocation = 0
              categoryEndLocation = 1
            }
          }
        } else if (activeMonth === 1) {
          categoryStartLocation = -4
          categoryEndLocation = 1
          switch (lastMonth) {
            case 8: {
              categoryStartLocation = -3
              categoryEndLocation = 1
              break
            }
            case 9: {
              categoryStartLocation = -2
              categoryEndLocation = 1
              break
            }
            case 10: {
              categoryStartLocation = -1
              categoryEndLocation = 1
              break
            }
            case 11: {
              categoryStartLocation = 0
              categoryEndLocation = 1
            }
          }
        } else if (activeMonth === 2) {
          categoryStartLocation = -3
          categoryEndLocation = 1
          switch (lastMonth) {
            case 9: {
              categoryStartLocation = -2
              categoryEndLocation = 1
              break
            }
            case 10: {
              categoryStartLocation = -1
              categoryEndLocation = 1
              break
            }
            case 11: {
              categoryStartLocation = 0
              categoryEndLocation = 1
            }
          }
        } else if (activeMonth === 3) {
          categoryStartLocation = -2
          categoryEndLocation = 1
          switch (lastMonth) {
            case 10: {
              categoryStartLocation = -1
              categoryEndLocation = 1
              break
            }
            case 11: {
              categoryStartLocation = 0
              categoryEndLocation = 1
            }
          }
        } else if (activeMonth === 4) {
          categoryStartLocation = -1
          categoryEndLocation = 1
          if (lastMonth === 11) {
            categoryStartLocation = 0
            categoryEndLocation = 1
          }
        } else if (activeMonth === 5) {
          categoryStartLocation = 0
          categoryEndLocation = 1
        }
      }
    }
  }
  return {
    'categoryStartLocation': categoryStartLocation,
    'categoryEndLocation': categoryEndLocation
  }
}

export {
  getFirstMonthIdx,
  getLastMonthIdx,
  setFirstMonthIdxAfterClickPrev,
  setLastMonthIdxAfterClickPrev,
  setFirstMonthIdxAfterClickNext,
  setLastMonthIdxAfterClickNext,
  getStartAndEndLocationByTargetData,
  getSelectedMonthYear
}

