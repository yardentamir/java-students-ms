package com.handson.basic.controller;

import com.handson.basic.model.MessageAndPhones;
import com.handson.basic.util.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api")
public class SmsController {

    @Autowired
    SmsService smsService;

    @RequestMapping(value = "/sms", method = RequestMethod.POST)
    public ResponseEntity<?> smsAll(@RequestBody MessageAndPhones messageAndPhones)
    {
        AtomicBoolean isSuccess = new AtomicBoolean(true);

        new Thread(() -> {
            messageAndPhones.getPhones()
                    .parallelStream()
                    .forEach(phone -> {
                        boolean sendSuccess = smsService.send(messageAndPhones.getMessage(), phone);
                        if (!sendSuccess) {
                            isSuccess.set(false);
                        }
                    });

        }).start();

        try {
            Thread.sleep(1000); // Adjust the timeout as needed
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (isSuccess.get()) {
            return new ResponseEntity<>("SENDING", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
