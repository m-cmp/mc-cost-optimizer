package com.mcmp.dummybe.dao.login;

import com.mcmp.dummybe.model.login.CurrentLoginModel;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class LoginDAO {

    @Resource(name="sqlSessionTemplateCostOptimize")
    private SqlSessionTemplate sqlSession;

    public int testQuery(){
        return sqlSession.selectOne("cost-optimize-login.testQuery");
    }
    public CurrentLoginModel currentLogin(){
        return sqlSession.selectOne("cost-optimize-login.login");
    }
}
