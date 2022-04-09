package com.elasticsearch.engine.elasticsearchengine.config;

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
     * es-c1.enmonster.org:9200,es-c2.enmonster.org:9200
     */
    private String hosts;

    private String userName;

    private String password;

}
