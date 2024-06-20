package com.mcmp.slack_demo.mail.config;

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
public class PersistenceMailConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.mailing")
    public DataSource dataSourceMailing() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public PlatformTransactionManager transactionManagerMailing () {
        return new DataSourceTransactionManager(dataSourceMailing());
    }

    @Bean
    public SqlSessionFactory sqlSessionMailing() throws Exception {

        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceMailing());
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/mailing/*_SQL.xml"));

        return sqlSession.getObject();
    }
    @Bean
    public SqlSessionTemplate sqlSessionTemplateMailing() throws Exception {
        return new SqlSessionTemplate(sqlSessionMailing(), ExecutorType.SIMPLE);
    }
}
