/**
 * 
 */
package com.llsfw.core.datasource;

import com.llsfw.generator.model.standard.system.TtDynamicDataSource;

/**
 * @author kkll
 *
 */
public class DataSourceThreadLocal extends ThreadLocal<TtDynamicDataSource> {

    /*
     * 修改点参考:http://blog.csdn.net/cxh5060/article/details/49275633
     * 
     * @see java.lang.ThreadLocal#remove()
     */
    @Override
    public void remove() {
        super.remove();
        this.initialValue();
    }

}
