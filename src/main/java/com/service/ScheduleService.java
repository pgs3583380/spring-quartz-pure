package com.service;

import com.model.QrtzTriggers;
import com.vo.QrtzTriggersVo;

import java.util.List;

public interface ScheduleService {
    QrtzTriggers selectByPrimaryKey(QrtzTriggers key);

    List<QrtzTriggersVo> findAll();

    /**
     * 新增
     */
    void insert(String Descriptio, String cronExpression,String jobClassName);

    /**
     * 直接修改 只能修改运行的时间，参数、同异步等无法修改
     */
    void update(String groupName, String JobName, String Descriptio, String cronExpression);

    /**
     * 运行一次任务
     */
    void runOnce(String groupName, String jobName);

    /**
     * 暂停任务
     */
    void pauseJob(String groupName, String jobName);

    /**
     * 恢复任务
     */
    void resumeJob(String groupName, String jobName);

    /**
     * 删除一个任务
     */
    void deleteJob(String groupName, String jobName);
}