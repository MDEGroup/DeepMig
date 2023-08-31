package com.llsfw.core.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GlobalListener implements ServletContextListener, ServletContextAttributeListener, HttpSessionListener,
        HttpSessionAttributeListener, ServletRequestListener, ServletRequestAttributeListener {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * 监听器操作对象
     */
    private IGlobalListenerOp igo;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.igo = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean("globalListener", IGlobalListenerOp.class);
        LOG.info("GlobalListenerOp初始化状态:" + this.igo);
        if (igo != null) {
            this.igo.contextInitialized(sce);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (igo != null) {
            this.igo.contextDestroyed(sce);
        }
    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        if (igo != null) {
            this.igo.attributeAdded(srae);
        }
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        if (igo != null) {
            this.igo.attributeRemoved(srae);
        }
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        if (igo != null) {
            this.igo.attributeReplaced(srae);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        if (igo != null) {
            this.igo.requestDestroyed(sre);
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        if (igo != null) {
            this.igo.requestInitialized(sre);
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        if (igo != null) {
            this.igo.attributeAdded(se);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        if (igo != null) {
            this.igo.attributeRemoved(se);
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        if (igo != null) {
            this.igo.attributeReplaced(se);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (igo != null) {
            this.igo.sessionCreated(se);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        if (igo != null) {
            this.igo.sessionDestroyed(se);
        }
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent scae) {
        if (igo != null) {
            this.igo.attributeAdded(scae);
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scae) {
        if (igo != null) {
            this.igo.attributeRemoved(scae);
        }
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scae) {
        if (igo != null) {
            this.igo.attributeReplaced(scae);
        }
    }
}
