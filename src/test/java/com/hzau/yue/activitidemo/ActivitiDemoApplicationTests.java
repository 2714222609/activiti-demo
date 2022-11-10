package com.hzau.yue.activitidemo;

import com.hzau.yue.activitidemo.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ActivitiDemoApplicationTests {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 部署流程
     */
    @Test
    public void findProcess() {
        securityUtil.logInAs("user_00");
        Page<ProcessDefinition> definitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
        log.info("> 可用的流程定义总数： {}", definitionPage.getTotalItems());
        for (ProcessDefinition processDefinition : definitionPage.getContent()) {
            log.info("> 流程定义内容：{}", processDefinition);
        }
    }


    /**
     * 启动一个流程实例
     */
    @Test
    public void startProcess() {
        securityUtil.logInAs("user_00");
        ProcessInstance processInstance = processRuntime.
                start(ProcessPayloadBuilder.
                        start().withProcessDefinitionKey("demo").
                        build());
        log.info("> 流程实例的内容，{}", processInstance);
    }

    /**
     * 执行任务
     */
    @Test
    public void doTask() {
        securityUtil.logInAs("user_00");
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage != null && taskPage.getTotalItems() > 0) {
            for (Task task : taskPage.getContent()) {
                // 拾取任务
                taskRuntime.claim(TaskPayloadBuilder
                        .claim()
                        .withTaskId(task.getId()).build());
                log.info("> 任务内容, {}", task);
                // 完成任务
                taskRuntime.complete(TaskPayloadBuilder
                        .complete()
                        .withTaskId(task.getId())
                        .build());
            }
        }

    }

}
