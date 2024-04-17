package com.mcmp.dummybe.service.login;

import com.mcmp.dummybe.dao.login.LoginDAO;
import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.login.CurrentLoginModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginService {
    @Autowired
    private LoginDAO loginDao;
    public ResultModel loginInfo(){
        ResultModel result = new ResultModel();

        CurrentLoginModel resultData = loginDao.currentLogin();

        result.setResult(resultData);
        return result;
    }
}
