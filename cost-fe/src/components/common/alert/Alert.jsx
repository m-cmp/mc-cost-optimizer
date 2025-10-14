import { useEffect } from "react";
import { createPortal } from "react-dom";
import clsx from "clsx";
import { Icons } from "../../../icons/Icons";

/**
 * Toast-style Alert Component
 *
 * 전역 우측 상단에 쌓이는 알림 컴포넌트.
 * 어디서든 <Alert />만 호출하면 동일한 위치에 표시됩니다.
 *
 * @component
 *
 * @prop {"info" | "success" | "danger" | "warning"} [variant="info"]
 *   알림 색상 및 아이콘 스타일
 *
 * @prop {string} title
 *   알림 제목
 *
 * @prop {string} message
 *   알림 메시지
 *
 * @prop {boolean} [dismissible=true]
 *   닫기 버튼 표시 여부
 *
 * @prop {number} [duration=0]
 *   자동 닫힘 시간(ms).
 *   0이면 자동 닫힘 없음
 *
 * @prop {() => void} [onClose]
 *   닫기 버튼 클릭 또는 자동 닫힘 시 실행되는 콜백
 */
export default function Alert({
  variant = "info",
  title,
  message,
  dismissible = true,
  duration = 0,
  onClose,
}) {
  // 자동 닫기
  useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        onClose?.();
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [duration, onClose]);

  // 전역 컨테이너 생성 (없으면 body 밑에 append)
  const containerId = "global-toast-container";
  let container = document.getElementById(containerId);
  if (!container) {
    container = document.createElement("div");
    container.id = containerId;
    container.className = "toast-container position-fixed top-0 end-0 p-3"; // 위치는 여기서만 제어
    container.style.zIndex = 9999;
    document.body.appendChild(container);
  }

  const toast = (
    <div
      className={clsx("toast show text-white", {
        "bg-success": variant === "success",
        "bg-danger": variant === "danger",
        "bg-info": variant === "info",
        "bg-warning": variant === "warning",
      })}
      role="alert"
      aria-live="assertive"
      aria-atomic="true"
      style={{ margin: 0 }} // ✅ margin 제거 → 밑 레이아웃에 영향 없음
    >
      <div className="toast-header">
        {Icons[variant] && <span className="me-2">{Icons[variant]({})}</span>}
        <strong className="me-auto">{title}</strong>
        {dismissible && (
          <button
            type="button"
            className="btn-close"
            aria-label="Close"
            onClick={onClose}
          />
        )}
      </div>
      <div className="toast-body">{message}</div>
    </div>
  );

  return createPortal(toast, container);
}
