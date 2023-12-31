/**
 * MyWebBinding.java
 * Created at 2013-11-29
 * Created by wangkang
 * Copyright (C) llsfw.
 */
package com.llsfw.core.controller.base;

import java.util.Date;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * <p>
 * ClassName: MyWebBinding
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2013年11月29日
 * </p>
 */
public class MyWebBinding implements WebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(Date.class, new DateConvertEditor());
    }

}
