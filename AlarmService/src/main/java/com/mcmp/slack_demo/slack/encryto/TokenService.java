package com.mcmp.slack_demo.slack.encryto;

import com.mcmp.slack_demo.slack.model.SaveTokenModel;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    // 추후 encrypto 사용하도록 변경(지금은 잘 안되네;;)
//    @Autowired
//    @Qualifier("jasyptStringEncryptor")
//    private Encrypt encryptor;

//    private TokenDao tokenDao = new TokenDao();

    @Autowired
    private TokenDao tokenDao;

//    private TokenDao tokenDao;
////
//    @Autowired
//    public TokenService(TokenDao tokenDao) {
//        this.tokenDao = tokenDao;
//    }

    public void storeToken(SaveTokenModel model) throws Exception {
        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword("mcmp");
        jasypt.setAlgorithm("PBEWITHMD5ANDDES");

        model.setToken(jasypt.encrypt(model.getToken()));
        model.setChannel(jasypt.encrypt(model.getChannel()));
        tokenDao.saveEncryptedToken(model);
    }

    public Map<String, String> retrieveToken(String id) throws Exception {

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();

        jasypt.setPassword("mcmp");

        jasypt.setAlgorithm("PBEWITHMD5ANDDES");
        String encryptedToken = tokenDao.getEncryptedToken(id);
        String encryptedChannel = tokenDao.getEncryptedChannel(id);
        String encryptedToken2 = jasypt.decrypt(encryptedToken);
        String encryptedChannel2 = jasypt.decrypt(encryptedChannel);

        Map<String, String> tokenAndChannel= new HashMap<>();
        tokenAndChannel.put("token", encryptedToken2);
        tokenAndChannel.put("channel", encryptedChannel2);

        return tokenAndChannel;
    }
}
