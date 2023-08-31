package com.llsfw.core.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;

public interface IGlobalListenerOp {

    void attributeAdded(ServletRequestAttributeEvent srae);

    void attributeRemoved(ServletRequestAttributeEvent srae);

    void attributeReplaced(ServletRequestAttributeEvent srae);

    void requestDestroyed(ServletRequestEvent sre);

    void requestInitialized(ServletRequestEvent sre);

    void attributeAdded(HttpSessionBindingEvent se);

    void attributeRemoved(HttpSessionBindingEvent se);

    void attributeReplaced(HttpSessionBindingEvent se);

    void sessionCreated(HttpSessionEvent se);

    void sessionDestroyed(HttpSessionEvent se);

    void attributeAdded(ServletContextAttributeEvent scae);

    void attributeRemoved(ServletContextAttributeEvent scae);

    void attributeReplaced(ServletContextAttributeEvent scae);

    void contextInitialized(ServletContextEvent sce);

    void contextDestroyed(ServletContextEvent sce);

}
