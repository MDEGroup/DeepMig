/**
 * 
 */
package com.llsfw.core.interceptor;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.llsfw.core.common.CookieUtil;

/**
 * @author kkll
 *
 */
public class ThemeChangeInterceptor implements HandlerInterceptor {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    private static final String THEME_NAME = "themeName";

    /**
     * 系统默认皮肤
     */
    private String defailtTheme;

    public void setDefailtTheme(String defailtTheme) {
        this.defailtTheme = defailtTheme;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {
        try {
            if (StringUtils.isEmpty(CookieUtil.getCookie(request, THEME_NAME))) {
                CookieUtil.setCookie(response, THEME_NAME, defailtTheme, "/", false);
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("RequestAttrThemeChangeInterceptor error:", e);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // Do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // Do nothing
    }

}
