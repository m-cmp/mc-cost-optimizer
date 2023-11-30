import { saveAs } from 'file-saver';
/**
 * Init workbook via excel js
 *
 * @param workbook
 * @param excelFileName
 */
function initWorkBookViaExcelJs(workbook, excelFileName) {
  let initWorkbook = workbook;
  initWorkbook.created = new Date();
  initWorkbook.modified = new Date();
  initWorkbook.lastPrinted = new Date();
}

function saveAndReturnDownloadFile(workbook, excelFileName) {
  workbook.xlsx.writeBuffer().then(function (data) {
    const blob = new Blob([data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
    saveAs(blob, `${excelFileName}.xlsx`);
  });
}

function saveAndReturnCSVFile(workbook, excelFileName) {
  workbook.csv.writeBuffer().then(function (data) {
    const blob = new Blob([data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
    saveAs(blob, `${excelFileName}.csv`);
  });
}

function saveAndReturnSupportedUTF18CSVFile(workbook, excelFileName, type = 'csv') {
  workbook.csv.writeBuffer().then(function (data) {
    const blob = new Blob(["\uFEFF"+data], {
      type: 'text/csv; charset=utf-18'
    });
    saveAs(blob, `${excelFileName}.${type}`);
  });
}

export {
  initWorkBookViaExcelJs,
  saveAndReturnDownloadFile,
  saveAndReturnCSVFile,
  saveAndReturnSupportedUTF18CSVFile
}
