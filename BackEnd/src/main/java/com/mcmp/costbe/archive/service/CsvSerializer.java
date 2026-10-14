package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.model.SerializedCsv;
import com.mcmp.costbe.invoice.model.InvoiceItemModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * 인보이스 상세 → CSV → gzip. row_count / sha256 / byte_size(gzip 크기) 함께 산출.
 * 외부 라이브러리 없이 CSV 이스케이프(따옴표/콤마/개행) 직접 처리.
 */
@Component
public class CsvSerializer {

    private static final String HEADER = "accountID,productID,csp,bill,resourceID";

    public SerializedCsv toCsvGz(List<InvoiceItemModel> rows) {
        StringBuilder sb = new StringBuilder(HEADER).append('\n');
        long count = 0;
        if (rows != null) {
            for (InvoiceItemModel r : rows) {
                if (r == null) continue;
                sb.append(esc(r.getAccountID())).append(',')
                  .append(esc(r.getProductID())).append(',')
                  .append(esc(r.getCsp())).append(',')
                  .append(r.getBill()).append(',')
                  .append(esc(r.getResourceID())).append('\n');
                count++;
            }
        }
        byte[] gz = gzip(sb.toString().getBytes(StandardCharsets.UTF_8));
        return new SerializedCsv(gz, count, sha256Hex(gz), gz.length);
    }

    /** RFC4180 최소 이스케이프: 콤마/따옴표/개행 있으면 따옴표로 감싸고 내부 따옴표는 2배. */
    private String esc(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    private byte[] gzip(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gz = new GZIPOutputStream(bos)) {
            gz.write(data);
            gz.finish();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("CSV gzip 실패", e);
        }
    }

    private String sha256Hex(byte[] data) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder hex = new StringBuilder(d.length * 2);
            for (byte b : d) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("sha256 계산 실패", e);
        }
    }
}
