package com.elasticsearch.engine.elasticsearchengine;

import com.elasticsearch.engine.elasticsearchengine.common.parse.ann.model.QueryHandlerFactory;
import com.elasticsearch.engine.elasticsearchengine.config.LoadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: ElasticsearchEngineConfiguration
 * @date 2022-01-26 11:28
 */
@Slf4j
@Configuration
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
