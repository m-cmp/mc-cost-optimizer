package com.mcmp.dummybe.dao.common;

import com.mcmp.dummybe.model.common.CompanyVendorModel;
import com.mcmp.dummybe.model.common.VendorStatusModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CommonDAO {
    @Resource(name="sqlSessionTemplateCostOptimize")
    private SqlSessionTemplate sqlSession;

    public List<VendorStatusModel> vendorStatus(){
        return sqlSession.selectList("cost-optimize-common.vendorStatus");
    }

    public List<CompanyVendorModel> vendorAccountList() {
        return sqlSession.selectList("cost-optimize-common-company.selectCompanyVendor");
    }
}
