/**
 * Constants.java
 * Created at 2013-11-29
 * Created by wangkang
 * Copyright (C) llsfw.
 */
package com.llsfw.core.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.llsfw.core.exception.SystemException;

/**
 * <p>
 * ClassName: Constants
 * </p>
 * <p>
 * Description: 常量
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2013年11月29日
 * </p>
 */
public class Constants {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * <p>
     * Field SCHEDULER_CLEAR_OP_PSWD: 清除计划任务数据操作密码
     * </p>
     */
    public static final String SCHEDULER_CLEAR_OP_PSWD = "48527136";

    /**
     * <p>
     * Field DEFAULT_JOB_MAP_DATA: 空jobMap数据
     * </p>
     */
    public static final String EMPTY_JOB_MAP_DATA = "[]";

    /**
     * <p>
     * Field CURRENT_LOGIN_NAME: 当前登陆的用户名
     * </p>
     */
    public static final String CURRENT_LOGIN_NAME = "CURRENT_LOGIN_NAME";

    /**
     * <p>
     * Field DEF_CHARACTER_SET_ENCODING: 默认字符集编码
     * </p>
     */
    public static final String DEF_CHARACTER_SET_ENCODING = "UTF-8";

    /**
     * <p>
     * Field SUCCESS:成功
     * </p>
     */
    public static final String SUCCESS = "1";
    /**
     * <p>
     * Field FAIL:失败
     * </p>
     */
    public static final String FAIL = "-1";

    /**
     * <p>
     * Field APP_LEVEL: 应用级别
     * </p>
     */
    public static final int APP_LEVEL = 1;

    /**
     * <p>
     * Field MENU_LEVEL: 目录级别
     * </p>
     */
    public static final int MENU_LEVEL = 2;

    /**
     * <p>
     * Field FUNCTION_LEVEL: 功能级别
     * </p>
     */
    public static final int FUNCTION_LEVEL = 3;

    /**
     * <p>
     * Field IO_BUFFERED: 缓冲区大小
     * </p>
     */
    public static final int IO_BUFFERED = 1024;

    /**
     * <p>
     * Field EXCEPTION_MSG_LENGTH: 记录异常的长度(oracle字段长度4000,预计在异常信息中不可能出现333个汉字,则定义为3000)
     * </p>
     */
    public static final int EXCEPTION_MSG_LENGTH = 3000;

    /**
     * 私有化构造函数
     */
    private Constants() {

    }

    /**
     * 
     * 读取返回的信息
     * 
     * @param in
     *            输入流
     * 
     * @return 数据
     * @throws SystemException
     */
    public static String getData(InputStream in) throws SystemException {
        String result = "";
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (Exception e) {
            throw new SystemException(e);
        } finally {
            if (result != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error("getData", e);
                }
            }
        }
        return sb.toString();
    }
}
