package com.centricsoftware.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootTest
@ComponentScan(value="com.centricsoftware")
public class RabbitMQTest {
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    /**
//     * 测试直接模式发送
//     */
//    @Test
//    public void sendDirect() {
////        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DIRECT_MODE_QUEUE_ONE, new MessageStruct("direct " +
////                "message"));
//        HashMap<String, String> map = new HashMap<>();
//        map.put("message","test direct message");
//        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DIRECT_MODE_QUEUE_ONE, JSONUtil.toJsonStr(map));
////        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DIRECT_MODE_QUEUE_ONE, "test simple message");
//    }
//    /**
//     * 测试延迟队列发送
//     */
//    @Test
//    public void sendDelay() {
//        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DELAY_MODE_QUEUE, Constants.RabbitConsts.DELAY_QUEUE,
//                new MessageStruct(
//                "delay message, delay 5s, " + DateUtil
//                .date()), message -> {
//            message.getMessageProperties().setHeader("x-delay", 5000);
//            return message;
//        });
//        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DELAY_MODE_QUEUE, Constants.RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message,  delay 2s, " + DateUtil
//                .date()), message -> {
//            message.getMessageProperties().setHeader("x-delay", 2000);
//            return message;
//        });
//        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DELAY_MODE_QUEUE, Constants.RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message,  delay 8s, " + DateUtil
//                .date()), message -> {
//            message.getMessageProperties().setHeader("x-delay", 8000);
//            return message;
//        });
//    }

}
