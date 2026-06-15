import clsx from "clsx";

export default function Button({
  children,
  variant = "primary",
  size = "md",
  shape = "default",
  icon: Icon,
  iconPosition = "left", // left | right | only
  fullWidth = false,
  disabled = false,
  loading = false,
  badge,
  className,
  onClick,
  ...props
}) {
  // 크기 매핑
  const sizeClass = {
    sm: "btn-sm",
    md: "",
    lg: "btn-lg",
    xl: "btn-xl",
  }[size];

  // 모양 매핑
  const shapeClass = {
    default: "",
    square: "btn-square",
    pill: "btn-pill",
  }[shape];

  // variant → Tabler 클래스 그대로 활용
  const baseClass = `btn btn-${variant}`;

  const classes = clsx(
    baseClass,
    sizeClass,
    shapeClass,
    fullWidth && "w-100",
    loading && "btn-loading",
    className
  );

  return (
    <button
      className={classes}
      disabled={disabled || loading}
      onClick={onClick}
      {...props}
    >
      {/* 아이콘 only */}
      {Icon && iconPosition === "only" && <Icon size={20} stroke={1.5} />}

      {/* 아이콘 + 텍스트 */}
      {Icon && iconPosition === "left" && (
        <Icon
          size={20}
          stroke={1.5}
          style={{ marginRight: children ? 6 : 0 }}
        />
      )}
      {children}
      {Icon && iconPosition === "right" && (
        <Icon size={20} stroke={1.5} style={{ marginLeft: children ? 6 : 0 }} />
      )}

      {/* Badge (알림용) */}
      {badge && <span className="badge ms-2">{badge}</span>}
    </button>
  );
}
