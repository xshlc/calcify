package com.cmgmtfs.calcify;

//import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.cmgmtfs.calcify.utils.SmsUtils.sendSMS;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@EnableEncryptableProperties
public class CalcifyApplication {

    private static final int STRENGTH = 12;

    public static void main(String[] args) {
        SpringApplication.run(CalcifyApplication.class, args);
        sendSMS("12345", "hello");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }
}
