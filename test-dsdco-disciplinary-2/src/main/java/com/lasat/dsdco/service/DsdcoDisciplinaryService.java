package com.lasat.dsdco.service;

import com.lasat.dsdco.bean.DsdcoTarget;
import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.util.JsonUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * calculate service of disciplinary_2 of the optimization reducer from NASA
 */
@Service
public class DsdcoDisciplinaryService {

    // the message producer of system optimization
    private final ResourceBundle mqConfig = ResourceBundle.getBundle("mqConfig");
    private final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqConfig.getString("consumerGroupDisciplinary2Test"));
    private final DefaultMQProducer producer = new DefaultMQProducer(mqConfig.getString("producerGroupDisciplinary2Test"));

    private Long currentTaskId = null;
    private Integer currentIteratorCount = null;

    private final String CURRENT_DISCIPLINARY_NAME = "test-disciplinary-2";

    @Autowired
    private MatlabService4Disciplinary2 matlabService4Disciplinary2;

    /**
     * initialize the consumer of disciplinary
     *
     * @throws MQClientException the exception for mq service
     */
    @PostConstruct
    public void initializeConsumer() throws MQClientException {
        // initialize the consumer
        consumer.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        consumer.setInstanceName(mqConfig.getString("consumerInstanceNameDisciplinary2Test"));
        consumer.subscribe(mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("tagTest"));
        consumer.setMessageModel(MessageModel.BROADCASTING);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt messageExt : list) {
                    String targetJson = new String(messageExt.getBody());
                    System.out.println("Message received: " + targetJson);
                    DsdcoTarget target = getTargetFromJson(targetJson);
                    if (target != null) {
                        if (currentTaskId == null || currentIteratorCount == null) {
                            currentTaskId = target.getTaskId();
                            currentIteratorCount = target.getIteratorCount();
                        }

                        if (target.getIteratorCount().equals(-1)) {
                            System.out.println("Current task has been finished, variables will be reset");
                            resetCalculateVariables();
                        }

                        if (!target.getTaskId().equals(currentTaskId) || !target.getIteratorCount().equals(currentIteratorCount)) {
                            System.out.println("Duplicate message received: " + targetJson);
                            System.out.println("Current task id: " + currentTaskId);
                            System.out.println("Current iterator: " + currentIteratorCount);
                        } else {
                            DsdcoTarget closetPoint = getClosetPointByPGA(target);
                            sendResultToSystemCalculator(closetPoint);
                            System.out.println("The closet point is: " + closetPoint);
                            currentIteratorCount++;
                        }
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("Consumer of the disciplinary2 of the reducer is started");

        // initialize the producer
        producer.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        producer.setInstanceName(mqConfig.getString("producerInstanceNameDisciplinary2Test"));
        producer.start();
    }

    /**
     * get the dsdco target from mq message
     *
     * @param json the message in json type from message queue
     * @return dsdco target
     */
    public DsdcoTarget getTargetFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, DsdcoTarget.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * clear the variables
     */
    public void resetCalculateVariables() {
        currentIteratorCount = null;
        currentTaskId = null;
    }

    /**
     * get the closet point from given point by parallel genetic algorithm
     *
     * @param dsdcoTarget the given target
     * @return closet point
     */
    public DsdcoTarget getClosetPointByPGA(DsdcoTarget dsdcoTarget) {
        dsdcoTarget.setDisciplinaryName(CURRENT_DISCIPLINARY_NAME);
        Double[] targetVarOrigin = dsdcoTarget.getVariables();
        double[] targetVal = new double[targetVarOrigin.length];
        for (int i = 0; i < targetVal.length; i++) {
            targetVal[i] = targetVarOrigin[i];
        }
        OptimizationResult optimizationResult = matlabService4Disciplinary2.getClosetPoint(targetVal);
        return convertOptimizationResult2DsdcoTarget(optimizationResult);
    }

    /**
     * send the calculate result to system calculator
     *
     * @param dsdcoTarget the closet point got by PGA
     */
    public void sendResultToSystemCalculator(DsdcoTarget dsdcoTarget) {
        try {
            // set topic and tag of the message
            String topic = mqConfig.getString("disciplinary2SystemTopicTest");
            String tag = mqConfig.getString("tagTest");

            // convert the message to json
            ObjectMapper jsonMapper = new ObjectMapper();
            Map<String, Object> targetMap = JsonUtil.convertTargetToMap(dsdcoTarget);
            String targetJson = jsonMapper.writeValueAsString(targetMap);

            // create a message
            Message targetPointMsg = new Message(topic, tag, targetJson.getBytes());

            // send the message to every disciplinary calculator
            // which means that the message model of consumer should be BROADCAST
            producer.send(targetPointMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * convert the optimization result to dsdco target
     * @return the dsdco target converted from optimization result
     */
    private DsdcoTarget convertOptimizationResult2DsdcoTarget(OptimizationResult result) {
        Double[] variables = new Double[result.getVariables().length];
        for (int i = 0; i < variables.length; i++) {
            variables[i] = result.getVariables()[i];
        }
        return new DsdcoTarget(currentTaskId, CURRENT_DISCIPLINARY_NAME, currentIteratorCount, variables);
    }
}
