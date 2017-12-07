package com.job;

import com.service.ScheduleService;
import com.util.CommonUtils;
import com.util.MyJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@DisallowConcurrentExecution
public class TestJob extends MyJob {
    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);
    @Resource
    private ScheduleService scheduleService;

    @Override
    public void before(JobExecutionContext context) {
        logger.info("测试用例开始执行，时间:{}", CommonUtils.getCurrentTime());
    }

    @Override
    public void doWork(JobExecutionContext context) {
        scheduleService.findAll();
    }

    @Override
    public void after(JobExecutionContext context) {
        logger.info("测试用例结束执行，时间:{}", CommonUtils.getCurrentTime());
    }
}