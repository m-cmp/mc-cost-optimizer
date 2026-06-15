import React from "react";

/**
 * @component Grid (Tabler 기반)
 *
 * Tabler의 row/col 클래스를 활용한 반응형 Grid
 *
 * @prop {boolean} [equalHeight=false]
 *   true면 같은 행의 아이템 높이를 맞춤.
 *
 * @prop {Array<string>} [colWidths]
 *   CSS Grid의 fr 단위로 열 너비 지정 (예: ["1fr", "2fr"])
 *
 * @prop {React.ReactNode} children
 *   Grid 안에 배치될 요소들.
 */
export default function Grid({ equalHeight = false, colWidths, children }) {
  // colWidths가 있으면 CSS Grid 사용, 없으면 기존 Bootstrap/Tabler 방식 사용
  if (colWidths) {
    const gridStyle = {
      display: "grid",
      gridTemplateColumns: colWidths.join(" "),
      gap: "1rem",
      ...(equalHeight ? { alignItems: "stretch" } : {}),
    };

    return (
      <div style={gridStyle}>
        {React.Children.map(children, (child) => {
          if (!React.isValidElement(child)) return child;

          return equalHeight
            ? React.cloneElement(child, {
                style: {
                  ...child.props.style,
                  height: "100%",
                  display: "flex",
                  flexDirection: "column"
                },
              })
            : child;
        })}
      </div>
    );
  }

  // 기존 방식 (colWidths가 없을 때)
  return (
    <div className={`row gy-4 ${equalHeight ? "align-items-stretch" : ""}`}>
      {React.Children.map(children, (child) => {
        if (!React.isValidElement(child)) return child;

        const colSpan = child.props.colSpan || 6;
        const colClass = `col-12 col-md-${colSpan}`;

        return (
          <div className={colClass}>
            {equalHeight
              ? React.cloneElement(child, {
                  className: `${child.props.className || ""} h-100`,
                })
              : child}
          </div>
        );
      })}
    </div>
  );
}
