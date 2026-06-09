import { useState, useEffect } from "react";
import { getApiKeyStatus, saveApiKey, deleteApiKey } from "@/api/llm_recommender/apikey";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

const PROVIDERS = ["openai", "anthropic", "google"];
const PROVIDER_LABELS = { openai: "OpenAI (GPT)", anthropic: "Anthropic (Claude)", google: "Google (Gemini)" };
const EMPTY_INPUTS = { openai: "", anthropic: "", google: "" };
const EMPTY_STATUS = { openai: false, anthropic: false, google: false };
export function useApiKey() {
  const [registered, setRegistered] = useState(EMPTY_STATUS);
  const [inputs, setInputs] = useState(EMPTY_INPUTS);
  const [saving, setSaving] = useState(false);
  const { addAlert } = useAlertStore();

  const fetchAllStatus = async () => {
    const results = await Promise.allSettled(
      PROVIDERS.map((p) => getApiKeyStatus(p))
    );
    const next = {};
    results.forEach((r, i) => {
      next[PROVIDERS[i]] =
        r.status === "fulfilled" ? r.value.data.registered : false;
    });
    setRegistered(next);
  };

  useEffect(() => {
    fetchAllStatus();
  }, []);

  const handleInputChange = (provider, value) => {
    setInputs((prev) => ({ ...prev, [provider]: value }));
  };

  const handleSave = async () => {
    const targets = PROVIDERS.filter((p) => inputs[p].trim() !== "");
    if (targets.length === 0) return;

    setSaving(true);
    try {
      await Promise.all(targets.map((p) => saveApiKey(p, inputs[p].trim())));
      setInputs(EMPTY_INPUTS);
      await fetchAllStatus();
      addAlert({
        variant: "success",
        title: "Success",
        message: "API key has been saved successfully.",
      });
    } catch (err) {
      logger.error("API Key Save Error:", err);
      addAlert({
        variant: "danger",
        title: "Error",
        message: "An error occurred while saving the API key.",
      });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (provider) => {
    try {
      await deleteApiKey(provider);
      await fetchAllStatus();
      addAlert({
        variant: "success",
        title: "Success",
        message: `${provider} API key has been deleted.`,
      });
    } catch (err) {
      logger.error("API Key Delete Error:", err);
      addAlert({
        variant: "danger",
        title: "Error",
        message: "An error occurred while deleting the API key.",
      });
    }
  };

  return { registered, inputs, saving, handleInputChange, handleSave, handleDelete };
}
