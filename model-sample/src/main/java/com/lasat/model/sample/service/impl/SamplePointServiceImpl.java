package com.lasat.model.sample.service.impl;

import com.lasat.model.sample.bean.SamplePoint;
import com.lasat.model.sample.mapper.SamplePointMapper;
import com.lasat.model.sample.service.SamplePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SamplePointServiceImpl implements SamplePointService {

    @Autowired
    private SamplePointMapper samplePointMapper;

    @Override
    public SamplePoint getById(Integer id) {
        return samplePointMapper.selectById(id);
    }

    @Override
    public void insert(SamplePoint samplePoint) {
        samplePointMapper.insert(samplePoint);
    }

    @Override
    public List<SamplePoint> selectPartitions(Integer start) {
        return samplePointMapper.getPartitions(start);
    }
}
