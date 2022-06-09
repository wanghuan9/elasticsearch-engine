package com.elasticsearch.engine.common.queryhandler.sql;

import com.elasticsearch.engine.GlobalConfig;
import com.elasticsearch.engine.common.utils.*;
import com.elasticsearch.engine.config.ElasticSearchProperties;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.domain.SqlResponse;
import com.elasticsearch.engine.model.emenu.DataType;
import com.elasticsearch.engine.model.emenu.EsVersionConstant;
import com.elasticsearch.engine.model.emenu.SqlFormat;
import com.elasticsearch.engine.model.exception.EsEngineQueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author wanghuan
 * @description: sql查询执行器
 * @date 2022-04-24 18:08
 */
@Slf4j
@Component
public class EsSqlExecuteHandler {

    private static final String SQL_QUERY_PREFIX;
    private static final String SQL_TRANSLATE_PREFIX;

    static {
        EsVersionConstant constant = EsVersionConstant.of(GlobalConfig.elasticVersion);
        SQL_QUERY_PREFIX = constant.getSqlQueryPrefix();
        SQL_TRANSLATE_PREFIX = constant.getSqlTranslatePrefix();
    }


    @Resource
    private ElasticSearchProperties elasticSearchProperties;

    /**
     * 通过sql进行查询
     *
     * @param sql       sql脚本（支持mysql语法）
     * @param sqlFormat sql请求返回类型
     * @return
     * @throws Exception
     */
    public String queryBySql(String sql, SqlFormat sqlFormat) {
        String host = elasticSearchProperties.getHosts();
        if (StringUtils.isEmpty(host)) {
            host = CommonConstant.DEFAULT_ES_HOST;
        }
        String ipport = "";
        String[] hosts = host.split(",");
        if (hosts.length == 1) {
            ipport = hosts[0];
        } else {//随机选择配置的地址
            int randomindex = new Random().nextInt(hosts.length);
            ipport = hosts[randomindex];
        }
        ipport = "http://" + ipport;
        log.info(ipport + SQL_QUERY_PREFIX + sqlFormat.getFormat());
        log.info("{\"query\":\"" + sql + "\"}");

        String username = elasticSearchProperties.getUsername();
        String password = elasticSearchProperties.getPassword();
        try {
            if (!StringUtils.isEmpty(username)) {
                return HttpClientTool.execute(ipport + SQL_QUERY_PREFIX + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}", username, password);
            }
            return HttpClientTool.execute(ipport + SQL_QUERY_PREFIX + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将sql翻译为es查询语句
     *
     * @param sql       sql脚本（支持mysql语法）
     * @param sqlFormat sql请求返回类型
     * @return
     * @throws Exception
     */
    public String querySqlTranslate(String sql, SqlFormat sqlFormat) {
        String host = elasticSearchProperties.getHosts();
        if (StringUtils.isEmpty(host)) {
            host = CommonConstant.DEFAULT_ES_HOST;
        }
        String ipport = "";
        String[] hosts = host.split(",");
        if (hosts.length == 1) {
            ipport = hosts[0];
        } else {//随机选择配置的地址
            int randomindex = new Random().nextInt(hosts.length);
            ipport = hosts[randomindex];
        }
        ipport = "http://" + ipport;
        log.info(ipport + SQL_TRANSLATE_PREFIX + sqlFormat.getFormat());
        log.info("{\"query\":\"" + sql + "\"}");

        String username = elasticSearchProperties.getUsername();
        String password = elasticSearchProperties.getPassword();
        try {
            if (!StringUtils.isEmpty(username)) {
                return HttpClientTool.execute(ipport + SQL_TRANSLATE_PREFIX + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}", username, password);
            }
            return HttpClientTool.execute(ipport + SQL_TRANSLATE_PREFIX + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过sql进行查询
     *
     * @param sql   sql脚本（支持mysql语法）
     * @param clazz 索引pojo类类型
     * @param clazz 索引pojo类类型
     * @return
     * @throws Exception
     */
    public <T> List<T> queryBySql(String sql, Class<T> clazz, Boolean isExtendQuery) {
        String s = queryBySql(sql, SqlFormat.JSON);
        SqlResponse sqlResponse = JsonParser.asObject(s, SqlResponse.class);
        //正常响应时 status 为null
        if (Objects.nonNull(sqlResponse.getStatus())) {
            throw new EsEngineQueryException("SQL查询异常:  " + JsonParser.asJson(sqlResponse));
        }
        List<T> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sqlResponse.getRows())) {
            for (List<String> row : sqlResponse.getRows()) {
                try {
                    result.add(generateObjBySqlReps(sqlResponse.getColumns(), row, clazz, isExtendQuery));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }


    /**
     * 将sql查询结果转换为指定类
     *
     * @param columns
     * @param rows
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T generateObjBySqlReps(List<SqlResponse.ColumnsDTO> columns, List<String> rows, Class<T> clazz, Boolean isExtendQuery) throws Exception {
        if (rows.size() != columns.size()) {
            throw new Exception("sql column not match");
        }
        //单个结果: count,sum 结果转换
        if (rows.size() == 1 && (ReflectionUtils.isBaseType(clazz) || clazz.equals(BigDecimal.class))) {
            return (T) BeanTools.fieldTypeCovert(DataType.getDataTypeByStr(columns.get(0).getType()), rows.get(0), clazz);
        }
        //entity listEntity
        Map<String, BeanTools.NameTypeValueMap> valueMap = new HashMap(32);
        for (int i = 0; i < rows.size(); i++) {
            BeanTools.NameTypeValueMap m = new BeanTools.NameTypeValueMap();
            m.setDataType(DataType.getDataTypeByStr(columns.get(i).getType()));
            String paramName = columns.get(i).getName();
            //是否下划线转驼峰转 
            if (GlobalConfig.namingStrategy) {
                paramName = CaseFormatUtils.underscoreToCamel(paramName);
            }
            m.setFieldName(paramName);
            m.setValue(rows.get(i));
            valueMap.put(paramName, m);
        }
        T t = (T) BeanTools.typeMapToObject(valueMap, clazz);
        return t;
    }

}
