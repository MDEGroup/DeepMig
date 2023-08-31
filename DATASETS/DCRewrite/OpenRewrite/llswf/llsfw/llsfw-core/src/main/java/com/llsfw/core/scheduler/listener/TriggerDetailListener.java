/**
 * TriggerListener.java
 * Created at 2014年2月8日
 * Created by wangkang
 * Copyright (C) 2014 SHANGHAI VOLKSWAGEN, All rights reserved.
 */
package com.llsfw.core.scheduler.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.springframework.beans.factory.annotation.Autowired;

import com.llsfw.core.exception.SystemException;
import com.llsfw.core.service.quartz.TriggerListenerService;

/**
 * <p>
 * ClassName: TriggerListener
 * </p>
 * <p>
 * Description: 全局触发器监听
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2014年2月8日
 * </p>
 */
public class TriggerDetailListener implements org.quartz.TriggerListener {

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
        return "triggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) { // 2
        try {
            tls.saveTriggerFired(context);
        } catch (SystemException e) {
            LOG.error("triggerFired error:", e);
        }
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) { // 3
        boolean vetoed = false;
        try {
            vetoed = tls.saveVetoJobExecution(context);
        } catch (SystemException e) {
            LOG.error("vetoJobExecution error:", e);
        }
        return vetoed;
    }

    @Override
    public void triggerMisfired(Trigger trigger) { // 1
        tls.saveTriggerMisfired(trigger);
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) { // 7
        try {
            tls.saveTriggerComplete(context, triggerInstructionCode);
        } catch (SystemException e) {
            LOG.error("triggerComplete error:", e);
        }
    }
}
