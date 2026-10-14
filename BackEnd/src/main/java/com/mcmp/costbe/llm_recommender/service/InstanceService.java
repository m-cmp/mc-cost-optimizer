package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.invoice.model.InvoiceItemModel;
import com.mcmp.costbe.invoice.model.InvoiceReqModel;
import com.mcmp.costbe.invoice.service.InvoiceService;
import com.mcmp.costbe.llm_recommender.dao.InstanceDao;
import com.mcmp.costbe.llm_recommender.model.ResourceInstance;
import com.mcmp.costbe.tumblebugMeta.model.mci.TbInfraNodeSpecModel;
import com.mcmp.costbe.tumblebugMeta.service.VMMetaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private static final Logger log = LoggerFactory.getLogger(InstanceService.class);

    // TB rate-limits the node-spec endpoint at 2 req/sec; keep in-flight lookups at/under that.
    private static final int SPEC_LOOKUP_CONCURRENCY = 2;

    private final InstanceDao instanceDao;
    private final VMMetaService vmMetaService;
    private final InvoiceService invoiceService;

    public List<ResourceInstance> getInstances(String nsId) {
        List<ResourceInstance> instances = instanceDao.selectInstancesByNs(Map.of("nsId", nsId));
        // One Tumblebug call per instance. TB rate-limits this endpoint at 2 req/sec,
        // so cap concurrency at 2 (was parallelStream over the whole common pool, which
        // burst past the limit and got 429s -> spec="-"). Combined with the 429 backoff
        // retry in VMMetaService.getTBBNodeSpec, spec now resolves reliably. Failures
        // still leave spec=null (rendered as "-") without failing the request.
        fillSpecs(instances);
        // Attach this month's per-resource cost (USD). A cost-source failure must
        // never break the instance grid, so it is swallowed and leaves usd=null.
        fillMonthlyCost(nsId, instances);
        return instances;
    }

    /**
     * Fills {@link ResourceInstance#setUsd} with the current month's billed amount per
     * resource, reusing the invoice query (servicegroup_meta-based, USD-normalized) and
     * matching on resourceID == csp_instanceid. AWS/GCP can return several rows per
     * resource (product/service split), so amounts are summed per instance id.
     */
    private void fillMonthlyCost(String nsId, List<ResourceInstance> instances) {
        try {
            LocalDate now = LocalDate.now();
            InvoiceReqModel req = new InvoiceReqModel();
            // getAWSInvoice derives year_month + month date-range from today internally.
            req.setToday(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            req.setSelectedProjects(List.of(nsId));
            req.setSelectedCsps(null); // null/empty -> all CSPs included

            List<InvoiceItemModel> rows = invoiceService.getAWSInvoice(req); // all-CSP merge
            if (rows == null || rows.isEmpty()) {
                return;
            }

            Map<String, Double> costByInstance = rows.stream()
                    .filter(r -> r.getResourceID() != null)
                    .collect(Collectors.groupingBy(
                            InvoiceItemModel::getResourceID,
                            Collectors.summingDouble(InvoiceItemModel::getBill)));

            for (ResourceInstance inst : instances) {
                Double cost = costByInstance.get(inst.getInstanceId());
                if (cost != null) {
                    inst.setUsd(cost);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fill monthly cost for ns {}: {}", nsId, e.getMessage());
        }
    }

    /**
     * Resolves each instance's spec via Tumblebug with concurrency capped at
     * {@link #SPEC_LOOKUP_CONCURRENCY} to stay within TB's 2 req/sec rate limit.
     */
    private void fillSpecs(List<ResourceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return;
        }
        int poolSize = Math.max(1, Math.min(SPEC_LOOKUP_CONCURRENCY, instances.size()));
        ExecutorService specPool = Executors.newFixedThreadPool(poolSize);
        try {
            List<Future<?>> futures = instances.stream()
                    .map(inst -> specPool.submit(() -> fillSpec(inst)))
                    .collect(Collectors.toList());
            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // fillSpec swallows its own failures (spec stays null); nothing to do here.
                    log.debug("spec lookup task failed: {}", e.getMessage());
                }
            }
        } finally {
            specPool.shutdown();
        }
    }

    private void fillSpec(ResourceInstance instance) {
        TbInfraNodeSpecModel spec = vmMetaService.getTBBNodeSpec(
                instance.getNsId(), instance.getMciId(), instance.getVmId());

        if (spec == null || spec.getCspSpecName() == null) {
            return;
        }

        instance.setSpec(String.format("%s (%s vCPU / %s GiB)",
                spec.getCspSpecName(), formatNumber(spec.getVCPU()), formatNumber(spec.getMemoryGiB())));
    }

    private static String formatNumber(Double value) {
        if (value == null) {
            return "?";
        }
        if (value == Math.floor(value) && !value.isInfinite()) {
            return String.valueOf(value.longValue());
        }
        return String.valueOf(value);
    }
}
