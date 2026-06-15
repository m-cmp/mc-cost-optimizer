package com.mcmp.cost.ncp.collector.credential;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CSP 크레덴셜 조회기. openbao.enabled 플래그로 동작이 갈린다.
 *  - true  : env 무시하고 OpenBao 에서 바로 조회. 없으면 ERROR 로그.
 *  - false : env 우선 → 없으면 OpenBao 시도 → 둘 다 없으면 ERROR 로그.
 *
 * 각 크레덴셜마다 "어디서 가져왔는지(ENV/OPENBAO)" 또는 "못 가져왔는지"를 로그로 남긴다.
 * 보안상 값(value)은 절대 로그에 남기지 않는다.
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
        log.info("CredentialResolver 초기화 - openbao.enabled={}", openbaoEnabled);
    }

    /** 필수 크레덴셜. 성공 시 출처 INFO, 실패 시 ERROR(설정 후 재시작 안내). */
    public String resolve(String provider, String openBaoKey, String envValue) {
        Source src = sourceOf(provider, openBaoKey, envValue);
        String csp = provider.toUpperCase();
        if (src != Source.NONE) {
            log.info("[{}] credential '{}' 가져옴 (source={})", csp, openBaoKey, src);
        } else if (openbaoEnabled) {
            log.error("[{}] credential '{}' 못 가져옴 - OpenBao(secret/csp/{}) 에 없음 (openbao.enabled=true). 설정 후 다시 시작하세요.",
                    csp, openBaoKey, provider);
        } else {
            log.error("[{}] credential '{}' 못 가져옴 - env/OpenBao(secret/csp/{}) 어디에도 없음. 설정 후 다시 시작하세요.",
                    csp, openBaoKey, provider);
        }
        return valueFrom(src, provider, openBaoKey, envValue);
    }

    /** 선택 크레덴셜. 성공 시 출처 INFO, 실패 시 DEBUG(에러 아님). */
    public String resolveOptional(String provider, String openBaoKey, String envValue) {
        Source src = sourceOf(provider, openBaoKey, envValue);
        String csp = provider.toUpperCase();
        if (src != Source.NONE) {
            log.info("[{}] credential '{}' 가져옴 (source={}, optional)", csp, openBaoKey, src);
        } else {
            log.debug("[{}] credential '{}' 미설정 (optional)", csp, openBaoKey);
        }
        return valueFrom(src, provider, openBaoKey, envValue);
    }

    /** 정책에 따라 어느 소스에서 값을 얻을지 결정 (출처만 판단) */
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
