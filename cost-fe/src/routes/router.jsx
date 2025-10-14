import React, { Suspense } from "react";
import { createBrowserRouter } from "react-router-dom";
import { ROUTES } from "./routes";
import Layout from "../components/layout/Layout";
import Loading from "../components/common/loading/Loading";

const withSuspense = (element) => (
  <Suspense fallback={<Loading fullscreen color="teal" />}>{element}</Suspense>
);

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: ROUTES.filter((r) => r.layout === "app").map((r) => ({
      path: r.path === "/" ? undefined : r.path, // index route 처리
      index: r.path === "/",
      element: withSuspense(r.element),
    })),
  },
  ...ROUTES.filter((r) => r.layout === "none").map((r) => ({
    path: r.path,
    element: withSuspense(r.element),
  })),
]);
