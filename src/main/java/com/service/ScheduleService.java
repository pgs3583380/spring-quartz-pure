package com.service;

import com.model.QrtzTriggers;
import com.vo.QrtzTriggersVo;

import java.util.List;

public interface ScheduleService {
    QrtzTriggersVo selectByPrimaryKey(QrtzTriggers key);

    List<QrtzTriggersVo> findAll();

    /**
     * 新增
     */
    void insert(String description, String cronExpression,String jobClassName);

    /**
     * 直接修改 只能修改运行的时间，参数、同异步等无法修改
     */
    void update(String jobGroup, String jobName, String description, String cronExpression);

    /**
     * 运行一次任务
     */
    void runOnce(String jobGroup, String jobName);

    /**
     * 暂停任务
     */
    void pauseJob(String jobGroup, String jobName);

    /**
     * 恢复任务
     */
    void resumeJob(String jobGroup, String jobName);

    /**
     * 删除一个任务
     */
    void deleteJob(String jobGroup, String jobName);
}