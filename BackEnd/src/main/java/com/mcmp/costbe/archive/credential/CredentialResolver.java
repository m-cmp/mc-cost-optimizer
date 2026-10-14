package com.mcmp.costbe.archive.credential;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CSP 크레덴셜 조회기 (costCollector 사본, BackEnd 독립).
 *  - openbao.enabled=true  : env 무시하고 OpenBao(secret/data/csp/{provider}) 조회
 *  - openbao.enabled=false : env 우선 → 없으면 OpenBao → 둘 다 없으면 null
 * 보안상 값은 절대 로그에 남기지 않는다.
 */
@Slf4j
@Component
public class CredentialResolver {

    private enum Source { ENV, OPENBAO, NONE }

    private final boolean openbaoEnabled;
    private final OpenBaoClient openBaoClient;

    public CredentialResolver(@Value("${openbao.enabled:false}") boolean openbaoEnabled,
                              OpenBaoClient openBaoClient) {
        this.openbaoEnabled = openbaoEnabled;
        this.openBaoClient = openBaoClient;
        log.info("[archive] CredentialResolver 초기화 - openbao.enabled={}", openbaoEnabled);
    }

    public String resolve(String provider, String openBaoKey, String envValue) {
        Source src = sourceOf(provider, openBaoKey, envValue);
        String csp = provider.toUpperCase();
        if (src != Source.NONE) {
            log.info("[archive][{}] credential '{}' 가져옴 (source={})", csp, openBaoKey, src);
        } else if (openbaoEnabled) {
            log.error("[archive][{}] credential '{}' 못 가져옴 - OpenBao(secret/csp/{}) 에 없음.", csp, openBaoKey, provider);
        } else {
            log.error("[archive][{}] credential '{}' 못 가져옴 - env/OpenBao(secret/csp/{}) 어디에도 없음.", csp, openBaoKey, provider);
        }
        return valueFrom(src, provider, openBaoKey, envValue);
    }

    private Source sourceOf(String provider, String openBaoKey, String envValue) {
        if (openbaoEnabled) {
            return isEmpty(openBaoValue(provider, openBaoKey)) ? Source.NONE : Source.OPENBAO;
        }
        if (!isEmpty(envValue)) {
            return Source.ENV;
        }
        return isEmpty(openBaoValue(provider, openBaoKey)) ? Source.NONE : Source.OPENBAO;
    }

    private String valueFrom(Source src, String provider, String openBaoKey, String envValue) {
        switch (src) {
            case ENV:     return envValue;
            case OPENBAO: return openBaoValue(provider, openBaoKey);
            default:      return null;
        }
    }

    private String openBaoValue(String provider, String openBaoKey) {
        String v = openBaoClient.readCsp(provider).get(openBaoKey);
        return isEmpty(v) ? null : v;
    }

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
