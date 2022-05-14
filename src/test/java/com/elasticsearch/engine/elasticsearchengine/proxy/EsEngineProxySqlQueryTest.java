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

        String s = esSqlExecuteHandler.queryBySql(sql, SqlFormat.JSON);
        System.out.println(s);

        //@Query(value = "insert into studenttb(student_name,student_age) value(?1,?2)", nativeQuery = true)
        List<SupplierItemEntity> supplierItemEntities = esSqlExecuteHandler.queryBySql(sql, SupplierItemEntity.class);
        System.out.println(JsonParser.asJson(supplierItemEntities));
    }

    @Test
    public void trs() {
        //常规
        String sql1 = "select * from supplier_item_spare where item_no='20201226204656658857'";
        //like
        String sql2 = "select * from supplier_item_spare where product_name  like '%机%' ";
        //group by
        String sql3 = "select status from supplier_item_spare group by status";
        //sum
        String sql4 = "select sum(status) from supplier_item_spare";
        //count
        String sql5 = "select count(1) from supplier_item_spare";
        //having
        String sql6 = "select status,count(*) as count  from supplier_item_spare group by status having count>0";
        
        /**
         * 不支持的sql
         */
        String sqlError1 = "select distinct status from supplier_item_spare";
        String sqlError2 = "SELECT * FROM supplier_item_spare i inner join supplier_item_spare d WHERE iitem_no = '20201226204656658857' and status=1";

        String s1 = esSqlExecuteHandler.querySqlTranslate(sql1, SqlFormat.JSON);
        System.out.println("sql1: " +s1);
        String s2 = esSqlExecuteHandler.querySqlTranslate(sql2, SqlFormat.JSON);
        System.out.println("sql2: " +s2);
        String s3 = esSqlExecuteHandler.querySqlTranslate(sql3, SqlFormat.JSON);
        System.out.println("sql3: " +s3);
        String s4 = esSqlExecuteHandler.querySqlTranslate(sql4, SqlFormat.JSON);
        System.out.println("sql4: " +s4);
        String s5 = esSqlExecuteHandler.querySqlTranslate(sql5, SqlFormat.JSON);
        System.out.println("sql5: " +s5);


        String se1 = esSqlExecuteHandler.querySqlTranslate(sqlError1, SqlFormat.JSON);
        System.out.println("sqlError1: " +se1);
        String se2 = esSqlExecuteHandler.querySqlTranslate(sqlError2, SqlFormat.JSON);
        System.out.println("sqlError2: " +se2);

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