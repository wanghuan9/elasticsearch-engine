package com.elasticsearch.engine.elasticsearchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEsEngine
@SpringBootApplication
public class ElasticsearchEngineApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchEngineApplicationTests.class, args);
    }

}
