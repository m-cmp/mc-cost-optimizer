# 비용 인보이스 아카이빙 설계문서 (v1)

## 0. 결정 요약 (확정된 정책)

| 항목 | 결정 |
|---|---|
| 무엇을 담나 | **BE가 발행하는 인보이스(집계)** 를 버킷에 저장 |
| 무엇을 지우나 | **raw 원본** (아카이브는 인보이스만, raw는 **복사 없이 삭제** = Option B) |
| 트리거 | **수동** — 인보이스 화면의 `[아카이빙]` 버튼(월/CSP 선택 모달) |
| 아카이빙 ↔ 삭제 | **완전 분리 버튼**. 아카이빙=비파괴(언제든), 삭제=별도 액션(VERIFIED+게이트 통과 달만) |
| 삭제 게이트 | **CSP별 확정 신호**: AWS=`certifed_fixed_yn='Y'`, GCP/Azure/NCP=`대상월<현재월`+버퍼3일 (§6 검증됨) |
| 미마감 아카이브 | 허용(사용자 책임). 단 삭제는 게이트 통과 시에만 |
| 배치 위치 | **BackEnd(api)** 내부 (인보이스가 여기서 나옴) |
| 포맷 | CSV(gzip) — 인보이스는 정형 컬럼 |
| 비파괴 원칙 | 삭제 전 **S3 검증(HEAD/rowcount)** 필수, 집계(`monthly_summation`)는 **안 지움** |
| 저장 위치 | **기존 CUR 버킷 재사용 + 전용 prefix**(`invoice-archive/`). 새 버킷 X. 버킷/prefix/region은 **app config(env)** — 정적 1행이라 테이블 대신 properties(콜렉터가 export name/cron을 env로 두는 패턴과 정합) |
| DB 신규 테이블 | **`cost_archive_manifest` 1개만**(쌓이는 운영 기록). 정적 설정은 테이블 X → env. 테이블은 `CREATE TABLE IF NOT EXISTS` + 부팅 자동 |

> ⚠️ **Option B의 트레이드오프(문서화)**: raw를 복사 없이 삭제하므로 **line-item 상세는 영구 소멸**. 인보이스(파생·고정환율·귀속동결·네트워크 과소집계)만 남고 재계산/원천대조 불가. "집계만 있으면 됨"을 전제로 채택.

---

## 1. 각 CSP raw 저장 위치 (= 삭제 대상) — 실 스키마로 검증됨(dump-202607031008)

| CSP | raw 테이블 | 실제 월 파티션 컬럼 | **검증된 삭제 SQL** (예: 202606) |
|---|---|---|---|
| **AWS** | `tbl_table_billing_detail_<YYYYMM>` (월별 별도 테이블) | 테이블명=월 | `DROP TABLE tbl_table_billing_detail_202606;` |
| **AWS** | `cur_origin` | ⚠️ 월 컬럼 없음 → `lineitem_usagestartdate`(timestamp) | `DELETE FROM cur_origin WHERE lineitem_usagestartdate >= '2026-06-01' AND lineitem_usagestartdate < '2026-07-01';` |
| **GCP** | `gcp_billing_raw` | `invoice_month` varchar(6) | `DELETE FROM gcp_billing_raw WHERE invoice_month='202606';` |
| **NCP** | `ncp_cost_vm_month` | `demand_month` varchar | `DELETE FROM ncp_cost_vm_month WHERE demand_month='202606';` |
| | `ncp_cost_vm_daily` | `demand_month` varchar(6) (또는 `target_date` date) | `DELETE FROM ncp_cost_vm_daily WHERE demand_month='202606';` |
| | `ncp_cost_service_month` | `demand_month` varchar | `DELETE FROM ncp_cost_service_month WHERE demand_month='202606';` |
| **Azure** | `azure_cost_vm_daily` | `usage_date` varchar **YYYYMMDD** | `DELETE FROM azure_cost_vm_daily WHERE usage_date LIKE '202606%';` |
| | `azure_cost_service_daily` | `usage_date` varchar YYYYMMDD | `DELETE FROM azure_cost_service_daily WHERE usage_date LIKE '202606%';` |

> **삭제 함정(검증됨)**: ① AWS `cur_origin`엔 `invoice_month`/월 컬럼이 **없음** → `lineitem_usagestartdate` 범위로만 됨(`data_collect_date`는 수집일이라 부정확). ② Azure `usage_date`는 **YYYYMMDD 문자열** → `LIKE '202606%'`(등호·하이픈 안 됨). ③ GCP는 `invoice_month`(varchar6). ④ AWS 상세는 월별 테이블이라 `DROP`이 정답.

- **안 지우는 것**: `monthly_summation`, `daily_summation_by_product`, `daily_abnormal_by_product`, `budget_monthly`, `alarm_history` (집계/이력 — 대시보드·인보이스 재조회용).
- AWS `tbl_table_billing_detail_*`는 월별 테이블이라 **DROP이 DELETE보다 깔끔**(공간 즉시 반환).

## 2. 인보이스 원천 (담을 데이터)

`BackEnd/.../mapper/bill/invoice_SQL.xml`:
- `getSummaryBill(prevMonths, selectedProjects, selectedCsps)` → (yearMonth × csp) 합계 bill
- `getAWSInvoice` / `getNCPInvoice` / `getAzureInvoice` / `getGCPInvoice` → CSP별 상세(프로젝트/서비스 브레이크다운)
- 통화: NCP/Azure는 `/${krwPerUsd}` USD 환산, project_cd(namespace) 필터 내장

→ 아카이브 산출물 = **대상 월의 `getSummaryBill` + 4개 CSP 상세**를 CSV로 직렬화.

---

## 3. 아키텍처 (BackEnd 내부)

```
[cost-fe 관리페이지]
      │  (BE 프록시)
      ▼
ArchiveController ──▶ CostArchiveFacade ────────────────────────────────┐
                          │                                             │
     ┌────────────────────┼───────────────┬──────────────┬─────────────┤
     ▼                    ▼               ▼              ▼             ▼
InvoiceQueryService  CsvSerializer   S3ArchiveClient  ManifestDao  PurgeGatePolicy
 (기존 invoice 매퍼)   (+gzip)        (awssdk:s3, 신규)   (신규 테이블)   (게이트 판정)
                                                          │
                                    RawPurgerRegistry ◀───┘
                                    ├ AwsRawPurger  ┐
                                    ├ GcpRawPurger  │ Strategy (CSP별)
                                    ├ NcpRawPurger  │
                                    └ AzureRawPurger┘
```

- **신규 의존성**: `software.amazon.awssdk:s3:2.26.30` + `sts:2.26.30`(AssumeRole). costCollector와 동일 버전.
- **자격증명 = costCollector 경로 재사용**: OpenBao base(`secret/data/csp/aws`) → `temp_cmp_user_role_arn.role_arn`로 **AssumeRole** → 세션 크레덴셜로 **PutObject**. (CUR 읽기와 동일 방식. 코드는 **BackEnd에 독립 사본** — costCollector 무수정)
- **⚠️ role `s3-assume-role-v4-2`에 `PutObject`(invoice-archive/*) 권한 필요**(읽기전용이면 추가). region 설정화.

## 4. DB 설계 (신규 테이블 1개: manifest) + 저장 위치 설정(env)

```sql
-- ★ 반드시 CREATE TABLE IF NOT EXISTS: 없으면 생성, 있으면 스킵(오류 X).
--   앱 부팅 시 자동 실행(콜렉터의 initTable / GcpCollectorInitializer 패턴 동일).
CREATE TABLE IF NOT EXISTS cost_archive_manifest (
  year_month     VARCHAR(6)   NOT NULL,   -- 202506
  csp            VARCHAR(10)  NOT NULL,   -- AWS/GCP/NCP/AZURE/ALL
  s3_key         VARCHAR(512),
  row_count      BIGINT,
  byte_size      BIGINT,
  sha256         CHAR(64),
  status         VARCHAR(20),             -- ARCHIVED → VERIFIED / FAILED
  raw_purged     TINYINT(1) DEFAULT 0,    -- raw 삭제 여부(Option B)
  raw_purged_at  DATETIME,
  archived_at    DATETIME,
  updated_at     DATETIME,
  PRIMARY KEY (year_month, csp),
  INDEX (status), INDEX (archived_at)
);
```
- **현황/월선택은 이 테이블 기준** (raw 지워도 "아카이브됨" 유지).

**저장 위치 설정 = 테이블 아님, app config(env)** — 정적 1행이라 테이블은 과함(수동 INSERT 불필요):
```properties
# ★ bucket_nm + role_arn 은 temp_cmp_user_role_arn(csp='AWS')에서 조회 재사용 → env에 두지 않음
archive.s3.prefix=${ARCHIVE_S3_PREFIX:invoice-archive}   # 아카이브 전용
archive.s3.region=${ARCHIVE_S3_REGION:ap-northeast-2}    # 테이블에 없음 → env
```
→ prefix/region만 `@ConfigurationProperties("archive.s3")`. **bucket은 `UserArnDao`가 temp 테이블에서 조회**(role_arn과 함께).
- ※ `temp_cmp_user_role_arn`은 **접근정보(bucket+role) 읽기 재사용 O**, 다만 여기에 아카이브 설정을 **추가/repurpose는 X**.
- run 테이블은 v1 생략(수동·단발). 자동화(v2) 때 추가.

**최종 S3 경로**: `s3://mcmp-costopti-curbucket-new/invoice-archive/year_month=<ym>/invoice_<ym>.csv.gz` (CUR 데이터와 prefix로 분리)

## 5. 처리 흐름

```
[아카이브]
1. ym, csps, projects 입력
2. InvoiceQueryService.fetchInvoice(ym, csps, projects)
3. CsvSerializer.toCsvGz(invoice)  → bytes
4. bucket = 기존 CUR 버킷(DB설정), key = invoice-archive/year_month=<ym>/invoice_<ym>.csv.gz  ← 전용 prefix로 CUR와 구분
5. S3ArchiveClient.put(bucket, key, bytes, SSE)
6. S3ArchiveClient.head(key) 검증 → byte_size 일치
7. ManifestDao.upsert(status=VERIFIED, rowcount, sha256, key)
8. ★ log.info("Archived {}/{} → s3://{}/{} ({} rows, {} bytes)", ym, csp, bucket, key, rows, bytes)
   → **아카이빙 시마다 저장 위치(s3://버킷/키)·행수·크기 로그** (감사·추적용)

[raw 삭제 — 아카이브와 완전 별개 액션(POST /purge). 아카이빙 눌러도 삭제 안 됨]
8. PurgeGatePolicy.canPurge(ym, csp)  // manifest VERIFIED && CSP별 확정신호
9. 통과 시: RawPurgerRegistry.get(csp).purge(ym)   // CSP별 DELETE/DROP
10. ManifestDao.markPurged(ym, csp)
```

## 6. 삭제 게이트 정책 (콜렉터 재수집 범위 코드 검증 반영)

### 검증된 CSP별 재수집 범위
| CSP | 재수집 대상 | 과거월 재수집 | 안전 시점 |
|---|---|---|---|
| GCP | `now().minusDays(1)` 어제 1일 | ❌ | 월 종료 직후 |
| Azure | `now().minusDays(1)` 어제 1일 | ❌ | 월 종료 직후 |
| NCP | 현재월(`startMonth=endMonth=이번달`) | ❌ | 다음 달 시작 |
| AWS | `certifed_fixed_yn='N'`(미확정월) 계속 재수집 | ✅ 확정 전까지 | **`certifed_fixed_yn='Y'`(InvoiceId 발행) 이후** |

→ GCP/Azure/NCP는 지난 달을 다시 안 긁고 **월말에 값이 고정**됨. AWS만 "확정까지 재수집"하나 **정확한 플래그 존재**.

### `PurgeGatePolicy.canPurge(ym, csp)` = 전부 참
1. `manifest.status == VERIFIED` (아카이브 검증 완료)
2. **CSP별 확정 신호**:
   - **AWS**: `cur_process_info.certifed_fixed_yn='Y'` (해당 월) — N일 추측 대신 실제 확정 플래그
   - **GCP/Azure/NCP**: `ym < 현재월` (월 종료) + 버퍼(기본 3일, `archive.purge.buffer-days`)
3. 진행 중 수집 잡 없음 (동시성)

> 단일 N일로 통일 시 AWS 인보이스 발행이 병목이라 **15일이면 충분**(초안의 90일은 과도). 다만 **AWS는 플래그 조회가 정확**하므로 CSP별 신호 권장.

실패 시 삭제 버튼 비활성 + 사유 표시("AWS 인보이스 미발행 / 아직 이번 달").

## 7. API 명세

| 메소드 | 경로 | 설명 |
|---|---|---|
| GET | `/api/cost/archive/status` | manifest 기반 현황(월×CSP 매트릭스) |
| GET | `/api/cost/archive/months` | 아카이브 가능한 연·월(기존 데이터 기반) |
| GET | `/api/cost/archive/gate?yearMonth=&csp=` | 삭제 게이트 판정(사유 포함) |
| POST | `/api/cost/archive` | `{yearMonth, csps[], projects[]}` → **아카이브만(비파괴, 삭제 안 함)** |
| POST | `/api/cost/archive/purge` | `{yearMonth, csp}` → **raw 삭제(게이트 통과 시만, 아카이브와 완전 별개)** |

## 8. 프론트엔드 설계 (UI)

**원칙: 아카이빙 버튼 ↔ 삭제 버튼 완전 분리. 아카이빙은 비파괴(언제든), 삭제는 VERIFIED+게이트 통과 달만 별도.**

### 8.1 배치 (`BillingReportPage` — 기존: BaseInfoCard + MonthlyOverviewCard + InvoiceTable)
```
┌ BaseInfoCard ─────────┐ ┌ MonthlyOverviewCard ──┐
└───────────────────────┘ └───────────────────────┘
┌ InvoiceTable (전체폭) ───────────────────────────────┐
│  인보이스 상세                       [🗄 아카이빙] ←카드헤더 우측 │
│  ─────────────────────────────────────────────────  │
│  (그리드…)                                            │
└──────────────────────────────────────────────────────┘

[아카이빙] 클릭 → 모달 (삭제 체크박스 없음):
┌ 비용 아카이빙 ─────────────────────────────┐
│ 대상 월  [2026-06 ▾]  ← GET /months          │
│ CSP  [☑AWS ☑GCP ☑NCP ☑AZURE]                │
│ 프로젝트 [전체 ▾]                            │
│                        [취소] [아카이빙 실행] │
│  (실행 시 진행률 바)                          │
└──────────────────────────────────────────────┘

아카이브 현황 + 삭제 (배지/작은 목록, manifest 기준):
   2026-04  ✅아카이브됨(raw purged)
   2026-05  ✅아카이브됨            [raw 삭제]  ← VERIFIED+게이트 통과 시만 활성
   2026-06  ⟳ 미아카이브                        (삭제버튼 아예 없음)
```

### 8.2 신규 컴포넌트 (2개 + 현황 표시)
| 컴포넌트 | 위치 | 역할 |
|---|---|---|
| `ArchiveButton` | InvoiceTable 카드 헤더 우측 | 클릭 → ArchiveModal 오픈 (outline 스타일) |
| `ArchiveModal` | 모달 | 월/CSP/프로젝트 선택 + 실행 + 진행률. **삭제 체크박스 없음** |
| `ArchiveStatus` | MonthlyOverviewCard 하단 or 별도 카드 | manifest 기반 배지 + **VERIFIED+게이트 통과 달만 `[raw 삭제]`** 버튼(→ ConfirmDialog) |

### 8.3 ArchiveModal 필드
| 필드 | 소스 | 비고 |
|---|---|---|
| 대상 월 | `GET /months` | 드롭다운, 필수 |
| CSP | 체크박스 4개 | 기본 전체 |
| 프로젝트(namespace) | 현재 필터 or 드롭다운 | 선택 |
| 진행률 | POST 응답 | 실행 중 바 |

→ **삭제 옵션은 모달에 없음.** 삭제는 8.1 "현황"의 `[raw 삭제]` 버튼으로만.

### 8.4 API 연동 (BackEnd 프록시 경유 — alarm API 패턴과 정합)
| 훅/호출 | 엔드포인트 | 용도 |
|---|---|---|
| `useArchivableMonths()` | GET `/api/cost/archive/months` | 모달 월 드롭다운 |
| `useArchiveStatus()` | GET `/api/cost/archive/status` | 현황 배지/목록 |
| `archive(opts)` | POST `/api/cost/archive` | 아카이브(비파괴) → 완료 후 status refetch |
| `checkGate(ym,csp)` | GET `/api/cost/archive/gate` | 삭제 버튼 활성 판정 |
| `purgeRaw(ym,csp)` | POST `/api/cost/archive/purge` | raw 삭제(확인 모달 후) → status refetch |

### 8.5 파일 배치(제안)
```
cost-fe/src/pages/billingReport/components/
  ArchiveButton.jsx
  ArchiveModal.jsx
  ArchiveStatus.jsx          (배지/목록 + [raw 삭제])
cost-fe/src/api/billing/archive.js     (5개 호출)
cost-fe/src/hooks/useArchive.js        (archive/purge mutation + status)
```
→ 기존 `BillingReportPage`/`InvoiceTable`은 버튼 1개 꽂는 수준으로 변경 최소.

---

## 9. 코딩 규칙 (메소드 시그니처)

### 명명 규칙
- 동사-우선, 단일 책임: `archive*`, `purge*`, `fetch*`, `evaluate*`, `verify*`
- 입출력 DTO: `*Options`(입력), `*Result`(출력). 예: `ArchiveOptions`, `ArchiveResult`, `GateResult`, `PurgeResult`
- CSP 열거: `enum Csp { AWS, GCP, NCP, AZURE }`
- 한 클래스 = 한 관심사(쿼리/직렬화/S3/매니페스트/게이트/삭제 분리)

### Facade (진입점 — 컨트롤러는 이것만 의존)
```java
public interface CostArchiveFacade {
    ArchiveResult archiveInvoice(ArchiveOptions opts);       // 인보이스 저장(+검증)
    GateResult    evaluatePurgeGate(YearMonth ym, Csp csp);  // 삭제 가능?
    PurgeResult   purgeRaw(YearMonth ym, Csp csp);           // raw 삭제(게이트 통과 시)
    List<ArchiveStatus> getStatus();                         // 현황 매트릭스
    List<YearMonth>     archivableMonths();                  // 선택 가능 연월
}
```

### 하위 컴포넌트
```java
class InvoiceQueryService {                       // 기존 invoice 매퍼 래핑
    InvoiceData fetchInvoice(YearMonth ym, List<Csp> csps, List<String> projects);
}
class CsvSerializer {
    byte[] toCsvGz(InvoiceData data);
}
interface S3ArchiveClient {                        // 신규(awssdk:s3)
    PutResult  put(String key, byte[] body);
    HeadResult head(String key);
}
interface ArchiveManifestDao {
    void upsert(ArchiveManifest m);
    void markPurged(YearMonth ym, Csp csp);
    List<ArchiveManifest> findAll();
    Optional<ArchiveManifest> find(YearMonth ym, Csp csp);
}
class PurgeGatePolicy {
    GateResult canPurge(YearMonth ym, Csp csp);   // 검증완료 && 마감경과 && 수집창밖
}
```

### Strategy (CSP별 raw 삭제 — 이질성 격리)
```java
interface RawPurger {
    Csp csp();
    long countRows(YearMonth ym);
    PurgeResult purge(YearMonth ym);   // CSP별 DELETE/DROP
}
class AwsRawPurger   implements RawPurger { /* DROP tbl_table_billing_detail_YYYYMM + DELETE cur_origin */ }
class GcpRawPurger   implements RawPurger { /* DELETE gcp_billing_raw WHERE invoice_month */ }
class NcpRawPurger   implements RawPurger { /* DELETE ncp_cost_vm_month/daily/service_month */ }
class AzureRawPurger implements RawPurger { /* DELETE azure_cost_vm_daily/service_daily */ }

@Component
class RawPurgerRegistry {                         // Spring이 List<RawPurger> 주입 → Map<Csp,..>
    RawPurger get(Csp csp);
}
```

### (선택) Template Method — 공통 삭제 절차
```java
abstract class AbstractRawPurger implements RawPurger {
    public final PurgeResult purge(YearMonth ym) {
        long before = countRows(ym);
        int deleted = deletePartition(ym);        // ← CSP별 구현
        return new PurgeResult(csp(), ym, before, deleted);
    }
    protected abstract int deletePartition(YearMonth ym);
}
```

---

## 10. Facade 패턴 적용 분석 (요청)

**결론: Facade 적합. Strategy와 병용이 정석.**

- **Facade가 맞는 이유**: 아카이브 1건이 (인보이스 쿼리 → 직렬화 → S3 업로드 → 검증 → 매니페스트 → 게이트 판정 → CSP별 raw 삭제) 6~7개 서브시스템을 오케스트레이션. 컨트롤러/페이지가 이 복잡도를 몰라도 되게 **`CostArchiveFacade` 하나로 단순 인터페이스** 제공 → 교과서적 Facade.
- **Strategy 병용**: raw 삭제만 CSP마다 테이블/컬럼이 달라(이질성) → `RawPurger` 전략으로 분리, Facade가 레지스트리로 디스패치. **가변부(CSP별 삭제)만 격리**하고 나머지는 공통.
- **Template Method(선택)**: 삭제 공통 절차(count→delete→result)를 상위, `deletePartition`만 하위. 중복 제거.
- **효과**: 컨트롤러는 Facade만 의존 → 테스트/교체 쉬움. CSP 추가 = `RawPurger` 1개 추가(Facade·컨트롤러 무변경, OCP 준수).

패턴 지도: **Facade(진입) + Strategy(CSP별 삭제) + Template Method(삭제 절차) [+ Registry(전략 주입)]**

---

## 11. 구현 플랜 (단계별, 의존순, 각 단계 수용기준)

**아카이브 단위 = (year_month × csp)** — manifest PK와 일치. 즉 CSP별 CSV 1개 = manifest 1행 → 삭제·게이트도 CSP별로 물림.

### Phase 0 — 전제(병행)
- [ ] role `s3-assume-role-v4-2`에 `PutObject`(invoice-archive/*) 권한 확인/추가 (인프라, **런타임 전까지만**)
- [ ] 노출된 AWS 키 회전/폐기

### Phase 1 — 기반: DB + 설정 + S3 접근(AssumeRole)
1. `cost_archive_manifest` DDL + **`ArchiveSchemaInitializer`**(@PostConstruct, `CREATE TABLE IF NOT EXISTS`)
2. **`ArchiveS3Properties`** `@ConfigurationProperties("archive.s3")`(bucket/prefix/region) + `application.properties` + docker env
3. pom: `awssdk:s3` + `awssdk:sts`
4. **자격증명/AssumeRole 포팅**(BackEnd 독립 사본, costCollector 무수정):
   - `UserArnDao` — `temp_cmp_user_role_arn` **SELECT**(bucket_nm, role_arn) by cmpUserId
   - `ArchiveCredentialProvider` — OpenBao/env base 키 → STS AssumeRole(role_arn) → 세션 크레덴셜
5. **`S3ArchiveClient`** — `put(key,bytes)`/`head(key)`, 세션 크레덴셜 + SSE
- ✅ **수용기준**: BackEnd 부팅 → manifest 테이블 자동 생성 + **실 버킷 put/head 스모크 성공**

### Phase 2 — 아카이브 코어(E2E 저장)
6. DTO: `enum Csp`, `ArchiveOptions/Result`, `ArchiveManifest`, `InvoiceData`, `PutResult/HeadResult`
7. **`InvoiceQueryService`** — 기존 `get{CSP}Invoice` 매퍼 래핑(대상 (ym,csp))
8. **`CsvSerializer`** — `InvoiceData→CSV→gzip` + `row_count`/`sha256`/`byte_size` 계산
9. **`ArchiveManifestDao`** + 매퍼(upsert/find/findAll/markPurged)
10. **`CostArchiveFacade.archiveInvoice`** — csp 루프: fetch→csv→put→head검증→manifest(VERIFIED)→**로그(s3위치·행수·크기)**. 실패는 **CSP별 격리→FAILED 기록**(부분성공 허용). 재실행=같은 key 덮어쓰기(멱등)
- ✅ **수용기준**: 한 달 아카이브 → `s3://…/invoice-archive/year_month=…/…csv.gz` 생성 + manifest VERIFIED + 로그. 재실행 멱등

### Phase 3 — 조회 API(현황/월/게이트)
11. **`archivableMonths()`** — `monthly_summation` 등에서 distinct `year_month`
12. **`getStatus()`** — manifest × 최근 N개월 → 월×CSP 매트릭스(미아카이브/ARCHIVED/purged)
13. **`PurgeGatePolicy`** — AWS=`cur_process_info.certifed_fixed_yn='Y'` 조회(신규 mapper) / GCP·Azure·NCP=`ym<현재월`+버퍼. `evaluatePurgeGate()`
14. **`ArchiveController`** GET status/months/gate + POST archive
- ✅ **수용기준**: curl로 status/months/gate/archive 동작(프론트 없이)

### Phase 4 — 삭제(Strategy)
15. **`RawPurger`** + `AbstractRawPurger`(Template) + **Aws/Gcp/Ncp/Azure 4종**(§1 검증 SQL) + `RawPurgerRegistry`
    - ⚠️ **NCP 3테이블 = `@Transactional` 묶음**. **AWS `DROP TABLE`은 DDL auto-commit**(트랜잭션 밖) → DROP 먼저 후 cur_origin DELETE, 실패 시 로그
16. **`CostArchiveFacade.purgeRaw`** — 게이트 재확인→purge→manifest `markPurged`→로그
17. **`ArchiveController`** POST purge
- ✅ **수용기준**: 게이트 통과 달 raw 삭제 + `raw_purged=1`, 미통과 달은 거부(사유)

### Phase 5 — 프론트엔드
18. `api/billing/archive.js`(5 호출) + `useArchive.js`(archive/purge mutation + status)
19. `ArchiveButton` + `ArchiveModal`(월/CSP/프로젝트 + 진행률)
20. `ArchiveStatus`(배지 + 게이트 기반 `[raw 삭제]` + `ConfirmDialog`)
21. `BillingReportPage`/`InvoiceTable` 헤더에 버튼 연결(변경 최소)
- ✅ **수용기준**: 화면에서 아카이브→현황→삭제 E2E

### Phase 6 — 검증/마무리
22. 단위테스트: `CsvSerializer`, `PurgeGatePolicy`, 각 `RawPurger` SQL, `ManifestDao`
23. 통합 스모크: 실 버킷 1달 E2E + 로그 확인 + (게이트 통과 달)삭제
24. README/설계문서 갱신, 배포 env 정리

### 횡단 관심사(전 단계 공통)
- **에러 처리**: CSP별 격리, FAILED 기록, 부분성공 허용
- **멱등**: 같은 key 덮어쓰기 + manifest upsert
- **트랜잭션**: NCP 다중테이블 원자적 / AWS DROP은 auto-commit 인지
- **로깅**: 아카이브·삭제 시 s3위치·행수·크기(§5-8)
- **보안**: creds 커밋 금지(env/OpenBao), IAM `invoice-archive/*` 제한
- **불변조**: costCollector CUR 코드 무수정(AssumeRole 독립 사본)
- **★ 클라우드 API 과금 방어(필수)**:
  - STS AssumeRole **세션 캐싱**(만료 전 재사용) → STS 호출 시간당 ~1회
  - S3는 **PutObject/HeadObject targeted only** — **List/recursive/Get 금지**(현황·월목록은 DB manifest)
  - **재시도 루프 금지**(실패=FAILED 기록만), **요청당 상한 = 1개월 × ≤4 CSP**(전체월 순회 X)
  - **v1 스케줄러 없음(수동만)**. v2 자동화 시 **월간 크론 + 중복실행 락**(2분 크론 폭주 사고 재발 방지)
  - 프론트: 요청 중 버튼 비활성 + 폴링 재트리거 금지

## 12. 리스크 / 미결정 (검토 반영)

- **Option B 데이터 손실**: line-item 영구 소멸 — **수용 전제(확정)**.
- **인보이스 완전성**: VM 귀속 기반이라 네트워크 등 과소집계(제품 특성). 아카이브도 그 값 그대로 — **인지/수용**.
- ✅ **게이트**: CSP별 확정 신호로 확정(§6, 코드 검증). ~~N일 추측~~ 불필요.
- ✅ **자격증명**: OpenBao base(`secret/data/csp/aws`) → AssumeRole(temp 테이블 role_arn) → PutObject. costCollector 경로 재사용(BackEnd 독립 사본). 별도 OpenBao 항목 불필요.
- ✅ **혼재 없음(검증)**: temp 테이블 읽기전용, costCollector/BackEnd 물리분리, S3 prefix 분리(읽기 vs 쓰기), STS 다중세션 안전.
- ⚠️ **런타임 전 확인 1개**: role `s3-assume-role-v4-2`에 `PutObject`(invoice-archive/*) 권한 유무 → 없으면 인프라 요청.
