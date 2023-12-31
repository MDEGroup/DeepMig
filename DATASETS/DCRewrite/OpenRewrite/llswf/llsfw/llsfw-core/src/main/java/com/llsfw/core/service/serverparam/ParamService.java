/**
 * ParamService.java
 * Created at 2013-12-02
 * Created by wangkang
 * Copyright (C) llsfw.
 */
package com.llsfw.core.service.serverparam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.llsfw.core.common.SystemParamDef;
import com.llsfw.generator.mapper.standard.system.TtServerGlobalParametersMapper;
import com.llsfw.generator.model.standard.system.TtServerGlobalParameters;

/**
 * <p>
 * ClassName: ServerParamService
 * </p>
 * <p>
 * Description: 获取参数服务
 * </p>
 * <p>
 * Author: wangkang
 * </p>
 * <p>
 * Date: 2013年12月2日
 * </p>
 */
@Service
public class ParamService {

    /**
     * <p>
     * Field tgpm: SERVER端参数定义
     * </p>
     */
    @Autowired
    private TtServerGlobalParametersMapper tgpm;

    @Autowired
    @Qualifier("systemParam")
    private SystemParamDef systemParam;

    /**
     * <p>
     * Description: 返回服务端参数<br />
     * 优先获取数据库中的配置,如果数据库中没有配置,则从spring-config.xml配置中获取<br />
     * 如果都没有设置的话,则返回为null<br />
     * 逻辑考虑:数据库中环境变量表默认可不初始化数据,只有在实际需求和应用配置不匹配的情况下,可做配置,变更程序行为.
     * </p>
     * 
     * @param code
     *            参数名称
     * @return 值
     */
    public String getServerParamter(String code) {
        TtServerGlobalParameters tsgp = this.getServerGlobalParamters(code);
        if (null != tsgp) {
            return tsgp.getParametersValue();
        }
        return systemParam.getData().get(code);
    }

    /**
     * <p>
     * Description: 返回服务端参数
     * </p>
     * 
     * @param code
     *            参数名称
     * @return 参数对象
     */
    private TtServerGlobalParameters getServerGlobalParamters(String code) {
        return this.tgpm.selectByPrimaryKey(code);
    }
}
