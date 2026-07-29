import React, { useEffect, useRef, useState } from "react";
import { TabulatorFull as Tabulator } from "tabulator-tables";
import "tabulator-tables/dist/css/tabulator.min.css"; // Tabulator 기본 CSS
import * as XLSX from "xlsx";
import Card from "@/components/common/card/Card";
import Button from "@/components/common/button/Button";
import { Icons } from "@/icons/Icons";
import ArchiveModal from "./ArchiveModal";
import "@/index.css";

window.XLSX = XLSX;

export default function InvoiceTable({ invoice, months = [], archiving, onArchive }) {
  const tableRef = useRef(null);
  const tabulatorInstance = useRef(null);
  const today = new Date().toISOString().split("T")[0];

  const hasData = invoice && invoice.length > 0;

  // Cost archiving — the button lives on the export toolbar; the status table is a separate
  // card below the invoice (rendered by the page). Here we only trigger the archive modal.
  const [archiveOpen, setArchiveOpen] = useState(false);

  useEffect(() => {
    if (!tableRef.current || !hasData) return;

    if (tabulatorInstance.current) {
      tabulatorInstance.current.replaceData(invoice);
      return;
    }

    tabulatorInstance.current = new Tabulator(tableRef.current, {
      data: invoice,
      layout: "fitColumns",
      responsiveLayout: "collapse",
      groupBy: ["csp", "productID"],
      groupStartOpen: [true, false],
      groupHeader: [
        (value, count, data) => {
          const total = data.reduce((sum, row) => sum + row.bill, 0).toFixed(2);
          return `${value} — ${count} items / Total: ${total} USD`;
        },
        (value, count, data) => {
          const total = data.reduce((sum, row) => sum + row.bill, 0).toFixed(2);
          return `Service: ${value} — ${count} items / Total: ${total} USD`;
        },
      ],
      columns: [
        {
          title: "CSP",
          field: "csp",
          width: 150,
          formatter: function () {
            // 그룹화된 테이블에서는 CSP 컬럼을 빈칸으로 처리
            return "";
          },
        },
        { title: "Account ID", field: "accountID" },
        { title: "Product", field: "productID" },
        { title: "Resource", field: "resourceID" },
        {
          title: "Amount (USD)",
          field: "bill",
          hozAlign: "right",
          sorter: "number",
          formatter: "money",
          formatterParams: { symbol: "$", precision: 2 },
        },
      ],
    });
  }, [invoice]);

  const handleExportCSV = () => {
    tabulatorInstance.current.download("csv", `invoice_${today}.csv`);
  };

  const handleExportExcel = () => {
    tabulatorInstance.current.download("xlsx", `invoice_${today}.xlsx`, {
      sheetName: "Invoice Data",
    });
  };

  return (
    <Card
      title="Invoices"
      titleSize={2}
      noBodyWrapper
      noPadding
      actions={
        <div className="btn-list">
          <Button
            icon={Icons.exportCsv}
            iconPosition="only"
            variant="info"
            size="sm"
            onClick={handleExportCSV}
          />
          <Button
            icon={Icons.exportXls}
            iconPosition="only"
            variant="info"
            size="sm"
            onClick={handleExportExcel}
          />
          <Button variant="primary" size="sm" onClick={() => setArchiveOpen(true)}>
            🗄 Archive
          </Button>
        </div>
      }
    >
      {!hasData ? (
        <div className="text-center py-5 text-muted">
          No invoice data available
        </div>
      ) : (
        <div ref={tableRef} style={{ width: "100%", height: "450px" }} />
      )}

      <ArchiveModal
        open={archiveOpen}
        onClose={() => setArchiveOpen(false)}
        months={months}
        archiving={archiving}
        onArchive={onArchive}
      />
    </Card>
  );
}
