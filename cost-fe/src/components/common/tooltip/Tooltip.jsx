import { useEffect, useRef } from "react";

let Tooltip = null;
const loadBootstrap = async () => {
  if (!Tooltip) {
    try {
      const bootstrap = await import(
        "bootstrap/dist/js/bootstrap.bundle.min.js"
      );
      Tooltip = bootstrap.Tooltip;
    } catch (error) {
      console.error("Failed to load Bootstrap:", error);
    }
  }
  return Tooltip;
};

/**
 * @component Tooltip
 *
 * @description
 * Tabler 기반 Tooltip 컴포넌트
 *
 * @prop {React.ReactNode} children - Tooltip이 적용될 요소
 * @prop {string} title - Tooltip에 표시될 텍스트
 * @prop {string} [placement="top"] - Tooltip 위치 (top, right, bottom, left)
 * @prop {boolean} [html=false] - HTML 사용 여부
 * @prop {string} [className=""] - 추가 CSS 클래스
 */
export default function TooltipComponent({
  children,
  title,
  placement = "top",
  html = false,
  className = "",
}) {
  const elementRef = useRef(null);
  const tooltipInstanceRef = useRef(null);

  useEffect(() => {
    let isMounted = true;

    const initTooltip = async () => {
      const TooltipClass = await loadBootstrap();

      if (!TooltipClass || !elementRef.current || !isMounted) {
        return;
      }

      // 기존 tooltip이 있으면 제거
      if (tooltipInstanceRef.current) {
        tooltipInstanceRef.current.dispose();
      }

      // 새로운 tooltip 생성
      tooltipInstanceRef.current = new TooltipClass(elementRef.current, {
        placement,
        html,
        trigger: "hover focus",
      });
    };

    initTooltip();

    // Cleanup
    return () => {
      isMounted = false;
      if (tooltipInstanceRef.current) {
        tooltipInstanceRef.current.dispose();
        tooltipInstanceRef.current = null;
      }
    };
  }, [title, placement, html]);

  return (
    <span
      ref={elementRef}
      data-bs-toggle="tooltip"
      data-bs-placement={placement}
      data-bs-html={html}
      title={title}
      className={className}
    >
      {children}
    </span>
  );
}
