package com.xlinclass.controller;


import com.xlinclass.consumer.Constant;
import com.xlinclass.listenser.RabbitSendMassage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    @Autowired
    private RabbitSendMassage rabbitSendMassage;

    @GetMapping(value = "/producer/send")
    public Object producer(String msg){
        rabbitSendMassage.sendMassgee(Constant.ORDER_CHANGE_NAME,Constant.ORDER_ROUTINFKEY_NAME,msg);
        return "sucess";
    }
}
