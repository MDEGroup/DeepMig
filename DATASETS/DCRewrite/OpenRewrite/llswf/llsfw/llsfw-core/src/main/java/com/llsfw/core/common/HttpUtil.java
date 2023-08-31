/**
 * HttpUtil.java
 * Created at 2014-08-12
 * Created by wangkang
 * Copyright (C) 2014 SHANGHAI VOLKSWAGEN, All rights reserved.
 */
package com.llsfw.core.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;

/**
 * <p>
 * ClassName: HttpUtil
 * </p>
 * <p>
 * Description: 文件下载
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2014年8月12日
 * </p>
 */
public class HttpUtil {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(remoteAddr)) {
            remoteAddr = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isEmpty(remoteAddr)) {
            remoteAddr = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(remoteAddr)) {
            remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
    }

    /**
     * <p>
     * Description: 为httpclient请求设置代理(当代理为host和port为空的时候什么都不做)
     * </p>
     * 
     * @param request
     *            请求对象
     * @param proxyHost
     *            代理地址
     * @param proxyPort
     *            代理端口
     * @return 设置代理后的请求
     */
    public HttpGet proxy(HttpGet request, String proxyHost, String proxyPort) {

        // 设置请求和传输超时时间
        RequestConfig requestConfig;
        int connectrequesttimeout = 2 * 60 * 1000;
        int sockettimeout = 2 * 60 * 1000;
        int connecttimeout = 2 * 60 * 1000;

        // host和port都不为空的时候就设置代理
        if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {

            // 设置代理对象 ip/代理名称,端口
            HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort));

            // 设置代理配置
            requestConfig = RequestConfig.custom().setProxy(proxy).setConnectionRequestTimeout(connectrequesttimeout)
                    .setSocketTimeout(sockettimeout).setConnectTimeout(connecttimeout).build();

            // 设置代理
            request.setConfig(requestConfig);
        } else {
            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectrequesttimeout)
                    .setSocketTimeout(sockettimeout).setConnectTimeout(connecttimeout).build();
            request.setConfig(requestConfig);
        }

        // 返回
        return request;
    }

    /**
     * <p>
     * Description: 为httpclient请求设置代理(当代理为host和port为空的时候什么都不做)
     * </p>
     * 
     * @param request
     *            请求对象
     * @param proxyHost
     *            代理地址
     * @param proxyPort
     *            代理端口
     * @return 设置代理后的请求
     */
    public HttpPost proxy(HttpPost request, String proxyHost, String proxyPort) {

        // host和port都不为空的时候就设置代理
        if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {

            // 设置代理对象 ip/代理名称,端口
            HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort));

            // 设置代理配置
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

            // 设置代理
            request.setConfig(config);

        }

        // 返回
        return request;
    }

    /**
     * <p>
     * Description: 下载文件
     * </p>
     * 
     * @param request
     *            请求参数
     * @param response
     *            响应参数
     * @param downloadFile
     *            需下载的文件
     * @param fileName
     *            文件名称
     * @throws IOException
     */
    public void download(HttpServletRequest request, HttpServletResponse response, File downloadFile, String fileName)
            throws IOException {

        // 记录文件大小
        long fileLength = downloadFile.length();

        // 记录已下载文件大小
        long pastLength = 0;

        // 客户端请求的字节总量
        long contentLength = 0;

        // 负责读取数据
        RandomAccessFile raf = null;

        // 缓冲
        BufferedOutputStream out = null;

        // 暂存容器
        byte[] b = new byte[Constants.IO_BUFFERED];

        final String range = "Range";

        try {

            // 判断是否包含断点请求
            if (request.getHeader(range) != null) {
                LOG.info("request.getHeader(\"" + range + "\")=" + request.getHeader(range));

                //// 获取range,客户端请求的是 969998336 之后的字节 ,设置响应类型
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                String rangeBytes = "";
                rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                contentLength = fileLength - pastLength;
            } else { // 从开始进行下载
                contentLength = fileLength;// 客户端要求全文下载
            }

            if (pastLength != 0) {
                // 不是从最开始下载,
                // 响应的格式是: Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                LOG.info("----------------------------不是从开始进行下载！服务器即将开始断点续传...");
                String contentRange = new StringBuffer("bytes ").append(Long.toString(pastLength)).append("-")
                        .append(Long.toString(fileLength - 1)).append("/").append(Long.toString(fileLength)).toString();
                LOG.info("Content-Range:" + contentRange);
                response.setHeader("Content-Range", contentRange);
            } else {
                // 是从开始下载
                LOG.info("----------------------------是从开始进行下载！");
            }

            // 设置相应参数
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Accept-Ranges", "bytes"); // 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1 200 OK
            LOG.info("Content-Length:" + contentLength);
            response.setHeader("Content-Length", String.valueOf(contentLength));
            response.setHeader("Content-disposition",
                    "attachment; filename="
                            + new String(fileName.getBytes(com.llsfw.core.common.Constants.DEF_CHARACTER_SET_ENCODING),
                                    "ISO8859-1"));

            out = new BufferedOutputStream(response.getOutputStream());
            raf = new RandomAccessFile(downloadFile, "r");

            raf.seek(pastLength); // 形如 bytes=969998336- 的客户端请求，跳过 969998336 个字节
            int n = 0;
            while ((n = raf.read(b, 0, Constants.IO_BUFFERED)) != -1) {
                out.write(b, 0, n);
            }

        } catch (IOException e) {
            LOG.info("下载出错:", e);
            throw new IOException(e);
        } finally {
            out.flush();
            out.close();
            raf.close();
        }
    }
}
