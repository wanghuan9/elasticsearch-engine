# elasticsearch-engine

## introduce

elasticsearch-engine is an ElasticSearch query engine framework encapsulated based on HighLevelRestClient. Supports ElasticSearch annotation-based structured query;
Query based on sql statement;
And integrate common ORM framework, provide Mapper interface based on ORM framework to automatically generate ElasticSearch Sql query statement, and execute ElasticSearch query;

**You can implement ElasticSearch query by marking an annotation on the Mapper interface that needs to query ElasticSearch without additional code development;
And you can dynamically switch the query between ElasticSearch and Mysql through the configuration center configuration,
Implement ElasticSearch query downgrade.**

## Main features

1. Implement elasticsearch query based on annotations

2. Implement elasticsearch query based on sql statement

3. Automatically generate elasticsearch queries based on the mybatis mapper interface, and support database return table queries

4. Automatically generate elasticsearch query based on jpa repository interface, and support database return table query

5. Automatically generate elasticsearch query based on jooq dao implementation class, and support database return table query

## Architecture modules

1. elasticsearch-engine-base provides basic functions such as annotation query, sql statement query, ORM query sql parsing, sql rewriting and so on
2. elasticsearch-engine-mybatis implements sql interception, rewriting, and execution of elasticsearch queries based on mybatis interceptor
3. elasticsearch-engine-jpa is based on aop, hibernate sql interceptor and re-jpa parameter binding module to realize sql interception, rewrite and execute elasticsearch query
4. elasticsearch-engine-jooq is based on aop, jooq execution listener to implement sql interception, rewrite, and execute elasticsearch queries

## Instructions for use
For all complete examples, please refer to [Example](https://gitee.com/my-source-project/elasticsearch-engine-demo)

### 1. Annotation query

#### 1.1 Complex parameters

1) Add maven dependencies

````xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
````

2) Define the query model

````java
package com.elasticsearch.engine.demo.dto.query;

import com.elasticsearch.engine.base.mapping.annotation.*;
import com.elasticsearch.engine.base.mapping.model.extend.PageParam;
import com.elasticsearch.engine.base.mapping.model.extend.RangeParam;
import com.elasticsearch.engine.base.mapping.model.extend.SignParam;
import com.elasticsearch.engine.base.model.annotion.Base;
import com.elasticsearch.engine.base.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.base.model.annotion.Ignore;
import com.elasticsearch.engine.base.model.emenu.EsConnector;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghuan
 * @description: Basic test for parsing query annotations
 * @mail 958721894@qq.com
 * @date 2022-05-31 22:40
 */
@EsQueryIndex(value = "person_es_index")
@Data
public class PersonBaseQuery {

    @Term
    private BigDecimal salary;

    @Terms(value = @Base("item_no"))
    private List<String> personNos;

    @Terms
    private List<String> personNoList;

    @Range(value = @Base(value = "status", connect = EsConnector.SHOULD), tag = Range.LE_GE)
    private RangeParam rangeStatus;

    @Range
    private RangeParam createTime;

    @WildCard
    private String address;

    @Prefix
    private String personName;

    @To(@Base("create_time"))
    private LocalDateTime createTimeEnd;

    @From(value = @Base("create_time"))
    private LocalDateTime createTimeStart;

    @PageAndOrder
    private PageParam pageParam;

    /**
     * Mark annotations do not parse the value, only parse the annotation value
     * It is necessary to set the value value to be not empty, the query condition will take effect, but the set value will not be parsed, just mark whether to add the condition
     * So value can be set arbitrarily, but note that string cannot be an empty string, and the array type cannot be null
     *
     * SignParam represents an unparsed parameter value type
     * can also be represented using Sign.DEFAULT_STRING
     */
    @Sort
    private SignParam sortStatus;

    @Aggs(value = @Base("status"), type = Aggs.COUNT_DESC)
    private SignParam groupStatus;

    /**
     * Indicates that a field is ignored, and the ignored field will not be parsed when querying whether the attribute value is empty or not
     */
    @Ignore
    private String token;

}
````

3) Declare the query interface

````java

@EsQueryIndex("person_es_index")
public interface PersonEsModelRepository extends BaseESRepository<PersonEsEntity, Long> {
    /**
     * queryByMode
     *
     * @param param
     * @return
     */
    List<PersonEsEntity> queryByMode(PersonBaseQuery param);
}

````

4) Test example

````java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineProxyModelQueryTest {
    @Resource
    private PersonEsModelRepository personEsModelRepository;

    /**
     * model query test
     */
    @Test
    public void queryByModelTest() {
        PersonBaseQuery person = new PersonBaseQuery();
        person.setPageParam(PageParam.builderPage().currentPage(1).pageSize(100).build());
        person.setSalary(new BigDecimal("67700"));
        person.setPersonName("Zhang");
        person.setAddress("Tianfu");
        person.setCreateTimeStart(LocalDateTime.now().minusDays(300));
        person.setCreateTimeEnd(LocalDateTime.now());
        List<PersonEsEntity> res = personEsModelRepository.queryByMode(person);
        log.info("res:{}", JsonParser.asJson(res));
    }
}
````