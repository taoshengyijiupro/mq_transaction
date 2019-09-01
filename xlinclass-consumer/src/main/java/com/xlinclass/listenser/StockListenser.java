package com.xlinclass.listenser;


import com.rabbitmq.client.Channel;
import com.xlinclass.consumer.Constant;
import com.xlinclass.utils.RedisUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StockListenser {

    @Autowired
    private RedisUtil redisUtil;

    @RabbitListener(queues = Constant.ORDER_QUEUE_NAME,
            containerFactory = "simpleRabbitListenerContainerFactory")
    public void stockListenser(Message message, Channel channel) throws IOException {
        //幂等性
        String messageId = message.getMessageProperties().getMessageId();  //id获取
        if (messageId == null) {
            return;
        }
        //从redis中查找全局唯一ID
         String orderId= (String) redisUtil.get(messageId);
        if (orderId == null || orderId.length() == 0){
            System.out.println("执行业务逻辑");
            if (false){
                //手动确认
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }else {
                System.out.println("消息退回");
                //批量退回  第二个参数 false:单挑退回   true:批量退回
                //channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
                //拒绝消息  退回死信队列
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                //单条退回
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }
        }

        //写入redis
        redisUtil.set(messageId,"orderId");
        System.out.println(message);
    }

    public boolean decreaseStock(){
        return true;
    }

    //消息退回  死循环  再开一个队列进行消费

//    @RabbitListener(queues = Constant.ORDER_QUEUE_NAME,
//            containerFactory = "simpleRabbitListenerContainerFactory")
//    public void stockListenserReturn(Message message, Channel channel) throws IOException {
//        System.out.println("处理退回操作");
//        //手动确认
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//    }

    @RabbitListener(queues = Constant.DEAD_QUEUE_NAME,
            containerFactory = "simpleRabbitListenerContainerFactory")
    public void stockListenserReturn1(Message message, Channel channel) throws IOException {
        System.out.println("死信队列处理消息");
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        System.out.println("死信消费者");
    }

//    @RabbitListener(queues = Constant.ORDER_QUEUE_NAME,
//            containerFactory = "simpleRabbitListenerContainerFactory")
//    public void stockListenserReturn2(Message message, Channel channel) throws IOException {
//        System.out.println("处理退回操作");
//        //手动确认
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//    }
}

