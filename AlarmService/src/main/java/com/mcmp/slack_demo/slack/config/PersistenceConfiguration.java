package com.mcmp.slack_demo.slack.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.slack")
    public DataSource dataSourceSlack() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public PlatformTransactionManager transactionManagerSlack () {
        return new DataSourceTransactionManager(dataSourceSlack());
    }

    @Bean
    public SqlSessionFactory sqlSessionSlack() throws Exception {

        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceSlack());
        sqlSession.setConfigLocation(new ClassPathResource("/config/spring/mybatis-config-slack.xml"));
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/slack/*_SQL.xml"));

        return sqlSession.getObject();
    }
    @Bean
    public SqlSessionTemplate sqlSessionTemplateSlack() throws Exception {
        return new SqlSessionTemplate(sqlSessionSlack(), ExecutorType.SIMPLE);
    }
}
