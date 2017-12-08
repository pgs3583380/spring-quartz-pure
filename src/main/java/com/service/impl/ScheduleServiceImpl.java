package com.service.impl;

import com.dao.QrtzTriggersMapper;
import com.model.QrtzTriggers;
import com.service.ScheduleService;
import com.util.CommonUtils;
import com.util.Constants;
import com.util.MyJob;
import com.vo.QrtzTriggersVo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    @Resource
    private QrtzTriggersMapper triggersMapper;
    @Resource
    private Scheduler scheduler;

    @Override
    public QrtzTriggersVo selectByPrimaryKey(QrtzTriggers key) {
        return triggersMapper.selectByPrimaryKey(key);
    }

    @Override
    public List<QrtzTriggersVo> findAll() {
        return triggersMapper.findAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insert(String description, String cronExpression, String jobClassName) {
        try {
            Class<? extends MyJob> clazz = (Class<? extends MyJob>) Class.forName(jobClassName);
            String[] classs = jobClassName.split("\\.");
            String name = classs[classs.length - 1] + CommonUtils.timeFormat(new Date(), Constants.simplifyTimestampPattern);
            String group = Constants.DEFAULT_GROUP_NAME;
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(name, group).build();
            //表达式调度构建器,增加执行特性 missfire之后只执行一次
            CronScheduleBuilder cornSB = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(cornSB).withDescription(description).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.info("创建定时任务失败");
        }
    }

    @Override
    public void update(String jobGroup, String jobName, String description, String cronExpression) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed();
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).withDescription(description).build();
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            // 忽略状态为PAUSED的任务，解决集群环境中在其他机器设置定时任务为PAUSED状态后，集群环境启动另一台主机时定时任务全被唤醒的bug
            if (!triggerState.name().equalsIgnoreCase("PAUSED")) {
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (SchedulerException e) {
            logger.error("更新定时任务失败", e);
        }
    }

    @Override
    public void runOnce(String jobGroup, String jobName) {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("运行一次失败");
        }
    }

    @Override
    public void pauseJob(String jobGroup, String jobName) {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("暂停任务失败");
        }
    }

    @Override
    public void resumeJob(String jobGroup, String jobName) {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("恢复任务失败");
        }
    }

    @Override
    public void deleteJob(String jobGroup, String jobName) {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("删除任务失败");
        }
    }
}