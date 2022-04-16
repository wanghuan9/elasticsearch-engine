package com.elasticsearch.engine.elasticsearchengine.proxy;

import com.elasticsearch.engine.elasticsearchengine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItem;
import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItemResExtend;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.AggEntityExtend;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Sign;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.proxy.entity.params.SupplierItemProxyResExtend;
import com.elasticsearch.engine.elasticsearchengine.proxy.repository.SupplierItemRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询引擎 查询测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class EsEngineProxyQueryTest {

    @Resource
    private SupplierItemRepository supplierItemMapper;

    /**
     * 查询单个测试
     */
    @Test
    public void queryOneResponse() {
        SupplierItem supplierItem = new SupplierItem();
        List<String> itemNoList = Lists.newArrayList("20201226204656658857");
        supplierItem.setItemNo(itemNoList);
        SupplierItemEntity supplierItemEntity = supplierItemMapper.queryOne(supplierItem);
        log.info("res:{}", JsonParser.asJson(supplierItemEntity));
    }

    /**
     * List查询测试
     */
    @Test
    public void queryListResponse() {
        SupplierItem supplierItem = new SupplierItem();
        List<String> itemNoList = Lists.newArrayList("20201226204656658857");
        supplierItem.setItemNo(itemNoList);
        List<SupplierItemEntity> supplierItemEntities = supplierItemMapper.queryList(supplierItem);
        log.info("res:{}", JsonParser.asJson(supplierItemEntities));
    }


    /**
     * List查询
     */
    @Test
    public void queryListResponseBaseResp() {
        SupplierItem supplierItem = new SupplierItem();
        List<String> itemNoList = Lists.newArrayList("20201226204656658857");
        supplierItem.setItemNo(itemNoList);
        BaseResp<SupplierItemEntity> resp = supplierItemMapper.queryByParam(supplierItem);
        log.info("res:{}", JsonParser.asJson(resp));
    }

    /**
     * 自定义结果查询测试
     */
    @Test
    public void querySupplierItemResExtend() {
        SupplierItemResExtend supplierItemResExtend = new SupplierItemResExtend();
        List<String> itemNoList = Lists.newArrayList("20201226204656658857");
        supplierItemResExtend.setItemNoList(itemNoList);
        supplierItemResExtend.setStatus(Sign.DEFAULT_INTER);
        List<AggEntityExtend> aggEntityExtends = supplierItemMapper.queryAggs(supplierItemResExtend);
        log.info("res:{}", JsonParser.asJson(aggEntityExtends));
    }

    /**
     * 辅助查询测试
     */
    @Test
    public void querySearchResponse() {
        SupplierItemProxyResExtend supplierItemResExtend = new SupplierItemProxyResExtend();
        List<String> itemNoList = Lists.newArrayList("20201226204656658857");
        supplierItemResExtend.setItemNoList(itemNoList);
        SearchResponse searchResponse = supplierItemMapper.querySearchResponse(supplierItemResExtend);
        log.info("res:{}", JsonParser.asJson(searchResponse));
    }

}
