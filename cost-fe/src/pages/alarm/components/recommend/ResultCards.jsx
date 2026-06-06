const BADGE = {
  terminate: "bg-danger",
  downsize: "bg-info",
  upsize: "bg-warning text-dark",
  migrate: "bg-primary",
  keep: "bg-secondary",
};

function Card({ item }) {
  const r = item.data || {};
  if (r.status === "insufficient_data") {
    return (
      <div className="card border-warning mb-2">
        <div className="card-body">
          <div className="d-flex justify-content-between">
            <code>{item.instanceId}</code>
            <span className="badge bg-warning text-dark">INSUFFICIENT DATA</span>
          </div>
          <small className="text-muted">Not enough data to recommend.</small>
        </div>
      </div>
    );
  }
  if (r.status === "error") {
    return (
      <div className="card border-danger mb-2">
        <div className="card-body">
          <div className="d-flex justify-content-between">
            <code>{item.instanceId}</code>
            <span className="badge bg-danger">ERROR</span>
          </div>
          <small className="text-danger">{r.error || "Request failed"}</small>
        </div>
      </div>
    );
  }
  return (
    <div className="card mb-2">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center">
          <code>{item.instanceId}</code>
          <span className={`badge ${BADGE[r.recommendation] || "bg-secondary"}`}>
            {(r.recommendation || "").toUpperCase()}
          </span>
        </div>
        <p className="mb-1 mt-2"><strong>{r.detail}</strong></p>
        <p className="mb-1 text-muted">{r.reasoning}</p>
        <small className="text-muted">confidence: {r.confidence}</small>
      </div>
    </div>
  );
}

export default function ResultCards({ results }) {
  if (!results.length) return null;
  return (
    <div className="mt-3">
      {results.map((item) => <Card key={item.instanceId} item={item} />)}
    </div>
  );
}
