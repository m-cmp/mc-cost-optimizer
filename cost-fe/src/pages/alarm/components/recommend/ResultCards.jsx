import { BADGE } from "./constants";

const badgeBase = {
  display: "inline-block", fontSize: 11, fontWeight: 700,
  padding: "2px 10px", borderRadius: 10, letterSpacing: 0.3,
};

export function Badge({ recommendation }) {
  const style = { ...badgeBase, ...(BADGE[recommendation] || { background: "#e5e7eb", color: "#475569" }) };
  return <span style={style}>{(recommendation || "").toUpperCase()}</span>;
}

const cardBase = {
  border: "1px solid #e5e7eb", borderLeft: "4px solid #2563eb", borderRadius: 10,
  padding: "14px 16px", marginBottom: 10, background: "#fff",
};

function Card({ item }) {
  const r = item.data || {};

  if (r.status === "insufficient_data") {
    return (
      <div style={{ ...cardBase, borderLeftColor: "#b45309", background: "#fef3c7" }}>
        <div className="d-flex align-items-center gap-2">
          <span style={{ ...badgeBase, background: "#fef3c7", color: "#b45309" }}>INSUFFICIENT DATA</span>
          <span style={{ fontWeight: 600 }}>{item.instanceId}</span>
        </div>
        <div style={{ fontSize: 12.5, color: "#6b7280", marginTop: 4 }}>
          Not enough data to recommend — judgment withheld.
        </div>
      </div>
    );
  }

  if (r.status === "error") {
    const message =
      r.errorCode === "NO_API_KEY"
        ? "No API key registered for this provider. Add one via \"API Key Management\" and try again."
        : r.error || "Request failed";
    return (
      <div style={{ ...cardBase, borderLeftColor: "#b91c1c", background: "#fef2f2" }}>
        <div className="d-flex align-items-center gap-2">
          <span style={{ ...badgeBase, background: "#fee2e2", color: "#b91c1c" }}>ERROR</span>
          <span style={{ fontWeight: 600 }}>{item.instanceId}</span>
        </div>
        <div style={{ fontSize: 12.5, color: "#b91c1c", marginTop: 4 }}>{message}</div>
      </div>
    );
  }

  return (
    <div style={cardBase}>
      <div className="d-flex align-items-center gap-2" style={{ marginBottom: 6 }}>
        <Badge recommendation={r.recommendation} />
        <span style={{ fontWeight: 600, fontSize: 13.5 }}>{item.instanceId}</span>
        <span style={{ fontSize: 11.5, color: "#6b7280", marginLeft: "auto" }}>
          confidence: {r.confidence}
        </span>
      </div>
      <div style={{ fontSize: 13, margin: "2px 0" }}>{r.detail}</div>
      <div style={{ fontSize: 12.5, color: "#6b7280" }}>{r.reasoning}</div>
      {r.answer && (
        <div style={{ fontSize: 12.5, marginTop: 8, paddingTop: 8, borderTop: "1px dashed #e5e7eb" }}>
          <strong style={{ color: "#2563eb" }}>Answer:</strong> {r.answer}
        </div>
      )}
    </div>
  );
}

export default function ResultCards({ results }) {
  if (!results.length) return null;
  return (
    <div className="results" style={{ marginTop: 18 }}>
      <h3 style={{ fontSize: 13, color: "#475569", margin: "0 0 10px", paddingBottom: 6, borderBottom: "1px dashed #e5e7eb" }}>
        Recommendation Result
      </h3>
      {results.map((item) => <Card key={item.instanceId} item={item} />)}
    </div>
  );
}
