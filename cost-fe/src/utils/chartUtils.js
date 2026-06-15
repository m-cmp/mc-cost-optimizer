import { MONTH_NAMES } from "../constants/dateConstants";

// yearMonth -> "Sep 24" 포맷 변환
export const formatYearMonth = (yearMonth) => {
  if (!yearMonth || yearMonth.length < 6) return ""; // 방어 코드
  const year = yearMonth.substring(0, 4);
  const month = parseInt(yearMonth.substring(4, 6), 10);
  return `${MONTH_NAMES[month - 1]} ${year.slice(2)}`;
};

// 3개월 단위 + 현재 월 포함
export const pickQuarterMonths = (months, curYear, curMonth) => {
  if (!Array.isArray(months)) return [];
  const sorted = [...months].sort((a, b) => parseInt(a) - parseInt(b));
  const result = [];
  for (let i = 0; i < sorted.length; i += 3) {
    result.push(sorted[i]);
  }
  const cur = `${curYear}${curMonth}`;
  if (!result.includes(cur)) result.push(cur);
  return result.sort((a, b) => parseInt(a) - parseInt(b));
};

// 최근 N개월 추출
export const getLastMonthsData = (monthlyBill, count = 4) => {
  if (!Array.isArray(monthlyBill)) return [];
  const sorted = [...monthlyBill].sort((a, b) => {
    const keyA = a.yearMonth
      ? parseInt(a.yearMonth)
      : parseInt((a.year ?? "0") + (a.month ?? "0"));
    const keyB = b.yearMonth
      ? parseInt(b.yearMonth)
      : parseInt((b.year ?? "0") + (b.month ?? "0"));
    return keyA - keyB;
  });
  return sorted.slice(-count);
};

// BarChart용 데이터 변환
export const toBarChartData = (monthlyBill, count = 4) => {
  const lastMonths = getLastMonthsData(monthlyBill, count);
  return {
    categories: lastMonths.map((m) => {
      const ym =
        m.yearMonth ?? (m.year && m.month ? `${m.year}${m.month}` : null);
      return ym ? formatYearMonth(ym) : "";
    }),
    series: [{ name: "Bill", data: lastMonths.map((m) => m.bill ?? 0) }],
  };
};

// LineChart용 데이터 변환
export const toLineChartData = (summaryBill, yearMonths, curYear, curMonth) => {
  if (!Array.isArray(yearMonths)) return { categories: [], series: [] };
  if (!Array.isArray(summaryBill)) return { categories: [], series: [] };

  const selectedMonths = pickQuarterMonths(yearMonths, curYear, curMonth);
  const categories = selectedMonths.map((ym) => formatYearMonth(ym));

  const series = summaryBill.map((provider) => ({
    name: provider.csp,
    data: selectedMonths.map((ym) => {
      const found = provider.monthlyBill.find((m) => m.yearMonth === ym);
      return found ? found.bill : 0;
    }),
  }));

  return { categories, series };
};
