import { useState } from "react";
import Card from "@/components/common/card/Card";
import Table from "@/components/common/table/Table";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";
import Loading from "@/components/common/loading/Loading";
import { Badge } from "./ResultCards";
import { useRecommendHistory } from "@/hooks/useRecommendHistory";

// response_json(전체 Recommendation)을 안전하게 파싱
function parseRow(row) {
  try {
    return JSON.parse(row?.responseJson || "{}");
  } catch {
    return {};
  }
}

export default function RecommendHistoryTab() {
  const { history, loading } = useRecommendHistory();
  const [selected, setSelected] = useState(null); // 상세 모달 대상 행

  const columns = [
    { key: "createdAt", label: "Date", className: "text-nowrap" },
    { key: "instanceId", label: "Instance", className: "text-nowrap" },
    {
      key: "recommendation",
      label: "Recommendation",
      className: "text-nowrap",
      render: (value, row) =>
        value ? (
          <Badge recommendation={value} />
        ) : (
          <span className="text-muted">{parseRow(row).status || "-"}</span>
        ),
    },
    {
      key: "confidence",
      label: "Confidence",
      className: "text-nowrap",
      render: (_, row) => parseRow(row).confidence || "-",
    },
  ];

  if (loading) return <Loading withLabel label="Loading history..." />;

  const detail = selected ? parseRow(selected) : null;

  return (
    <Card title="Recommendation History" titleSize={2} noPadding>
      <Table
        columns={columns}
        data={history}
        striped
        hover
        responsive
        stickyHeader
        pagination
        pageSize={10}
        paginationVariant="outline"
        onRowClick={(row) => setSelected(row)}
      />

      <Modal
        id="recHistoryDetailModal"
        open={!!selected}
        onClose={() => setSelected(null)}
        title="Recommendation Detail"
        size="md"
        centered
        footer={
          <Button variant="secondary" onClick={() => setSelected(null)}>
            Close
          </Button>
        }
      >
        {detail && (
          <div>
            <div className="mb-2">
              <strong>Instance:</strong> {selected.instanceId}
            </div>
            <div className="mb-2 d-flex align-items-center gap-2">
              <strong>Recommendation:</strong>{" "}
              {selected.recommendation ? (
                <Badge recommendation={selected.recommendation} />
              ) : (
                <span className="text-muted">{detail.status || "-"}</span>
              )}
            </div>
            <div className="mb-2">
              <strong>Confidence:</strong> {detail.confidence || "-"}
            </div>
            <div className="mb-2">
              <strong>Detail:</strong> {detail.detail || "-"}
            </div>
            <div className="mb-2">
              <strong>Reasoning:</strong> {detail.reasoning || "-"}
            </div>
            <div className="text-muted" style={{ fontSize: 12 }}>
              {selected.createdAt}
            </div>
          </div>
        )}
      </Modal>
    </Card>
  );
}
