package com.elasticsearch.engine.elasticsearchengine.execute;

import com.elasticsearch.engine.elasticsearchengine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.ann.model.EsBaseExecuteHandle;
import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.*;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.PageParam;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.RangeParam;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.SignParam;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Sign;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询引擎 查询测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class EsEngineQueryTest {

    @Resource
    private EsBaseExecuteHandle esBaseExecuteHandle;

    /**
     * 测试 Trem Trems Range
     */
    @Test
    public void findByProductNameAndStatusTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setStatus(RangeParam.builder().left(1).right(2).build());
        List<String> itemNoList = Lists.newArrayList();
        supplierItem.setItemNo(itemNoList);
        supplierItem.setProductName("k41");
        supplierItem.setWarehousePrice(new BigDecimal("22.01"));
        supplierItem.setCreateDt(RangeParam.builder().left(LocalDateTime.now()).right(LocalDateTime.now().plusDays(1L)).build());
        esBaseExecuteHandle.execute(supplierItem);
    }


    /**
     * 测试from to 及后缀
     */
    @Test
    public void fromToTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setCreateDtStart(LocalDateTime.now());
        supplierItem.setEndCreateDt(LocalDateTime.now());
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试WildCard
     */
    @Test
    public void wildCardTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setProductName("k41");
        esBaseExecuteHandle.execute(supplierItem);
    }


    /**
     * 测试prefix
     */
    @Test
    public void prefixTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setSkuName("小米");
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试分页
     */
    @Test
    public void pageTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setPageParam(PageParam.builderPage().currentPage(2).pageSize(3).build());
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试分页+排序
     */
    @Test
    public void pageAndOrderTest() {
        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setPageParam(PageParam.builderPage().currentPage(2).pageSize(3)
                .order(PageParam.builderOrder().orderFiled("status").orderType(SortOrder.ASC))
                .order(PageParam.builderOrder().orderFiled("create_dt").orderType(SortOrder.ASC))
                .build());
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 单个排序测试
     */
    @Test
    public void orderTest() {
        SupplierItemSort supplierItem = new SupplierItemSort();
        supplierItem.setProductName("k41");
        supplierItem.setStatus(SignParam.builder());
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 多个排序并指定顺序测试
     */
    @Test
    public void sortOrderTest() {
        SupplierItemSortOrder supplierItem = new SupplierItemSortOrder();
        supplierItem.setProductName("k41");
        supplierItem.setStatus(SignParam.builder());
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试分组 aggs
     */
    @Test
    public void aggTest() {
        SupplierItemAggs supplierItem = new SupplierItemAggs();
        supplierItem.setStatus(Sign.DEFAULT_INTER);
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试查询参数后缀
     */
    @Test
    public void collapseTest() {
        SupplierItem supplierItem = new SupplierItem();
        List<String> itemNoList = Lists.newArrayList("6547831", "6547832");
        supplierItem.setItemNoList(itemNoList);
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试ignore
     */
    @Test
    public void ignoreTest() {
        SupplierItemIgnore supplierItem = new SupplierItemIgnore();
        List<String> itemNoList = Lists.newArrayList("6547831", "6547832");
        supplierItem.setItemNo(itemNoList);
        supplierItem.setProductName("k41");
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试自定义扩展查询
     */
    @Test
    public void extendTest() {
        SupplierItemReqExtend supplierItem = new SupplierItemReqExtend();
        List<String> itemNoList = Lists.newArrayList("6547831", "6547832");
        supplierItem.setStatus(1);
        supplierItem.setItemNoList(itemNoList);
        esBaseExecuteHandle.execute(supplierItem, Object.class);
    }

    /**
     * 嵌套查询对象 扩展测试
     */
    @Test
    public void nestedExtendTest() {
        SupplierItemNestedExtend supplierItemNestedExtend = new SupplierItemNestedExtend();
        SupplierItemReqExtend supplierItem = new SupplierItemReqExtend();
        List<String> itemNoList = Lists.newArrayList("6547831", "6547832");
        supplierItem.setStatus(1);
        supplierItem.setItemNoList(itemNoList);
        supplierItemNestedExtend.setSupplierItemReqExtend(supplierItem);
        supplierItemNestedExtend.setProductName("123");
        esBaseExecuteHandle.execute(supplierItemNestedExtend, Object.class);
    }

    /**
     * 测试should
     */
    @Test
    public void shouldTest() {
        SupplierItemShould supplierItem = new SupplierItemShould();
        supplierItem.setItemNo("1");
        supplierItem.setStatus(1);
        supplierItem.setProductName("k41");
        esBaseExecuteHandle.execute(supplierItem);
    }

    /**
     * 测试 exist
     */
    @Test
    public void existTest() {
        SupplierItemExist supplierItemExist = new SupplierItemExist();
        supplierItemExist.setStatus(Sign.DEFAULT_STRING);
        supplierItemExist.setProductName(Sign.DEFAULT_STRING);
        esBaseExecuteHandle.execute(supplierItemExist);
    }

    /**
     * 测试多range
     */
    @Test
    public void rangeGroupTest() {
        RangeGroup rangeGroup = new RangeGroup();
        rangeGroup.setCreateDtStart(LocalDateTime.now());
        rangeGroup.setEndCreateDt(LocalDateTime.now().plusDays(1L));
        rangeGroup.setStartStatus(1);
        rangeGroup.setEndStatus(3);
        esBaseExecuteHandle.execute(rangeGroup);
    }
}
