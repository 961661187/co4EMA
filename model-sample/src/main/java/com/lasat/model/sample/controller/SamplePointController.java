package com.lasat.model.sample.controller;

import com.lasat.entity.Result;
import com.lasat.entity.StatusCode;
import com.lasat.model.sample.bean.SamplePoint;
import com.lasat.model.sample.service.SamplePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/samplePoint")
public class SamplePointController {

    @Autowired
    private SamplePointService samplePointService;

    @PutMapping
    public Result<String> insert(@RequestBody SamplePoint samplePoint) {
        samplePointService.insert(samplePoint);
        return new Result<>(true, StatusCode.OK, "insert succeed");
    }

    @GetMapping("/{startIndex}/{count}")
    public Result<List<SamplePoint>> getByPartitions(@PathVariable Integer startIndex, @PathVariable Integer count) {
        List<SamplePoint> samplePoints = samplePointService.selectPartitions(startIndex, count);
        return new Result<>(true, StatusCode.OK, "get points succeed", samplePoints);
    }

    @DeleteMapping
    public Result<String> clear() {
        samplePointService.clear();
        return new Result<>(true, StatusCode.OK, "database cleared");
    }

    @GetMapping
    public Result<List<SamplePoint>> getAll() {
        List<SamplePoint> samplePoints = samplePointService.getAll();
        return new Result<>(true, StatusCode.OK, "get points succeed", samplePoints);
    }
}
