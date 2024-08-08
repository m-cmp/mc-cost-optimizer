package com.mcmp.slack_demo.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PersistenceHistoryConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.history")
    public DataSource dataSourceHistory() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public PlatformTransactionManager transactionManagerHistory () {
        return new DataSourceTransactionManager(dataSourceHistory());
    }

    @Bean
    public SqlSessionFactory sqlSessionHistory() throws Exception {

        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceHistory());
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/history/*_SQL.xml"));

        return sqlSession.getObject();
    }
    @Bean
    public SqlSessionTemplate sqlSessionTemplateHistory() throws Exception {
        return new SqlSessionTemplate(sqlSessionHistory(), ExecutorType.SIMPLE);
    }

}
