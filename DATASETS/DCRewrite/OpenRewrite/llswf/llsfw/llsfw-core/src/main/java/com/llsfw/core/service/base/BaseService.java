/**
 * BaseService.java
 * Created at 2013-12-02
 * Created by wangkang
 * Copyright (C) llsfw.
 */
package com.llsfw.core.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.llsfw.core.service.serverparam.ParamService;

/**
 * <p>
 * ClassName: BaseService
 * </p>
 * <p>
 * Description: 根服务
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2013年12月2日
 * </p>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class BaseService {

    /**
     * <p>
     * Field ps: 参数获取服务
     * </p>
     */
    @Autowired
    private ParamService ps;

    public ParamService getPs() {
        return this.ps;
    }
}
