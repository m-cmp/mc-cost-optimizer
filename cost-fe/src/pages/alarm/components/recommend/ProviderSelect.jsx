import Dropdown from "@/components/common/dropdown/Dropdown";
import { PROVIDERS, MODELS } from "./constants";

// Provider + dependent Model selectors (mockup layout).
// All three providers (google/openai/anthropic) are wired to the backend; the
// selected provider is sent with each recommend request and routes to its bean.
export default function ProviderSelect({ provider, model, onProviderChange, onModelChange }) {
  const providerLabel = PROVIDERS.find((p) => p.value === provider)?.label || provider;
  const modelItems = (MODELS[provider] || []).map((m) => ({ value: m, label: m }));

  return (
    <div className="d-flex gap-3 flex-wrap mb-3">
      <div>
        <label className="form-label">Provider</label>
        <Dropdown
          trigger={providerLabel}
          items={PROVIDERS}
          selectedValue={provider}
          onSelect={(value) => onProviderChange(value)}
          className="btn-outline-secondary"
        />
      </div>
      <div>
        <label className="form-label">Model</label>
        <Dropdown
          trigger={model}
          items={modelItems}
          selectedValue={model}
          onSelect={(value) => onModelChange(value)}
          className="btn-outline-secondary"
        />
      </div>
    </div>
  );
}
