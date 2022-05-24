package com.lasat.model.proxy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lasat.model.proxy.bean.Proxy;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProxyMapper extends BaseMapper<Proxy> {

    @Select("SELECT * FROM proxy WHERE model_id = #{model_id}")
    List<Proxy> findByModelId(Integer model_id);
}
