package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.dao.InstanceDao;
import com.mcmp.costbe.llm_recommender.model.ResourceInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private final InstanceDao instanceDao;

    public List<ResourceInstance> getInstances(String nsId) {
        return instanceDao.selectInstancesByNs(Map.of("nsId", nsId));
    }
}
