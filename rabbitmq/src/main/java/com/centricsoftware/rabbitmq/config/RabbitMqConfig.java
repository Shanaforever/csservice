package com.centricsoftware.rabbitmq.config;

import com.centricsoftware.config.cons.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ配置，主要是配置队列，如果提前存在该队列，可以省略本配置类
 * @author zheng.gong
 * @date 2020/4/17
 */
@Slf4j
@Configuration
public class RabbitMqConfig {


    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        return rabbitTemplate;
    }
//
//    @Bean
//    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
////        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//
////        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        return factory;
//    }



    /**
     * 直接模式队列1
     */
    @Bean
    public Queue directOneQueue() {
        return new Queue(Constants.RabbitConsts.DIRECT_MODE_QUEUE_ONE);
    }
//
//    /**
//     * 队列2
//     */
//    @Bean
//    public Queue queueTwo() {
//        return new Queue(Constants.RabbitConsts.QUEUE_TWO);
//    }
//
//    /**
//     * 队列3
//     */
//    @Bean
//    public Queue queueThree() {
//        return new Queue(Constants.RabbitConsts.QUEUE_THREE);
//    }
//    /**
//     * 延迟队列
//     */
//    @Bean
//    public Queue delayQueue() {
//        return new Queue(Constants.RabbitConsts.DELAY_QUEUE, true);
//    }
//
//    /**
//     * 延迟队列交换器, x-delayed-type 和 x-delayed-message 固定
//     */
//    @Bean
//    public CustomExchange delayExchange() {
//        Map<String, Object> args = Maps.newHashMap();
//        args.put("x-delayed-type", "direct");
//        return new CustomExchange(Constants.RabbitConsts.DELAY_MODE_QUEUE, "x-delayed-message", true, false, args);
//    }

//    /**
//     * 延迟队列绑定自定义交换器
//     *
//     * @param delayQueue    队列
//     * @param delayExchange 延迟交换器
//     */
//    @Bean
//    public Binding delayBinding(Queue delayQueue, CustomExchange delayExchange) {
//        return BindingBuilder.bind(delayQueue).to(delayExchange).with(Constants.RabbitConsts.DELAY_QUEUE).noargs();
//    }

}
