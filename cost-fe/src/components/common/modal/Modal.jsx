import clsx from "clsx";

/**
 * @component Modal
 * @description
 * Tabler 기반 커스텀 모달 컴포넌트.
 * 다양한 케이스(기본 모달, 경고/성공 모달, 폼 모달 등)를 하나로 커버할 수 있도록 props로 제어 가능.
 *
 * @prop {string} id - 모달에 부여할 고유 id (HTML id)
 * @prop {boolean} open - 모달 표시 여부 (true면 표시, false면 숨김)
 * @prop {function} onClose - 닫기 버튼 클릭 시 실행될 함수
 * @prop {string} [title] - 모달 상단에 표시할 제목 (없으면 헤더 숨김 가능)
 * @prop {"sm"|"md"|"lg"|"xl"} [size="md"] - 모달 크기
 * @prop {boolean} [centered=false] - 모달을 수직 가운데 정렬할지 여부
 * @prop {boolean} [scrollable=false] - 모달 본문을 스크롤 가능하게 할지 여부
 * @prop {"success"|"danger"|"warning"|"info"|string} [statusColor] - 상태 표시 바 색상 (ex: "danger" → 빨간 경고 바)
 * @prop {boolean} [hideHeader=false] - 헤더를 완전히 숨길지 여부
 * @prop {boolean} [hideFooter=false] - 푸터를 완전히 숨길지 여부
 * @prop {boolean} [closeButton=true] - 헤더의 닫기 버튼 표시 여부
 * @prop {ReactNode} [headerExtra] - 헤더 우측에 추가할 커스텀 요소 (예: 버튼, 아이콘 등)
 * @prop {ReactNode} children - 모달 본문(body)에 들어갈 내용
 * @prop {ReactNode} [footer] - 모달 하단 푸터 영역 (버튼, 링크 등)
 */
export default function Modal({
  id,
  open,
  onClose,
  title,
  size = "md",
  centered = false,
  scrollable = false,
  statusColor,
  hideHeader = false,
  hideFooter = false,
  closeButton = true,
  headerExtra,
  children,
  footer,
}) {
  if (!open) return null;

  return (
    <div
      className={clsx("modal fade show")}
      id={id}
      style={{ display: "block", backgroundColor: "rgba(0,0,0,0.5)" }}
      tabIndex="-1"
      role="dialog"
      aria-modal="true"
    >
      <div
        className={clsx("modal-dialog", {
          "modal-sm": size === "sm",
          "modal-lg": size === "lg",
          "modal-xl": size === "xl",
          "modal-dialog-centered": centered,
          "modal-dialog-scrollable": scrollable,
        })}
        role="document"
      >
        <div className="modal-content">
          {/* Header */}
          {!hideHeader && (title || closeButton) && (
            <div className="modal-header">
              {title && <h5 className="modal-title">{title}</h5>}
              {headerExtra}
              {closeButton && (
                <button
                  type="button"
                  className="btn-close"
                  aria-label="Close"
                  onClick={onClose}
                />
              )}
            </div>
          )}

          {/* 상태바 (statusColor 지정 시) */}
          {statusColor && <div className={`modal-status bg-${statusColor}`} />}

          {/* Body */}
          <div className="modal-body">{children}</div>

          {/* Footer */}
          {!hideFooter && footer && (
            <div className="modal-footer">{footer}</div>
          )}
        </div>
      </div>
    </div>
  );
}
