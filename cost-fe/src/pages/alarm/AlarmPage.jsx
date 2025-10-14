import AlarmHistoryTable from "./components/AlarmHistoryTable";
import MailingGuideModal from "./components/modals/MailingGuideModal";
import SlackGuideModal from "./components/modals/SlackGuideModal";
import MailTestModal from "./components/modals/MailTestModal";
import SlackTestButton from "./components/SlackTestButton";
import Loading from "@/components/common/loading/Loading";
import AlertProvider from "@/components/common/alert/AlertProvider";
import { useAlarmHistory } from "@/hooks/useAlarmHistory";

export default function AlarmPage() {
  const { alarmData, loading } = useAlarmHistory();

  if (loading)
    return <Loading fullscreen withLabel label="Loading data..." />;

  return (
    <div>
      <div className="d-flex gap-3 mb-3">
        <MailingGuideModal />
        <SlackGuideModal />
        <MailTestModal />
        <SlackTestButton />
      </div>
      <AlarmHistoryTable data={alarmData} />
      <AlertProvider />
    </div>
  );
}
