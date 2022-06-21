package com.elasticsearch.engine.base;

import com.elasticsearch.engine.base.common.parse.ann.QueryHandlerFactory;
import com.elasticsearch.engine.base.config.ElasticSearchProperties;
import com.elasticsearch.engine.base.config.EsEngineConfigProperties;
import com.elasticsearch.engine.base.config.LoadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: com.elasticsearch.engine.base.ElasticsearchEngineConfiguration
 * @date 2022-01-26 11:28
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {ElasticSearchProperties.class, EsEngineConfigProperties.class})
@ComponentScan(basePackages = "com.elasticsearch.engine.base")
public class ElasticsearchEngineConfiguration {

    @PostConstruct
    public void load() {
        String banner = LoadFactory.readBanner();
        log.info(banner);
        log.info("es-helper-query-handler-scanner load handles:\n{}\n",
                QueryHandlerFactory.QUERY_HANDLE_MAP.entrySet().stream().map(
                        e -> "[\tes-helper] " + e.getKey() + ":" + e.getValue()
                ).collect(Collectors.joining("\n"))
        );
    }

}
