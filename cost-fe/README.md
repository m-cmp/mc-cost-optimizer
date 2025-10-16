## Cost-optimizer Frontend Development Guide

- 코드 변경의 경우 step1 부터, 단순 실행의 경우 상위 폴더로 이동해 step3 부터 진행하시면 됩니다.

### 1. 사전 준비

| 항목                | 요구사항 |
| ------------------- | -------- |
| Podman              | v5.6.1   |
| Podman Compose      | v1.5.0   |
| Node.js (FE 빌드용) | v22.18.0 |

### 2. FE 이미지 빌드 & 교체 절차

#### Step 1. FE 코드 수정

```bash
cd cost-fe
# 코드 수정 후 로컬 테스트
npm run dev
```

#### Step 2. Docker 이미지 빌드

```bash
podman build --tls-verify=false -t suihnyoon/mc-costopti-ui:test .
# --tls-verify=false 는 사설 레지스트리 인증을 무시하는 옵션입니다. (공용 빌드시 생략 가능)
```

#### (Optional) Step 2-1. 이미지 Push

# 다른 개발자나 서버에서 사용할 경우 push

podman push suihnyoon/mc-costopti-ui:test

\*\* 이미지 저장소 변경 시, mc-cost-optimizer의 docker-compose.yml 에서 fe의 image 설정 변경

```bash
fe:
    restart: always
    image: # 이 부분을 원하는 이미지 저장소로 변경
    networks:
      - mcmp_cost_network
    ports:
      - "8080:80"
      # podman 정책 상 비루트 권한으로 실행되어 80으로 열 수 없음 (로컬 개발 시 80 이외의 포트로 확인)
```

#### Step 3. Compose로 재기동

```bash
cd ../mc-cost-optimizer

podman-compose up -d fe

# fe 서비스만 재시작, FE 컨테이너가 자동으로 교체됩니다.
```

#### Step 4. 변경 확인

```bash

podman ps

# 혹은 브라우저에서 확인 http://localhost:<docker-compose 에서 설정한 포트번호>

```

### 3. 다른 개발자가 FE 변경 반영받기

```bash

# 최신 이미지 pull

podman compose pull fe

# 또는 수동 갱신

podman pull docker.io/suihnyoon/mc-costopti-ui:test

# 다시 실행

podman-compose up -d fe
```

### 5. 문제 해결 (Troubleshooting)

| 증상                                    | 원인                      | 해결 방법                                                                                            |
| --------------------------------------- | ------------------------- | ---------------------------------------------------------------------------------------------------- |
| `rootlessport cannot expose port 80`    | Podman rootless 포트 제한 | `8080:80` 80 포트 이외 매핑으로 변경                                                                 |
| `no image found for architecture arm64` | Apple Silicon 환경        | `platform: linux/amd64` 지정 또는 QEMU 설정, `podman-compose --podman-run-args "--arch amd64" up -d` |
| `SIGKILL during vite build`             | VM 메모리 부족            | `podman machine set --memory 8192`                                                                   |
