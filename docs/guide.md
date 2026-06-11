# mc-cost-optimizer 실행 가이드

작성일: 2026/05/13
 
> 대상 버전 / 이미지 태그
> - webconsole 0.5.3 
> - tumblebug 0.12.8
---

## 1. 환경변수 

### 2.1 `.env`, `docker-compose.yaml`파일 위치

```
docs/.env
docs/docker-compose.yaml
```

## 3. DB 초기화

### 3.1 DDL 위치
테이블이 제대로 생성되지 않았을 경우 아래 DDL 실행
- `mysql/init_cost_db_ddl.sql` 
- `mysql/init_mail_db_ddl.sql`
- `mysql/init_slack_db_ddl.sql`
---

## 4. 실행 순서(AWS VM 생성 테스트 가정)
- AWS CUR 발급 및 계정 생성 부분은 첨부드린 PDF 참고
### 4.1 Tumblebug 프로젝트에 AWS 크레덴셜 입력
```
cd /cb-tumblebug/init # init 디렉토리 이동
./genCredential.sh # CSP별 크레덴셜 입력
./encCredential.sh # 암호화 진행
```
### 4.2 mc-admin-cli 환경 구성
```
mc-admin-cli/conf/docker/docker-compose.yaml 수정 # docs에 있는 docker-compose.yaml로 변경
mc-admin-cli/conf/docker/.env 수정 # docs에 있는 .env에 credential을 채운 후 변경 
```
- `.env` 환경변수 파일에서 AWS의 Credential 입력 위치는 아래와 같습니다.
``` 
# AWS 수집 시 
  CO_AWS_ACCESS_KEY_ID=accesskey
  CO_AWS_SECRET_ACCESS_KEY=secretkey
```
### 4.3 Tumblebug에서 초기화 진행
```
cd /cb-tumblebug/init # 이동
./init.sh # 초기화 진행
```
### 4.4 MCI 생성 후 에이전트 설치
```
mc-admin-cli에서 mcc installsh 실행
gmail, slack 알림 발송의 경우 Cost Analysis 접속 후 상단의 Alarm 탭에서 가이드 확인
매일 오전 9시 이후 비용 수집 배치 진행
에이전트 설치 후 1시간마다 메트릭 정보 수집 
```
