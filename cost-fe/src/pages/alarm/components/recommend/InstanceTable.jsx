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
    { key: "instanceId", label: "Instance ID", className: "text-nowrap", render: (v) => <code>{v}</code> },
    { key: "name", label: "Name", className: "text-nowrap" },
    { key: "csp", label: "CSP" },
    { key: "spec", label: "Current Spec", className: "text-nowrap" },
    { key: "usd", label: "Monthly Cost (USD)", render: (v) => `$${v.toFixed(1)}` },
    { key: "status", label: "Status", render: (v) => <span style={statusBadge}>{v}</span> },
  ];

  return <Table columns={columns} data={instances} hover responsive />;
}
