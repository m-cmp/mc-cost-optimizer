package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.model.Csp;
import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.invoice.dao.InvoiceDao;
import com.mcmp.costbe.invoice.model.InvoiceItemModel;
import com.mcmp.costbe.invoice.model.InvoiceReqModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 대상 (월 × CSP)의 인보이스 상세를 기존 invoice 매퍼로 조회한다.
 * - today = 대상월 1일 → DateCalculator 로 월 경계(AWS 쿼리용) + year_month 도출
 * - 단일 CSP 필터(selectedCsps=[csp])
 * - 월별 테이블 없음(BadSqlGrammarException) 등은 "데이터 없음"으로 처리 → 빈 리스트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceQueryService {

    private final InvoiceDao invoiceDao;
    private final DateCalculator dateCalculator;

    public List<InvoiceItemModel> fetchInvoice(String yearMonth, Csp csp, List<String> projects) {
        InvoiceReqModel req = new InvoiceReqModel();
        String today = yearMonth + "01";                 // 대상월 1일
        req.setToday(today);
        req.setYear_month(yearMonth);
        req.setSelectedProjects(projects != null ? projects : Collections.emptyList());
        req.setSelectedCsps(Collections.singletonList(csp.name()));
        DateRangeModel range = dateCalculator.dateRangeCalculator(today);
        req.setCurMonthStartDate(range.getStartDate());
        req.setCurMonthEndDate(range.getEndDate());

        try {
            List<InvoiceItemModel> rows = switch (csp) {
                case AWS   -> invoiceDao.getAWSInvoice(req);
                case NCP   -> invoiceDao.getNCPInvoice(req);
                case AZURE -> invoiceDao.getAzureInvoice(req);
                case GCP   -> invoiceDao.getGCPInvoice(req);
            };
            return rows != null ? rows : Collections.emptyList();
        } catch (BadSqlGrammarException ex) {
            // AWS 월별 테이블 미존재(이미 purge 등) → 아카이브 대상 없음
            log.warn("[archive] invoice 조회 테이블 없음 {}/{}: {}", yearMonth, csp, ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("[archive] invoice 조회 오류 {}/{}: {}", yearMonth, csp, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
