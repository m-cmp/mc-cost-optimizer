#!/usr/bin/env bash
# 이번 세션에서 수정된 모듈들을 podman으로 빌드 + 태깅 (개인 Docker Hub: suihnyoon)
# 사용: ./build-images.sh
set -euo pipefail
cd "$(dirname "$0")"

TAG="260731-001"
HUB_NS="suihnyoon"
PLATFORM="linux/amd64"   # 운영 서버가 Mac이 아닌 x86_64 리눅스라서 크로스 빌드

# module_dir -> image 이름 접미사 (docker-compose.yaml 의 cloudbaristaorg/mc-costopti-* 와 동일한 접미사, 네임스페이스만 개인 Hub로 교체)
declare -a MODULES=(
  "BackEnd:mc-costopti-api"
  "costProcessor:mc-costopti-costprocessor"
  "costCollector:mc-costopti-costcollector"
  "costSelector:mc-costopti-costselector"
  "azure-vm-rightsizer:mc-costopti-azure-vm-rightsizer"
  "ncp-vm-rightsizer:mc-costopti-ncp-vm-rightsizer"
  "gcpCollector:mc-costopti-gcpcollector"
  "cost-fe:mc-costopti-ui"
  "cost-azure-collector:mc-costopti-azure-collector"
  "cost-ncp-collector:mc-costopti-ncp-collector"
  "AlarmService:mc-costopti-alarm"
)

for entry in "${MODULES[@]}"; do
  dir="${entry%%:*}"
  name="${entry##*:}"
  image="${HUB_NS}/${name}"
  echo "▶ building ${image}:${TAG}  (from ${dir}/Dockerfile, platform=${PLATFORM})"
  podman build --platform "${PLATFORM}" -t "${image}:${TAG}" "${dir}"
done

echo "✅ done"
podman images | grep "${TAG}"
