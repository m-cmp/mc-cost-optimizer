package com.mcmp.costbe.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
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

@Configuration
public class PersistenceBillConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.bill")
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
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/bill/*_SQL.xml"));
        return sqlSession.getObject();
    }

    @Primary
    @Bean
    public SqlSessionTemplate sqlSessionTemplateBill() throws Exception {
        return new SqlSessionTemplate(sqlSessionBill(), ExecutorType.SIMPLE);
    }
}

