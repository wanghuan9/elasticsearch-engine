package com.elasticsearch.engine.elasticsearchengine.proxy;

import com.elasticsearch.engine.elasticsearchengine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.proxy.repository.SupplierItemParamRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-16 22:11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class EsEngineProxyParamQueryTest {


    @Resource
    private SupplierItemParamRepository supplierItemParamRepository;

    /**
     * 查询单个测试
     */
    @Test
    public void queryOneResponse() {
        SupplierItemEntity supplierItemEntity = supplierItemParamRepository.queryOne("20201226204656658857", null);
        log.info("res:{}", JsonParser.asJson(supplierItemEntity));
    }

    /**
     * List查询测试
     */
    @Test
    public void queryListResponse() {
        List<String> itemNoList = Lists.newArrayList("20201226204656658857");
        List<SupplierItemEntity> res = supplierItemParamRepository.queryList(itemNoList);
        log.info("res:{}", JsonParser.asJson(res));
    }
}
