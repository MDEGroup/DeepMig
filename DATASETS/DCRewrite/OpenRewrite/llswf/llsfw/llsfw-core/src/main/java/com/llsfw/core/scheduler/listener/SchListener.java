/**
 * SchedulerListener.java
 * Created at 2014年1月24日
 * Created by wangkang
 * Copyright (C) 2014 SHANGHAI VOLKSWAGEN, All rights reserved.
 */
package com.llsfw.core.scheduler.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.llsfw.core.common.Constants;
import com.llsfw.core.exception.ExceptionUtil;
import com.llsfw.core.service.applog.AppLogService;

/**
 * <p>
 * ClassName: SchedulerListener
 * </p>
 * <p>
 * Description: 计划任务监听器
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2014年1月24日
 * </p>
 */
public class SchListener implements org.quartz.SchedulerListener {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * <p>
     * Field as: 日志服务
     * </p>
     */
    @Autowired
    private AppLogService als;

    /**
     * 返回操作人
     * 
     * @return
     */
    private static String getOptionBy() {
        try {
            return "(操作人:" + (SecurityUtils.getSubject().getPrincipal() == null ? "server"
                    : SecurityUtils.getSubject().getPrincipal()) + ")";
        } catch (Exception e) { // NOSONAR
            LOG.info(e.getMessage());
            return "";
        }
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        als.saveScheduledLog(
                "(triggerPaused)" + triggerKey.getName() + "/" + triggerKey.getGroup() + "被暂停了" + getOptionBy());
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        als.saveScheduledLog( // NOSONAR
                "(triggerResumed)" + triggerKey.getName() + "/" + triggerKey.getGroup() + "被恢复了" + getOptionBy());
    }

    @Override
    public void jobScheduled(Trigger trigger) {
        als.saveScheduledLog(
                "(jobScheduled)" + "作业" + trigger.getJobKey().getName() + "/" + trigger.getJobKey().getGroup() + "被触发器"
                        + trigger.getKey().getName() + "/" + trigger.getKey().getGroup() + "触发了" + getOptionBy());
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        als.saveScheduledLog(
                "(jobUnscheduled)" + triggerKey.getName() + "/" + triggerKey.getGroup() + "被移除了" + getOptionBy());
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        als.saveScheduledLog("(triggerFinalized)" + "作业" + trigger.getJobKey().getName() + "/"
                + trigger.getJobKey().getGroup() + ",触发器" + trigger.getKey().getName() + "/"
                + trigger.getKey().getGroup() + "已经执行完成,后续将不会继续触发" + getOptionBy());
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        if (StringUtils.isEmpty(triggerGroup)) {
            als.saveScheduledLog("(triggersPaused)" + "触发器组全部被暂停了" + getOptionBy());
        } else {
            als.saveScheduledLog("(triggersPaused)" + "触发器组" + triggerGroup + "被暂停了" + getOptionBy());
        }

    }

    @Override
    public void triggersResumed(String triggerGroup) {
        if (StringUtils.isEmpty(triggerGroup)) {
            als.saveScheduledLog("(triggersResumed)" + "触发器组全部被恢复了" + getOptionBy());
        } else {
            als.saveScheduledLog("(triggersResumed)" + "触发器组" + triggerGroup + "被恢复了" + getOptionBy());
        }
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        als.saveScheduledLog("(jobAdded)" + "作业" + jobDetail.getKey().getName() + "/" + jobDetail.getKey().getGroup()
                + "被添加了" + getOptionBy());
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        als.saveScheduledLog(
                "(jobDeleted)" + "作业" + jobKey.getName() + "/" + jobKey.getGroup() + "被删除了" + getOptionBy());
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        als.saveScheduledLog(
                "(jobPaused)" + "作业" + jobKey.getName() + "/" + jobKey.getGroup() + "被暂停了" + getOptionBy());
    }

    @Override
    public void jobsPaused(String jobGroup) {
        if (StringUtils.isEmpty(jobGroup)) {
            als.saveScheduledLog("(jobsPaused)" + "作业全部被暂停了" + getOptionBy());
        } else {
            als.saveScheduledLog("(jobsPaused)" + "作业组" + jobGroup + "被暂停了" + getOptionBy());
        }

    }

    @Override
    public void jobResumed(JobKey jobKey) {
        als.saveScheduledLog(
                "(jobResumed)" + "作业" + jobKey.getName() + "/" + jobKey.getGroup() + "被恢复了" + getOptionBy());
    }

    @Override
    public void jobsResumed(String jobGroup) {
        if (StringUtils.isEmpty(jobGroup)) {
            als.saveScheduledLog("(jobsResumed)" + "作业全部被恢复了" + getOptionBy());
        } else {
            als.saveScheduledLog("(jobsResumed)" + "作业组" + jobGroup + "被恢复了" + getOptionBy());
        }
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        // 获得异常详细信息
        String exceptionDetail = ExceptionUtil.createStackTrackMessage(cause);
        if (exceptionDetail.length() > Constants.EXCEPTION_MSG_LENGTH) {
            exceptionDetail = exceptionDetail.substring(0, Constants.EXCEPTION_MSG_LENGTH);
        }
        als.saveScheduledLog("(schedulerError)" + "计划任务出错:" + msg + "\n" + exceptionDetail);
    }

    @Override
    public void schedulerInStandbyMode() {
        als.saveScheduledLog("(schedulerInStandbyMode)" + "计划任务为待机状态" + getOptionBy());
    }

    @Override
    public void schedulerStarted() {
        als.saveScheduledLog("(schedulerStarted)" + "计划任务已经启动" + getOptionBy());
    }

    @Override
    public void schedulerStarting() {
        als.saveScheduledLog("(schedulerStarting)" + "计划任务正在启动中" + getOptionBy());
    }

    @Override
    public void schedulerShutdown() {
        als.saveScheduledLog("(schedulerShutdown)" + "计划任务已关闭" + getOptionBy());
    }

    @Override
    public void schedulerShuttingdown() {
        als.saveScheduledLog("(schedulerShuttingdown)" + "计划任务正在关闭中" + getOptionBy());
    }

    @Override
    public void schedulingDataCleared() {
        als.saveScheduledLog("(schedulingDataCleared)" + "计划任务数据被清除" + getOptionBy());
    }

}
