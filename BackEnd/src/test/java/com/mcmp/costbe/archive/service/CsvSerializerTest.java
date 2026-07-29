package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.model.SerializedCsv;
import com.mcmp.costbe.invoice.model.InvoiceItemModel;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class CsvSerializerTest {

    private final CsvSerializer serializer = new CsvSerializer();

    private String gunzip(byte[] gz) throws IOException {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(gz))) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private InvoiceItemModel row(String acc, String prod, String csp, double bill, String res) {
        InvoiceItemModel m = new InvoiceItemModel();
        m.setAccountID(acc);
        m.setProductID(prod);
        m.setCsp(csp);
        m.setBill(bill);
        m.setResourceID(res);
        return m;
    }

    @Test
    void serializesHeaderRowsAndMeta() throws Exception {
        SerializedCsv csv = serializer.toCsvGz(List.of(
                row("123", "AmazonEC2", "AWS", 1.5, "i-abc"),
                row("456", "AmazonS3", "AWS", 0.25, "bucket-x")));

        String content = gunzip(csv.getGz());
        assertThat(content).startsWith("accountID,productID,csp,bill,resourceID\n");
        assertThat(content).contains("123,AmazonEC2,AWS,1.5,i-abc");
        assertThat(csv.getRowCount()).isEqualTo(2);
        assertThat(csv.getByteSize()).isEqualTo(csv.getGz().length); // byteSize = gzip 크기
        assertThat(csv.getSha256()).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    void escapesCommaQuoteNewline() throws Exception {
        SerializedCsv csv = serializer.toCsvGz(List.of(row("a,b", "pro\"d", "AWS", 1.0, "r\nn")));
        String content = gunzip(csv.getGz());
        assertThat(content).contains("\"a,b\"");       // 콤마 → 따옴표 감쌈
        assertThat(content).contains("\"pro\"\"d\"");  // 따옴표 → 2배
        assertThat(content).contains("\"r\nn\"");       // 개행 → 따옴표 감쌈
    }

    @Test
    void handlesNullRowsAndNullFieldsWithoutNpe() throws Exception {
        assertThat(serializer.toCsvGz(null).getRowCount()).isZero();

        SerializedCsv csv = serializer.toCsvGz(List.of(new InvoiceItemModel())); // 필드 전부 null
        assertThat(csv.getRowCount()).isEqualTo(1);
        assertThat(gunzip(csv.getGz())).startsWith("accountID,productID,csp,bill,resourceID\n");
    }
}
