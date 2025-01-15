package com.cmgmtfs.calcify.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SmsUtils {
//    public static final String FROM_NUMBER = "";
//    public static final String SID_KEY = "";
//    public static final String TOKEN_KEY = "";
    private static String sid;
    private static String token;
    private static String fromNumber;

    // Use @Value to inject values from application.properties or application.yml
    @Value("${twilio.sid}")
    public void setSid(String sid) {
        this.sid = sid;
    }

    @Value("${twilio.auth-token}")
    public void setToken(String token) {
        this.token = token;
    }

    @Value("${twilio.from-number}")
    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }


    public static void sendSMS(String to, String messageBody) {
        System.out.println("SID: " + sid + ", TOKEN: " + token + ", FROM: " + fromNumber);

//        Twilio.init(SID_KEY, TOKEN_KEY);
//        Message message = creator(new PhoneNumber("+"+to), new PhoneNumber(FROM_NUMBER), messageBody).create();
//        System.out.println(message);
    }

}
