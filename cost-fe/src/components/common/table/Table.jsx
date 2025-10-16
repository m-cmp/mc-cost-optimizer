import { useState } from "react";
import clsx from "clsx";

export default function Table({
  columns = [],
  data = [],
  striped = false,
  hover = false,
  responsive = true,
  nowrap = false,
  stickyHeader = false,
  variant = "",
  pagination = false,
  pageSize = 10,
  paginationVariant = "default",
}) {
  const [currentPage, setCurrentPage] = useState(1);

  const totalPages = pagination ? Math.ceil(data.length / pageSize) : 1;

  // ✅ 올바른 pagination 처리
  const paginatedData = pagination
    ? data.slice((currentPage - 1) * pageSize, currentPage * pageSize)
    : data;

  const tableClasses = clsx("table", {
    "table-striped": striped,
    "table-hover": hover,
    "table-nowrap": nowrap,
  });

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  const paginationClasses = clsx("pagination justify-content-center mt-3", {
    "pagination-outline": paginationVariant === "outline",
    "pagination-circle": paginationVariant === "circle",
  });

  return (
    <div className={clsx({ "table-responsive": responsive })}>
      <table className={tableClasses}>
        <thead className={clsx({ "sticky-top": stickyHeader })}>
          <tr>
            {columns.map((col) => (
              <th key={col.key} scope="col" className={col.className || ""}>
                {col.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {paginatedData.map((row, i) => (
            <tr key={i} className={variant ? `table-${variant}` : ""}>
              {columns.map((col) => (
                <td key={col.key} className={col.className || ""}>
                  {/* ✅ fallback 추가 */}
                  {row[col.key] !== undefined &&
                  row[col.key] !== null &&
                  row[col.key] !== ""
                    ? row[col.key]
                    : "-"}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      {pagination && totalPages > 1 && (
        <ul
          className={paginationClasses}
          style={{ marginTop: "1rem", marginBottom: "1rem" }}
        >
          {/* 이전 버튼 */}
          <li className={clsx("page-item", { disabled: currentPage === 1 })}>
            <button
              className={clsx("page-link", {
                "page-text": paginationVariant === "text",
              })}
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
            >
              {paginationVariant === "text" ? "Previous" : "«"}
            </button>
          </li>

          {/* 페이지 번호 */}
          {Array.from({ length: totalPages }, (_, idx) => (
            <li
              key={idx + 1}
              className={clsx("page-item", {
                active: currentPage === idx + 1,
              })}
            >
              <button
                className={clsx("page-link", {
                  "page-text": paginationVariant === "text",
                })}
                onClick={() => handlePageChange(idx + 1)}
              >
                {idx + 1}
              </button>
            </li>
          ))}

          {/* 다음 버튼 */}
          <li
            className={clsx("page-item", {
              disabled: currentPage === totalPages,
            })}
          >
            <button
              className={clsx("page-link", {
                "page-text": paginationVariant === "text",
              })}
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
            >
              {paginationVariant === "text" ? "Next" : "»"}
            </button>
          </li>
        </ul>
      )}
    </div>
  );
}
