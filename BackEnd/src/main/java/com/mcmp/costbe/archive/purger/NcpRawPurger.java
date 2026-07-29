package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.dao.RawPurgeDao;
import com.mcmp.costbe.archive.model.Csp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** NCP: vm_month + vm_daily + service_month 3테이블 원자적 DELETE(@Transactional in DAO). */
@Component
@RequiredArgsConstructor
public class NcpRawPurger extends AbstractRawPurger {

    private final RawPurgeDao dao;

    @Override public Csp csp() { return Csp.NCP; }

    @Override
    protected long deletePartition(String yearMonth) {
        return dao.purgeNcpAll(yearMonth);
    }
}
