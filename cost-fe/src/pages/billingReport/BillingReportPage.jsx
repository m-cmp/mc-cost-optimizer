// src/pages/BillingReportPage.jsx
import Grid from "@/components/layout/Grid";
import BaseInfoCard from "./components/BaseInfoCard";
import InvoiceTable from "./components/InvoiceTable";
import MonthlyOverviewCard from "./components/MonthlyOverviewCard";
import Loading from "@/components/common/loading/Loading";
import { useInvoiceData } from "@/hooks/useInvoiceData";

export default function BillingReportPage() {
  const { baseInfo, summary, invoice, loading } = useInvoiceData();

  if (loading) return <Loading fullscreen withLabel label="Loading data..." />;

  return (
    <>
      <Grid cols={2} gap={5} equalHeight>
        <BaseInfoCard
          totalAmount={baseInfo?.reduce((sum, item) => sum + item.cost, 0) || 0}
          providers={baseInfo || []}
        />
        <MonthlyOverviewCard data={summary} />
        <InvoiceTable invoice={invoice?.invoice || []} colSpan={12} />
      </Grid>
    </>
  );
}
