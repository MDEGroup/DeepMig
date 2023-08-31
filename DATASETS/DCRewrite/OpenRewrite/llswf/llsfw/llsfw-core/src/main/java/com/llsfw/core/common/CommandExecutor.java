/**
 * CommandExecutor.java
 * Created at 2015年3月13日
 * Created by wangkang
 * Copyright (C) 2015 SHANGHAI VOLKSWAGEN, All rights reserved.
 */
package com.llsfw.core.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.llsfw.core.exception.SystemException;

/**
 * <p>
 * ClassName: CommandExecutor
 * </p>
 * <p>
 * Description: 执行操作系统命令
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2015年3月13日
 * </p>
 */
public class CommandExecutor {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * <p>
     * Description: 执行操作系统命令
     * </p>
     * 
     * @param command
     *            命令
     * @throws SystemException
     */
    public void runCommand(String command) throws SystemException {
        Process child = null;
        BufferedReader reader = null;
        try {
            Runtime rt = Runtime.getRuntime();
            child = rt.exec(command);

            // 以下代码为控制台输出相关的批出理
            String line = null;
            reader = new BufferedReader(new InputStreamReader(child.getInputStream(), "UTF-8"));
            while ((line = reader.readLine()) != null) {
                LOG.info(line);
            }

            // 等待刚刚执行的命令的结束
            while (true) {
                if (child.waitFor() == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new SystemException(e);
        } finally {
            child.destroy();
            try {
                reader.close();
            } catch (IOException e) {
                LOG.error("runCommand:", e);
            }
        }
    }
}
