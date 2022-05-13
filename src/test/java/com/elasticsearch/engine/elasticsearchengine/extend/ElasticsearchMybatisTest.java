package com.elasticsearch.engine.elasticsearchengine.extend;

import com.elasticsearch.engine.elasticsearchengine.ElasticsearchEngineApplicationTests;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.extend.mybatis.mapper.ItemsMapper;
import com.elasticsearch.engine.elasticsearchengine.model.exception.ItemsEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-07 23:44
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchEngineApplicationTests.class)
public class ElasticsearchMybatisTest {

    @Resource
    private ItemsMapper itemsMapper;

    @Test
    public void test() {
        ItemsEntity byItemNo = itemsMapper.getByItemNo("20201226204656658857","1");
        System.out.println(JsonParser.asJson(byItemNo));

    }
}
