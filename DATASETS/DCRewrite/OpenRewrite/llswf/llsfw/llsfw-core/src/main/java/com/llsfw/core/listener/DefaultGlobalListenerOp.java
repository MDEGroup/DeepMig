package com.llsfw.core.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultGlobalListenerOp implements IGlobalListenerOp {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        LOG.trace(srae.toString());
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        LOG.trace(srae.toString());
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        LOG.trace(srae.toString());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        LOG.trace(sre.toString());
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        LOG.trace(sre.toString());
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        LOG.trace(se.toString());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        LOG.trace(se.toString());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        LOG.trace(se.toString());
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.trace(se.toString());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.trace(se.toString());
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent scae) {
        LOG.trace(scae.toString());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scae) {
        LOG.trace(scae.toString());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scae) {
        LOG.trace(scae.toString());
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.trace(sce.toString());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.trace(sce.toString());
    }

}
