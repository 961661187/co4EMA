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
    public void insert(SamplePoint samplePoint) {
        samplePointMapper.insert(samplePoint);
    }

    @Override
    public List<SamplePoint> selectPartitions(Integer start, Integer count) {
        return samplePointMapper.getPartitions(start, count);
    }

    @Override
    public void clear() {
        samplePointMapper.delete(null);
    }

    @Override
    public List<SamplePoint> getAll() {
        return samplePointMapper.selectList(null);
    }
}
