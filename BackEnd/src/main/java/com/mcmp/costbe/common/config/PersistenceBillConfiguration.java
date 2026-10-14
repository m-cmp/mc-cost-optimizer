package com.mcmp.costbe.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class PersistenceBillConfiguration {

    // KRW→USD 환율. 매퍼 XML의 ${krwPerUsd} 를 파싱 시점에 치환 (env: EXCHANGE_KRW_PER_USD, 기본 1400.0)
    @Value("${exchange.krw-per-usd:1400.0}")
    private String krwPerUsd;

    // D-4: Azure Object Storage 는 Storage Account 단위로 과금되고 Tumblebug 은 계정명을 노출하지 않으므로,
    // Tumblebug Azure 자격증명(S3AccessKey)의 Storage Account 이름을 env(AZURE_OBJSTG_STORAGE_ACCOUNTS, 쉼표 구분)로 받아
    // 매퍼 XML 의 ${azureObjStgAccounts} 에 SQL IN 리스트 리터럴('a','b')로 치환한다. 비어 있으면 '' → 어떤 행도 매칭되지 않음.
    @Value("${costopti.objstorage.azure.storage-accounts:}")
    private String azureObjStgAccounts;

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.cost.optimize")
    public DataSource dataSourceBill() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManagerBill() {
        return new DataSourceTransactionManager(dataSourceBill());
    }

    @Primary
    @Bean
    public SqlSessionFactory sqlSessionBill() throws Exception {
        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceBill());
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/**/*_SQL.xml"));
        // 매퍼 XML 파싱 시 ${krwPerUsd} 치환용 (런타임 ${} 토큰은 변수에 없으므로 그대로 유지됨)
        Properties configProps = new Properties();
        configProps.setProperty("krwPerUsd", krwPerUsd);
        configProps.setProperty("azureObjStgAccounts", toSqlInList(azureObjStgAccounts));
        sqlSession.setConfigurationProperties(configProps);
        return sqlSession.getObject();
    }

    /**
     * 쉼표 구분 Storage Account 이름 → SQL IN 리스트 리터럴. Azure Storage Account 이름 규칙(소문자 영숫자 3~24자)에 맞는 값만 통과시켜
     * 설정값이 SQL 에 그대로 들어가더라도 인젝션이 불가능하게 한다. 유효한 값이 없으면 '' (어떤 resource_name 과도 일치하지 않음).
     */
    static String toSqlInList(String csv) {
        if (csv == null) return "''";
        List<String> items = Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .filter(s -> s.matches("[a-z0-9]{3,24}"))
                .distinct()
                .collect(Collectors.toList());
        if (items.isEmpty()) return "''";
        return items.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
    }

    @Primary
    @Bean
    public SqlSessionTemplate sqlSessionTemplateBill() throws Exception {
        return new SqlSessionTemplate(sqlSessionBill(), ExecutorType.SIMPLE);
    }
}

