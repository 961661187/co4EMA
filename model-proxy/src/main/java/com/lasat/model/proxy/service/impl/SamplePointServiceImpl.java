package com.lasat.model.proxy.service.impl;

import com.lasat.model.proxy.bean.SamplePoint;
import com.lasat.model.proxy.mapper.SamplePointMapper;
import com.lasat.model.proxy.service.SamplePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SamplePointServiceImpl implements SamplePointService {

    @Autowired
    private SamplePointMapper samplePointMapper;

    @Override
    public SamplePoint getById(Integer id) {
        return samplePointMapper.selectById(id);
    }

    @Override
    public Boolean insert(SamplePoint samplePoint) {
        try {
            samplePointMapper.insert(samplePoint);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
