import { useRef, useState } from "react";
import clsx from "clsx";
import Table from "@/components/common/table/Table";

const statusBadge = {
  display: "inline-block", fontSize: 11, padding: "1px 8px", borderRadius: 10,
  background: "#dcfce7", color: "#15803d",
};

// Truncates long content with an ellipsis and shows the full value in a tooltip
// after ~0.3s of hover — but only when the content is actually clipped.
function TruncatedCell({ value, children }) {
  const textRef = useRef(null);
  const [truncatable, setTruncatable] = useState(false);

  // Measure on hover: scrollWidth > clientWidth means the text is clipped.
  const checkTruncation = () => {
    const el = textRef.current;
    if (el) setTruncatable(el.scrollWidth > el.clientWidth);
  };

  return (
    <span
      className={clsx("trunc-cell", { "is-truncatable": truncatable })}
      onMouseEnter={checkTruncation}
    >
      <span className="trunc-cell__text" ref={textRef}>
        {children ?? value}
      </span>
      <span className="trunc-cell__tip">{value}</span>
    </span>
  );
}

export default function InstanceTable({ instances, selected, onToggle, onToggleAll, max }) {
  const atLimit = selected.length >= max;
  const allChecked = instances.length > 0 && selected.length === instances.length;

  const columns = [
    {
      key: "select",
      className: "text-center",
      label: (
        <input
          type="checkbox"
          className="form-check-input"
          checked={allChecked}
          onChange={(e) => onToggleAll(e.target.checked)}
        />
      ),
      render: (_value, row) => {
        const checked = selected.includes(row.instanceId);
        return (
          <input
            type="checkbox"
            className="form-check-input"
            checked={checked}
            disabled={!checked && atLimit}
            onChange={() => onToggle(row.instanceId)}
          />
        );
      },
    },
    {
      key: "instanceId",
      label: "Instance ID",
      className: "text-center",
      render: (v) => (
        <TruncatedCell value={v}>
          <code>{v}</code>
        </TruncatedCell>
      ),
    },
    {
      key: "name",
      label: "Name",
      className: "text-center",
      render: (v) => <TruncatedCell value={v ?? "-"}>{v ?? "-"}</TruncatedCell>,
    },
    { key: "csp", label: "CSP", className: "text-center" },
    {
      key: "spec",
      label: "Current Spec",
      className: "text-center",
      render: (v) => <TruncatedCell value={v ?? "-"}>{v ?? "-"}</TruncatedCell>,
    },
    { key: "usd", label: "Monthly Cost (USD)", className: "text-center", render: (v) => (v == null ? "-" : `$${v.toFixed(1)}`) },
    { key: "status", label: "Status", className: "text-center", render: (v) => <span style={statusBadge}>{v}</span> },
  ];

  return (
    <div className="instance-table">
      <style>{`
        /* Fixed layout keeps columns at proportional widths regardless of
           content length, so a long Instance ID no longer stretches the table. */
        .instance-table table { table-layout: fixed; width: 100%; }
        /* Let tooltips escape the responsive wrapper instead of being clipped.
           With table-layout: fixed the table fits, so no horizontal scroll is lost. */
        .instance-table .table-responsive { overflow: visible; }
        .instance-table th, .instance-table td { vertical-align: middle; }

        /* Column proportions (sum = 100%) */
        .instance-table th:nth-child(1), .instance-table td:nth-child(1) { width: 4%; }   /* select   */
        .instance-table th:nth-child(2), .instance-table td:nth-child(2) { width: 24%; }  /* Instance ID */
        .instance-table th:nth-child(3), .instance-table td:nth-child(3) { width: 18%; }  /* Name     */
        .instance-table th:nth-child(4), .instance-table td:nth-child(4) { width: 8%; }   /* CSP      */
        .instance-table th:nth-child(5), .instance-table td:nth-child(5) { width: 18%; }  /* Spec     */
        .instance-table th:nth-child(6), .instance-table td:nth-child(6) { width: 12%; }  /* USD      */
        .instance-table th:nth-child(7), .instance-table td:nth-child(7) { width: 16%; }  /* Status   */

        /* Ellipsis truncation */
        .trunc-cell { position: relative; display: block; max-width: 100%; }
        .trunc-cell__text {
          display: block;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        /* Hover tooltip — shows the full value after a 0.3s delay,
           only when the cell is actually truncated. */
        .trunc-cell__tip {
          position: absolute;
          left: 0;
          bottom: calc(100% + 4px);
          z-index: 1080;
          padding: 4px 8px;
          border-radius: 4px;
          background: #1f2937;
          color: #fff;
          font-size: 11px;
          line-height: 1.4;
          text-align: left;
          white-space: normal;
          word-break: break-all;
          width: max-content;
          max-width: 320px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
          opacity: 0;
          visibility: hidden;
          pointer-events: none;
          transition: opacity 0.12s ease, visibility 0.12s ease;
        }
        .trunc-cell.is-truncatable:hover .trunc-cell__tip {
          opacity: 1;
          visibility: visible;
          transition-delay: 0.3s;
        }
      `}</style>
      <Table columns={columns} data={instances} hover responsive />
    </div>
  );
}
