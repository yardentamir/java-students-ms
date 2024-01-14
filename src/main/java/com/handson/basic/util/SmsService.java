package com.handson.basic.util;

import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    protected final Log logger = LogFactory.getLog(getClass());
    OkHttpClient client = new OkHttpClient.Builder().build();

    @Value("${sms4free.key}")
    private String ACCOUNT_KEY;
    @Value("${sms4free.user}")
    private String ACCOUNT_USER;
    @Value("${sms4free.password}")
    private String ACCOUNT_PASS;


    //Overloaded constructor to send a message to both a phone number and an email address.
    public boolean send(String text, String phoneNumber) {

        if (phoneNumber == null) return false;
        MediaType mediaType = MediaType.parse("application/json");
        //Define The URL We Wanna Post to
        String url = "https://api.sms4free.co.il/ApiSMS/v2/SendSMS";

        String key = ACCOUNT_KEY;
        String user = ACCOUNT_USER;
        String pass = ACCOUNT_PASS;
        try {

            //create the form body as json
            String json = "{\"key\":\"" + key + "\",\"user\":\"" + user + "\",\"pass\":\"" + pass
                    + "\",\"sender\":\"" + "0547780882" + "\",\"recipient\":\"" + phoneNumber + "\",\"msg\":\"" + text + "\"}";

            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", RequestBody.create(mediaType, json))
                    .addHeader("content-type", "application/json")
                    .build();

            Response res = client.newCall(request).execute();
            String data = res.body().string();
            logger.info("code " + res.code());
            logger.info("data " + data);
            //print result
            boolean success = res.code() < 300 && res.code() >= 200;

            return success;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
