package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.dao.InstanceDao;
import com.mcmp.costbe.llm_recommender.model.ResourceInstance;
import com.mcmp.costbe.tumblebugMeta.model.mci.TbInfraNodeSpecModel;
import com.mcmp.costbe.tumblebugMeta.service.VMMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private final InstanceDao instanceDao;
    private final VMMetaService vmMetaService;

    public List<ResourceInstance> getInstances(String nsId) {
        List<ResourceInstance> instances = instanceDao.selectInstancesByNs(Map.of("nsId", nsId));
        // One Tumblebug call per instance, fanned out in parallel; failures
        // leave spec=null (rendered as "-" by the frontend) without failing the request.
        instances.parallelStream().forEach(this::fillSpec);
        return instances;
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
