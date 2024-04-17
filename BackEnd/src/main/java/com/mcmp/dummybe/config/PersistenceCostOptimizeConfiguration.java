package com.mcmp.dummybe.config;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PersistenceCostOptimizeConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.cost.optimize")
    public DataSource dataSourceCostOptimize() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManagerCostOptimize() {
        return new DataSourceTransactionManager(dataSourceCostOptimize());
    }

    @Primary
    @Bean
    public SqlSessionFactory sqlSessionCostOptimize() throws Exception {
        SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
        sqlSession.setDataSource(dataSourceCostOptimize());
//        sqlSession.setConfigLocation(new ClassPathResource("/config/spring/mybatis-config-cost-optimize.xml"));
        sqlSession.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_URL_PREFIX + "/mapper/cost-optimize/**/*_SQL.xml"));
        return sqlSession.getObject();
    }

    @Primary
    @Bean
    public SqlSessionTemplate sqlSessionTemplateCostOptimize() throws Exception {
        return new SqlSessionTemplate(sqlSessionCostOptimize(), ExecutorType.SIMPLE);
    }

}
