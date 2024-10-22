package com.mcmp.costbe.common.config;

import com.mcmp.costbe.common.model.ReadyzModel;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ReadyzConfig {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqltemplate;

    private String readyzStatus;

    @Bean
    public ReadyzConfig ReadyzConfig() {
        this.readyzStatus = (isDBConnection()) ? "OK" : "BE DB CONNECTION FAIL";
        return this;
    }

    public synchronized String getState() {
        return this.readyzStatus;
    }

    public synchronized void setState(ReadyzModel newState) {
        this.readyzStatus = newState.getStatus();
    }

    public boolean isDBConnection(){
        try(SqlSession session = sqltemplate.getSqlSessionFactory().openSession()){
            session.getConnection().isValid(3);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
