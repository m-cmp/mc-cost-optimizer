import { useState } from "react";
import { mockInstances } from "@/config/mockData";
import { useLlmRecommend } from "@/hooks/useLlmRecommend";
import ProviderSelect from "./ProviderSelect";
import InstanceTable from "./InstanceTable";
import ResultCards from "./ResultCards";

const MAX = 5;

export default function RecommendTab() {
  const [selected, setSelected] = useState([]);
  const [provider, setProvider] = useState("gemini");
  const { results, progress, running, run } = useLlmRecommend();

  const toggle = (id) =>
    setSelected((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : prev.length < MAX ? [...prev, id] : prev
    );

  return (
    <div>
      <div className="d-flex gap-3 align-items-center mb-3">
        <ProviderSelect value={provider} onChange={setProvider} />
        <button
          className="btn btn-primary"
          disabled={running || selected.length === 0}
          onClick={() => run(selected, provider)}
        >
          {running ? `Recommending… ${progress.done}/${progress.total}` : "Recommend"}
        </button>
        <small className="text-muted">Selected {selected.length}/{MAX}</small>
      </div>

      <InstanceTable instances={mockInstances} selected={selected} onToggle={toggle} max={MAX} />

      {running && (
        <div className="progress mb-2" style={{ height: 6 }}>
          <div
            className="progress-bar"
            style={{ width: `${progress.total ? (progress.done / progress.total) * 100 : 0}%` }}
          />
        </div>
      )}

      <ResultCards results={results} />
    </div>
  );
}
