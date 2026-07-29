package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.dao.RawPurgeDao;
import com.mcmp.costbe.archive.model.Csp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** AWS: 월별 상세 테이블 DROP(auto-commit) + cur_origin 해당 월 DELETE. */
@Component
@RequiredArgsConstructor
public class AwsRawPurger extends AbstractRawPurger {

    private final RawPurgeDao dao;

    @Override public Csp csp() { return Csp.AWS; }

    @Override
    protected long deletePartition(String yearMonth) {
        dao.dropAwsDetailTable(yearMonth);        // DDL: 트랜잭션 밖(auto-commit)
        return dao.purgeAwsCurOrigin(yearMonth);  // 반환행수 = cur_origin 삭제분
    }
}
