//package com.mcmp.slack_demo.slack.encryto;
//import org.jasypt.encryption.StringEncryptor;
//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class Encrypt {
//    @Bean("jasyptStringEncryptor")
//
//    public StringEncryptor stringEncryptor() {
//
//
//
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//
//
//
//        config.setPassword("test"); // encrypt key
//
//
//
//        config.setAlgorithm("PBEWITHMD5ANDDES");
//
//        config.setPoolSize("1");
//
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//
//        config.setStringOutputType("base64");
//
//        encryptor.setConfig(config);
////`
//        return encryptor;
//
//    }
//
//}
