// src/pages/HomePage.jsx
import Grid from "@/components/layout/Grid";
import BillingSummaryCard from "./components/BillingSummaryCard";
import TopServicesCard from "./components/TopServicesCard";
import ServiceCostListCard from "./components/ServiceCostListCard";
import Loading from "@/components/common/loading/Loading";
import { useBillingData } from "@/hooks/useBillingData";

export default function HomePage() {
  const { summary, top5, services, loading } = useBillingData();

  if (loading) {
    return <Loading fullscreen withLabel label="Loading data..." />;
  }

  return (
    <>
      <Grid cols={2} minColWidth={320} equalHeight>
        <BillingSummaryCard chartData={summary} />
        <TopServicesCard data={top5} />
        {/*<ServiceCostListCard services={services} />*/}
      </Grid>
    </>
  );
}
