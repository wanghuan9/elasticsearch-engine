package com.elasticsearch.engine.elasticsearchengine;

import com.elasticsearch.engine.elasticsearchengine.model.annotion.EnableEsHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEsHelper
@SpringBootApplication
public class ElasticsearchEngineApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchEngineApplicationTests.class, args);
    }

}
