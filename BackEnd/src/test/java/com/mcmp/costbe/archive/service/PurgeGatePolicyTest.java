package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.dao.ArchiveDao;
import com.mcmp.costbe.archive.dao.ArchiveManifestDao;
import com.mcmp.costbe.archive.model.ArchiveManifest;
import com.mcmp.costbe.archive.model.Csp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurgeGatePolicyTest {

    private ArchiveManifestDao manifestDao;
    private ArchiveDao archiveDao;
    private PurgeGatePolicy policy;

    @BeforeEach
    void setup() {
        manifestDao = mock(ArchiveManifestDao.class);
        archiveDao = mock(ArchiveDao.class);
        policy = new PurgeGatePolicy(manifestDao, archiveDao);
        ReflectionTestUtils.setField(policy, "bufferDays", 3);
    }

    private ArchiveManifest verified(boolean purged) {
        ArchiveManifest m = new ArchiveManifest();
        m.setStatus("VERIFIED");
        m.setRawPurged(purged);
        return m;
    }

    private String ym(int minusMonths) {
        return YearMonth.now().minusMonths(minusMonths).format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    @Test
    void deniesWhenNoManifest() {
        when(manifestDao.find(any(), any())).thenReturn(null);
        assertThat(policy.canPurge(ym(2), Csp.GCP).isCanPurge()).isFalse();
    }

    @Test
    void deniesWhenNotVerified() {
        ArchiveManifest m = new ArchiveManifest();
        m.setStatus("FAILED");
        when(manifestDao.find(any(), any())).thenReturn(m);
        assertThat(policy.canPurge(ym(2), Csp.GCP).isCanPurge()).isFalse();
    }

    @Test
    void deniesWhenAlreadyPurged() {
        when(manifestDao.find(any(), any())).thenReturn(verified(true));
        assertThat(policy.canPurge(ym(2), Csp.GCP).isCanPurge()).isFalse();
    }

    @Test
    void awsAllowedOnlyWhenFinalized() {
        when(manifestDao.find(any(), any())).thenReturn(verified(false));

        when(archiveDao.isAwsMonthFinalized(any())).thenReturn(true);
        assertThat(policy.canPurge(ym(2), Csp.AWS).isCanPurge()).isTrue();

        when(archiveDao.isAwsMonthFinalized(any())).thenReturn(false);
        assertThat(policy.canPurge(ym(2), Csp.AWS).isCanPurge()).isFalse();
    }

    @Test
    void nonAwsAllowsPastMonthDeniesCurrent() {
        when(manifestDao.find(any(), any())).thenReturn(verified(false));
        assertThat(policy.canPurge(ym(2), Csp.GCP).isCanPurge()).isTrue();  // 2달 전(마감+버퍼 경과)
        assertThat(policy.canPurge(ym(0), Csp.NCP).isCanPurge()).isFalse(); // 이번 달
    }

    @Test
    void deniesInvalidYearMonth() {
        assertThat(policy.canPurge("bad", Csp.GCP).isCanPurge()).isFalse();
    }
}
