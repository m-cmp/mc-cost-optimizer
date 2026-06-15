import { NavLink } from "react-router-dom";
import { ROUTES } from "../../routes/routes";
import { Icons } from "../../icons/Icons";

export default function Navbar() {
  const navRoutes = ROUTES.filter((r) => r.showInNav);

  return (
    <header className="navbar navbar-expand-md d-print-none">
      <div className="container-xl">
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbar-menu"
          aria-controls="navbar-menu"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbar-menu">
          <ul className="navbar-nav">
            {navRoutes.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                end
                className={({ isActive }) =>
                  `nav-item ${isActive ? "active" : ""}`
                }
              >
                <span className="nav-link">
                  {item.icon && (
                    <span className="nav-link-icon">{Icons[item.icon]()}</span>
                  )}
                  <span className="nav-link-title">{item.label}</span>
                </span>
              </NavLink>
            ))}
          </ul>
        </div>
      </div>
    </header>
  );
}
