package com.lasat.model.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lasat.model.sample.bean.SamplePoint;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SamplePointMapper extends BaseMapper<SamplePoint> {

    @Select("SELECT * FROM sample_point LIMIT #{start}, #{count}")
    List<SamplePoint> getPartitions(Integer start, Integer count);
}
