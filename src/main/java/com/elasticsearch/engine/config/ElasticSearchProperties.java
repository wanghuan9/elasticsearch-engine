package com.elasticsearch.engine.config;

import com.elasticsearch.engine.model.constant.CommonConstant;
import joptsimple.internal.Strings;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wanghuan
 * @description: ElasticsearchConfig
 * @date 2022-01-26 11:28
 */
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticSearchProperties {

    /**
     * elasticsearch hots配置,多台机器配置格式如下:
     * es-c1.enmonster.org:9200,es-c2.enmonster.org:9200
     */
    private String hosts = CommonConstant.DEFAULT_ES_HOST;

    /**
     * elasticsearch 认证用户名
     */
    private String username = Strings.EMPTY;

    /**
     * elasticsearch 认证密码
     */
    private String password = Strings.EMPTY;

}
