package org.hibernate.type.descriptor.sql;

import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.exception.EsEngineJpaExecuteException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.jboss.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author wanghuan
 * @description 重写jpa参数绑定的代码, 实现添加自定义逻辑
 * <p>
 * 重新需要注意包名要和源码包名一致
 * @mail 958721894@qq.com
 * @date 2022/6/9 20:45
 */
public abstract class BasicBinder<J> implements ValueBinder<J> {

    private static final Logger log = CoreLogging.logger(BasicBinder.class);

    private static final String BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - [%s]";
    private static final String NULL_BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - [null]";

    private final JavaTypeDescriptor<J> javaDescriptor;
    private final SqlTypeDescriptor sqlDescriptor;

    public JavaTypeDescriptor<J> getJavaDescriptor() {
        return javaDescriptor;
    }

    public SqlTypeDescriptor getSqlDescriptor() {
        return sqlDescriptor;
    }

    public BasicBinder(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
        this.javaDescriptor = javaDescriptor;
        this.sqlDescriptor = sqlDescriptor;
    }

    @Override
    public final void bind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException {
        final boolean traceEnabled = log.isTraceEnabled();

        if (value == null) {
            if (traceEnabled) {
                log.trace(String.format(NULL_BIND_MSG_TEMPLATE, index, JdbcTypeNameMapper.getTypeName(getSqlDescriptor().getSqlType())));
            }
            st.setNull(index, sqlDescriptor.getSqlType());
        } else {
            if (traceEnabled) {
                log.trace(String.format(BIND_MSG_TEMPLATE, index, JdbcTypeNameMapper.getTypeName(sqlDescriptor.getSqlType()), getJavaDescriptor().extractLoggableRepresentation(value)));
            }
            doBind(st, value, index, options);
        }
        inspect(st);
    }

    /**
     * Perform the binding.  Safe to assume that value is not null.
     *
     * @param st      The prepared statement
     * @param value   The value to bind (not null).
     * @param index   The index at which to bind
     * @param options The binding options
     * @throws SQLException Indicates a problem binding to the prepared statement.
     */
    protected abstract void doBind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException;


    /**
     * 处理jpa非回表查询
     * 获取带参数的sql 去es执行
     *
     * @param statement
     */
    private void inspect(PreparedStatement statement) {
        String st = statement.toString();
        //bind方法会被多次调用,解析一个参数调用一次
        //包含 NOT SPECIFIED 说明jpa参数解析还未完成, 直接返回让继续解析. 等全部解析完成再处理后续逻辑
        if (st.contains("NOT SPECIFIED")) {
            return;
        }
        String sql = st.substring(st.indexOf(":") + 1);
        //非select语句直接返回
        if (!sql.trim().startsWith(CommonConstant.SELECT_SQL_PREFIX_LOWER) && !sql.trim().startsWith(CommonConstant.SELECT_SQL_PREFIX_UPPER)) {
            return;
        }
        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
        if (Objects.nonNull(isEsQuery) && isEsQuery) {
            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
            throw new EsEngineJpaExecuteException(sql);
        }
    }

}