package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.dao.RawPurgeDao;
import com.mcmp.costbe.archive.model.Csp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Azure: vm_daily + service_daily + resource_daily(K8S/Object Storage) 3테이블 원자적 DELETE(@Transactional in DAO). */
@Component
@RequiredArgsConstructor
public class AzureRawPurger extends AbstractRawPurger {

    private final RawPurgeDao dao;

    @Override public Csp csp() { return Csp.AZURE; }

    @Override
    protected long deletePartition(String yearMonth) {
        return dao.purgeAzureAll(yearMonth);
    }
}
