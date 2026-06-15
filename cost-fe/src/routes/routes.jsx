/**
 * @prop {string} path
 *   The URL path for the router. (e.g., "/", "/billing-report")
 *
 * @prop {React.ReactElement} element
 *   The React component to render at this path.
 *
 * @prop {string} label
 *   The label text to display in the navigation bar or menu.
 *
 * @prop {string} [icon]
 *   The Tabler icon key value to display in the navigation bar.
 *   (Matches the names defined in src/icons/Icons.jsx file)
 *
 * @prop {"app" | "none"} layout
 *   Which layout to apply.
 *   - `"app"`: Wrapped with common Layout
 *   - `"none"`: Independent page without Layout (e.g., login, 404)
 *
 * @prop {boolean} showInNav
 *   Whether to display in the navigation bar menu.
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
