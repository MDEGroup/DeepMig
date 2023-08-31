package com.llsfw.core.pagequery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.llsfw.core.datasource.DynamicDataSource;
import com.llsfw.core.exception.SystemException;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class }))
public class PageInterceptor implements Interceptor {

    /**
     * 本地变量
     */
    private static final ThreadLocal<PageResult> LOCAL_PAGE = new ThreadLocal<>();

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * 方言
     */
    private String dialect;

    /**
     * sqlId
     */
    private String pageSqlId;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getPageSqlId() {
        return pageSqlId;
    }

    public void setPageSqlId(String pageSqlId) {
        this.pageSqlId = pageSqlId;
    }

    /**
     * 获取Page参数
     *
     * @return
     */
    private static PageResult getLocalPage() {
        return LOCAL_PAGE.get();
    }

    /**
     * 设置Page参数
     * 
     * @param page
     */
    private static void setLocalPage(PageResult page) {
        LOCAL_PAGE.set(page);
    }

    /**
     * 移除本地变量
     */
    private static void clearLocalPage() {
        LOCAL_PAGE.remove();
    }

    /**
     * 开始分页
     *
     * @param pageSize
     *            每页显示数量
     * @param curPage
     *            页码
     * @throws SystemException
     */
    public static void startPage(int pageSize, int curPage) throws SystemException {

        // 规范参数
        if (pageSize <= 0) {
            throw new SystemException("pageSize <= 0");
        }
        if (curPage <= 0) {
            throw new SystemException("curpage <= 0");
        }

        // 实例化分页对象
        PageResult page = new PageResult();
        page.setPageSize(pageSize); // 设置每页条数
        page.setCurPage(curPage); // 设置当前页

        // 保存对象至本地变量中
        setLocalPage(page);

    }

    /**
     * 开始分页
     * 
     * @param pageSize
     *            每页数量
     * @param curPage
     *            当前页数
     * @param totalRecords
     *            总记录数
     * @throws SystemException
     *             异常
     */
    public static void startPage(int pageSize, int curPage, int totalRecords) throws SystemException {

        // 规范参数
        if (totalRecords < 0) {
            throw new SystemException("totalRecords < 0");
        }

        // 设置总行数
        startPage(pageSize, curPage);
        PageResult page = getLocalPage();
        page.setTotalRecords(totalRecords);

        // 保存对象至本地变量中
        setLocalPage(page);

    }

    /**
     * 将结果集转换为PageResult
     * 
     * @param result
     * @return
     */
    public static <E> PageResult<E> getPageResult(List<E> result) {
        return (PageResult<E>) result;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            // 获得分页对象
            PageResult pr = getLocalPage();

            // 获取必要参数
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String id = mappedStatement.getId();
            LOG.debug("MappedStatement id = " + id);
            // 判断是否需要分页
            if (id.matches(pageSqlId) && pr != null) {
                Object parameter = invocation.getArgs()[1];
                BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                String sql = boundSql.getSql();
                // 获得链接
                Connection connection = getConnection(mappedStatement);
                // 查询总行数,如果当前PageResult对象包含总页数,则这里不做查询
                if (!pr.isHasTotalRecords()) {
                    int count = this.countRecords(connection, sql, mappedStatement, boundSql);
                    pr.setTotalRecords(count);
                }
                // 计算页数
                if (pr.getTotalRecords() > 0) {
                    int totalPages = pr.getTotalRecords() / pr.getPageSize()
                            + ((pr.getTotalRecords() % pr.getPageSize() > 0) ? 1 : 0);
                    pr.setTotalPages(totalPages); // 设置总页数
                    if (pr.getCurPage() > pr.getTotalPages()) { // NOSONAR
                        pr.setCurPage(pr.getTotalPages());
                    }
                } else {
                    pr.setCurPage(1); // 没有数据,默认当前为第一页
                    pr.setTotalPages(0); // 没有数据,总页数为0
                }
                // 生成分页SQL
                String pageSql = generatePageSql(sql, pr);
                invocation.getArgs()[2] = RowBounds.DEFAULT;
                MappedStatement newMappedStatement = copyFromNewSql(mappedStatement, boundSql, pageSql,
                        boundSql.getParameterMappings(), boundSql.getParameterObject());
                invocation.getArgs()[0] = newMappedStatement;
                // 当count>0的时候才执行查询语句,否则不执行
                if (pr.getTotalRecords() > 0) {
                    // 执行分页查询
                    Object result = invocation.proceed();
                    // 得到处理结果
                    pr.addAll((List) result);
                }
                // 分页返回值
                return pr;
            }
            // 常规返回值
            return invocation.proceed();
        } finally {
            // 清理本地变量
            clearLocalPage();
        }
    }

    /**
     * 复制MappedStatement
     * 
     * @param ms
     * @param newSqlSource
     * @return
     */
    private static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        String[] keys = ms.getKeyProperties();
        if (keys != null) {
            String keysstr = Arrays.toString(keys);
            keysstr = keysstr.replace("[", "");
            keysstr = keysstr.replace("]", "");
            builder.keyProperty(keysstr);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        builder.databaseId(ms.getDatabaseId());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        String[] keyColumns = ms.getKeyColumns();
        if (keyColumns != null) {
            String keysstr = Arrays.toString(keyColumns);
            keysstr = keysstr.replace("[", "");
            keysstr = keysstr.replace("]", "");
            builder.keyColumn(keysstr);
        }
        builder.lang(ms.getLang());
        String[] resulSets = ms.getResulSets();
        if (resulSets != null) {
            String keysstr = Arrays.toString(resulSets);
            keysstr = keysstr.replace("[", "");
            keysstr = keysstr.replace("]", "");
            builder.resulSets(keysstr);
        }
        builder.resultSetType(ms.getResultSetType());
        builder.statementType(ms.getStatementType());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /**
     * 复制MappedStatement
     * 
     * @param mappedStatement
     * @param boundSql
     * @param sql
     * @param parameterMappings
     * @param parameter
     * @return
     */
    private static MappedStatement copyFromNewSql(MappedStatement mappedStatement, BoundSql boundSql, String sql,
            List<ParameterMapping> parameterMappings, Object parameter) {
        BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql, sql, parameterMappings, parameter);
        return copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
    }

    /**
     * 复制MappedStatement
     * 
     * @param mappedStatement
     * @param boundSql
     * @param sql
     * @param parameterMappings
     * @param parameter
     * @return
     */
    private static BoundSql copyFromBoundSql(MappedStatement mappedStatement, BoundSql boundSql, String sql,
            List<ParameterMapping> parameterMappings, Object parameter) {
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql, parameterMappings, parameter);
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    /**
     * 获得数据库连接
     * 
     * @param mappedStatement
     * @return
     * @throws SQLException
     */
    private static Connection getConnection(MappedStatement mappedStatement) throws SQLException {
        DataSource dataSource = DynamicDataSource.getCurrentDataSource();
        if (dataSource == null) {
            dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * 生成分页SQL语句
     * 
     * @param sql
     *            sql语句
     * @param page
     *            分页信息
     * @return 分页sql语句
     */
    private String generatePageSql(String sql, PageResult pr) {
        if (pr != null && (dialect != null || !"".equals(dialect))) {
            StringBuilder pageSql = new StringBuilder();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" LIMIT " + (pr.getCurPage() - 1) * pr.getPageSize() + "," + pr.getPageSize());
            } else if ("oracle".equals(dialect)) {
                pageSql.append(" SELECT * FROM (SELECT TMP_TB.*,ROWNUM ROW_ID FROM ( ");
                pageSql.append(sql);
                pageSql.append(" )  TMP_TB WHERE ROWNUM <= ");
                pageSql.append(pr.getPageSize() + ((pr.getCurPage() - 1) * pr.getPageSize()));
                pageSql.append(" ) WHERE ROW_ID >= ");
                pageSql.append(1 + ((pr.getCurPage() - 1) * pr.getPageSize()));
            }
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    /**
     * 获得总记录数
     * 
     * @param originalSql
     * @param mappedStatement
     * @param boundSql
     * @return
     * @throws SQLException
     */
    private int countRecords(Connection connection, String originalSql, MappedStatement mappedStatement,
            BoundSql boundSql) throws SQLException {
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            int totalRecorld = 0;
            Object paramObject = boundSql.getParameterObject();
            StringBuilder countSql = new StringBuilder(originalSql.length() + 100);
            countSql.append(" select count(1) from ( ").append(originalSql).append(" ) C ");
            countStmt = connection.prepareStatement(countSql.toString());
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), originalSql,
                    boundSql.getParameterMappings(), paramObject);
            setParameters(countStmt, mappedStatement, countBS, paramObject);
            rs = countStmt.executeQuery();
            if (rs.next()) {
                totalRecorld = rs.getInt(1);
            }
            return totalRecorld;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (countStmt != null) {
                countStmt.close();
            }
        }
    }

    /**
     * 设置SQL参数
     * 
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
            Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    value = this.setParameters(parameterObject, typeHandlerRegistry, boundSql, propertyName,
                            configuration, prop, metaObject);
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    setParameters(typeHandler, mappedStatement, propertyName);
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    private static void setParameters(TypeHandler typeHandler, MappedStatement mappedStatement, String propertyName) {
        if (typeHandler == null) {
            throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
                    + " of statement " + mappedStatement.getId());
        }
    }

    private Object setParameters(Object parameterObject, TypeHandlerRegistry typeHandlerRegistry, BoundSql boundSql,
            String propertyName, Configuration configuration, PropertyTokenizer prop, MetaObject metaObject) {
        Object value;
        if (parameterObject == null) {
            value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
        } else if (boundSql.hasAdditionalParameter(propertyName)) {
            value = boundSql.getAdditionalParameter(propertyName);
        } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
                && boundSql.hasAdditionalParameter(prop.getName())) {
            value = boundSql.getAdditionalParameter(prop.getName());
            if (value != null) {
                value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
            }
        } else {
            value = metaObject == null ? null : metaObject.getValue(propertyName);
        }
        return value;
    }

    @Override
    public Object plugin(Object target) {
        LOG.debug("plugin");
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        LOG.debug("setProperties");
    }

}
