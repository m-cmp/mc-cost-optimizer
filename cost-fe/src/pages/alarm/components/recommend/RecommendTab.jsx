import { useState, useEffect } from "react";
import Card from "@/components/common/card/Card";
import Button from "@/components/common/button/Button";
import { mockInstances } from "@/config/mockData";
import { useLlmRecommend } from "@/hooks/useLlmRecommend";
import { getModels } from "@/api/llm_recommender/llmRecommender";
import ProviderSelect from "./ProviderSelect";
import InstanceTable from "./InstanceTable";
import ResultCards, { Badge } from "./ResultCards";
import { MODELS } from "./constants";
import ApiKeyModal from "../modals/ApiKeyModal";

const MAX = 5;

// Chips only suggest questions we can actually answer from this instance's
// usage/score data (no cost catalog), so they don't trigger "no data" replies.
const QUICK_CHIPS = [
  "Why this recommendation?",
  "Any stability / headroom risk?",
  "Risks if I downsize?",
  "What instance characteristics fit?",
];

export default function RecommendTab() {
  const [selected, setSelected] = useState([]);
  const [provider, setProvider] = useState("google");
  const [model, setModel] = useState("gemini-flash-latest");
  const [models, setModels] = useState(MODELS); // fallback until /models loads
  const [ask, setAsk] = useState("");
  const [echo, setEcho] = useState("");
  const { results, progress, running, run } = useLlmRecommend();

  // Load the selectable model catalog from the backend (config-driven; no rebuild to change models).
  useEffect(() => {
    getModels()
      .then((res) => {
        const data = res?.data?.Data;
        if (data && Object.keys(data).length) setModels(data);
      })
      .catch(() => {}); // keep fallback on failure
  }, []);

  const handleProviderChange = (p) => {
    setProvider(p);
    setModel((models[p] || [])[0] || "");
  };

  const toggle = (id) =>
    setSelected((prev) =>
      prev.includes(id)
        ? prev.filter((x) => x !== id)
        : prev.length < MAX
          ? [...prev, id]
          : prev,
    );

  const toggleAll = (checked) =>
    setSelected(
      checked ? mockInstances.slice(0, MAX).map((i) => i.instanceId) : [],
    );

  const addChip = (text) =>
    setAsk((prev) => (prev.trim() ? `${prev.trim()}\n${text}` : text));

  const handleRecommend = () => {
    setEcho(ask.trim()); // snapshot the inquiry for the result banner
    run(selected, provider, model, ask.trim());
  };

  return (
    <Card title="Resource Recommendation Request" titleSize={2}>
      <p className="text-muted" style={{ marginTop: 0 }}>
        Select instances to get resizing recommendations.
      </p>

      <div className="d-flex gap-3 justify-content-between">
        <ProviderSelect
          provider={provider}
          model={model}
          models={models}
          onProviderChange={handleProviderChange}
          onModelChange={setModel}
        />
        <div className="d-flex gap-3 mt-4 mb-3">
          <ApiKeyModal />
        </div>
      </div>

      <InstanceTable
        instances={mockInstances}
        selected={selected}
        onToggle={toggle}
        onToggleAll={toggleAll}
        max={MAX}
      />

      {/* additional inquiry (visual stub — not wired to backend in v1) */}
      <div className="mt-3">
        <div
          className="d-flex align-items-center gap-2 mb-1"
          style={{ fontSize: 13, fontWeight: 600 }}
        >
          Additional inquiry
          <span className="badge bg-secondary-lt">optional</span>
        </div>
        <span className="text-muted" style={{ fontSize: 12 }}>
          Optional question — answered from this instance's usage data. The
          answer appears on each result card.
        </span>
        <textarea
          className="form-control mt-2"
          style={{ minHeight: 64 }}
          placeholder="e.g. Why this size? / Is headroom enough for spikes? / Risks if I downsize?"
          value={ask}
          onChange={(e) => setAsk(e.target.value)}
        />
        <div className="d-flex flex-wrap gap-2 mt-2">
          {QUICK_CHIPS.map((c) => (
            <Button
              key={c}
              variant="light"
              size="sm"
              className="border"
              onClick={() => addChip(c)}
            >
              {c}
            </Button>
          ))}
        </div>
      </div>

      {/* run */}
      <div className="d-flex align-items-center gap-3 mt-3">
        <Button
          variant="primary"
          disabled={running || selected.length === 0}
          onClick={handleRecommend}
        >
          {running
            ? `Recommending… ${progress.done}/${progress.total}`
            : "Recommend"}
        </Button>
        <span className="text-muted" style={{ fontSize: 12 }}>
          {selected.length} selected
        </span>
      </div>

      {running && (
        <div className="progress mt-2" style={{ height: 6 }}>
          <div
            className="progress-bar"
            style={{
              width: `${progress.total ? (progress.done / progress.total) * 100 : 0}%`,
            }}
          />
        </div>
      )}

      {/* results */}
      {results.length > 0 && echo && (
        <div
          className="text-muted mt-3"
          style={{
            fontSize: 12,
            background: "#f8fafc",
            border: "1px solid #e5e7eb",
            borderRadius: 8,
            padding: "8px 12px",
            whiteSpace: "pre-line",
          }}
        >
          💬 <b>Inquiry sent with the prompt:</b>
          {"\n"}
          {echo}
        </div>
      )}

      <ResultCards results={results} />

      {/* badge legend */}
      <div
        className="d-flex align-items-center gap-2 flex-wrap text-muted mt-3"
        style={{ fontSize: 11.5 }}
      >
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
