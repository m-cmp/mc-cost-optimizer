package com.mcmp.dummybe.dao.abnormal;

import com.mcmp.dummybe.model.abnormal.AbnormalDataModel;
import com.mcmp.dummybe.model.abnormal.AbnormalItemModel;
import com.mcmp.dummybe.model.abnormal.AbnormalMessageModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class AbnormalDAO {

    @Resource(name="sqlSessionTemplateCostOptimize")
    private SqlSessionTemplate sqlSession;

    public List<AbnormalDataModel> selectAbnormalData(){
        return sqlSession.selectList("cost_optimize_abnormal.selectAbnormalData");
    }

    public List<String> selectAbnormalItem(){
        return sqlSession.selectList("cost_optimize_abnormal.selectAbnormalItem");
    }

    public List<AbnormalMessageModel> selectAbnormalMessage() {
        return sqlSession.selectList("cost_optimize_abnormal.selectAbnormalMessage");
    }
}
