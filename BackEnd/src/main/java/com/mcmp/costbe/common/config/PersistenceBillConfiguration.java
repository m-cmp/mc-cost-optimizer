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
import java.util.Properties;

@Configuration
public class PersistenceBillConfiguration {

    // KRW→USD 환율. 매퍼 XML의 ${krwPerUsd} 를 파싱 시점에 치환 (env: EXCHANGE_KRW_PER_USD, 기본 1400.0)
    @Value("${exchange.krw-per-usd:1400.0}")
    private String krwPerUsd;

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
        sqlSession.setConfigurationProperties(configProps);
        return sqlSession.getObject();
    }

    @Primary
    @Bean
    public SqlSessionTemplate sqlSessionTemplateBill() throws Exception {
        return new SqlSessionTemplate(sqlSessionBill(), ExecutorType.SIMPLE);
    }
}

