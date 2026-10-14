import { useState } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";

const CSPS = ["AWS", "GCP", "NCP", "AZURE"];
const fmt = (ym) => (ym && ym.length === 6 ? `${ym.slice(0, 4)}-${ym.slice(4)}` : ym);

/**
 * Archive modal. No delete option (delete is a separate "Delete Raw" action in the status table).
 * props: open, onClose, months[], archiving, onArchive({yearMonth,csps,projects}) => result
 */
export default function ArchiveModal({ open, onClose, months, archiving, onArchive }) {
  const [ym, setYm] = useState("");
  const [csps, setCsps] = useState(CSPS);
  const [result, setResult] = useState(null);

  const toggle = (c) =>
    setCsps((prev) => (prev.includes(c) ? prev.filter((x) => x !== c) : [...prev, c]));

  const handleRun = async () => {
    if (!ym || archiving || csps.length === 0) return;
    setResult(null);
    const r = await onArchive({ yearMonth: ym, csps, projects: [] });
    setResult(r);
  };

  const close = () => {
    setResult(null);
    onClose();
  };

  const footer = (
    <>
      <Button variant="secondary" onClick={close}>
        Close
      </Button>
      <Button
        variant="primary"
        disabled={!ym || archiving || csps.length === 0}
        onClick={handleRun}
      >
        {archiving ? "Archiving…" : "Archive"}
      </Button>
    </>
  );

  return (
    <Modal id="archiveModal" open={open} onClose={close} title="Cost Archive" size="md" footer={footer}>
      <div className="mb-3">
        <label className="form-label">Target month</label>
        <select
          className="form-select"
          value={ym}
          onChange={(e) => setYm(e.target.value)}
          disabled={archiving}
        >
          <option value="">Select…</option>
          {months.map((m) => (
            <option key={m} value={m}>
              {fmt(m)}
            </option>
          ))}
        </select>
      </div>

      <div className="mb-3">
        <label className="form-label">CSP</label>
        <div className="d-flex gap-3">
          {CSPS.map((c) => (
            <label key={c} className="form-check">
              <input
                type="checkbox"
                className="form-check-input"
                checked={csps.includes(c)}
                onChange={() => toggle(c)}
                disabled={archiving}
              />
              <span className="form-check-label">{c}</span>
            </label>
          ))}
        </div>
      </div>

      {result && Array.isArray(result.results) && (
        <div className="mt-2" style={{ fontSize: 13 }}>
          <div className="fw-bold mb-1">Results ({fmt(result.yearMonth)})</div>
          {result.results.map((r) => (
            <div key={r.csp} className="d-flex justify-content-between border-bottom py-1">
              <span>{r.csp}</span>
              <span
                className={
                  r.status === "VERIFIED"
                    ? "text-success"
                    : r.status === "SKIPPED"
                      ? "text-muted"
                      : "text-danger"
                }
              >
                {r.status}
                {r.status === "VERIFIED" ? ` (${r.rowCount} rows)` : ""}
                {r.message ? ` — ${r.message}` : ""}
              </span>
            </div>
          ))}
        </div>
      )}
    </Modal>
  );
}
