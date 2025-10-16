import React, { useState } from "react";
import Button from "@/components/common/button/Button";
import { sendSlackMessage } from "@/api/alarm/alarm";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @component SlackTestButton
 * @description
 * Slack test alarm send button.
 * Sends a test message to Slack channel by calling the API when clicked.
 *
 * @prop {string} [userId="mcmp-user"] - User ID (ID mapped to backend)
 */
export default function SlackTestButton({ userId = "mcmp-user" }) {
  const [loading, setLoading] = useState(false);
  const { addAlert } = useAlertStore();

  const handleSlackTest = async () => {
    try {
      setLoading(true);
      await sendSlackMessage({
        userId,
        message: "This is a Slack test message.",
      });
      addAlert({
        variant: "success",
        title: "Success",
        message: "Slack test message has been sent.",
      });
    } catch (err) {
      logger.error("Slack Test Error:", err);
      addAlert({
        variant: "danger",
        title: "Failed",
        message: "An error occurred while sending Slack message.",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Button
      variant="outline-primary"
      disabled={loading}
      onClick={handleSlackTest}
    >
      {loading ? "Sending..." : "Slack Test"}
    </Button>
  );
}
