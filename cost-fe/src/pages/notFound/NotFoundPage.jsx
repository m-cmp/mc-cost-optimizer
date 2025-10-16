import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <div className="page page-center">
      <div className="container-tight py-4 text-center">
        <h1 className="display-1 fw-bold text-primary">404</h1>
        <p className="fs-3">Page Not Found</p>
        <p className="text-secondary mb-4">
          The page you requested does not exist or may have been moved.
        </p>
        <Link to="/" className="btn btn-primary">
          Back to Home
        </Link>
      </div>
    </div>
  );
}
