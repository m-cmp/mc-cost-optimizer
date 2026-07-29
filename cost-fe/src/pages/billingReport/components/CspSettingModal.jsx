import { useState, useEffect } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";
import { startCurSetup, getCurSetupStatus } from "@/api/billing/curSetup";
import { startGcpSetup, getGcpSetupStatus } from "@/api/billing/gcpSetup";

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

const GCP_STEPS_ORDER = [
  "OpenBao permission check",
  "GCP credentials check",
  "BigQuery connection test",
  "Billing table discovery",
  "Store to OpenBao (cost/gcp)",
];

const STEPS_ORDER = AWS_STEPS_ORDER;

const STATUS_COLOR = {
  OK:      "text-success",
  WARN:    "text-warning",
  SKIP:    "text-muted",
  FAILED:  "text-danger",
  PENDING: "text-secondary",
};

const STATUS_BADGE = {
  OK:      "badge bg-success text-white",
  WARN:    "badge bg-warning text-dark",
  SKIP:    "badge bg-secondary text-white",
  FAILED:  "badge bg-danger text-white",
  PENDING: "badge bg-light text-secondary border",
};

const STATUS_ICON = {
  OK:      "✓",
  WARN:    "!",
  SKIP:    "–",
  FAILED:  "✗",
  PENDING: "…",
};

export default function CspSettingModal({ open, onClose }) {
  const [csp, setCsp] = useState("aws");

  // AWS state
  const [phase, setPhase] = useState("idle");
  const [steps, setSteps] = useState([]);
  const [result, setResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState("");
  const [currentStatus, setCurrentStatus] = useState(null);

  // GCP state
  const [gcpPhase, setGcpPhase] = useState("idle");
  const [gcpSteps, setGcpSteps] = useState([]);
  const [gcpResult, setGcpResult] = useState(null);
  const [gcpErrorMsg, setGcpErrorMsg] = useState("");

  // AWS 상태 조회
  useEffect(() => {
    if (!open || csp !== "aws") return;
    setPhase("checking");
    getCurSetupStatus()
      .then((res) => {
        const s = res.data;
        setCurrentStatus(s);
        setPhase(s.costCredsStored && s.dbRegistered ? "configured" : "idle");
      })
      .catch(() => {
        setCurrentStatus(null);
        setPhase("idle");
      });
  }, [open, csp]);

  // GCP 상태 조회
  useEffect(() => {
    if (!open || csp !== "gcp") return;
    setGcpPhase("checking");
    getGcpSetupStatus()
      .then((res) => {
        setGcpPhase(res.data.configured ? "configured" : "idle");
      })
      .catch(() => setGcpPhase("idle"));
  }, [open, csp]);

  const resetAws = () => {
    setPhase("idle");
    setSteps([]);
    setResult(null);
    setErrorMsg("");
    setCurrentStatus(null);
  };

  const resetGcp = () => {
    setGcpPhase("idle");
    setGcpSteps([]);
    setGcpResult(null);
    setGcpErrorMsg("");
  };

  const handleClose = () => {
    resetAws();
    resetGcp();
    setCsp("aws");
    onClose();
  };

  const handleCspChange = (next) => {
    if (next === csp) return;
    resetAws();
    resetGcp();
    setCsp(next);
  };

  const handleStart = async () => {
    setPhase("running");
    setSteps(AWS_STEPS_ORDER.map((name) => ({ name, status: "PENDING", message: null })));
    try {
      const res = await startCurSetup();
      const returned = res.data.steps || [];
      const merged = AWS_STEPS_ORDER.map((name) => {
        const found = returned.find((s) => s.name === name);
        return found || { name, status: "PENDING", message: null };
      });
      setSteps(merged);
      setResult(res.data);
      setPhase(merged.some((s) => s.status === "FAILED") ? "error" : "done");
    } catch (err) {
      const msg = err?.raw?.response?.data?.error || err?.userMessage || "An unknown error occurred.";
      setErrorMsg(msg);
      setPhase("error");
    }
  };

  const handleGcpStart = async () => {
    setGcpPhase("running");
    setGcpSteps(GCP_STEPS_ORDER.map((name) => ({ name, status: "PENDING", message: null })));
    try {
      const res = await startGcpSetup();
      const returned = res.data.steps || [];
      const merged = GCP_STEPS_ORDER.map((name) => {
        const found = returned.find((s) => s.name === name);
        return found || { name, status: "PENDING", message: null };
      });
      setGcpSteps(merged);
      setGcpResult(res.data);
      setGcpPhase(merged.some((s) => s.status === "FAILED") ? "error" : "done");
    } catch (err) {
      const msg = err?.raw?.response?.data?.error || err?.userMessage || "An unknown error occurred.";
      setGcpErrorMsg(msg);
      setGcpPhase("error");
    }
  };

  const failedStep = steps.find((s) => s.status === "FAILED");
  const gcpFailedStep = gcpSteps.find((s) => s.status === "FAILED");

  const footer = (() => {
    if (csp === "gcp") {
      if (gcpPhase === "checking") return <Button variant="secondary" disabled>Loading…</Button>;
      if (gcpPhase === "configured") return (
        <>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="outline-primary" onClick={() => setGcpPhase("idle")}>Reconfigure</Button>
        </>
      );
      if (gcpPhase === "idle") return (
        <>
          <Button variant="secondary" onClick={handleClose}>Cancel</Button>
          <Button variant="primary" onClick={handleGcpStart}>Start Setup</Button>
        </>
      );
      if (gcpPhase === "running") return <Button variant="secondary" disabled>In progress…</Button>;
      if (gcpPhase === "done") return <Button variant="primary" onClick={handleClose}>Done</Button>;
      return (
        <>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="primary" onClick={() => setGcpPhase("idle")}>Retry</Button>
        </>
      );
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

  const title =
    (csp === "aws" && phase === "done") || (csp === "gcp" && gcpPhase === "done")
      ? "Cost Setup — Complete"
      : "Cost Setup";

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
        (csp === "gcp" && (gcpPhase === "configured" || gcpPhase === "done"))
          ? "success"
          : (csp === "aws" && phase === "error") || (csp === "gcp" && gcpPhase === "error")
          ? "danger"
          : undefined
      }
      footer={footer}
    >
      {/* CSP tabs */}
      <ul className="nav nav-tabs mb-3" style={{ borderBottom: "1px solid #e5e7eb", flexShrink: 0 }}>
        <li className="nav-item">
          <a
            className={`nav-link ${csp === "aws" ? "active" : ""}`}
            role="button"
            onClick={() => handleCspChange("aws")}
            style={{ fontSize: 14, padding: "6px 16px" }}
          >
            AWS
          </a>
        </li>
        <li className="nav-item">
          <a
            className={`nav-link ${csp === "gcp" ? "active" : ""}`}
            role="button"
            onClick={() => handleCspChange("gcp")}
            style={{ fontSize: 14, padding: "6px 16px" }}
          >
            GCP
          </a>
        </li>
      </ul>

      {/* AWS content */}
      {csp === "aws" && (
        <div style={{ overflowY: "auto", maxHeight: 420 }}>
          {phase === "checking" && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>Checking current setup status…</p>
          )}

          {phase === "configured" && (
            <div className="border border-success rounded p-3" style={{ fontSize: 13 }}>
              <div className="d-flex align-items-center gap-2 mb-3">
                <span className="badge bg-success text-white">Configured</span>
                <span className="text-muted" style={{ fontSize: 12 }}>AWS CUR setup is complete.</span>
              </div>
              <div className="mb-2">
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Credentials</div>
                <span className="text-success" style={{ fontSize: 13 }}>✓ Stored in OpenBao (cost/aws)</span>
              </div>
              <div>
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>DB</div>
                <span className="text-success" style={{ fontSize: 13 }}>✓ Registered</span>
              </div>
            </div>
          )}

          {phase === "idle" && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>
              Automatically configures IAM user, S3 bucket, and CUR report.
            </p>
          )}

          {(phase === "running" || phase === "done" || phase === "error") && (
            <div>
              {phase === "running" && (
                <p className="text-muted mb-3" style={{ fontSize: 14 }}>
                  Setting up AWS resources. Please wait…
                </p>
              )}

              <div className="mb-3">
                {steps.map((step) => (
                  <div
                    key={step.name}
                    className="d-flex align-items-start gap-2 py-1 border-bottom"
                    style={{ fontSize: 14 }}
                  >
                    <span
                      className={`fw-bold ${STATUS_COLOR[step.status] || "text-secondary"}`}
                      style={{ width: 14, flexShrink: 0, marginTop: 1 }}
                    >
                      {STATUS_ICON[step.status] || "?"}
                    </span>
                    <div className="flex-grow-1">
                      <span>{step.name}</span>
                      {step.status === "SKIP" && step.message && (
                        <span className="text-muted ms-1">({step.message})</span>
                      )}
                      {step.status === "WARN" && step.message && (
                        <div className="text-warning" style={{ fontSize: 12 }}>{step.message}</div>
                      )}
                      {step.status === "FAILED" && step.message && (
                        <div className="text-danger" style={{ fontSize: 12 }}>{step.message}</div>
                      )}
                    </div>
                    <span className={`${STATUS_BADGE[step.status] || "badge bg-light border"} ms-auto`} style={{ fontSize: 12, minWidth: 52, textAlign: "center" }}>
                      {step.status}
                    </span>
                  </div>
                ))}
              </div>

              {phase === "done" && result && (
                <div className="border border-success rounded p-3 mb-0" style={{ fontSize: 13 }}>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Bucket</div>
                    <code style={{ wordBreak: "break-all", fontSize: 12 }}>{result.bucketName}</code>
                  </div>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Report</div>
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
                    <>
                      <div><strong>Failed step:</strong> {failedStep.name}</div>
                      <div><strong>Reason:</strong> {failedStep.message}</div>
                    </>
                  ) : (
                    <div>{errorMsg}</div>
                  )}
                  <div className="mt-1 text-muted" style={{ fontSize: 12 }}>
                    Completed steps will be skipped on retry.
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* GCP content */}
      {csp === "gcp" && (
        <div style={{ overflowY: "auto", maxHeight: 420 }}>
          {gcpPhase === "checking" && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>Checking current setup status…</p>
          )}

          {gcpPhase === "configured" && (
            <div className="border border-success rounded p-3" style={{ fontSize: 13 }}>
              <div className="d-flex align-items-center gap-2 mb-3">
                <span className="badge bg-success text-white">Configured</span>
                <span className="text-muted" style={{ fontSize: 12 }}>GCP Setup is complete.</span>
              </div>
              <div className="mb-2">
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Credentials</div>
                <span className="text-success" style={{ fontSize: 13 }}>✓ Stored in OpenBao (cost/gcp)</span>
              </div>
              <div>
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>DB</div>
                <span className="text-success" style={{ fontSize: 13 }}>✓ Registered</span>
              </div>
            </div>
          )}

          {gcpPhase === "idle" && (
            <div>
              <p className="text-muted mb-2" style={{ fontSize: 14 }}>
                Automatically discovers and registers the BigQuery billing export table.
              </p>
              <div className="alert alert-info py-2" style={{ fontSize: 12 }}>
                <strong>Prerequisite:</strong> Cloud Billing data export must be enabled in GCP Console
                (Billing → Billing export → BigQuery export) before running Setup.
              </div>
            </div>
          )}

          {(gcpPhase === "running" || gcpPhase === "done" || gcpPhase === "error") && (
            <div>
              {gcpPhase === "running" && (
                <p className="text-muted mb-3" style={{ fontSize: 14 }}>
                  Connecting to BigQuery and discovering billing table. Please wait…
                </p>
              )}

              <div className="mb-3">
                {gcpSteps.map((step) => (
                  <div
                    key={step.name}
                    className="d-flex align-items-start gap-2 py-1 border-bottom"
                    style={{ fontSize: 14 }}
                  >
                    <span
                      className={`fw-bold ${STATUS_COLOR[step.status] || "text-secondary"}`}
                      style={{ width: 14, flexShrink: 0, marginTop: 1 }}
                    >
                      {STATUS_ICON[step.status] || "?"}
                    </span>
                    <div className="flex-grow-1">
                      <span>{step.name}</span>
                      {step.status === "WARN" && step.message && (
                        <div className="text-warning" style={{ fontSize: 12 }}>{step.message}</div>
                      )}
                      {step.status === "FAILED" && step.message && (
                        <div className="text-danger" style={{ fontSize: 12 }}>{step.message}</div>
                      )}
                    </div>
                    <span className={`${STATUS_BADGE[step.status] || "badge bg-light border"} ms-auto`} style={{ fontSize: 12, minWidth: 52, textAlign: "center" }}>
                      {step.status}
                    </span>
                  </div>
                ))}
              </div>

              {gcpPhase === "done" && gcpResult && (
                <div className={`border rounded p-3 mb-0 ${gcpResult.dataset ? "border-success" : "border-warning"}`} style={{ fontSize: 13 }}>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Project</div>
                    <code style={{ fontSize: 12 }}>{gcpResult.projectId}</code>
                  </div>
                  {gcpResult.dataset && gcpResult.table ? (
                    <>
                      <div className="mb-2">
                        <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Billing Table</div>
                        <code style={{ wordBreak: "break-all", fontSize: 12 }}>{gcpResult.dataset}.{gcpResult.table}</code>
                      </div>
                      <div className="text-muted pt-2 border-top" style={{ fontSize: 12 }}>
                        GCP Collector will use this table on next restart.
                      </div>
                    </>
                  ) : (
                    <div className="alert alert-warning py-2 mb-0 mt-2" style={{ fontSize: 12 }}>
                      <strong>Billing table not found yet.</strong><br />
                      GCP creates the export table within 24h after enabling Billing Export.
                      Credentials are saved — re-run Setup once the table appears.
                    </div>
                  )}
                </div>
              )}

              {gcpPhase === "error" && (
                <div className="alert alert-danger py-2 mb-0" style={{ fontSize: 13 }}>
                  {gcpFailedStep ? (
                    <>
                      <div><strong>Failed step:</strong> {gcpFailedStep.name}</div>
                      <div><strong>Reason:</strong> {gcpFailedStep.message}</div>
                    </>
                  ) : (
                    <div>{gcpErrorMsg}</div>
                  )}
                  <div className="mt-1 text-muted" style={{ fontSize: 12 }}>
                    Completed steps will be skipped on retry.
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </Modal>
  );
}
