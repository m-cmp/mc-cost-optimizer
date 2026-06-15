# cost-azure-collector

## local 개발 환경 실행 방법.

1. DB 실행
```shell
docker-compose up

```
2. Init DDL
```shell
src/main/resource/ddl_*.sql 파일에 정의
```
3. Spring batch 테이블
```shell
src/main/resource/ddl_batch_schema-mariadb.sql 파일
```
4. appliation 실행
IntelliJ에서 CostAzureCollectorApplication를 실행.


## 개발 및 운영계 환경
현재 특별한 환경이 없어서 application-xxx.yml 파일은 없다.
환경이 설정되면 application.yml을 복사하여 파일을 생성하고 DB 주소 값을 변경하면 된다.

# 특이사항
Azure는 일별 비용을 조회하는 API가 제공되어 Service 별 비용과, VM 별 비용을 적재하도록 개발이 완료되었다.
다만, 해당 비용이 어떠한 이유로(사용량 기타등등) 해당 비용이 나왔는지에 대해서는 원시데이터라 불리는 데이터를 요청하여
이를 파싱하는 작업이 필요한데, 해당 프로젝트에서는 비용에 대해서만 수집하면 되는 요건이므로 단순 API로만 사용하는걸로 개발하였다.

# 수집 방법

Azure의 비용은 API 를 통해서 수집된다. (추후 Schedule을 이용할 수 도 있다.) 
사전에 Credential이 DB에 저장되어있어야한다. (subscriptionId까지 입력이 되어야한다.)

