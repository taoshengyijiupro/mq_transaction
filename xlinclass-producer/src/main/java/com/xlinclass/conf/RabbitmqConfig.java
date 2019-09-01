package com.xlinclass.conf;


import com.xlinclass.consumer.Constant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("192.168.1.130",5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("taoshengyijiurabbitmq");
        //是否开启消息确认机制  生产者
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    /**
     * 因为要设置回调类，所以应是prototype类型，如果是singleton类型，则回调类为最后一次设置
     * @return
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());
        //exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(){
        TomcatServletWebServerFactory tomcatServletWebServerFactory =
                new TomcatServletWebServerFactory(7000);
        return tomcatServletWebServerFactory;
    }

    @Bean
    public Queue queue() {
        //durable  是否持久化
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange",Constant.DEAD_CHANGE_NAME);
        map.put("x-dead-letter-routing-key",Constant.DEAD_ROUTINFKEY_NAME);
        return new Queue(Constant.ORDER_QUEUE_NAME, false,false,false,map);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue(Constant.DEAD_QUEUE_NAME);
    }

    @Bean
    public DirectExchange exchange() {
        Map<String,Object> map = new HashMap<>();
        map.put("alternate-exchange",Constant.ORDER_ALTERNATE_CHANGE_NAME);
        return new DirectExchange(Constant.ORDER_CHANGE_NAME,false,false,map);
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(Constant.DEAD_CHANGE_NAME);
    }

    /**
     * 备用交换机
     * @return
     */
    @Bean
    public FanoutExchange alternateExchange() {
        return new FanoutExchange(Constant.ORDER_ALTERNATE_CHANGE_NAME);
    }


    /**
     * 死信交换机直接绑定队列
     * @return
     */
    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(Constant.DEAD_ROUTINFKEY_NAME);
    }


    /**
     * 备用交换机直接绑定队列
     * @param queue
     * @return
     */
    @Bean
    public Binding alternateBinding(Queue queue) {
        return BindingBuilder.bind(queue).to(alternateExchange());
    }


    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(Constant.ORDER_ROUTINFKEY_NAME);
    }
}
