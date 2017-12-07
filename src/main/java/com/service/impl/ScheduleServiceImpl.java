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
    public QrtzTriggers selectByPrimaryKey(QrtzTriggers key) {
        return triggersMapper.selectByPrimaryKey(key);
    }

    @Override
    public List<QrtzTriggersVo> findAll() {
        return triggersMapper.findAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insert(String Descriptio, String cronExpression, String jobClassName) {
        try {
            Class<? extends MyJob> clazz = (Class<? extends MyJob>) Class.forName(jobClassName);
            String[] classs = jobClassName.split("\\.");
            String name = classs[classs.length - 1] + CommonUtils.timeFormat(new Date(), Constants.simplifyTimestampPattern);
            String group = Constants.DEFAULT_GROUP_NAME;
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(name, group).build();
            CronScheduleBuilder cornSB = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(cornSB).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.info("创建定时任务失败");
        }
    }

    @Override
    public void update(String groupName, String jobName, String Description, String cronExpression) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, groupName);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            if (!triggerState.name().equalsIgnoreCase("PAUSED")) {
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (SchedulerException e) {
            logger.error("更新定时任务失败", e);
        }
    }

    @Override
    public void runOnce(String groupName, String jobName) {
        JobKey jobKey = new JobKey(jobName, groupName);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("运行一次失败");
        }
    }

    @Override
    public void pauseJob(String groupName, String jobName) {
        JobKey jobKey = new JobKey(jobName, groupName);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("暂停任务失败");
        }
    }

    @Override
    public void resumeJob(String groupName, String jobName) {
        JobKey jobKey = new JobKey(jobName, groupName);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("恢复任务失败");
        }
    }

    @Override
    public void deleteJob(String groupName, String jobName) {
        JobKey jobKey = new JobKey(jobName, groupName);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("删除任务失败");
        }
    }
}