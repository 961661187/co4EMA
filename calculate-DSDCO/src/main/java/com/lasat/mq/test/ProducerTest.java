package com.lasat.mq.test;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;

public class ProducerTest {

    public static void main(String[] args) throws MQClientException {
        //create a producer and set producer group name
        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        //the address of name server, this address should be get from properties file
        producer.setNamesrvAddr("47.94.88.239:9876");
        producer.setInstanceName("producer");
        //start the producer
        producer.start();
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(5000);  //send test message per 5 seconds
                //create a message, and set its topic, tag and body, which contains the data of message
                Message msg = new Message("Topic-test",// topic
                        "testTag",// tag
                        (new Date() + " RocketMQ test msg " + i).getBytes()// body
                );

                //send the message and get the message sending result
                SendResult sendResult = producer.send(msg);

                System.out.println(sendResult.getMsgId());
                System.out.println(sendResult.getMessageQueue());
                System.out.println(sendResult.getSendStatus());
                System.out.println(sendResult.getOffsetMsgId());
                System.out.println(sendResult.getQueueOffset());
                System.out.println();
                System.out.println("================================================");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }
}

