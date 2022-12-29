package dsdco.service;

import com.alibaba.fastjson.JSON;
import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;
import com.lasat.dsdco.util.ConvertUtil;
import com.lasat.dsdco.util.ObjectMapperSingleton;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * calculate service of disciplinary_2 of the optimization reducer from NASA
 */
@Service
public class FdsdcoDisciplinaryService {
    private final String DISCIPLINARY_NAME = "test-disciplinary-2";

    // the message producer of system optimization
    private final ResourceBundle mqConfig = ResourceBundle.getBundle("mqConfig4testDisciplinary2");
    private final DefaultMQPushConsumer consumer4point = new DefaultMQPushConsumer(mqConfig.getString("consumerGroupDisciplinary2Test4point"));
    private final DefaultMQPushConsumer consumer4space = new DefaultMQPushConsumer(mqConfig.getString("consumerGroupDisciplinary2Test4space"));

    private int pointCalculationCount = 0;
    private int spaceCalculationCount = 0;
    private int messageCount = 0;

    @Autowired
    private MatlabService4Disciplinary2 matlabService4Disciplinary2;
    @Autowired
    private MqService4Disciplinary2 mqService4Disciplinary2;

    /**
     * initialize the consumer of disciplinary
     *
     * @throws MQClientException the exception for mq service
     */
    @PostConstruct
    public void initializeConsumer4point() throws MQClientException {
        // initialize the consumer
        consumer4point.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        consumer4point.setInstanceName(mqConfig.getString("consumerInstanceNameDisciplinary2Test4point"));
        consumer4point.subscribe(mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("pointTagTest"));

        consumer4point.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for (MessageExt messageExt : list) {
                messageCount++;
                String targetJson = new String(messageExt.getBody());
                System.out.println("Point msg received: " + targetJson);
                Point target = null;
                try {
                    target = ObjectMapperSingleton.getInstance().getObjectMapper().readValue(targetJson, Point.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (target != null) {
                    if (target.getIteratorCount().equals(-1)) {
                        System.out.println("Current task has been finished");
                        System.out.println("point calculation Count: " + pointCalculationCount);
                        System.out.println("space calculation Count: " + spaceCalculationCount);
                        System.out.println("message Count: " + messageCount);
                        pointCalculationCount = 0;
                        spaceCalculationCount = 0;
                        break;
                    }
                    pointCalculationCount++;
                    Point closetPoint = getClosetPointBySQP(target, target.getTaskId(), target.getIteratorCount());
                    String pointStr = JSON.toJSONString(closetPoint);
                    mqService4Disciplinary2.sendMessage(pointStr, mqConfig.getString("disciplinary2SystemTopicTest"), mqConfig.getString("pointTagTest"));
                    System.out.println("The closet point is: " + closetPoint);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer4point.start();
    }

    @PostConstruct
    public void initializeConsumer4space() throws MQClientException {
        // initialize the consumer
        consumer4space.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        consumer4space.setInstanceName(mqConfig.getString("consumerInstanceNameDisciplinary2Test4space"));
        consumer4space.subscribe(mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("spaceTagTest"));

        consumer4space.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for (MessageExt messageExt : list) {
                messageCount++;
                String spaceJson = new String(messageExt.getBody());
                System.out.println("Space msg received: " + spaceJson);
                Space space = null;
                try {
                    space = ObjectMapperSingleton.getInstance().getObjectMapper().readValue(spaceJson, Space.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (space != null) {
                    spaceCalculationCount++;
                    space.setValid(matlabService4Disciplinary2.isSpaceValid(space));
                    space.setDisciplinaryName(DISCIPLINARY_NAME);
                    String spaceStr = JSON.toJSONString(space);
                    mqService4Disciplinary2.sendMessage(spaceStr, mqConfig.getString("disciplinary2SystemTopicTest"), mqConfig.getString("spaceTagTest"));
                    System.out.println("Space msg sent: " + spaceStr);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer4space.start();
        System.out.println("Consumer for space started");
    }

    /**
     * get the closet point from given point by parallel genetic algorithm
     *
     * @param point the given target
     * @return closet point
     */
    public Point getClosetPointBySQP(Point point, Long taskId, Integer iterCount) {
        Double[] targetVarOrigin = point.getVariables();
        double[] targetVal = new double[targetVarOrigin.length];
        for (int i = 0; i < targetVal.length; i++) {
            targetVal[i] = targetVarOrigin[i];
        }
        OptimizationResult optimizationResult = matlabService4Disciplinary2.getClosetPoint(targetVal);
        return ConvertUtil.optimizationResult2Point(optimizationResult, DISCIPLINARY_NAME, taskId, iterCount);
    }
}
