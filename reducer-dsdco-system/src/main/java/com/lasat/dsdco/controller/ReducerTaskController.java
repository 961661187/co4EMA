package com.lasat.dsdco.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.lasat.dsdco.service.DsdcoSystemService;
import com.lasat.entity.Result;
import com.lasat.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reducer")
public class ReducerTaskController {

    @Autowired
    private DsdcoSystemService dsdcoSystemService;

    // TODO The fucking debug has not been finished
    @GetMapping("/commit/{taskId}")
    public Result<String> commitReducerTask(@PathVariable Long taskId) throws InterruptedException, JsonProcessingException {
        dsdcoSystemService.closeTask();
        dsdcoSystemService.setTaskId(taskId);
        Thread.sleep(500);
        dsdcoSystemService.startTask();
        return new Result<>(true, StatusCode.OK, "task commit succeed");
    }

    @GetMapping("/close")
    public Result<String> closeCurrentTask() {
        dsdcoSystemService.closeTask();
        return new Result<>(true, StatusCode.OK, "task reset succeed");
    }
}
