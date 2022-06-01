package com.elasticsearch.engine.config;

import com.elasticsearch.engine.model.exception.EsHelperConfigException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: ElasticsearchConfig
 * @date 2022-01-26 11:28
 */
@EnableConfigurationProperties(ElasticSearchProperties.class)
@Configuration
public class ElasticsearchConfig {

    @Resource
    private ElasticSearchProperties elasticSearchProperties;


    public static HttpHost[] hostGen(String[] elasticsearchHost) {
        if (ArrayUtils.isEmpty(elasticsearchHost)) {
            throw new IllegalArgumentException("elasticsearch host must not empty");
        }
        return Arrays.stream(elasticsearchHost).map(
                HttpHost::create
        ).toArray(HttpHost[]::new);
    }

    @Bean(destroyMethod = "close")
//    @ConditionalOnBean(name = "enableEsEngineConfig")
    public RestHighLevelClient restHighLevelClient() {
        String hosts = elasticSearchProperties.getHosts();
        RestClientBuilder builder;
        configCheck();
        if (StringUtils.isAnyEmpty(elasticSearchProperties.getUsername(), elasticSearchProperties.getPassword())) {
            /** ps: 创建非认证客户端*/
            builder = RestClient.builder(hostGen(hosts.split(",")));
        } else {
            /** ps: 创建认证客户端*/
            CredentialsProvider credentials = credentials();
            builder = RestClient.builder(hostGen(hosts.split(","))).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentials));
        }
        return new RestHighLevelClient(builder);
    }

    @Bean(destroyMethod = "close")
    public RestClient getRestClient() {
        String hosts = elasticSearchProperties.getHosts();
        configCheck();
        if (StringUtils.isAnyEmpty(elasticSearchProperties.getUsername(), elasticSearchProperties.getPassword())) {
            /** ps: 创建非认证客户端*/
            return RestClient.builder(hostGen(hosts.split(","))).build();
        } else {
            /** ps: 创建认证客户端*/
            CredentialsProvider credentials = credentials();
            return RestClient.builder(hostGen(hosts.split(","))).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentials)).build();
        }
    }

    /**
     * 构建客户端认证信息
     *
     * @return
     */
    private CredentialsProvider credentials() {
        // 阿里云ES集群需要basic auth验证。
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        //访问用户名和密码为您创建阿里云Elasticsearch实例时设置的用户名和密码，也是Kibana控制台的登录用户名和密码。
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticSearchProperties.getUsername(), elasticSearchProperties.getPassword()));
        return credentialsProvider;
    }

    /**
     * 检查配置项
     */
    private void configCheck() {
        if (Objects.isNull(elasticSearchProperties)) {
            throw new EsHelperConfigException("elasticSearch config hosts is null");
        }

        if (StringUtils.isEmpty(elasticSearchProperties.getHosts())) {
            throw new EsHelperConfigException("elasticSearch config hosts is empty");
        }
    }

}
