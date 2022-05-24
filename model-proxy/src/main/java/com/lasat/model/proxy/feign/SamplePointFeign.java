package com.lasat.model.proxy.feign;

import com.lasat.entity.Result;
import com.lasat.model.proxy.bean.SamplePoint;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "modelSample")
@RequestMapping("/samplePoint")
public interface SamplePointFeign {
    @PutMapping
    Result<String> insert(@RequestBody SamplePoint samplePoint);
    @GetMapping("/{startIndex}")
    Result<List<SamplePoint>> getByPartitions(@PathVariable Integer startIndex);
    @DeleteMapping
    Result<String> clear();
    @GetMapping
    Result<List<SamplePoint>> getAll();
}
