import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <div className="page page-center">
      <div className="container-tight py-4 text-center">
        <h1 className="display-1 fw-bold text-primary">404</h1>
        <p className="fs-3">페이지를 찾을 수 없습니다</p>
        <p className="text-secondary mb-4">
          요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
        </p>
        <Link to="/" className="btn btn-primary">
          홈으로 돌아가기
        </Link>
      </div>
    </div>
  );
}
