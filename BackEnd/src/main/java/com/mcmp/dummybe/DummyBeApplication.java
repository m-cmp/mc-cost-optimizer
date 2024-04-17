package com.mcmp.dummybe;

import com.mcmp.dummybe.dao.login.LoginDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DummyBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyBeApplication.class, args);

    }

}
