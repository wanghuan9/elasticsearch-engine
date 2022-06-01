package com.elasticsearch.engine.error;

import com.elasticsearch.engine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.error.repository.SupplierItemErrorRepository;
import com.elasticsearch.engine.execute.querymodel.SupplierItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 查询引擎 异常场景测试  未继承BaseESRepository
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class EsEngineProxyNoExtendQueryTest {

    @Resource
    private SupplierItemErrorRepository supplierItemErrorRepository;

    @Test
    public void testNoExtendBaseESRepository() {
        supplierItemErrorRepository.queryOne(new SupplierItem());
    }


}
