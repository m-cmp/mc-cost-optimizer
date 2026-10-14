// src/pages/BillingReportPage.jsx
import { useState } from "react";
import Grid from "@/components/layout/Grid";
import BaseInfoCard from "./components/BaseInfoCard";
import InvoiceTable from "./components/InvoiceTable";
import ArchiveStatus from "./components/ArchiveStatus";
import MonthlyOverviewCard from "./components/MonthlyOverviewCard";
import CspSettingModal from "./components/CspSettingModal";
import Loading from "@/components/common/loading/Loading";
import Button from "@/components/common/button/Button";
import { useInvoiceData } from "@/hooks/useInvoiceData";
import { useArchive } from "@/hooks/useArchive";

export default function BillingReportPage() {
  const { baseInfo, summary, invoice, loading } = useInvoiceData();
  const { months, status, archiving, purging, runArchive, runPurge, checkGate } = useArchive();
  const [settingOpen, setSettingOpen] = useState(false);

  if (loading) return <Loading fullscreen withLabel label="Loading data..." />;

  return (
    <>
      <div className="d-flex gap-3 mb-3">
        <Button variant="primary" onClick={() => setSettingOpen(true)}>
          Cost Setup
        </Button>
      </div>

      <Grid cols={2} gap={5} equalHeight>
        <BaseInfoCard
          totalAmount={baseInfo?.reduce((sum, item) => sum + item.cost, 0) || 0}
          providers={baseInfo || []}
        />
        <MonthlyOverviewCard data={summary} />
        <InvoiceTable
          invoice={invoice?.invoice || []}
          colSpan={12}
          months={months}
          archiving={archiving}
          onArchive={runArchive}
        />
        <ArchiveStatus
          colSpan={12}
          status={status}
          purging={purging}
          onPurge={runPurge}
          checkGate={checkGate}
        />
      </Grid>

      <CspSettingModal open={settingOpen} onClose={() => setSettingOpen(false)} />
    </>
  );
}
