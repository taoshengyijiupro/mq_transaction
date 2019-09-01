package com.xlinclass.listenser;


import com.xlinclass.callback.CallBackProducer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class RabbitSendMassage {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(new CallBackProducer());
        rabbitTemplate.setReturnCallback(new CallBackProducer());
    }

    public void sendMassgee(String exchange,String routingKey,String msg){
        // 设置消息唯一id 保证每次重试消息id唯一
        Message message = MessageBuilder.withBody(msg.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8")
                .setMessageId(UUID.randomUUID() + "").build();
        //消息id设置在请求头里面 用UUID做全局ID
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getMessageId());
        rabbitTemplate.convertAndSend(exchange,routingKey,message,correlationData);
    }
}
