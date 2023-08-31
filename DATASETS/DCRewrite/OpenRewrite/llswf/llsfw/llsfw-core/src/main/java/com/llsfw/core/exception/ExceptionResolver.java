/**
 * 
 */
package com.llsfw.core.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Administrator
 *
 */
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * 错误页面
     */
    private String errorPage;

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object,
            Exception exception) {
        LOG.error("exception:", exception);
        String excpResult = ExceptionUtil.createStackTrackMessage(exception);
        ModelAndView modelAndView = new ModelAndView(errorPage);
        modelAndView.addObject("errorMsg", excpResult);
        return modelAndView;
    }

}
