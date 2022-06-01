package com.elasticsearch.engine.error;

import com.elasticsearch.engine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.error.repository.SupplierItemErrorRepository2;
import com.elasticsearch.engine.error.repository.SupplierItemParamErrorRepository;
import com.elasticsearch.engine.execute.querymodel.SupplierItem;
import com.elasticsearch.engine.execute.resultmodel.SupplierItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 查询引擎 查询测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class EsEngineProxyErrorQueryTest {

    @Resource
    private SupplierItemErrorRepository2 supplierItemErrorRepository2;

    @Resource
    private SupplierItemParamErrorRepository supplierItemParamErrorRepository;


    /**
     * 测试为添加 @EsQueryIndex
     */
    @Test
    public void testUndefineEsQueryIndexError() {
        supplierItemErrorRepository2.queryOne("", 1);
    }

    /**
     * 测试接口定义 入参异常
     */
    @Test
    public void testRequestParamError() {
        supplierItemParamErrorRepository.queryOne(new SupplierItem(), new SupplierItemEntity());
    }


    /**
     * 测试接口定义 出参异常
     */
    @Test
    public void testResponseParamError() {
        supplierItemParamErrorRepository.queryOne(new SupplierItem());
    }

}
