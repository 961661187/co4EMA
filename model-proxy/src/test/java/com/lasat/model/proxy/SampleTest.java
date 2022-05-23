package com.lasat.model.proxy;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.lasat.model.proxy.bean.SamplePoint;
import com.lasat.model.proxy.mapper.SamplePointMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SampleTest {

    @Autowired
    private SamplePointMapper samplePointMapper;

    @Test
    public void testMapper() {
        List<SamplePoint> samplePoints = samplePointMapper.selectList(null);
        System.out.println(samplePoints.size());
    }
}
