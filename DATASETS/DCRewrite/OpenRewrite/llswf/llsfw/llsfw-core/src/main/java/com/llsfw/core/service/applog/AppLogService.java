/**
 * AppLogService.java
 * Created at 2013-12-17
 * Created by wangkang
 * Copyright (C) llsfw.
 */
package com.llsfw.core.service.applog;

import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.llsfw.core.service.base.BaseService;
import com.llsfw.generator.mapper.standard.system.TtOnlineSessionHisMapper;
import com.llsfw.generator.mapper.standard.system.TtOnlineSessionMapper;
import com.llsfw.generator.mapper.standard.system.TtRequestResponseLogMapper;
import com.llsfw.generator.mapper.standard.system.TtScheduledLogMapper;
import com.llsfw.generator.model.standard.system.TtOnlineSession;
import com.llsfw.generator.model.standard.system.TtOnlineSessionCriteria;
import com.llsfw.generator.model.standard.system.TtOnlineSessionHis;
import com.llsfw.generator.model.standard.system.TtRequestResponseLog;
import com.llsfw.generator.model.standard.system.TtScheduledLog;

/**
 * <p>
 * ClassName: AppLogService
 * </p>
 * <p>
 * Description: 日志服务
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2013年12月17日
 * </p>
 */
@Service
public class AppLogService extends BaseService {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * <p>
     * Field tslm: 计划任务日志服务
     * </p>
     */
    @Autowired
    private TtScheduledLogMapper tslm;

    @Autowired
    private TtOnlineSessionMapper tosm;

    @Autowired
    private TtOnlineSessionHisMapper toshm;

    @Autowired
    private TtRequestResponseLogMapper trrlm;

    /**
     * 存储请求响应日志-该方法吃掉所有异常,不影响其他的应用-无事务-超时时间2秒
     * 
     * @param log
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, timeout = 2)
    public void saveRequestResponseLog(TtRequestResponseLog log) {
        try {
            trrlm.insertSelective(log);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * 清空OnlineSession表中的数据-该方法吃掉所有异常,不影响其他的应用-无事务-超时时间2秒
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, timeout = 2)
    public void clearOnlineSession() {
        try {
            tosm.deleteByExample(new TtOnlineSessionCriteria());
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * session创建记录-该方法吃掉所有异常,不影响其他的应用-无事务-超时时间2秒
     * 
     * @param tos
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, timeout = 2)
    public void sessionCreated(TtOnlineSession tos) {
        try {
            tosm.insertSelective(tos);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * session销毁记录-该方法吃掉所有异常,不影响其他的应用-无事务-超时时间2秒
     * 
     * @param tosh
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, timeout = 2)
    public void sessionDestroyed(TtOnlineSessionHis tosh) {
        try {
            tosm.deleteByPrimaryKey(tosh.getSessionId());
            toshm.insertSelective(tosh);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * <p>
     * Description: 保存记录(此方法吃掉所有异常,避免异常日志切换形成递归.)
     * </p>
     * 
     * @param msg
     *            信息
     */
    public void saveScheduledLog(String msg) {
        try {

            // 获得UUID
            String uuid = UUID.randomUUID().toString();

            // 设置
            TtScheduledLog tsl = new TtScheduledLog();
            tsl.setLogid(uuid);
            tsl.setCreateDate(new Date());
            tsl.setMsg(msg);

            // 保存
            this.tslm.insert(tsl);

        } catch (Exception e) {
            LOG.error("AppLogService.saveScheduledLog保存日志异常:", e);
        }
    }
}
