/**
 * @component Loading
 *
 * @prop {"border" | "grow" | "dots"} [variant="border"]
 *   스피너 종류. 기본값은 border.
 *
 * @prop {string} [color="blue"]
 *   색상 (blue, azure, indigo, purple, pink, red, orange, yellow, lime, green, teal, cyan)
 *
 * @prop {"md" | "sm"} [size="md"]
 *   크기 (기본 md, 작은 크기 sm)
 *
 * @prop {boolean} [fullscreen=false]
 *   전체 화면 중앙 로딩 여부
 *
 * @prop {boolean} [inline=false]
 *   텍스트 옆이나 버튼 안에서 인라인으로 쓰고 싶을 때
 *
 * @prop {string} [label="Loading..."]
 *   스크린리더용 접근성 텍스트
 *
 * @prop {boolean} [withLabel=false]
 *   스피너 옆에 "Loading..." 같은 레이블을 표시할지 여부
 */
import clsx from "clsx";

const COLORS = [
  "blue",
  "azure",
  "indigo",
  "purple",
  "pink",
  "red",
  "orange",
  "yellow",
  "lime",
  "green",
  "teal",
  "cyan",
];

export default function Loading({
  variant = "border",
  color = "blue",
  size = "md",
  fullscreen = false,
  inline = false,
  label = "Loading...",
  withLabel = false,
}) {
  const colorClass = COLORS.includes(color) ? `text-${color}` : "";
  const sizeClass = size === "sm" ? "spinner-border-sm" : "";

  const spinnerClass = clsx(
    variant === "border" && "spinner-border",
    variant === "grow" && "spinner-grow",
    colorClass,
    sizeClass
  );

  const wrapperClass = clsx(
    inline
      ? "d-inline-flex align-items-center"
      : "d-flex align-items-center justify-content-center",
    fullscreen && "position-fixed top-0 start-0 vh-100 vw-100 bg-light"
  );

  if (variant === "dots") {
    return (
      <span className={wrapperClass} role="status">
        {withLabel && <span>{label}</span>}
        <span className="animated-dots"></span>
      </span>
    );
  }

  return (
    <div className={wrapperClass}>
      <div className={spinnerClass} role="status">
        <span className="visually-hidden">{label}</span>
      </div>
      {withLabel && <span className="ms-2">{label}</span>}
    </div>
  );
}
