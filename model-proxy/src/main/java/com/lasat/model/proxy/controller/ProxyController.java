package com.lasat.model.proxy.controller;

import com.lasat.entity.Result;
import com.lasat.entity.StatusCode;
import com.lasat.model.proxy.bean.Proxy;
import com.lasat.model.proxy.bean.SamplePoint;
import com.lasat.model.proxy.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    private ProxyService proxyService;

    @GetMapping("/train")
    public Result<String> train() {
        proxyService.train();
        return new Result<>(true, StatusCode.OK, "neural network will be retrained");
    }

    @PostMapping("/tr")
    public Result<Double> getTr(@RequestBody SamplePoint samplePoint) {
        Double tr = proxyService.getTr(samplePoint);
        if (tr == null) return new Result<>(false, StatusCode.ERROR, "the network should be trained");
        else return new Result<>(true, StatusCode.OK, "get step response time succeed", tr);
    }

    @PostMapping("/trList")
    public Result<List<Double>> getTrList(@RequestBody List<SamplePoint> samplePointList) {
        List<Double> trList = proxyService.getTrList(samplePointList);
        if (trList == null) return new Result<>(false, StatusCode.ERROR, "the network should be trained");
        else return new Result<>(true, StatusCode.OK, "get step response time succeed", trList);
    }

    @GetMapping
    public Result<List<Proxy>> getAll() {
        List<Proxy> proxyList = proxyService.getAll();
        return new Result<>(true, StatusCode.OK, "get proxy succeed", proxyList);
    }

    @GetMapping("/{modelId}")
    public Result<List<Proxy>> getByModelId(@PathVariable Integer modelId) {
        List<Proxy> proxyList = proxyService.findByModelId(modelId);
        return new Result<>(true, StatusCode.OK, "get proxy succeed", proxyList);
    }
}
