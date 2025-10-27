# cost-ncp-collector


## local 개발 환경 실행 방법.

1. DB 실행
```shell
docker-compose up

```
2. Init DDL
```shell
src/main/resource/ddl_*.sql
```
3. Spring batch 테이블 DDL
```shell
src/main/resource/ddl_batch_schema-mariadb.sql
```
4. appliation 실행
IntelliJ에서 CostNcpCollectorApplication를 실행.

## 개발 및 운영계 환경
현재 특별한 환경이 없어서 application-xxx.yml 파일은 없다.
환경이 설정되면 application.yml을 복사하여 파일을 생성하고 DB 주소 값을 변경하면 된다.

# 특이사항
NCP는 현재 일별 요금을 조회하는 기능을 제공하지 않는다.
모든 비용에 대한 데이터가 월간 청구금액 API밖에 제공하지 않는다.
다만, 매일 8시 경에 해당 데이터가 업데이트가 일어난다.
따라서 테이블에 누적하여 적재하면 추후에 일별 요금의 변화량을 분석 할 수 있다.

현재는 해당 API를 이용하여 월별 요금을 적재하는 부분까지 개발을 완료하였다.

# 수집 방법

NCP의 비용은 API 를 통해서 수집된다. (추후 Schedule을 이용할 수 도 있다.) 
사전에 Credential이 DB에 저장되어있어야한다.

