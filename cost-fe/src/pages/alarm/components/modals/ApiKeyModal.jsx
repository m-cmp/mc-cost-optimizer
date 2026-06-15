import { useState } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";

const PROVIDER_LABELS = {
  openai: "OpenAI (GPT)",
  anthropic: "Anthropic (Claude)",
  google: "Google (Gemini)",
};

export default function ApiKeyModal({ apiKey }) {
  const [open, setOpen] = useState(false);
  const [confirmTarget, setConfirmTarget] = useState(null); // 삭제 확인 대상 provider
  const { registered, inputs, saving, handleInputChange, resetInputs, handleSave, handleDelete } =
    apiKey;

  const handleClose = () => {
    resetInputs();
    setOpen(false);
  };

  const handleSaveAndClose = async () => {
    await handleSave();
    setOpen(false);
  };

  const handleConfirmDelete = async () => {
    await handleDelete(confirmTarget);
    setConfirmTarget(null);
  };

  return (
    <>
      <Button variant="secondary" onClick={() => setOpen(true)}>
        🔑 API Key Management
      </Button>

      {/* API Key 관리 모달 */}
      <Modal
        id="apiKeyModal"
        open={open}
        onClose={handleClose}
        title="API Key Management"
        size="md"
        centered
        footer={
          <div className="d-flex justify-content-between w-100">
            <Button variant="secondary" onClick={handleClose}>
              Close
            </Button>
            <Button
              variant="primary"
              onClick={handleSaveAndClose}
              disabled={saving}
              loading={saving}
            >
              Save
            </Button>
          </div>
        }
      >
        <p className="text-muted small mb-3">
          Enter your API key for each provider. Keys are encrypted with AES-256-GCM before storage.
          Saved keys are displayed as masked and the original value will never be exposed again.
        </p>

        {["openai", "anthropic", "google"].map((provider) => (
          <div key={provider} className="mb-3 row align-items-center">
            <label className="col-4 col-form-label">
              <strong>{PROVIDER_LABELS[provider]}</strong>
              {registered[provider] && (
                <span className="ms-2 text-success small">✓</span>
              )}
            </label>
            <div className={registered[provider] ? "col-6" : "col-8"}>
              <input
                type="password"
                className="form-control"
                placeholder={registered[provider] ? "••••••••••••" : "Enter API key"}
                value={registered[provider] ? "" : inputs[provider]}
                onChange={(e) => handleInputChange(provider, e.target.value)}
                autoComplete="off"
                disabled={registered[provider]}
              />
            </div>
            {registered[provider] && (
              <div className="col-2">
                <Button
                  variant="outline-danger"
                  size="sm"
                  onClick={() => setConfirmTarget(provider)}
                >
                  Delete
                </Button>
              </div>
            )}
          </div>
        ))}

        <div className="alert alert-warning small mt-3 mb-0">
          ⚠ Keys are never exposed in logs, responses, or error messages. The master key is injected
          via environment variable $LLM_KEY_MASTER.
        </div>
      </Modal>

      {/* 삭제 확인 모달 */}
      <Modal
        id="apiKeyDeleteConfirmModal"
        open={!!confirmTarget}
        onClose={() => setConfirmTarget(null)}
        title="Delete API Key"
        size="sm"
        centered
        statusColor="danger"
        footer={
          <div className="d-flex justify-content-end gap-2 w-100">
            <Button variant="secondary" onClick={() => setConfirmTarget(null)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleConfirmDelete}>
              Delete
            </Button>
          </div>
        }
      >
        <p className="mb-0">
          Are you sure you want to delete the{" "}
          <strong>{PROVIDER_LABELS[confirmTarget]}</strong> API key?
          <br />
          <span className="text-muted small">This action cannot be undone.</span>
        </p>
      </Modal>
    </>
  );
}
