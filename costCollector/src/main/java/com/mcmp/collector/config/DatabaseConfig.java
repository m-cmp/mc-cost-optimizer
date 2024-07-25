package com.mcmp.collector.config;

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
public class DatabaseConfig {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.batch")
    public DataSource dataSourceBatch() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManagerBatch() {
        return new DataSourceTransactionManager(dataSourceBatch());
    }

    @Primary
    @Bean(name = "sqlSessionBatch")
    public SqlSessionFactory sqlSessionBatch() throws Exception {
        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceBatch());
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/unused/*_SQL.xml"));
        return sqlSession.getObject();
    }

    @Bean(name = "sqlSessionSimple")
    public SqlSessionFactory sqlSessionSimple() throws Exception {
        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceBatch());
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/unused/*_SQL.xml"));
        return sqlSession.getObject();
    }

    @Primary
    @Bean(name = "sqlSessionTemplateSimple")
    public SqlSessionTemplate sqlSessionTemplateSimple() throws Exception {
        return new SqlSessionTemplate(sqlSessionSimple(), ExecutorType.SIMPLE);
    }

}
