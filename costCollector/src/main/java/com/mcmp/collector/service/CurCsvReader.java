package com.mcmp.collector.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AWS CUR CSV 레코드 리더 (RFC 4180).
 *
 * <p>CUR 은 쉼표가 들어간 값(예: lineItem/LineItemDescription "$0.0045 per 1,000 PUT, COPY, POST, or LIST requests")을
 * 큰따옴표로 감싸서 내보낸다. 단순 {@code split(",")} 은 이 쉼표까지 칸 구분으로 봐서 뒤쪽 칸이 밀리고,
 * 밀린 값이 짧은 컬럼(product_instancetypefamily varchar(20) 등)을 넘으면 50행 묶음 INSERT 전체가 실패했다.
 *
 * <p>처리하는 규칙: 따옴표 안의 쉼표·줄바꿈은 값의 일부, 따옴표 안의 {@code ""} 는 따옴표 한 글자, 값을 감싼 따옴표는 제거.
 */
public final class CurCsvReader {

    private final BufferedReader reader;

    public CurCsvReader(BufferedReader reader) {
        this.reader = reader;
    }

    /** 다음 레코드의 필드 배열. 파일 끝이면 null. 따옴표 안 줄바꿈이 있으면 여러 물리 줄을 한 레코드로 합친다. */
    public String[] next() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        while (true) {
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (inQuotes) {
                    if (ch == '"') {
                        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                            cur.append('"');
                            i++;
                        } else {
                            inQuotes = false;
                        }
                    } else {
                        cur.append(ch);
                    }
                } else if (ch == '"') {
                    inQuotes = true;
                } else if (ch == ',') {
                    fields.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(ch);
                }
            }
            if (!inQuotes) {
                break;
            }
            String nextLine = reader.readLine();
            if (nextLine == null) {
                break; // 닫히지 않은 따옴표로 파일이 끝남 — 지금까지 읽은 값으로 마무리
            }
            cur.append('\n');
            line = nextLine;
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }

    /** 한 줄 문자열을 파싱 (테스트·헤더용). */
    public static String[] parseLine(String line) throws IOException {
        return new CurCsvReader(new BufferedReader(new java.io.StringReader(line))).next();
    }
}
