package com.lasat.fdsdco.service;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ResourceBundle;

/**
 * Send msg to mq
 * @author MactavishCui
 */
@Service
public class MqService4System {
    // the message producer of system optimization
    private final ResourceBundle mqConfig = ResourceBundle.getBundle("mqConfig4testSystem");
    private final DefaultMQProducer producer = new DefaultMQProducer(mqConfig.getString("producerGroupTest"));

    /**
     * initialize the producer
     */
    @PostConstruct
    public void initProducer() {
        producer.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        producer.setInstanceName(mqConfig.getString("producerInstanceNameTest"));
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void closeProducer() {
        producer.shutdown();
    }

    /**
     * send messages
     * @param msg JSON message
     * @param topic the topic of message
     * @param tag the tag
     */
    public void sendMessage(String msg, String topic, String tag) {
        try {
            // create a message
            Message targetPointMsg = new Message(topic, tag, msg.getBytes());
            // send the message to disciplinary calculators
            // which means that the message model of consumer should be BROADCAST
            producer.send(targetPointMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
