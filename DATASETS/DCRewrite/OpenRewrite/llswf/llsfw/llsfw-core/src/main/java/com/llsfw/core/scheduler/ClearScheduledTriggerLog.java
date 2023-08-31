/**
 * ClearScheduledTriggerLog.java
 * Created at 2014年3月7日
 * Created by wangkang
 * Copyright (C) 2014 SHANGHAI VOLKSWAGEN, All rights reserved.
 */
package com.llsfw.core.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.llsfw.core.service.job.JobService;

/**
 * <p>
 * ClassName: ClearScheduledTriggerLog
 * </p>
 * <p>
 * Description: 清理计划任务执行日志
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2014年3月7日
 * </p>
 */
@DisallowConcurrentExecution
public class ClearScheduledTriggerLog extends AbstractBaseJob {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobService js = this.getApplicationContext().getBean(JobService.class);
        js.clearScheduledTriggerLog();
        LOG.info("ClearScheduledTriggerLog executeInternal end");
    }

}
