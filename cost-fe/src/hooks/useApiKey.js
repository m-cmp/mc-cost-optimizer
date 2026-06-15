import { useState, useEffect } from "react";
import { getApiKeyStatus, saveApiKey, deleteApiKey } from "@/api/llm_recommender/apikey";
import { useAlertStore } from "@/stores/useAlertStore";
import { useProjectStore } from "@/stores/useProjectStore";
import { logger } from "@/utils/logger";

const PROVIDERS = ["openai", "anthropic", "google"];
const PROVIDER_LABELS = { openai: "OpenAI (GPT)", anthropic: "Anthropic (Claude)", google: "Google (Gemini)" };
const EMPTY_INPUTS = { openai: "", anthropic: "", google: "" };
const EMPTY_STATUS = { openai: false, anthropic: false, google: false };
export function useApiKey() {
  const [registered, setRegistered] = useState(EMPTY_STATUS);
  // Start as loading: registered defaults to all-false, so until the status
  // check resolves we can't yet tell "no keys registered" from "still checking".
  const [loading, setLoading] = useState(true);
  const [inputs, setInputs] = useState(EMPTY_INPUTS);
  const [saving, setSaving] = useState(false);
  const { addAlert } = useAlertStore();
  const projectId = useProjectStore((s) => s.projectId);

  const fetchAllStatus = async () => {
    if (!projectId) return; // nsId(=projectId) 준비 전엔 호출 보류
    const results = await Promise.allSettled(
      PROVIDERS.map((p) => getApiKeyStatus(p, projectId))
    );
    const next = {};
    results.forEach((r, i) => {
      next[PROVIDERS[i]] =
        r.status === "fulfilled" ? r.value.data.registered : false;
    });
    setRegistered(next);
    setLoading(false);
  };

  useEffect(() => {
    fetchAllStatus();
  }, [projectId]);

  const handleInputChange = (provider, value) => {
    setInputs((prev) => ({ ...prev, [provider]: value }));
  };

  const resetInputs = () => {
    setInputs(EMPTY_INPUTS);
  };

  const handleSave = async () => {
    if (!projectId) return;
    const targets = PROVIDERS.filter((p) => inputs[p].trim() !== "");
    if (targets.length === 0) return;

    setSaving(true);
    try {
      await Promise.all(targets.map((p) => saveApiKey(p, inputs[p].trim(), projectId)));
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
    if (!projectId) return;
    try {
      await deleteApiKey(provider, projectId);
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

  return { registered, loading, inputs, saving, handleInputChange, resetInputs, handleSave, handleDelete };
}
