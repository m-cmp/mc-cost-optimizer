import { useState } from "react";
import AlarmHistoryTable from "./components/AlarmHistoryTable";
import MailingGuideModal from "./components/modals/MailingGuideModal";
import SlackGuideModal from "./components/modals/SlackGuideModal";
import MailTestModal from "./components/modals/MailTestModal";
import SlackTestButton from "./components/SlackTestButton";
import RecommendTab from "./components/recommend/RecommendTab";
import Button from "@/components/common/button/Button";
import Loading from "@/components/common/loading/Loading";
import { useAlarmHistory } from "@/hooks/useAlarmHistory";

export default function AlarmPage() {
  const { alarmData, loading } = useAlarmHistory();
  const [tab, setTab] = useState("recommend"); // "recommend" | "history"

  return (
    <div>
      <div className="d-flex gap-2 mb-3">
        <Button
          variant={tab === "recommend" ? "primary" : "outline-secondary"}
          onClick={() => setTab("recommend")}
        >
          Resource Recommendation
        </Button>
        <Button
          variant={tab === "history" ? "primary" : "outline-secondary"}
          onClick={() => setTab("history")}
        >
          Alarm History
        </Button>
      </div>

      {tab === "recommend" && <RecommendTab />}

      {tab === "history" && (
        loading ? (
          <Loading fullscreen withLabel label="Loading data..." />
        ) : (
          <div>
            <div className="d-flex gap-3 mb-3">
              <MailingGuideModal />
              <SlackGuideModal />
              <MailTestModal />
              <SlackTestButton />
            </div>
            <AlarmHistoryTable data={alarmData} />
          </div>
        )
      )}
    </div>
  );
}
