import { useState } from "react";
import Card from "@/components/common/card/Card";
import Button from "@/components/common/button/Button";
import { mockInstances } from "@/config/mockData";
import { useLlmRecommend } from "@/hooks/useLlmRecommend";
import ProviderSelect from "./ProviderSelect";
import InstanceTable from "./InstanceTable";
import ResultCards, { Badge } from "./ResultCards";
import { MODELS } from "./constants";

const MAX = 5;

const QUICK_CHIPS = [
  "Prioritize cost savings",
  "Weigh stability / headroom more",
  "Consider migrating to another CSP",
  "Estimate the monthly savings too",
];

export default function RecommendTab() {
  const [selected, setSelected] = useState([]);
  const [provider, setProvider] = useState("google");
  const [model, setModel] = useState("gemini-flash-latest");
  const [ask, setAsk] = useState("");
  const [echo, setEcho] = useState("");
  const { results, progress, running, run } = useLlmRecommend();

  const handleProviderChange = (p) => {
    setProvider(p);
    setModel((MODELS[p] || [])[0] || "");
  };

  const toggle = (id) =>
    setSelected((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : prev.length < MAX ? [...prev, id] : prev
    );

  const toggleAll = (checked) =>
    setSelected(checked ? mockInstances.slice(0, MAX).map((i) => i.instanceId) : []);

  const addChip = (text) => setAsk((prev) => (prev.trim() ? `${prev.trim()}\n${text}` : text));

  const handleRecommend = () => {
    setEcho(ask.trim()); // snapshot the inquiry for the result banner
    run(selected, provider, model, ask.trim());
  };

  return (
    <Card title="Resource Recommendation Request" titleSize={2}>
      <p className="text-muted" style={{ marginTop: 0 }}>
        Select instances and ask the LLM for resizing recommendations.
      </p>

      <ProviderSelect
        provider={provider}
        model={model}
        onProviderChange={handleProviderChange}
        onModelChange={setModel}
      />

      <InstanceTable
        instances={mockInstances}
        selected={selected}
        onToggle={toggle}
        onToggleAll={toggleAll}
        max={MAX}
      />

      {/* additional inquiry (visual stub — not wired to backend in v1) */}
      <div className="mt-3">
        <div className="d-flex align-items-center gap-2 mb-1" style={{ fontSize: 13, fontWeight: 600 }}>
          Additional inquiry
          <span className="badge bg-secondary-lt">optional</span>
        </div>
        <span className="text-muted" style={{ fontSize: 12 }}>
          Free-form question sent with the score JSON; the LLM's grounded answer appears on each result card.
        </span>
        <textarea
          className="form-control mt-2"
          style={{ minHeight: 64 }}
          placeholder="e.g. Prioritize cost savings. / It's a nightly batch workload — is stability covered? / Would another CSP be cheaper?"
          value={ask}
          onChange={(e) => setAsk(e.target.value)}
        />
        <div className="d-flex flex-wrap gap-2 mt-2">
          {QUICK_CHIPS.map((c) => (
            <Button key={c} variant="light" size="sm" className="border" onClick={() => addChip(c)}>
              {c}
            </Button>
          ))}
        </div>
      </div>

      {/* run */}
      <div className="d-flex align-items-center gap-3 mt-3">
        <Button variant="primary" disabled={running || selected.length === 0} onClick={handleRecommend}>
          {running ? `Recommending… ${progress.done}/${progress.total}` : "Recommend"}
        </Button>
        <span className="text-muted" style={{ fontSize: 12 }}>{selected.length} selected</span>
      </div>

      {running && (
        <div className="progress mt-2" style={{ height: 6 }}>
          <div
            className="progress-bar"
            style={{ width: `${progress.total ? (progress.done / progress.total) * 100 : 0}%` }}
          />
        </div>
      )}

      {/* results */}
      {results.length > 0 && echo && (
        <div className="text-muted mt-3" style={{ fontSize: 12, background: "#f8fafc", border: "1px solid #e5e7eb", borderRadius: 8, padding: "8px 12px", whiteSpace: "pre-line" }}>
          💬 <b>Inquiry sent with the prompt:</b>{"\n"}{echo}
        </div>
      )}

      <ResultCards results={results} />

      {/* badge legend */}
      <div className="d-flex align-items-center gap-2 flex-wrap text-muted mt-3" style={{ fontSize: 11.5 }}>
        <span>Badge legend —</span>
        <Badge recommendation="upsize" />
        <Badge recommendation="downsize" />
        <Badge recommendation="keep" />
        <Badge recommendation="terminate" />
        <Badge recommendation="migrate" />
      </div>
    </Card>
  );
}
