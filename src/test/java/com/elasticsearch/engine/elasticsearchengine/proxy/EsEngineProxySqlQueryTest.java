package com.elasticsearch.engine.elasticsearchengine.proxy;

import com.elasticsearch.engine.elasticsearchengine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.SqlFormat;
import com.elasticsearch.engine.elasticsearchengine.proxy.repository.SupplierItemSqlRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghuan
 * @description: sql查询测试示例
 * @date 2022-04-24 18:42
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class EsEngineProxySqlQueryTest {

    @Resource
    private EsSqlExecuteHandler esSqlExecuteHandler;

    @Resource
    private SupplierItemSqlRepository supplierItemSqlRepository;


    @Test

    public void testSql1() {

        String sql = "select * from supplier_item_spare where item_no='20201226204656658857'";

        String s = esSqlExecuteHandler.queryBySQL(sql, SqlFormat.JSON);
        System.out.println(s);

        //@Query(value = "insert into studenttb(student_name,student_age) value(?1,?2)", nativeQuery = true)
        List<SupplierItemEntity> supplierItemEntities = esSqlExecuteHandler.queryBySQL(sql, SupplierItemEntity.class);
        System.out.println(JsonParser.asJson(supplierItemEntities));
    }

    @Test
    public void trs() {
        String sql = "select * from supplier_item_spare where item_no='20201226204656658857'";
        String sql2 = "select status from supplier_item_spare group by status";
        String sql3= "select sum(status) from supplier_item_spare";

        /**
         * 不支持的sql
         */
        String sqlError1 = "select distinct status from supplier_item_spare";
        String sqlError2 = "SELECT * FROM supplier_item_spare i inner join supplier_item_spare d WHERE iitem_no = '20201226204656658857' and status=1";

        String s = esSqlExecuteHandler.querySqlTranslate(sql, SqlFormat.JSON);
        System.out.println(s);
        String s2 = esSqlExecuteHandler.querySqlTranslate(sql2, SqlFormat.JSON);
        System.out.println(s2);
        String s3 = esSqlExecuteHandler.querySqlTranslate(sql3, SqlFormat.JSON);
        System.out.println(s3);


        String s4 = esSqlExecuteHandler.querySqlTranslate(sqlError1, SqlFormat.JSON);
        System.out.println(s4);
        String s5 = esSqlExecuteHandler.querySqlTranslate(sqlError2, SqlFormat.JSON);
        System.out.println(s5);

    }


    @Test
    public void testSql2() {
        SupplierItemEntity supplierItemEntity = supplierItemSqlRepository.queryOne("20201226204656658857", 1);
        System.out.println(JsonParser.asJson(supplierItemEntity));
    }

    @Test
    public void testSql3() {
        List<SupplierItemEntity> supplierItemEntities = supplierItemSqlRepository.queryList(Lists.newArrayList("20201226204656658857"));
        System.out.println(JsonParser.asJson(supplierItemEntities));
    }

    @Test
    public void testSql4() {
        SupplierItemEntity supplierItemEntity = supplierItemSqlRepository.queryByCreateDt(LocalDateTime.now());
        System.out.println(JsonParser.asJson(supplierItemEntity));
    }

    @Test
    public void testSql5() {
        Long count = supplierItemSqlRepository.count(LocalDateTime.now());
        System.out.println(JsonParser.asJson(count));
    }


}