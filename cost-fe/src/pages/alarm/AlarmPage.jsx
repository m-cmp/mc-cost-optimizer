import { useState } from "react";
import AlarmHistoryTable from "./components/AlarmHistoryTable";
import MailingGuideModal from "./components/modals/MailingGuideModal";
import SlackGuideModal from "./components/modals/SlackGuideModal";
import MailTestModal from "./components/modals/MailTestModal";
import SlackTestButton from "./components/SlackTestButton";
import RecommendTab from "./components/recommend/RecommendTab";
import Loading from "@/components/common/loading/Loading";
import { useAlarmHistory } from "@/hooks/useAlarmHistory";

export default function AlarmPage() {
  const { alarmData, loading } = useAlarmHistory();
  const [tab, setTab] = useState("recommend"); // "recommend" | "history"

  return (
    <div>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button
            className={`nav-link ${tab === "recommend" ? "active" : ""}`}
            onClick={() => setTab("recommend")}
          >
            Resource Recommendation
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${tab === "history" ? "active" : ""}`}
            onClick={() => setTab("history")}
          >
            Alarm History
          </button>
        </li>
      </ul>

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
