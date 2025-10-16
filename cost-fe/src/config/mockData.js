export const chartData = {
  status: "OK",
  Data: {
    curYear: "2025",
    curMonth: "08",
    prevYear: "2025",
    prevMonth: "07",
    curMonthBill: 0.2,
    prevMonthBill: 0.4,
    momBill: -0.2,
    momPer: "-50.0",
    monthlyBill: [
      { month: "08", year: "2025", bill: 0.2 },
      { month: "07", year: "2025", bill: 0.4 },
      { month: "06", year: "2025", bill: 0.5 },
      { month: "05", year: "2025", bill: 0.2 },
      { month: "04", year: "2025", bill: 0.5 },
      { month: "03", year: "2025", bill: 0.8 },
      { month: "02", year: "2025", bill: 0.6 },
      { month: "01", year: "2025", bill: 0.4 },
      { month: "12", year: "2024", bill: 1.0 },
      { month: "11", year: "2024", bill: 0.8 },
      { month: "10", year: "2024", bill: 2.0 },
      { month: "09", year: "2024", bill: 1.5 },
    ],
    selectedProjects: ["ns01"],
    selectedCsps: [],
  },
};

// 요금 상위 5개 서비스 카드용 (TopServicesCard)
export const top5billData = {
  status: "OK",
  Data: {
    top5bill: [
      { bill: 100.4, resourceNm: "AmazonEC2", csp: "AWS", isOthers: false },
      { bill: 80.2, resourceNm: "S3", csp: "AWS", isOthers: false },
      { bill: 50.5, resourceNm: "CloudFront", csp: "AWS", isOthers: false },
      { bill: 40.0, resourceNm: "RDS", csp: "AWS", isOthers: false },
      { bill: 20.0, resourceNm: "Lambda", csp: "AWS", isOthers: false },
    ],
  },
};

// 서비스별 누적 비용 카드용 (ServiceCostListCard)
export const billingAsset = {
  status: "OK",
  Data: {
    billingAsset: [
      {
        familyProductCode: "Virtual Machine",
        childProductCode: [
          {
            childProductCode: "AmazonEC2",
            unit: 116,
            bill: 100.4075592312,
          },
        ],
        totalUnit: 116,
        totalCost: 100.4075592312,
      },
      {
        familyProductCode: "Storage",
        childProductCode: [],
        totalUnit: 0,
        totalCost: 0.0,
      },
      {
        familyProductCode: "Database",
        childProductCode: [],
        totalUnit: 0,
        totalCost: 0.0,
      },
      {
        familyProductCode: "LB",
        childProductCode: [],
        totalUnit: 0,
        totalCost: 0.0,
      },
    ],
  },
};

// BaseInfoCard
export const baseInfoData = {
  status: "OK",
  Data: [
    { csp: "AWS", cost: 100.408, colorClass: "bg-google" },
    { csp: "GCP", cost: 0.0, colorClass: "bg-facebook" },
    { csp: "AZURE", cost: 0.0, colorClass: "bg-red" },
    { csp: "NCP", cost: 0.0, colorClass: "bg-green" },
  ],
};

// CSP별 연간 비용 데이터 (MonthlyOverviewCard)
export const summaryBillData = {
  status: "OK",
  Data: {
    curMonth: "08",
    curYear: "2025",
    summaryBill: [
      {
        csp: "AZURE",
        monthlyBill: [
          { yearMonth: "202508", bill: 120 },
          { yearMonth: "202507", bill: 140 },
          { yearMonth: "202506", bill: 150 },
          { yearMonth: "202505", bill: 160 },
          { yearMonth: "202504", bill: 170 },
          { yearMonth: "202503", bill: 180 },
          { yearMonth: "202502", bill: 190 },
          { yearMonth: "202501", bill: 200 },
          { yearMonth: "202412", bill: 210 },
          { yearMonth: "202411", bill: 220 },
          { yearMonth: "202410", bill: 230 },
          { yearMonth: "202409", bill: 240 },
        ],
      },
      {
        csp: "GCP",
        monthlyBill: [
          { yearMonth: "202508", bill: 95 },
          { yearMonth: "202507", bill: 110 },
          { yearMonth: "202506", bill: 80 },
          { yearMonth: "202505", bill: 130 },
          { yearMonth: "202504", bill: 70 },
          { yearMonth: "202503", bill: 100 },
          { yearMonth: "202502", bill: 120 },
          { yearMonth: "202501", bill: 90 },
          { yearMonth: "202412", bill: 100 },
          { yearMonth: "202411", bill: 60 },
          { yearMonth: "202410", bill: 75 },
          { yearMonth: "202409", bill: 85 },
        ],
      },
      {
        csp: "NCP",
        monthlyBill: [
          { yearMonth: "202508", bill: 60 },
          { yearMonth: "202507", bill: 58 },
          { yearMonth: "202506", bill: 55 },
          { yearMonth: "202505", bill: 53 },
          { yearMonth: "202504", bill: 52 },
          { yearMonth: "202503", bill: 50 },
          { yearMonth: "202502", bill: 48 },
          { yearMonth: "202501", bill: 47 },
          { yearMonth: "202412", bill: 45 },
          { yearMonth: "202411", bill: 44 },
          { yearMonth: "202410", bill: 43 },
          { yearMonth: "202409", bill: 42 },
        ],
      },
      {
        csp: "AWS",
        monthlyBill: [
          { yearMonth: "202508", bill: 300 },
          { yearMonth: "202507", bill: 280 },
          { yearMonth: "202506", bill: 260 },
          { yearMonth: "202505", bill: 250 },
          { yearMonth: "202504", bill: 230 },
          { yearMonth: "202503", bill: 210 },
          { yearMonth: "202502", bill: 200 },
          { yearMonth: "202501", bill: 190 },
          { yearMonth: "202412", bill: 170 },
          { yearMonth: "202411", bill: 150 },
          { yearMonth: "202410", bill: 130 },
          { yearMonth: "202409", bill: 120 },
        ],
      },
    ],
    yearMonths: [
      "202508",
      "202507",
      "202506",
      "202505",
      "202504",
      "202503",
      "202502",
      "202501",
      "202412",
      "202411",
      "202410",
      "202409",
    ],
    selectedProjects: ["ns01"],
    selectedCsps: [],
  },
};

// 인보이스 데이터 (InvoiceTable)
export const InvoiceData = {
  status: "OK",
  Data: {
    curMonth: "08",
    curYear: "2025",
    invoice: [
      {
        accountID: "5.46093E+11",
        productID: "AmazonEC2",
        csp: "AWS",
        bill: -366.460080002,
        resourceID: "",
      },
      {
        accountID: "5.46093E+11",
        productID: "AmazonEC2",
        csp: "AWS",
        bill: 13.971153225900004,
        resourceID:
          "arn:aws:ec2:ap-northeast-2:546092739842:natgateway/nat-0bb6832c41d7d3569",
      },
      {
        accountID: "5.46093E+11",
        productID: "AmazonEC2",
        csp: "AWS",
        bill: 47.90577821659999,
        resourceID:
          "arn:aws:ec2:ap-northeast-2:546092739842:natgateway/nat-0bd16963c68603ef3",
      },
      // ... 긴 데이터 생략 (원본 전체 유지 가능)
    ],
    selectedProjects: ["ns01"],
    selectedCsps: [],
  },
};

// Budget 관련 Mock 데이터
export const budgetData = {
  status: "OK",
  Data: {
    year: 2025,
    monthly: {
      categories: [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
      ],
      budget: [
        1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
      ],
      actual: [856, 925, 1150, 979, 832, 946, 1206, 1098, 459, 0, 0, 0],
    },
    cspBudgets: {
      AWS: [400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400],
      Azure: [300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300],
      GCP: [200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200],
    },
  },
};

export const alarmHistoryData = {
  status: "OK",
  Data: {
    selectedCsps: ["AWS"],
    selectedWorkspace: "ws01",
    selectedProjects: ["ns01"],
    curDate: "2025-08-28",
    alarmHistory: [
      {
        occure_time: "2024-10-08T10:30:00",
        csp_type: "AWS",
        resource_id: "i-07ba4df5e27e5d0a2",
        resource_type: "AmazonEC2",
        event_type: "사이즈 변경",
        note: "인스턴스(i-07ba4df5e27e5d0a2)를 기존 타입: c6g.8xlarge에서 추천 타입: c7g.8xlarge으로 변경하는 것을 추천드립니다.",
        plan: "최신화",
      },
      {
        occure_time: "2024-10-08T09:15:00",
        csp_type: "AWS",
        resource_id: "i-0f89759223127d99f",
        resource_type: "AmazonEC2",
        event_type: "사이즈 변경",
        note: "인스턴스(i-0f89759223127d99f)를 기존 타입: t2.medium에서 추천 타입: c6i.large으로 변경하는 것을 추천드립니다.",
        plan: "상향",
      },
      {
        occure_time: "2024-10-08T08:45:00",
        csp_type: "AWS",
        resource_id: "i-0dce57d1fb4de20f9",
        resource_type: "AmazonEC2",
        event_type: "사이즈 변경",
        note: "인스턴스(i-0dce57d1fb4de20f9)를 기존 타입: t2.small에서 추천 타입: t2.micro으로 변경하는 것을 추천드립니다.",
        plan: "하향",
      },
      {
        occure_time: "2024-10-08T07:20:00",
        csp_type: "AWS",
        resource_id: "i-068a979d22dbe093b",
        resource_type: "AmazonEC2",
        event_type: "사이즈 변경",
        note: "인스턴스(i-068a979d22dbe093b)를 기존 타입: t2.micro에서 DownSizing으로 변경하는 것을 추천드립니다.",
        plan: "하향",
      },
      {
        occure_time: "2024-10-07T18:30:00",
        csp_type: "AWS",
        resource_id: "AmazonECR",
        resource_type: "AmazonECR",
        event_type: "비정상",
        note: "지난달 비용(3.39 USD) 대비 이번달 비용(5.74 USD)이 69.36% 발생했습니다.",
        plan: "긴급",
      },
    ],
  },
};

export const mockBudgetData = [
  {
    csp: "AWS",
    year: 2025,
    month: 1,
    budget: 400.0,
    currency: "USD",
  },
  {
    csp: "AWS",
    year: 2025,
    month: 2,
    budget: 450.0,
    currency: "USD",
  },
  {
    csp: "Azure",
    year: 2025,
    month: 1,
    budget: 300.0,
    currency: "USD",
  },
  {
    csp: "NCP",
    year: 2025,
    month: 1,
    budget: 550000.0,
    currency: "KRW",
  },
];

// Budget vs Actual Comparison Mock 데이터
export const mockBudgetComparisonData = {
  year: 2025,
  months: [
    {
      month: 1,
      yearMonth: "202501",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 720, AWS: 350, NCP: 220, AZURE: 150 },
    },
    {
      month: 2,
      yearMonth: "202502",
      budget: { total: 850, AWS: 450, NCP: 250, AZURE: 150 },
      actual: { total: 780, AWS: 380, NCP: 240, AZURE: 160 },
    },
    {
      month: 3,
      yearMonth: "202503",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 850, AWS: 420, NCP: 260, AZURE: 170 },
    },
    {
      month: 4,
      yearMonth: "202504",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 690, AWS: 330, NCP: 210, AZURE: 150 },
    },
    {
      month: 5,
      yearMonth: "202505",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 750, AWS: 370, NCP: 230, AZURE: 150 },
    },
    {
      month: 6,
      yearMonth: "202506",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 810, AWS: 400, NCP: 250, AZURE: 160 },
    },
    {
      month: 7,
      yearMonth: "202507",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 770, AWS: 380, NCP: 240, AZURE: 150 },
    },
    {
      month: 8,
      yearMonth: "202508",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 790, AWS: 390, NCP: 250, AZURE: 150 },
    },
    {
      month: 9,
      yearMonth: "202509",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 730, AWS: 360, NCP: 220, AZURE: 150 },
    },
    {
      month: 10,
      yearMonth: "202510",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 680, AWS: 340, NCP: 200, AZURE: 140 },
    },
    {
      month: 11,
      yearMonth: "202511",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 0, AWS: 0, NCP: 0, AZURE: 0 },
    },
    {
      month: 12,
      yearMonth: "202512",
      budget: { total: 800, AWS: 400, NCP: 250, AZURE: 150 },
      actual: { total: 0, AWS: 0, NCP: 0, AZURE: 0 },
    },
  ],
};
