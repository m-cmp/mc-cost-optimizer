import Dropdown from "@/components/common/dropdown/Dropdown";
import Loading from "@/components/common/loading/Loading";
import { PROVIDERS } from "./constants";

// Provider + dependent Model selectors (mockup layout).
// All three providers (google/openai/anthropic) are wired to the backend; the
// selected provider is sent with each recommend request and routes to its bean.
// `models` (provider -> [modelId]) is supplied by the parent, loaded from GET /models.
// `registered` (provider -> bool) limits the provider list to ones with a saved API key.
// `loading` indicates the registered-key check (GET /apikey) is still in flight.
export default function ProviderSelect({ provider, model, models, registered, loading, onProviderChange, onModelChange }) {
  const availableProviders = PROVIDERS.filter((p) => registered?.[p.value]);
  const providerLabel = PROVIDERS.find((p) => p.value === provider)?.label || provider;
  const modelItems = ((models && models[provider]) || []).map((m) => ({ value: m, label: m }));

  if (loading) {
    return (
      <div className="mb-3">
        <label className="form-label">Provider</label>
        <Loading size="sm" withLabel label="Checking API keys..." />
      </div>
    );
  }

  if (availableProviders.length === 0) {
    return (
      <div className="mb-3">
        <label className="form-label">Provider</label>
        <div className="text-muted" style={{ fontSize: 13 }}>
          No API key registered yet. Add one via "API Key Management" to enable recommendations.
        </div>
      </div>
    );
  }

  return (
    <div className="d-flex gap-3 flex-wrap mb-3">
      <div>
        <label className="form-label">Provider</label>
        <Dropdown
          trigger={providerLabel}
          items={availableProviders}
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
