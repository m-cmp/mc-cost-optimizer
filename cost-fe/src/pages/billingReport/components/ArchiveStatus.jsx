import { useState } from "react";
import Card from "@/components/common/card/Card";
import Table from "@/components/common/table/Table";
import Button from "@/components/common/button/Button";
import Modal from "@/components/common/modal/Modal";

const fmt = (ym) => (ym && ym.length === 6 ? `${ym.slice(0, 4)}-${ym.slice(4)}` : ym);

/**
 * Archive status + raw deletion. Rendered as its own card below the invoice card,
 * using the shared paginated Table.
 * The delete button only appears on VERIFIED & not-yet-purged rows. On click the gate is
 * re-checked, then a confirm dialog, then the delete. Notices are shown in a modal popup.
 * props: status[](manifest), purging("ym:csp"), onPurge(ym,csp), checkGate(ym,csp)
 */
export default function ArchiveStatus({ status, purging, onPurge, checkGate }) {
  // Notice modal ({ title, message, color }) — replaces alert()
  const [notice, setNotice] = useState(null);
  const showNotice = (title, message, color = "danger") =>
    setNotice({ title, message, color });

  const handlePurge = async (ym, csp) => {
    // Check the gate only on click (avoids N calls per render = fewer API hits)
    let gate;
    try {
      gate = await checkGate(ym, csp);
    } catch (e) {
      showNotice("Gate check failed", "Could not verify whether deletion is allowed. Please try again.");
      return;
    }
    if (!gate?.data?.canPurge) {
      showNotice(
        "Cannot delete raw data",
        gate?.data?.reason || "Deletion is not allowed for this month yet.",
      );
      return;
    }
    if (
      !window.confirm(
        `Delete raw cost data for ${fmt(ym)} / ${csp}?\nThe invoice archive is kept, but the original cannot be recovered. Proceed?`,
      )
    )
      return;
    const r = await onPurge(ym, csp);
    if (r && !r.success)
      showNotice("Delete failed", r.message || "Failed to delete raw data.");
  };

  const columns = [
    { key: "yearMonth", label: "Month", render: (v) => fmt(v) },
    { key: "csp", label: "CSP" },
    {
      key: "status",
      label: "Status",
      render: (_v, row) => {
        const purged = row.rawPurged === true || row.rawPurged === 1;
        if (purged) return <span className="text-muted">Archived (raw purged)</span>;
        if (row.status === "VERIFIED") return <span className="text-success">Archived</span>;
        return <span className="text-danger">{row.status}</span>;
      },
    },
    { key: "rowCount", label: "Rows", className: "text-end", render: (v) => v ?? "-" },
    {
      key: "action",
      label: "",
      className: "text-end",
      render: (_v, row) => {
        const key = `${row.yearMonth}:${row.csp}`;
        const purged = row.rawPurged === true || row.rawPurged === 1;
        const verified = row.status === "VERIFIED";
        if (!verified || purged) return null;
        return (
          <Button
            variant="outline-danger"
            size="sm"
            disabled={purging === key}
            onClick={() => handlePurge(row.yearMonth, row.csp)}
          >
            {purging === key ? "Deleting…" : "Delete Raw"}
          </Button>
        );
      },
    },
  ];

  return (
    <>
      <Card title="Archive Status" titleSize={2}>
        {!status || status.length === 0 ? (
          <div className="text-muted p-2" style={{ fontSize: 13 }}>
            No archive history
          </div>
        ) : (
          <Table columns={columns} data={status} pagination pageSize={10} hover striped />
        )}
      </Card>

      <Modal
        id="archiveNoticeModal"
        open={!!notice}
        onClose={() => setNotice(null)}
        title={notice?.title}
        size="sm"
        centered
        statusColor={notice?.color}
        footer={
          <Button variant="secondary" onClick={() => setNotice(null)}>
            OK
          </Button>
        }
      >
        <div style={{ fontSize: 14 }}>{notice?.message}</div>
      </Modal>
    </>
  );
}
