package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.dao.RawPurgeDao;
import com.mcmp.costbe.archive.model.Csp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** GCP: gcp_billing_raw 의 해당 invoice_month DELETE. */
@Component
@RequiredArgsConstructor
public class GcpRawPurger extends AbstractRawPurger {

    private final RawPurgeDao dao;

    @Override public Csp csp() { return Csp.GCP; }

    @Override
    protected long deletePartition(String yearMonth) {
        return dao.purgeGcp(yearMonth);
    }
}
