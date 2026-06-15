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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private static final Logger log = LoggerFactory.getLogger(InstanceService.class);

    private final InstanceDao instanceDao;
    private final VMMetaService vmMetaService;
    private final InvoiceService invoiceService;

    public List<ResourceInstance> getInstances(String nsId) {
        List<ResourceInstance> instances = instanceDao.selectInstancesByNs(Map.of("nsId", nsId));
        // One Tumblebug call per instance, fanned out in parallel; failures
        // leave spec=null (rendered as "-" by the frontend) without failing the request.
        instances.parallelStream().forEach(this::fillSpec);
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
