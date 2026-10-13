package com.mcmp.collector.service;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CurCsvReaderTest {

    @Test
    public void quotedCommaStaysInOneField() throws Exception {
        String[] f = CurCsvReader.parseLine("id1,AmazonS3,\"$0.0045 per 1,000 PUT, COPY, POST, or LIST requests\",APN2,General Purpose");
        assertEquals(5, f.length);
        assertEquals("$0.0045 per 1,000 PUT, COPY, POST, or LIST requests", f[2]);
        assertEquals("General Purpose", f[4]);
    }

    @Test
    public void emptyFieldsAndEscapedQuotesAreKept() throws Exception {
        assertArrayEquals(new String[]{"a", "", "say \"hi\"", ""}, CurCsvReader.parseLine("a,,\"say \"\"hi\"\"\","));
    }

    @Test
    public void quotedNewlineJoinsPhysicalLines() throws Exception {
        CurCsvReader r = new CurCsvReader(new BufferedReader(new StringReader("h1,h2\n1,\"line one\nline two\"\n2,x\n")));
        assertArrayEquals(new String[]{"h1", "h2"}, r.next());
        assertArrayEquals(new String[]{"1", "line one\nline two"}, r.next());
        assertArrayEquals(new String[]{"2", "x"}, r.next());
        assertNull(r.next());
    }
}
