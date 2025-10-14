/**
 * @prop {string} path
 *   라우터의 URL 경로. (예: "/", "/billing-report")
 *
 * @prop {React.ReactElement} element
 *   해당 경로에서 렌더링할 React 컴포넌트.
 *
 * @prop {string} label
 *   네비게이션 바나 메뉴에서 표시할 라벨 텍스트.
 *
 * @prop {string} [icon]
 *   네비게이션 바에서 표시할 Tabler 아이콘 키값.
 *   (src/icons/Icons.jsx 파일에서 정의된 이름과 매칭)
 *
 * @prop {"app" | "none"} layout
 *   어떤 레이아웃을 적용할지 여부.
 *   - `"app"`: 공통 Layout으로 감쌈
 *   - `"none"`: Layout 없이 독립 페이지 (예: 로그인, 404)
 *
 * @prop {boolean} showInNav
 *   네비게이션 바 메뉴에 표시할지 여부.
 */
import HomePage from "../pages/home/Homepage";
import BillingReportPage from "../pages/billingReport/BillingReportPage";
import AlarmPage from "../pages/alarm/AlarmPage";
import BudgetPage from "../pages/budget/BudgetPage";
import NotFoundPage from "../pages/notFound/NotFoundPage";

export const ROUTES = [
  {
    path: "/",
    element: <HomePage />,
    label: "Home",
    icon: "home",
    layout: "app",
    showInNav: true,
  },
  {
    path: "/billing-report",
    element: <BillingReportPage />,
    label: "Billing Report",
    icon: "businessplan",
    layout: "app",
    showInNav: true,
  },
  {
    path: "/alarm",
    element: <AlarmPage />,
    label: "Alarm",
    icon: "alarm",
    layout: "app",
    showInNav: true,
  },
  {
    path: "/budget",
    element: <BudgetPage />,
    label: "Budget",
    icon: "budget",
    layout: "app",
    showInNav: true,
  },
  {
    path: "*",
    element: <NotFoundPage />,
    label: "Not Found",
    layout: "app",
    showInNav: false,
  },
];
