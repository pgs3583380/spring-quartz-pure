package com.controller;

import com.model.QrtzTriggers;
import com.service.ScheduleService;
import com.util.CommonUtils;
import com.util.Constants;
import com.vo.QrtzTriggersVo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/schedule")
public class SchedulerController {
    private static Logger log = Logger.getLogger(SchedulerController.class);

    @Resource
    private ScheduleService scheduleService;

    @RequestMapping(value = "/selectByCondition", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> selectByCondition() {
        List<QrtzTriggersVo> triggersVos = scheduleService.findAll();
        Map<String, Object> map = new HashMap<>();
        map.put("aaData", triggersVos);
        return map;
    }

    /**
     * 查询一个任务
     */
    @RequestMapping(value = "selectJob", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> selectJob(String jobGroup, String jobName) {
        Map<String, Object> map = new HashMap<>();
        Integer flag = Constants.SEARCH_FAIL;
        if (CommonUtils.isNotEmpty(jobGroup) && CommonUtils.isNotEmpty(jobName)) {
            QrtzTriggersVo job = scheduleService.selectByPrimaryKey(new QrtzTriggers(jobName, jobGroup));
            if (job != null) {
                map.put("job", job);
                flag = Constants.SEARCH_SUCCESS;
            }
        }
        map.put("flag", flag);
        return map;
    }

    /**
     * 暂停任务
     */
    @RequestMapping(value = "pauseJob", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> pauseJob(String jobGroup, String jobName) {
        Map<String, Object> map = new HashMap<>();
        scheduleService.pauseJob(jobGroup, jobName);
        map.put("flag", 1);
        return map;
    }

    /**
     * 启用任务
     */
    @RequestMapping(value = "resumeJob", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> resumeJob(String jobGroup, String jobName) {
        Map<String, Object> map = new HashMap<>();
        scheduleService.resumeJob(jobGroup, jobName);
        map.put("flag", 1);
        return map;
    }

    /**
     * 启用一次
     */
    @RequestMapping(value = "runOnce", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> runOnce(String jobGroup, String jobName) {
        Map<String, Object> map = new HashMap<>();
        scheduleService.runOnce(jobGroup, jobName);
        map.put("flag", 1);
        return map;
    }

    @RequestMapping(value = "saveOrupdateJob", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveOrupdateJob(String jobGroup, String jobName,
                                               String description, String cronExpression,String jobClassName) {
        Map<String, Object> map = new HashMap<>();
        QrtzTriggersVo job = scheduleService.selectByPrimaryKey(new QrtzTriggers(jobName, jobGroup));
        if (null != job) {//更新Job
            scheduleService.update(jobGroup, jobName, description, cronExpression);
        } else {
            scheduleService.insert(description, cronExpression, jobClassName);//新增Job
        }
        map.put("flag", 1);
        return map;
    }

    @RequestMapping(value = "deleteJob", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteJob(String jobGroup, String jobName) {
        Map<String, Object> map = new HashMap<>();
        if (CommonUtils.isNotEmpty(jobGroup) && CommonUtils.isNotEmpty(jobName)) {
            scheduleService.deleteJob(jobGroup, jobName);
        }
        map.put("flag", 1);
        return map;
    }
}