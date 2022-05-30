package com.elasticsearch.engine.elasticsearchengine;

import com.elasticsearch.engine.elasticsearchengine.config.ElasticsearchConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable ElasticSearch Handler
 * <p>
 * author     JohenTeng
 * date      2021/9/18
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ElasticsearchEngineConfiguration.class, ElasticsearchConfig.class})
@ComponentScan(basePackages = "com.elasticsearch.engine.elasticsearchengine")
public @interface EnableEsEngine {
}
