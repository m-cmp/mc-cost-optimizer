import Table from "@/components/common/table/Table";

const statusBadge = {
  display: "inline-block", fontSize: 11, padding: "1px 8px", borderRadius: 10,
  background: "#dcfce7", color: "#15803d",
};

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
    { key: "instanceId", label: "Instance ID", className: "text-nowrap text-center", render: (v) => <code>{v}</code> },
    { key: "name", label: "Name", className: "text-nowrap text-center" },
    { key: "csp", label: "CSP", className: "text-center" },
    { key: "spec", label: "Current Spec", className: "text-nowrap text-center", render: (v) => v ?? "-" },
    { key: "usd", label: "Monthly Cost (USD)", className: "text-center", render: (v) => (v == null ? "-" : `$${v.toFixed(1)}`) },
    { key: "status", label: "Status", className: "text-center", render: (v) => <span style={statusBadge}>{v}</span> },
  ];

  return (
    <div className="instance-table">
      <style>{`
        .instance-table th:nth-child(2),
        .instance-table td:nth-child(2) { width: 16%; }
        .instance-table th:nth-child(3),
        .instance-table td:nth-child(3) { width: 18%; }
        .instance-table th:nth-child(6),
        .instance-table td:nth-child(6) { width: 10%; }
      `}</style>
      <Table columns={columns} data={instances} hover responsive />
    </div>
  );
}
