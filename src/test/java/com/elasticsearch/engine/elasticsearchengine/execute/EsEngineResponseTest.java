package com.elasticsearch.engine.elasticsearchengine.execute;

import com.elasticsearch.engine.elasticsearchengine.ElasticsearchEngineConfiguration;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.EsBaseExecuteHandle;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItem;
import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItemResExtend;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.AggEntityExtend;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.RangeParam;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Sign;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询引擎响应结果构建测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineConfiguration.class)
public class EsEngineResponseTest {

    @Resource
    private EsBaseExecuteHandle esBaseExecuteHandle;

    /**
     * 测试默认参数 Trem Trems Range
     */
    @Test
    public void findByProductNameAndStatusTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setStatus(RangeParam.builder().left(0).right(3).build());
        BaseResp<SupplierItemEntity> baseHitBaseResp = esBaseExecuteHandle.execute(supplierItem, SupplierItemEntity.class);
        log.info(JsonParser.asJson(baseHitBaseResp));
    }

    /**
     * 测试普通查询响应
     */
    @Test
    public void responseTest() {
        SupplierItem supplierItem = new SupplierItem();
        List<String> itemNoList = Lists.newArrayList("20201205102325871450", "20201216103025852694");
        supplierItem.setItemNoList(itemNoList);
        BaseResp<SupplierItemEntity> baseHitBaseResp = esBaseExecuteHandle.execute(supplierItem, SupplierItemEntity.class);
        log.info(JsonParser.asJson(baseHitBaseResp));
    }

    /**
     * 测试自定义扩展response查询
     */
    @Test
    public void extendTest() {
        SupplierItemResExtend supplierItem = new SupplierItemResExtend();
        supplierItem.setStatus(Sign.DEFAULT_INTER);
        BaseResp<List<AggEntityExtend>> resp = esBaseExecuteHandle.execute(supplierItem, List.class);
        List<AggEntityExtend> result = resp.getResult();
        log.info(JsonParser.asJson(result));
    }

}
