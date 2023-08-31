/**
 * 
 */
package com.llsfw.core.interceptor;

import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.llsfw.core.common.CheckMobile;
import com.llsfw.core.common.HttpUtil;
import com.llsfw.core.common.UUID;
import com.llsfw.core.service.applog.AppLogService;
import com.llsfw.generator.model.standard.system.TtRequestResponseLog;

/**
 * 日志拦截器
 */
public class LogInterceptor implements HandlerInterceptor {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private AppLogService als;

    /**
     * 线程变量
     */
    public static final ThreadLocal<TtRequestResponseLog> requestResponseLog = new NamedThreadLocal<>(
            "TtRequestResponseLog");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        TtRequestResponseLog log = new TtRequestResponseLog();
        log.setBeginTime(new Date());
        requestResponseLog.set(log);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        TtRequestResponseLog log = requestResponseLog.get();
        if (log != null && modelAndView != null) {
            LOG.info("ViewName: " + modelAndView.getViewName());
            log.setResponseView(modelAndView.getViewName());
            requestResponseLog.set(log);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) // NOSONAR
            throws Exception {

        TtRequestResponseLog log = requestResponseLog.get();
        if (log != null) {
            // 其他信息
            log.setRequestId(UUID.getUUID(true));// 请求 ID
            log.setEndTime(new Date());// 结束时间
            log.setRequestTime(log.getEndTime().getTime() - log.getBeginTime().getTime()); // 请求耗时
            log.setLoginName((String) SecurityUtils.getSubject().getPrincipal());// 用户名
            if (SecurityUtils.getSubject().getSession() != null) {
                log.setSessionId(SecurityUtils.getSubject().getSession().getId().toString()); // 会话ID
            }
            // 请求细信息
            log.setScheme(request.getScheme());// 返回协议名称，http
            log.setServerName(request.getServerName());// 获得服务器名，本地IP
            log.setServerPort(new Long(request.getServerPort()));// 获取服务器端口号
            log.setRequestUri(request.getRequestURI()); // 请求路径
            log.setMethod(request.getMethod());// 请求方法 POST,GET,等
            log.setRequestParams(getRequestParamers(request));// 请求参数
            log.setMobilePat(CheckMobile.check(request.getHeader("user-agent")) ? (short) 1 : (short) 0);// 是否移动端访问
            log.setAuthType(request.getAuthType()); // 方法认证的名称
            log.setCharacterEncoding(request.getCharacterEncoding()); // 返回网页使用的编码，在网页的charset中的值
            log.setContentLength(new Long(request.getContentLength())); // 只用于POST请求，表示所发送数据的字节数
            log.setContentType(request.getContentType()); // 获取content-type
            log.setHeaderNames(getRequestHeaderNames(request));// 获得头信息
            log.setLocalAddr(request.getLocalAddr()); // 获取本地地址，根据访问方法不同而不同，为127.0.0.1或者公网ip
            log.setLocalName(request.getLocalName());// 获取本地IP的名称，如127.0.0.1就是localhost
            log.setLocalPort(new Long(request.getLocalPort()));// 获取端口号)
            log.setPathTranslated(request.getPathTranslated());// 映射到服务器实际路径之后的路径信息
            log.setProtocol(request.getProtocol());// 获取协议信息和版本号，即HTTP/1.1
            log.setRemoteAddr(HttpUtil.getRemoteAddr(request)); // 请求地址
            log.setRemoteHost(request.getRemoteHost());// 客户端的主机名
            log.setRemotePort(new Long(request.getRemotePort()));// 客户端的端口号
            log.setRemoteUser(request.getRemoteUser());// 客户端的用户名
            log.setRequestedSessionIdValid(request.isRequestedSessionIdValid() ? (short) 1 : (short) 0);// 检查SessionId是否是有效的
            log.setSecure(request.isSecure() ? (short) 1 : (short) 0);// 检查是否是安全通道
            // 响应信息
            log.setResponseHeaderNames(getResponseHeaderNames(response));// 响应头信息
            log.setResponseCharacterEnconding(response.getCharacterEncoding());// 获取编码类型
            log.setResponseContentType(response.getContentType());// 获取Content的MIME类型和编码
            log.setResponseStatus(new Long(response.getStatus()));// 获取response的状态码
            // JVM信息
            log.setJvmMaxMemory(Runtime.getRuntime().maxMemory() / 1024 / 1024);// 最大内存
            log.setJvmTotalMemory(Runtime.getRuntime().totalMemory() / 1024 / 1024); // 已分配内存
            log.setJvmFreeMemory(Runtime.getRuntime().freeMemory() / 1024 / 1024); // 已分配内存中的剩余空间
            log.setJvmUserMemory(log.getJvmMaxMemory() - log.getJvmTotalMemory() + log.getJvmFreeMemory()); // 最大可用内存
            // 数据格式化
            if (!StringUtils.isEmpty(log.getRequestParams()) && log.getRequestParams().length() > 3500) {
                log.setRequestParams(log.getRequestParams().substring(0, 3500));
            }
            if (!StringUtils.isEmpty(log.getHeaderNames()) && log.getHeaderNames().length() > 3500) {
                log.setHeaderNames(log.getHeaderNames().substring(0, 3500));
            }
            if (!StringUtils.isEmpty(log.getResponseHeaderNames()) && log.getResponseHeaderNames().length() > 3500) {
                log.setResponseHeaderNames(log.getResponseHeaderNames().substring(0, 3500));
            }
            if (!StringUtils.isEmpty(log.getExceptionStackTrack()) && log.getExceptionStackTrack().length() > 3500) {
                log.setExceptionStackTrack(log.getExceptionStackTrack().substring(0, 3500));
            }
            // 存储
            log.setCreateDate(new Date());
            als.saveRequestResponseLog(log);
        }
        // 清理
        requestResponseLog.remove();
    }

    /**
     * 获得头信息
     * 
     * @param request
     * @return
     */
    private static String getResponseHeaderNames(HttpServletResponse response) {
        StringBuilder params = new StringBuilder();
        Collection<String> names = response.getHeaderNames();
        params.append("?");
        if (!CollectionUtils.isEmpty(names)) {
            for (String name : names) {
                String values = response.getHeader(name);
                params.append(name + "=" + values + "&");
            }
        }
        return params.toString().substring(0, params.length() - 1);
    }

    /**
     * 获得头信息
     * 
     * @param request
     * @return
     */
    private static String getRequestHeaderNames(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> names = request.getHeaderNames();
        params.append("?");
        while (names.hasMoreElements()) {
            String paramName = names.nextElement();
            String values = request.getHeader(paramName);
            params.append(paramName + "=" + values + "&");
        }
        return params.toString().substring(0, params.length() - 1);
    }

    /**
     * 获得请求参数信息
     * 
     * @param request
     * @return
     */
    private static String getRequestParamers(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> names = request.getParameterNames();
        params.append("?");
        while (names.hasMoreElements()) {
            String paramName = names.nextElement();
            String[] values = request.getParameterValues(paramName);
            if (values.length == 1) {
                params.append(paramName + "=" + values[0] + "&");
            } else {
                for (int i = 0; i < values.length; i++) {
                    params.append(paramName + "[" + i + "]=" + values[i] + "&");
                }
            }
        }
        return params.toString().substring(0, params.length() - 1);
    }

}
