package com.lasat.fdsdco.controller;

import com.lasat.entity.Result;
import com.lasat.entity.StatusCode;
import com.lasat.fdsdco.service.FdsdcoSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestTaskController {

    @Autowired
    private FdsdcoSystemService fdsdcoSystemService;

    @GetMapping("/commit/{taskId}")
    public Result<String> commitReducerTask(@PathVariable Long taskId) throws InterruptedException {
        fdsdcoSystemService.closeTask();
        // prevent the effect of non-order messages
        Thread.sleep(50);
        fdsdcoSystemService.setTaskId(taskId);
        fdsdcoSystemService.startTask();
        return new Result<>(true, StatusCode.OK, "task commit succeed");
    }

    @GetMapping("/close")
    public Result<String> closeCurrentTask() {
        fdsdcoSystemService.closeTask();
        return new Result<>(true, StatusCode.OK, "task reset succeed");
    }
}
