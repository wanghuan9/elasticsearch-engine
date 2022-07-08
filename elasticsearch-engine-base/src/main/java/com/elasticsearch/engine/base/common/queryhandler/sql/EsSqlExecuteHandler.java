package com.elasticsearch.engine.base.common.queryhandler.sql;

import com.elasticsearch.engine.base.common.parse.sql.SqlResponseParse;
import com.elasticsearch.engine.base.common.utils.HttpClientTool;
import com.elasticsearch.engine.base.common.utils.JsonParser;
import com.elasticsearch.engine.base.config.ElasticSearchProperties;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.SqlResponse;
import com.elasticsearch.engine.base.model.emenu.EsVersionConstant;
import com.elasticsearch.engine.base.model.emenu.SqlFormat;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author wanghuan
 * @description: sql查询执行器
 * @date 2022-04-24 18:08
 */
@Slf4j
@Component
public class EsSqlExecuteHandler {

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
        EsVersionConstant constant = EsVersionConstant.of(EsEngineConfig.getElasticVersion());
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
        if (EsEngineConfig.getQueryJsonLog()) {
            log.info(ipport + constant.getSqlQueryPrefix() + sqlFormat.getFormat());
            log.info("{\"query\":\"" + sql + "\"}");
        }

        String username = elasticSearchProperties.getUsername();
        String password = elasticSearchProperties.getPassword();
        try {
            if (!StringUtils.isEmpty(username)) {
                return HttpClientTool.execute(ipport + constant.getSqlQueryPrefix() + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}", username, password);
            }
            return HttpClientTool.execute(ipport + constant.getSqlQueryPrefix() + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}");
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
        EsVersionConstant constant = EsVersionConstant.of(EsEngineConfig.getElasticVersion());
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
        log.info(ipport + constant.getSqlTranslatePrefix() + sqlFormat.getFormat());
        log.info("{\"query\":\"" + sql + "\"}");

        String username = elasticSearchProperties.getUsername();
        String password = elasticSearchProperties.getPassword();
        try {
            if (!StringUtils.isEmpty(username)) {
                return HttpClientTool.execute(ipport + constant.getSqlTranslatePrefix() + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}", username, password);
            }
            return HttpClientTool.execute(ipport + constant.getSqlTranslatePrefix() + sqlFormat.getFormat(), "{\"query\":\"" + sql + "\"}");
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
                    result.add(SqlResponseParse.generateObjBySqlReps(sqlResponse.getColumns(), row, clazz, isExtendQuery));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }


}
