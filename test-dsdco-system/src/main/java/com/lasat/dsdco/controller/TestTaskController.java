package com.lasat.dsdco.controller;


import com.lasat.dsdco.service.DsdcoSystemService;
import com.lasat.entity.Result;
import com.lasat.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestTaskController {

    @Autowired
    private DsdcoSystemService dsdcoSystemService;

    @GetMapping("/commit/{taskId}")
    public Result<String> commitReducerTask(@PathVariable Long taskId) throws InterruptedException {
        dsdcoSystemService.closeTask();
        // prevent the effect of non-order messages
        Thread.sleep(50);
        dsdcoSystemService.setTaskId(taskId);
        dsdcoSystemService.startTask();
        return new Result<>(true, StatusCode.OK, "task commit succeed");
    }

    @GetMapping("/close")
    public Result<String> closeCurrentTask() {
        dsdcoSystemService.closeTask();
        return new Result<>(true, StatusCode.OK, "task reset succeed");
    }
}
