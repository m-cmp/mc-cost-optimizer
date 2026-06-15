// Non-component constants kept out of component files so React Fast Refresh
// can hot-swap the components instead of forcing a full page reload.

export const PROVIDERS = [
  { value: "openai", label: "OpenAI (GPT)" },
  { value: "anthropic", label: "Anthropic (Claude)" },
  { value: "google", label: "Google (Gemini)" },
];

// Fallback model catalog — used ONLY if GET /models is unreachable (e.g. backend down).
// The live list is served by the backend (llm.models.* in application.properties),
// so models can be added/removed there without a frontend rebuild.
export const MODELS = {
  openai: ["gpt-4o", "gpt-4o-mini"],
  anthropic: ["claude-opus-4-8", "claude-sonnet-4-6"],
  google: ["gemini-flash-latest", "gemini-2.5-flash", "gemini-2.5-pro"],
};

// Badge colors mirror the mockup legend.
export const BADGE = {
  upsize: { background: "#fee2e2", color: "#b91c1c" },
  downsize: { background: "#dbeafe", color: "#1d4ed8" },
  keep: { background: "#dcfce7", color: "#15803d" },
  terminate: { background: "#fae8ff", color: "#a21caf" },
  migrate: { background: "#ffedd5", color: "#c2410c" },
};
