package com.elasticsearch.engine.elasticsearchengine.extend.mybatis;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Druid的SQL表名改写工具
 */
public class DruidShadowHelper {
    private static Logger logger = LoggerFactory.getLogger(DruidShadowHelper.class);

    private static boolean prettySql = false;

    public DruidShadowHelper() {
    }

    public DruidShadowHelper(boolean prettySql) {
        this.prettySql = prettySql;
    }

    public static String generateShadowSql(String originSql) {
        String dbType = "mysql";
        //解析sql
        List<SQLStatement> statementList = SQLUtils.parseStatements(originSql, dbType);
        if (CollectionUtils.isEmpty(statementList)) {
            logger.warn("无法解析,返回原SQL:{}", originSql);
            return originSql;
        }
        //遍历 - 获取表名
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        statementList.get(0).accept(visitor);
        if (MapUtils.isEmpty(visitor.getTables())) {
            logger.warn("无法获取表名,返回原SQL:{}", originSql);
            return originSql;
        }
        Map<String, String> tableMapping = new HashMap<>(visitor.getTables().size());
        visitor.getTables().keySet().forEach(tableNameStat -> {
            tableMapping.put(tableNameStat.getName(), "123");
        });
        //遍历 - 修改表名
        return SQLUtils.toSQLString(statementList, dbType, null, new SQLUtils.FormatOption(false, prettySql), tableMapping);
    }

}
