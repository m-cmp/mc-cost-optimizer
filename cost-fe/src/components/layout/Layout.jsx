/**
 * @component Layout
 * @description
 * 애플리케이션 전역 레이아웃 컴포넌트.
 * - 모든 페이지에서 공통으로 사용되는 구조를 정의
 * - 상단에 Navbar가 고정적으로 포함
 */
import Navbar from "./Navbar";
import { Outlet } from "react-router-dom";

export default function Layout({ children }) {
  return (
    <div>
      <Navbar />
      <div className="page-wrapper">
        <div className="page-body">
          <div className="container-xl">
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  );
}
