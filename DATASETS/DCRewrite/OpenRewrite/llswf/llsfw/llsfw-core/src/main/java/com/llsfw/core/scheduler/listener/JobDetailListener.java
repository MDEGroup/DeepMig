/**
 * JobListener.java
 * Created at 2014年2月8日
 * Created by wangkang
 * Copyright (C) 2014 SHANGHAI VOLKSWAGEN, All rights reserved.
 */
package com.llsfw.core.scheduler.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import com.llsfw.core.exception.SystemException;
import com.llsfw.core.service.quartz.TriggerListenerService;

/**
 * <p>
 * ClassName: JobListener
 * </p>
 * <p>
 * Description: 作业监听器
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2014年2月8日
 * </p>
 */
public class JobDetailListener implements org.quartz.JobListener {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * <p>
     * Field tls: 触发器监听服务
     * </p>
     */
    @Autowired
    private TriggerListenerService tls;

    @Override
    public String getName() {
        return "jobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) { // 4
        try {
            tls.saveJobToBeExecuted(context);
        } catch (SystemException e) {
            LOG.error("jobToBeExecuted:", e);
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) { // 5
        try {
            tls.saveJobExecutionVetoed(context);
        } catch (SystemException e) {
            LOG.error("jobExecutionVetoed:", e);
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) { // 6
        try {
            tls.saveJobWasExecuted(context, jobException);
        } catch (SchedulerException e) {
            LOG.error("jobWasExecuted:", e);
        }
    }

}
