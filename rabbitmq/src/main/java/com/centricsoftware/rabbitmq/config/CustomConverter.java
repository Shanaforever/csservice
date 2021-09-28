package com.centricsoftware.rabbitmq.config;

import ch.qos.logback.classic.Logger;
import cn.hutool.core.lang.Console;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.List;
import java.util.Map;

@Slf4j
public class CustomConverter {


    public void onMessage(byte[] message){
        System.out.println("---------onMessage----byte-------------");
        System.out.println(new String(message));
    }


    public void onMessage(String message){
        System.out.println("---------onMessage---String-------------");
        System.out.println(message);
    }


    public void onMessage(Map message){
        System.out.println("---------onMessage---map-------------");
        System.out.println(message.toString());
    }

    public void onMessage(List message){
        System.out.println("---------onMessage---List-------------");
        System.out.println(message.toString());
    }
}
