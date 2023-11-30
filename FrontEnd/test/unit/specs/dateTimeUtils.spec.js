import {
  getRangeOfDateByWeekNumber,
  add0StringToNumberLessThan10,
  splitYearMonthDateFromJsDate,
  formatMonthYearBySlash,
  splitYearMonthFromYYYYMM,
  splitYearMonthDateFromYYYYMMDD

} from '@/util/dateTimeUtils';

describe('Date time utils', () => {
  it('Get range of date by week number', () => {
    const startAndEndDateOfWeek  = getRangeOfDateByWeekNumber(11, 2019);
    const expectObject = {startDate: '2019-03-11', endDate: '2019-03-17'};

    expect(JSON.stringify(startAndEndDateOfWeek)).to.equal(JSON.stringify(expectObject));
  });

  it('Add 0 string to number less than 10', () => {
    expect('01').to.equal(add0StringToNumberLessThan10(1));
    expect('02').to.equal(add0StringToNumberLessThan10(2));
    expect('09').to.equal(add0StringToNumberLessThan10(9));
    expect('10').to.equal(add0StringToNumberLessThan10(10));
    expect('12').to.equal(add0StringToNumberLessThan10(12));
  });

  it('Split year month date from js date', () => {
    const date = new Date('2019-10-12');
    const splitDate  = splitYearMonthDateFromJsDate(date);
    const expectDate = {year: '2019', month: '10', date: '12'};

    expect(JSON.stringify(splitDate)).to.equal(JSON.stringify(expectDate));
    expect(JSON.stringify({})).to.equal(JSON.stringify(splitYearMonthDateFromJsDate()));
  });
  it('Format month year by slash', () => {
    expect('10/2019').to.equal(formatMonthYearBySlash(10, 2019));
    expect('9/2019').to.equal(formatMonthYearBySlash(9, 2019));
  });

  it('Split year month from YYYYMM', () => {
    const splitDate  = splitYearMonthFromYYYYMM('201910');
    const expectDate = {year: '2019', month: '10'};

    expect(JSON.stringify(splitDate)).to.equal(JSON.stringify(expectDate));
  });

  it('Split year month date from YYYY-MM-DD', () => {
    const splitDate  = splitYearMonthDateFromYYYYMMDD('2019-10-12');
    const expectDate = {year: '2019', month: '10', date: '12'};

    expect(JSON.stringify(splitDate)).to.equal(JSON.stringify(expectDate));
  });
});
