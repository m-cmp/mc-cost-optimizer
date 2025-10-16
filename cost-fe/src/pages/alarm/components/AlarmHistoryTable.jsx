import Table from "@/components/common/table/Table";
import Card from "@/components/common/card/Card";

const AlarmHistoryTable = ({ data }) => {
  const columns = [
    { key: "date", label: "Date", className: "text-nowrap" },
    { key: "csp", label: "CSP", className: "text-nowrap" },
    { key: "resourceId", label: "Resource ID", className: "text-nowrap" },
    { key: "resourceType", label: "Resource Type", className: "text-nowrap" },
    { key: "alarmType", label: "Alarm Type", className: "text-nowrap" },
    { key: "alarmMessage", label: "Alarm Message" },
    {
      key: "recommendType",
      label: "Recommendation Type",
      className: "text-nowrap",
    },
  ];

  return (
    <Card title={"Recommendation History"} titleSize={2} noPadding>
      <Table
        columns={columns}
        data={data}
        striped
        hover
        responsive
        stickyHeader
        pagination
        pageSize={10}
        paginationVariant="outline"
      />
    </Card>
  );
};

export default AlarmHistoryTable;
