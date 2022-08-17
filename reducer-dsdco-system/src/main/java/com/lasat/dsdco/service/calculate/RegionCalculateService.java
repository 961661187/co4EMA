package com.lasat.dsdco.service.calculate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lasat.dsdco.bean.DsdcoRegion;
import com.lasat.dsdco.bean.DsdcoTarget;
import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.service.calculate.point.GaBestPointService;
import com.lasat.dsdco.service.calculate.point.SqpBestPointService;
import com.lasat.dsdco.util.JsonUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class RegionCalculateService {

    // the message producer of system optimization
    private final ResourceBundle mqConfig = ResourceBundle.getBundle("mqConfig");
    private final DefaultMQProducer producer = new DefaultMQProducer(mqConfig.getString("producerGroup"));

    @Autowired
    SqpBestPointService bestPointService;

    /**
     * initialize the producer
     */
    @PostConstruct
    public void initProducer() {
        producer.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        producer.setInstanceName(mqConfig.getString("producerInstanceName"));
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
     * get the best optimization result in given region
     * @param dsdcoRegion region
     * @return min target function and its variables
     */
    public OptimizationResult getResultInRegion(DsdcoRegion dsdcoRegion) {
        int length = dsdcoRegion.getUpperLim().length;
        double[] upperLim = new double[length];
        double[] lowerLim = new double[length];

        for (int i = 0; i < length; i++) {
            upperLim[i] = dsdcoRegion.getUpperLim()[i];
            lowerLim[i] = dsdcoRegion.getLowerLim()[i];
        }

        OptimizationResult resultInRegion = bestPointService.getResultInRegion(upperLim, lowerLim);

        // parallel genetic algorithm can get the max score in given region
        // as a result, we set the calculate score as -mass, so the real min mass is -score
        return new OptimizationResult(-resultInRegion.getScore(), resultInRegion.getVariables());
    }

    /**
     * send the message to disciplinary calculator
     *
     * @param target the target point get by parallel genetic algorithm
     */
    public void sendTarget2Disciplinary(DsdcoTarget target) {
        try {
            // set topic and tag of the message
            String topic = mqConfig.getString("system2DisciplinaryTopic");
            String tag = mqConfig.getString("tag");

            // convert the message to json
            ObjectMapper jsonMapper = new ObjectMapper();
            Map<String, Object> targetMap = JsonUtil.convertTargetToMap(target);
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
}
