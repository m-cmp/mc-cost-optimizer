import { useState, useEffect } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";
import { startCurSetup, getCurSetupStatus } from "@/api/billing/curSetup";
import { getGcpSetupStatus, autoProvisionGcp, setupGcpDataset, confirmGcpBillingExport } from "@/api/billing/gcpSetup";

// ─── AWS ────────────────────────────────────────────────────────────────────

const AWS_STEPS_ORDER = [
  "OpenBao permission check",
  "Root key retrieval",
  "CUR existing report check",
  "IAM setup & key issuance",
  "S3 bucket creation",
  "CUR report creation",
  "Dedicated key storage (cost/aws)",
  "DB registration",
];

// ─── GCP Wizard ─────────────────────────────────────────────────────────────

const GCP_STEPS = ["Dataset", "Billing Export", "Table Detection"];

const stageToStepIdx = (stage) => {
  switch (stage) {
    case "DATASET":       return 0;
    case "BILLING_GUIDE": return 1;
    case "WAITING_TABLE": return 2;
    case "COMPLETE":      return 3;
    default:              return -1;
  }
};

// ─── Shared UI constants ─────────────────────────────────────────────────────

const STATUS_COLOR = {
  OK: "text-success", WARN: "text-warning",
  SKIP: "text-muted", FAILED: "text-danger", PENDING: "text-secondary",
};
const STATUS_BADGE = {
  OK: "badge bg-success text-white", WARN: "badge bg-warning text-dark",
  SKIP: "badge bg-secondary text-white", FAILED: "badge bg-danger text-white",
  PENDING: "badge bg-light text-secondary border",
};
const STATUS_ICON = { OK: "✓", WARN: "!", SKIP: "–", FAILED: "✗", PENDING: "…" };

// ─── Sub-components ──────────────────────────────────────────────────────────

function GcpStepper({ stage }) {
  const idx = stageToStepIdx(stage);
  if (idx < 0) return null;
  const complete = idx >= GCP_STEPS.length;
  const items = [];
  GCP_STEPS.forEach((label, i) => {
    const done   = complete || i < idx;
    const active = !complete && i === idx;
    items.push(
      <div key={label} className="text-center" style={{ flex: "0 0 auto" }}>
        <div style={{
          width: 26, height: 26, borderRadius: "50%", margin: "0 auto 3px",
          backgroundColor: done ? "#198754" : active ? "#0d6efd" : "#dee2e6",
          color: "#fff", display: "flex", alignItems: "center",
          justifyContent: "center", fontSize: 12, fontWeight: "bold",
        }}>
          {done ? "✓" : i + 1}
        </div>
        <div style={{ fontSize: 10, whiteSpace: "nowrap", color: done ? "#198754" : active ? "#0d6efd" : "#adb5bd" }}>
          {label}
        </div>
      </div>
    );
    if (i < GCP_STEPS.length - 1) {
      items.push(
        <div key={`l${i}`} style={{
          flex: 1, height: 2, marginBottom: 16,
          backgroundColor: done ? "#198754" : "#dee2e6",
        }} />
      );
    }
  });
  return <div className="d-flex align-items-center mb-4">{items}</div>;
}

function StepList({ steps }) {
  if (!steps?.length) return null;
  return (
    <div className="mb-3">
      {steps.map((s) => (
        <div key={s.name} className="d-flex align-items-start gap-2 py-1 border-bottom" style={{ fontSize: 13 }}>
          <span className={`fw-bold ${STATUS_COLOR[s.status] || "text-secondary"}`} style={{ width: 14, flexShrink: 0, marginTop: 1 }}>
            {STATUS_ICON[s.status] || "?"}
          </span>
          <div className="flex-grow-1">
            <span>{s.name}</span>
            {s.message && (
              <div className={s.status === "FAILED" ? "text-danger" : s.status === "WARN" ? "text-warning" : "text-muted"} style={{ fontSize: 11 }}>
                {s.message}
              </div>
            )}
          </div>
          <span className={STATUS_BADGE[s.status] || "badge bg-light border"} style={{ fontSize: 11, minWidth: 52, textAlign: "center" }}>
            {s.status}
          </span>
        </div>
      ))}
    </div>
  );
}

// ─── Main Component ──────────────────────────────────────────────────────────

export default function CspSettingModal({ open, onClose }) {
  const [csp, setCsp] = useState("aws");

  // ── AWS state ──────────────────────────────────────────────────────────────
  const [phase,         setPhase]         = useState("idle");
  const [steps,         setSteps]         = useState([]);
  const [result,        setResult]        = useState(null);
  const [errorMsg,      setErrorMsg]      = useState("");
  const [currentStatus, setCurrentStatus] = useState(null);

  // ── GCP state ──────────────────────────────────────────────────────────────
  const [gcpStage,        setGcpStage]        = useState(null);
  const [gcpBusy,         setGcpBusy]         = useState(false);
  const [gcpInfo,         setGcpInfo]         = useState(null);  // { adminKeyPresent, projectId, clientEmail, dataset, table }
  const [gcpDatasetAction,setGcpDatasetAction]= useState("create");
  const [gcpDatasetName,  setGcpDatasetName]  = useState("mcmp_billing_export");
  const [gcpSetupSteps,   setGcpSetupSteps]   = useState([]);
  const [gcpError,        setGcpError]        = useState("");
  const [gcpEditDataset,  setGcpEditDataset]  = useState(false);
  const [gcpTableCheckMsg,setGcpTableCheckMsg]= useState("");

  // ── AWS effects & handlers ─────────────────────────────────────────────────
  useEffect(() => {
    if (!open || csp !== "aws") return;
    setPhase("checking");
    getCurSetupStatus()
      .then((res) => {
        const s = res.data;
        setCurrentStatus(s);
        setPhase(s.costCredsStored && s.dbRegistered ? "configured" : "idle");
      })
      .catch(() => { setCurrentStatus(null); setPhase("idle"); });
  }, [open, csp]);

  const resetAws = () => { setPhase("idle"); setSteps([]); setResult(null); setErrorMsg(""); setCurrentStatus(null); };

  const handleStart = async () => {
    setPhase("running");
    setSteps(AWS_STEPS_ORDER.map((name) => ({ name, status: "PENDING", message: null })));
    try {
      const res = await startCurSetup();
      const returned = res.data.steps || [];
      const merged = AWS_STEPS_ORDER.map((name) => returned.find((s) => s.name === name) || { name, status: "PENDING", message: null });
      setSteps(merged);
      setResult(res.data);
      setPhase(merged.some((s) => s.status === "FAILED") ? "error" : "done");
    } catch (err) {
      setErrorMsg(err?.raw?.response?.data?.error || err?.userMessage || "An unknown error occurred.");
      setPhase("error");
    }
  };

  // ── GCP effects & handlers ─────────────────────────────────────────────────

  /** 상태만 조회 */
  const fetchGcpStatus = async () => {
    setGcpBusy(true);
    setGcpError("");
    try {
      const res = await getGcpSetupStatus();
      const d = res.data;
      setGcpStage(d.stage);
      setGcpInfo(d);
      if (d.dataset) setGcpDatasetName(d.dataset);
      return d;
    } catch {
      setGcpStage("NO_CREDENTIALS");
    } finally {
      setGcpBusy(false);
    }
  };

  /** WAITING_TABLE 단계에서 "지금 확인" 버튼 클릭 시 — BigQuery 실제 스캔 */
  const handleCheckTable = async () => {
    setGcpBusy(true);
    setGcpTableCheckMsg("");
    setGcpError("");
    try {
      const res = await confirmGcpBillingExport();
      const returnedSteps = res.data?.steps || [];
      const hasFailed = returnedSteps.some((s) => s.status === "FAILED");
      if (hasFailed) {
        setGcpError("An error occurred while checking the table.");
        setGcpSetupSteps(returnedSteps);
      } else {
        const d = await fetchGcpStatus();
        if (d?.stage === "WAITING_TABLE") {
          setGcpTableCheckMsg("Table not found yet. It may take up to 3 days after billing export is configured.");
        }
      }
    } catch {
      setGcpError("An error occurred while checking the table.");
    } finally {
      setGcpBusy(false);
    }
  };

  /** 버튼 클릭으로 수동 트리거 — SA 생성 + 역할 부여 + 데이터셋 생성 */
  const handleAutoProvision = async () => {
    setGcpBusy(true);
    setGcpSetupSteps([]);
    setGcpError("");
    setGcpStage(null);
    try {
      const provRes = await autoProvisionGcp();
      const provSteps = provRes.data?.steps || [];
      setGcpSetupSteps(provSteps);
      const hasFailed = provSteps.some((s) => s.status === "FAILED");
      if (hasFailed) {
        setGcpStage("NO_CREDENTIALS");
        setGcpError("An error occurred during auto setup. See details below.");
      } else {
        await fetchGcpStatus();
      }
    } catch {
      setGcpStage("NO_CREDENTIALS");
      setGcpError("An error occurred during auto setup.");
    } finally {
      setGcpBusy(false);
    }
  };

  useEffect(() => {
    if (!open || csp !== "gcp") return;
    setGcpEditDataset(false);
    setGcpSetupSteps([]);
    fetchGcpStatus();
  }, [open, csp]);

  const resetGcp = () => {
    setGcpStage(null); setGcpBusy(false); setGcpInfo(null);
    setGcpDatasetAction("create"); setGcpDatasetName("mcmp_billing_export");
    setGcpSetupSteps([]); setGcpError(""); setGcpEditDataset(false);
    setGcpTableCheckMsg("");
  };

  const handleDatasetSave = async () => {
    if (!gcpDatasetName.trim()) return;
    setGcpBusy(true);
    setGcpSetupSteps([]);
    setGcpError("");
    try {
      await setupGcpDataset(gcpDatasetAction, gcpDatasetName.trim());
      setGcpEditDataset(false);
      await fetchGcpStatus();
    } catch (err) {
      setGcpError(err?.raw?.response?.data?.error || "Failed to save dataset.");
    } finally {
      setGcpBusy(false);
    }
  };

  const handleBillingConfirm = async () => {
    setGcpBusy(true);
    setGcpSetupSteps([]);
    setGcpError("");
    try {
      const res = await confirmGcpBillingExport();
      const returnedSteps = res.data?.steps || [];
      const hasFailed = returnedSteps.some((s) => s.status === "FAILED");
      setGcpSetupSteps(returnedSteps);
      if (!hasFailed) {
        await fetchGcpStatus();
      } else {
        setGcpError("Some steps failed. See details below.");
      }
    } catch (err) {
      setGcpError(err?.raw?.response?.data?.error || "An error occurred.");
    } finally {
      setGcpBusy(false);
    }
  };

  // ── Close / CSP switch ─────────────────────────────────────────────────────
  const handleClose = () => { resetAws(); resetGcp(); setCsp("aws"); onClose(); };

  const handleCspChange = (next) => {
    if (next === csp) return;
    resetAws(); resetGcp(); setCsp(next);
  };

  // ── Tab badge helpers ──────────────────────────────────────────────────────
  const awsBadge = (phase === "configured" || phase === "done") ? " ✓" : "";
  const gcpBadge = gcpStage === "COMPLETE" ? " ✓" : (gcpStage && gcpStage !== "NO_CREDENTIALS" && gcpStage !== "DATASET") ? " ●" : "";

  // ── Footer ─────────────────────────────────────────────────────────────────
  const showDatasetForm = gcpStage === "DATASET" || gcpEditDataset;

  const footer = (() => {
    if (csp === "gcp") {
      if (gcpBusy || gcpStage === null) return <Button variant="secondary" disabled>Loading…</Button>;
      if (gcpStage === "NO_CREDENTIALS") {
        if (!gcpInfo?.adminKeyPresent) return <Button variant="secondary" onClick={handleClose}>Close</Button>;
        if (gcpError) return (
          <>
            <Button variant="secondary" onClick={handleClose}>Close</Button>
            <Button variant="outline-primary" onClick={handleAutoProvision}>Retry</Button>
          </>
        );
        return (
          <>
            <Button variant="secondary" onClick={handleClose}>Cancel</Button>
            <Button variant="primary" onClick={handleAutoProvision} disabled={gcpBusy}>Start GCP Setup</Button>
          </>
        );
      }
      if (showDatasetForm) return (
        <>
          {gcpEditDataset ? (
            <Button variant="secondary" onClick={() => { setGcpEditDataset(false); setGcpSetupSteps([]); setGcpError(""); }}>
              ← Back
            </Button>
          ) : (
            <Button variant="secondary" onClick={handleClose}>Cancel</Button>
          )}
          <Button variant="primary" onClick={handleDatasetSave} disabled={!gcpDatasetName.trim() || gcpBusy}>
            Save
          </Button>
        </>
      );
      if (gcpStage === "BILLING_GUIDE") return (
        <>
          <Button variant="secondary" onClick={handleClose}>Cancel</Button>
          <Button variant="primary" onClick={handleBillingConfirm}>Done →</Button>
        </>
      );
      if (gcpStage === "WAITING_TABLE") return (
        <>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="outline-primary" onClick={handleCheckTable} disabled={gcpBusy}>Check Now</Button>
        </>
      );
      if (gcpStage === "COMPLETE") return (
        <>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="outline-secondary" onClick={() => { setGcpEditDataset(true); }}>Reconfigure</Button>
        </>
      );
      return <Button variant="secondary" onClick={handleClose}>Close</Button>;
    }

    // AWS
    if (phase === "checking") return <Button variant="secondary" disabled>Loading…</Button>;
    if (phase === "configured") return (
      <>
        <Button variant="secondary" onClick={handleClose}>Close</Button>
        <Button variant="outline-primary" onClick={() => setPhase("idle")}>Reconfigure</Button>
      </>
    );
    if (phase === "idle") return (
      <>
        <Button variant="secondary" onClick={handleClose}>Cancel</Button>
        <Button variant="primary" onClick={handleStart}>Start Setup</Button>
      </>
    );
    if (phase === "running") return <Button variant="secondary" disabled>In progress…</Button>;
    if (phase === "done") return <Button variant="primary" onClick={handleClose}>Done</Button>;
    return (
      <>
        <Button variant="secondary" onClick={handleClose}>Close</Button>
        <Button variant="primary" onClick={() => setPhase("idle")}>Retry</Button>
      </>
    );
  })();

  const title = (() => {
    if (csp === "gcp") {
      if (gcpStage === "COMPLETE") return "Cost Setup — GCP Complete";
      if (gcpStage === "DATASET" || gcpEditDataset) return "GCP Setup — Dataset";
      if (gcpStage === "BILLING_GUIDE") return "GCP Setup — Billing Export";
      if (gcpStage === "WAITING_TABLE") return "GCP Setup — Waiting for Table";
    }
    if (csp === "aws" && (phase === "done" || phase === "configured")) return "Cost Setup — AWS Complete";
    return "Cost Setup";
  })();

  const failedStep = steps.find((s) => s.status === "FAILED");

  // ── Render ─────────────────────────────────────────────────────────────────
  return (
    <Modal
      id="cspSettingModal"
      open={open}
      onClose={handleClose}
      title={title}
      size="md"
      centered
      statusColor={
        (csp === "aws" && (phase === "configured" || phase === "done")) ||
        (csp === "gcp" && gcpStage === "COMPLETE")
          ? "success"
          : (csp === "aws" && phase === "error") || (csp === "gcp" && gcpError)
          ? "danger"
          : undefined
      }
      footer={footer}
    >
      {/* CSP tabs */}
      <ul className="nav nav-tabs mb-3" style={{ borderBottom: "1px solid #e5e7eb", flexShrink: 0 }}>
        <li className="nav-item">
          <a className={`nav-link ${csp === "aws" ? "active" : ""}`} role="button"
            onClick={() => handleCspChange("aws")} style={{ fontSize: 14, padding: "6px 16px" }}>
            AWS{awsBadge && <span className="text-success ms-1" style={{ fontSize: 12 }}>{awsBadge}</span>}
          </a>
        </li>
        <li className="nav-item">
          <a className={`nav-link ${csp === "gcp" ? "active" : ""}`} role="button"
            onClick={() => handleCspChange("gcp")} style={{ fontSize: 14, padding: "6px 16px" }}>
            GCP{gcpBadge && (
              <span className={gcpStage === "COMPLETE" ? "text-success ms-1" : "text-warning ms-1"} style={{ fontSize: 12 }}>
                {gcpBadge}
              </span>
            )}
          </a>
        </li>
      </ul>

      {/* ── AWS content ── */}
      {csp === "aws" && (
        <div style={{ overflowY: "auto", maxHeight: 420 }}>
          {phase === "checking" && <p className="text-muted mb-0" style={{ fontSize: 14 }}>Checking current setup status…</p>}

          {phase === "configured" && (
            <div className="border border-success rounded p-3" style={{ fontSize: 13 }}>
              <div className="d-flex align-items-center gap-2 mb-3">
                <span className="badge bg-success text-white">Configured</span>
                <span className="text-muted" style={{ fontSize: 12 }}>AWS CUR setup is complete.</span>
              </div>
              {currentStatus?.bucketName && (
                <div className="mb-2">
                  <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>S3 Bucket</div>
                  <code style={{ fontSize: 12 }}>{currentStatus.bucketName}</code>
                </div>
              )}
              {currentStatus?.reportName && (
                <div className="mb-2">
                  <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>CUR Report</div>
                  <code style={{ fontSize: 12 }}>{currentStatus.reportName}</code>
                </div>
              )}
            </div>
          )}

          {phase === "idle" && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>Automatically configures IAM user, S3 bucket, and CUR report.</p>
          )}

          {(phase === "running" || phase === "done" || phase === "error") && (
            <div>
              {phase === "running" && <p className="text-muted mb-3" style={{ fontSize: 14 }}>Setting up AWS resources. Please wait…</p>}
              <StepList steps={steps} />
              {phase === "done" && result && (
                <div className="border border-success rounded p-3 mb-0" style={{ fontSize: 13 }}>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase" }}>Bucket</div>
                    <code style={{ wordBreak: "break-all", fontSize: 12 }}>{result.bucketName}</code>
                  </div>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase" }}>Report</div>
                    <code style={{ fontSize: 12 }}>{result.reportName}</code>
                  </div>
                  <div className="text-muted pt-2 border-top" style={{ fontSize: 12 }}>
                    ⏱ CUR data will be available in S3 within 24 hours.
                  </div>
                </div>
              )}
              {phase === "error" && (
                <div className="alert alert-danger py-2 mb-0" style={{ fontSize: 13 }}>
                  {failedStep ? (
                    <><div><strong>Failed step:</strong> {failedStep.name}</div><div><strong>Reason:</strong> {failedStep.message}</div></>
                  ) : (
                    <div>{errorMsg}</div>
                  )}
                  <div className="mt-1 text-muted" style={{ fontSize: 12 }}>Completed steps will be skipped on retry.</div>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* ── GCP content ── */}
      {csp === "gcp" && (
        <div style={{ overflowY: "auto", maxHeight: 420 }}>
          {(gcpBusy && gcpStage === null) && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>Checking GCP setup status…</p>
          )}

          {gcpStage === "NO_CREDENTIALS" && (
            <div style={{ fontSize: 13 }}>
              {!gcpInfo?.adminKeyPresent ? (
                <div className="alert alert-warning mb-0" style={{ fontSize: 13 }}>
                  <div className="fw-bold mb-1">GCP credentials not registered</div>
                  <div style={{ fontSize: 12 }}>
                    Please register a GCP service account key (JSON) with Owner permission in credential management first.
                  </div>
                </div>
              ) : gcpSetupSteps.length > 0 ? (
                <>
                  <StepList steps={gcpSetupSteps} />
                  {gcpError && (
                    <div className="alert alert-danger py-2 mb-0" style={{ fontSize: 13 }}>{gcpError}</div>
                  )}
                </>
              ) : (
                <div>
                  <div className="fw-semibold mb-2">GCP Auto Setup</div>
                  <p className="text-muted mb-2" style={{ fontSize: 12, lineHeight: 1.7 }}>
                    The following will be created automatically:
                  </p>
                  <ul style={{ fontSize: 12, color: "#444", paddingLeft: 18, marginBottom: 0 }}>
                    <li>Create operational service account (mcmp-cost-collector) and grant IAM roles</li>
                    <li>Create default BigQuery dataset (mcmp_billing_export)</li>
                  </ul>
                </div>
              )}
            </div>
          )}

          {gcpStage && gcpStage !== "NO_CREDENTIALS" && (
            <GcpStepper stage={gcpEditDataset ? "DATASET" : gcpStage} />
          )}

          {/* Step 1: Dataset */}
          {gcpStage && showDatasetForm && (
            <div>
              <div className="mb-3" style={{ fontSize: 13 }}>
                <div className="fw-semibold mb-2">Configure dataset name</div>
                <div className="d-flex gap-3 mb-2">
                  {[["create", "Create new"], ["existing", "Use existing"]].map(([val, label]) => (
                    <label key={val} className="d-flex align-items-center gap-1" style={{ cursor: "pointer", fontSize: 13 }}>
                      <input type="radio" name="gcpDatasetAction" value={val}
                        checked={gcpDatasetAction === val}
                        onChange={() => setGcpDatasetAction(val)} />
                      {label}
                    </label>
                  ))}
                </div>
                <input
                  className="form-control form-control-sm"
                  style={{ fontSize: 13 }}
                  value={gcpDatasetName}
                  onChange={(e) => setGcpDatasetName(e.target.value)}
                  placeholder="mc_billing_export"
                />
                {gcpDatasetAction === "existing" && (
                  <div className="text-warning mt-1" style={{ fontSize: 11 }}>
                    ⚠ Entering the wrong name may cause billing data collection errors.
                  </div>
                )}
              </div>

              {gcpError && (
                <div className="alert alert-danger py-2 mb-2" style={{ fontSize: 13 }}>
                  <div>{gcpError}</div>
                </div>
              )}
              {gcpSetupSteps.length > 0 && <StepList steps={gcpSetupSteps} />}
              {gcpBusy && (
                <p className="text-muted" style={{ fontSize: 13 }}>Configuring IAM roles and dataset…</p>
              )}
            </div>
          )}

          {/* Step 2: Billing Export */}
          {gcpStage === "BILLING_GUIDE" && !gcpEditDataset && (
            <div style={{ fontSize: 13 }}>
              <div className="fw-semibold mb-2">Configure billing export in GCP Console</div>
              <p style={{ fontSize: 12, lineHeight: 1.9, color: "#444" }}>
                Follow the <a href="https://docs.cloud.google.com/billing/docs/how-to/export-data-bigquery-setup" target="_blank" rel="noreferrer">official guide</a> to set up billing export.
                Link the export to dataset <code style={{ fontSize: 11 }}>{gcpDatasetName}</code>.
                Click the button below when done.
                {gcpInfo?.dataset && (
                  <><br /><button className="btn btn-link p-0" style={{ fontSize: 11 }} onClick={() => setGcpEditDataset(true)}>Change dataset</button></>
                )}
              </p>
              <ol style={{ fontSize: 12, lineHeight: 1.9, color: "#444", paddingLeft: 18 }}>
                <li>In GCP Console, search for <strong>"billing export"</strong> in the search bar.</li>
                <li>Select <strong>Standard data (usage cost) export</strong>.</li>
                <li>Choose the dataset you just created (<code style={{ fontSize: 11 }}>{gcpDatasetName}</code>) as the export target.</li>
                <li>Click <strong>Save</strong>.</li>
              </ol>
              <div className="alert alert-warning py-2 mb-0" style={{ fontSize: 12 }}>
                ⚠ It may take up to <strong>3 days</strong> for the billing table to be created after enabling billing export.
              </div>
              {gcpBusy && (
                <p className="text-muted mt-2" style={{ fontSize: 13 }}>Configuring IAM roles and dataset…</p>
              )}
              {gcpSetupSteps.length > 0 && (
                <div className="mt-3">
                  <div className="text-muted mb-1" style={{ fontSize: 11 }}>Setup result</div>
                  <StepList steps={gcpSetupSteps} />
                </div>
              )}
              {gcpError && (
                <div className="alert alert-danger py-2 mt-2 mb-0" style={{ fontSize: 13 }}>{gcpError}</div>
              )}
            </div>
          )}

          {/* Step 3: Waiting for Table */}
          {gcpStage === "WAITING_TABLE" && !gcpEditDataset && (
            <div style={{ fontSize: 13 }}>
              {gcpInfo?.dataset && (
                <div className="d-flex align-items-center gap-2 mb-3 p-2 bg-light rounded">
                  <span className="text-muted" style={{ fontSize: 11 }}>Dataset</span>
                  <code style={{ fontSize: 12 }}>{gcpInfo.dataset}</code>
                  <button className="btn btn-link p-0 ms-auto" style={{ fontSize: 11 }}
                    onClick={() => setGcpEditDataset(true)}>Change</button>
                </div>
              )}
              <div className="text-center py-3">
                <div style={{ fontSize: 28, marginBottom: 8 }}>⏳</div>
                <div className="fw-semibold mb-1">Waiting for billing table</div>
                <div className="text-muted" style={{ fontSize: 12 }}>
                  GCP will automatically create the table within 3 days after billing export is configured.<br />
                  Auto-detected daily at 09:00. You can also check now.
                </div>
              </div>
              {gcpTableCheckMsg && (
                <div className="alert alert-warning py-2 mb-2" style={{ fontSize: 12 }}>{gcpTableCheckMsg}</div>
              )}
              {gcpError && (
                <div className="alert alert-danger py-2 mb-0" style={{ fontSize: 13 }}>{gcpError}</div>
              )}
            </div>
          )}

          {/* Complete */}
          {gcpStage === "COMPLETE" && !gcpEditDataset && (
            <div className="border border-success rounded p-3" style={{ fontSize: 13 }}>
              <div className="d-flex align-items-center gap-2 mb-3">
                <span className="badge bg-success text-white">Configured</span>
                <span className="text-muted" style={{ fontSize: 12 }}>GCP billing data collection is ready.</span>
              </div>
              <div className="mb-2">
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Project</div>
                <code style={{ fontSize: 12 }}>{gcpInfo?.projectId}</code>
              </div>
              <div className="mb-2">
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Service Account</div>
                <code style={{ fontSize: 12 }}>{gcpInfo?.clientEmail}</code>
              </div>
              <div>
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Billing Table</div>
                <code style={{ fontSize: 12 }}>{gcpInfo?.dataset}.{gcpInfo?.table}</code>
              </div>
            </div>
          )}
        </div>
      )}
    </Modal>
  );
}
