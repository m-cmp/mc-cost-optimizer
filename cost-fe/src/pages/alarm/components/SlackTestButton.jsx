import React, { useState } from "react";
import Button from "@/components/common/button/Button";
import { sendSlackMessage } from "@/api/alarm/alarm";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @component SlackTestButton
 * @description
 * Slack Test 알람 전송 버튼.
 * 클릭 시 API를 호출하여 Slack 채널로 테스트 메시지를 보냅니다.
 *
 * @prop {string} [userId="mcmp-user"] - 사용자 ID (백엔드와 매핑되는 ID)
 */
export default function SlackTestButton({ userId = "mcmp-user" }) {
  const [loading, setLoading] = useState(false);
  const { addAlert } = useAlertStore();

  const handleSlackTest = async () => {
    try {
      setLoading(true);
      await sendSlackMessage({
        userId,
        message: "이것은 Slack 테스트 메시지입니다.",
      });
      addAlert({
        variant: "success",
        title: "성공",
        message: "Slack 테스트 메시지가 전송되었습니다.",
      });
    } catch (err) {
      logger.error("Slack Test Error:", err);
      addAlert({
        variant: "danger",
        title: "실패",
        message: "Slack 메시지 전송 중 오류가 발생했습니다.",
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
