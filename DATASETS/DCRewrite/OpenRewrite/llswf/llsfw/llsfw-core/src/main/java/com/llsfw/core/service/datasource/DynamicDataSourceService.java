/**
 * 
 */
package com.llsfw.core.service.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.llsfw.core.service.base.BaseService;
import com.llsfw.generator.mapper.standard.system.TtDynamicDataSourceMapper;
import com.llsfw.generator.model.standard.system.TtDynamicDataSource;

/**
 * @author wangkang
 *
 */
@Service
public class DynamicDataSourceService extends BaseService {

    @Autowired
    private TtDynamicDataSourceMapper tddsm;

    /**
     * 根据数据源名称返回数据源数据
     * 
     * @param dbsName
     *            数据源名称
     * @return 数据源数据
     */
    public TtDynamicDataSource getDataSourceData(String dbsName) {
        return tddsm.selectByPrimaryKey(dbsName);
    }
}
