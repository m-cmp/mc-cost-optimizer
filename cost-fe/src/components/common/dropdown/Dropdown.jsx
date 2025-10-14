import { useState, useRef, useEffect } from "react";

export default function Dropdown({
  trigger,
  items = [],
  placement = "bottom-start",
  className = "",
  menuClassName = "",
  disabled = false,
  onSelect,
  selectedValue = null,
}) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);
  const buttonRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleToggle = () => {
    if (!disabled) {
      setIsOpen(!isOpen);
    }
  };

  const handleItemClick = (item) => {
    if (item.disabled) return;

    if (onSelect) {
      onSelect(item.value, item);
    }
    setIsOpen(false);
  };

  const getPlacementClass = () => {
    const placementMap = {
      "bottom-start": "dropdown-menu-start",
      "bottom-end": "dropdown-menu-end",
      "top-start": "dropdown-menu-start dropup",
      "top-end": "dropdown-menu-end dropup",
    };
    return placementMap[placement] || "dropdown-menu-start";
  };

  return (
    <div
      className={`dropdown ${isOpen ? "show" : ""} ${className}`}
      ref={dropdownRef}
    >
      <button
        ref={buttonRef}
        className={`btn dropdown-toggle ${disabled ? "disabled" : ""}`}
        type="button"
        onClick={handleToggle}
        disabled={disabled}
        data-bs-toggle="dropdown"
        aria-expanded={isOpen}
      >
        {trigger}
      </button>

      <div
        className={`dropdown-menu ${
          isOpen ? "show" : ""
        } ${getPlacementClass()} ${menuClassName}`}
        style={{
          minWidth: buttonRef.current?.offsetWidth + 'px' || 'auto',
        }}
      >
        {items.map((item, index) => {
          if (item.type === "header") {
            return (
              <h6 key={index} className="dropdown-header">
                {item.label}
              </h6>
            );
          }

          if (item.type === "divider") {
            return <div key={index} className="dropdown-divider"></div>;
          }

          const isSelected = selectedValue === item.value;

          return (
            <a
              key={index}
              className={`dropdown-item text-center ${
                item.disabled ? "disabled" : ""
              } ${isSelected ? "active" : ""}`}
              href="#"
              onClick={(e) => {
                e.preventDefault();
                handleItemClick(item);
              }}
              style={{
                minWidth: "5rem",
                width: "max-content",
              }}
            >
              {item.icon && (
                <svg
                  className="icon dropdown-item-icon me-2"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  {item.icon}
                </svg>
              )}
              <span>{item.label}</span>
              {item.badge && (
                <span
                  className={`badge ms-auto ${
                    item.badge.className || "bg-primary"
                  }`}
                >
                  {item.badge.text}
                </span>
              )}
              {item.description && (
                <small className="text-muted d-block">{item.description}</small>
              )}
            </a>
          );
        })}
      </div>
    </div>
  );
}
