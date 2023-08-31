/**
 * 
 */
package com.llsfw.core.exception;

/**
 * @author Administrator
 *
 */
public class ExceptionUtil {

    private ExceptionUtil() {

    }

    /**
     * <p>
     * 将异常堆栈信息以字符串的格式返回
     * </p>
     * 
     * @param e
     *            异常对象
     * @return
     */
    public static String createStackTrackMessage(Exception e) {
        StringBuilder messsage = new StringBuilder();
        if (e != null) {
            messsage.append(e.getClass()).append(" : ").append(e.getMessage()).append("<br />");
            StackTraceElement[] elements = e.getStackTrace();
            for (StackTraceElement stackTraceElement : elements) {
                messsage.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(stackTraceElement.toString()).append("<br />");
            }
        }
        return messsage.toString();
    }

}
