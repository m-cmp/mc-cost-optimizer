package com.mcmp.cost.azure.collector.service.impl;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.serializer.SerializerFactory;
import com.azure.core.util.serializer.SerializerAdapter;
import com.azure.core.util.serializer.SerializerEncoding;
import com.azure.resourcemanager.costmanagement.fluent.models.QueryResultInner;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;

import java.io.IOException;
import java.util.List;

/**
 * Cost Management Query 결과의 다음 페이지(nextLink)를 끝까지 가져온다.
 * SDK 1.0.0 의 queries().usage() 는 첫 페이지만 반환하고 nextLink 추적 API 가 없어서,
 * SDK 가 쓰는 HttpPipeline(인증·재시도 정책 포함)으로 같은 쿼리 본문을 nextLink 에 POST 한다.
 */
final class AzureCostQueryPager {

    /** 응답이 계속 nextLink 를 돌려주는 비정상 상황에서 무한 반복하지 않기 위한 상한 */
    static final int MAX_PAGES = 50;

    private AzureCostQueryPager() {}

    /**
     * @param firstNextLink 첫 페이지 응답의 nextLink
     * @param rows          가져온 row 를 이어 붙일 목록 (첫 페이지 row 가 이미 들어 있음)
     * @return 추가로 가져온 페이지 수
     * @throws IllegalStateException 응답 오류·페이지 상한 초과. 호출 측은 이번 결과 전체를 버려야 한다
     */
    static int fetchRemainingRows(HttpPipeline pipeline, QueryDefinition query, String firstNextLink,
                                  List<List<Object>> rows) throws IOException {
        SerializerAdapter serializer = SerializerFactory.createDefaultManagementSerializerAdapter();
        String body = serializer.serialize(query, SerializerEncoding.JSON);

        String nextLink = firstNextLink;
        int pages = 0;
        while (nextLink != null && !nextLink.isBlank()) {
            if (pages >= MAX_PAGES) {
                throw new IllegalStateException("Cost Management paging exceeded " + MAX_PAGES + " pages");
            }
            HttpRequest request = new HttpRequest(HttpMethod.POST, nextLink)
                    .setHeader(HttpHeaderName.CONTENT_TYPE, "application/json")
                    .setBody(body);
            try (HttpResponse response = pipeline.send(request).block()) {
                if (response == null) {
                    throw new IllegalStateException("Cost Management paging: empty response");
                }
                String responseBody = response.getBodyAsString().block();
                if (response.getStatusCode() / 100 != 2) {
                    throw new IllegalStateException("Cost Management paging failed: HTTP " + response.getStatusCode() + " " + responseBody);
                }
                QueryResultInner page = serializer.deserialize(responseBody, QueryResultInner.class, SerializerEncoding.JSON);
                if (page == null) {
                    throw new IllegalStateException("Cost Management paging: unreadable response body");
                }
                if (page.rows() != null) rows.addAll(page.rows());
                nextLink = page.nextLink();
            }
            pages++;
        }
        return pages;
    }
}
