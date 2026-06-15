/**
 * @component Card
 *
 * @description
 * 기본 카드 컴포넌트. `title`과 `actions`를 주면 헤더가 있는 카드,
 * 아무 것도 안 주면 단순한 내용 카드로 동작.
 *
 * @prop {string|React.ReactNode} [title]
 *   카드 상단 제목 (없으면 헤더가 렌더링되지 않음)
 *
 * @prop {number} [titleSize=3]
 *   제목의 시맨틱 레벨 (1~6) → <h1>~<h6>
 *
 * @prop {number} [span=1]
 *   Grid에서 차지할 열(column) 개수
 *
 * @prop {React.ReactNode} [actions]
 *   헤더 우측에 표시할 액션 버튼/아이콘 (옵션)
 *
 * @prop {boolean} [noPadding=false]
 *   카드 body의 기본 padding 제거 여부
 *
 * @prop {React.ReactNode} children
 *   카드 본문(body) 컨텐츠
 */
export default function Card({
  title,
  titleSize = 3,
  span = 1,
  actions,
  noPadding = false, // 새로 추가
  children,
}) {
  const TitleTag = `h${titleSize}`;

  return (
    <div
      className="card"
      style={{
        gridColumn: `span ${span} / span ${span}`,
        display: "flex",
        flexDirection: "column",
        height: "100%",
      }}
    >
      {(title || actions) && (
        <div
          className="card-header"
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          {title && (
            <TitleTag className={`card-title h${titleSize}`}>{title}</TitleTag>
          )}
          {actions && <div className="card-actions">{actions}</div>}
        </div>
      )}

      <div
        className="card-body"
        style={{
          flex: 1,
          padding: noPadding ? 0 : undefined, // noPadding이면 padding 제거
        }}
      >
        {children}
      </div>
    </div>
  );
}
