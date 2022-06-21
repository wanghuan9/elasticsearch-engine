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
5) Query effect

````json
{
  "from": 0,
  "size": 100,
  "timeout": "10s",
  "query": {
    "bool": {
      "filter": [
        {
          "wildcard": {
            "address": {
              "wildcard": "*Tianfu*"
            }
          }
        },
        {
          "prefix": {
            "personName": {
              "value": "Zhang"
            }
          }
        },
        {
          "term": {
            "salary": {
              "value": 67700
            }
          }
        },
        {
          "range": {
            "create_time": {
              "from": "2021-08-23T21:17:23.385Z",
              "to": "2022-06-19T21:17:23.385Z",
              "include_lower": true,
              "include_upper": true,
              "time_zone": "+08:00",
              "format": "8uuuu-MM-dd'T'HH:mm:ss.SSS'Z'"
            }
          }
        }
      ]
    }
  }
}
````

#### 1.2 Simple parameters

1) Declare the query interface

````java

@EsQueryIndex(value = "person_es_index")
public interface PersonEsParamRepository extends BaseESRepository<PersonEsEntity, Long> {
    /**
     * List query
     *
     * @return
     */
    List<PersonEsEntity> queryList(@Terms List<String> personNoList);
}
````

2) Test example

````java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineProxyModelQueryTest {
    @Resource
    private PersonEsParamRepository personEsParamRepository;

    /**
     * List query test
     */
    @Test
    public void queryListResponse() {
        List<String> personNoList = Lists.newArrayList("US2022060100001", "US2022060100002");
        List<PersonEsEntity> res = personEsParamRepository.queryList(personNoList);
        log.info("res:{}", JsonParser.asJson(res));
    }
}

````

3) Query effect

````json
{
  "size": 1000,
  "timeout": "10s",
  "query": {
    "bool": {
      "filter": [
        {
          "terms": {
            "personNo": [
              "US2022060100001",
              "US2022060100002"
            ]
          }
        }
      ]
    }
  }
}

````

### 2.sql query

1) Declare the query interface

````java

@EsQueryIndex("person_es_index")
public interface PersonEsSqlRepository extends BaseEsRepository<PersonEsEntity, Long> {
    /**
     * Object parameter test
     * @param person
     * @return
     */
    @EsQuery("SELECT * FROM person_es_index WHERE status = #{person.status} AND sex = #{person.sex}")
    List<PersonEntity> pageQuery(PersonEntity person);
}
````

2) Test example

````java
/**
 * Object parameter query test
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineProxySqlQueryTest {
    @Resource
    private PersonEsSqlRepository personEsSqlRepository;

    @Test
    public void testSqlPageQuery() {
        PersonEntity person = new PersonEntity();
        person.setStatus(1);
        person.setSex(1);
        List<PersonEntity> results = personEsSqlRepository.pageQuery(person);
        System.out.println(JsonParser.asJson(results));
    }
}
````

3) Query effect

````
2022-06-21 15:46:24.781INFO 52845---[main]c.e.e.b.c.q.sql.EsSqlExecuteHandler:http://localhost:9200/_sql?format=json
2022-06-21 15:46:24.781INFO 52845---[main]c.e.e.b.c.q.sql.EsSqlExecuteHandler:{"query":"SELECT * FROM person_es_index WHERE status = 1 AND sex = 1"}
````

### 3. Expand the query

#### 3.1 mybatis

1) Add maven dependencies

````xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-mybatis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

````

2) Add the corresponding es query annotation to the mapper interface

````java

@EsQueryIndex("person_es_index")
@Mapper
public interface PersonMapper {

    @MybatisEsQuery
    PersonEsEntity queryOne(@Param("personNo") String personNo, @Param("status") Integer status);
}
````

3) Test example

````java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendMybatisQueryTest {
    @Resource
    private PersonMapper personMapper;

    /**
     * single query
     */
    @Test
    public void testSqlOne() {
        PersonEsEntity personEsEntity = personMapper.queryOne("US2022060100001", 1);
        log.info("res:{}", JsonParser.asJson(personEsEntity));
    }
}
````

4) Query effect
4) Query effect

````
2022-06-21 15:54:48.017 INFO 53454 --- [main] c.e.e.m.i.MybatisEsQueryInterceptor: raw sql: SELECT * FROM person WHERE person_no = ? AND status = ?
2022-06-21 15:54:48.075 INFO 53454 --- [main] c.e.e.m.i.MybatisEsQueryInterceptor : After rewriting sql: SELECT * FROM person_es_index WHERE personNo = ? AND status = ?
2022-06-21 15:54:48.076 INFO 53454 --- [main] c.e.e.m.i.MybatisEsQueryInterceptor : After replacing parameters sql: SELECT * FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1
2022-06-21 15:54:48.076 INFO 53454 --- [main] c.e.e.b.c.q.sql.EsSqlExecuteHandler: http://localhost:9200/_sql?format=json
2022-06-21 15:54:48.076 INFO 53454 --- [main] c.e.e.b.c.q.sql.EsSqlExecuteHandler : {"query":"SELECT * FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1"}
````

#### 3.2 jpa

1) Add maven dependencies

````xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-jpa</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

````

2) Add the corresponding es query annotation to the repository interface

````java

@EsQueryIndex("person_es_index")
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    @JpaEsQuery
    PersonEntity getByPersonNoAndStatus(String personNo, Integer status);
}
````

3) Test example

````java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendJpaQueryTest {
    @Resource
    private PersonRepository personRepository;

    /**
     * single query
     */
    @Test
    public void testSqlOne() {
        PersonEntity personEntity = personRepository.getByPersonNoAndStatus("US2022060100001", 1);
        log.info("res:{}", JsonParser.asJson(personEntity));
    }
}
````

4) Query effect

````
2022-06-21 16:00:20.962 INFO 53773 --- [main] c.e.e.b.c.parse.sql.EsSqlQueryHelper : raw sql: select personenti0_.id as id1_1_, personenti0_.address as address2_1_, personenti0_.company as company3_1_, personenti0_.create_time as create_t4_1_, personenti0_.create_user as create_u5_1_, personenti0_.person_name as person_n6_1_, personenti0_.person_no as person_n7_1_, personenti0_.phone as phone8_1_, personenti0_.salary as salary9_1_, personenti0_.sex as sex10_1_, personenti0_.status as status11_1_ from person personenti0_ where personenti0 person_no='US2022060100001' and personenti0_.status=1
2022-06-21 16:00:21.008 INFO 53773 --- [ main] c.e.e.b.c.parse.sql.EsSqlQueryHelper : After rewriting sql: SELECT id, address, company, createTime, createUser, personName, personNo, phone, salary, sex, status FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1
2022-06-21 16:00:21.009 INFO 53773 --- [ main] c.e.e.b.c.parse.sql.EsSqlQueryHelper : sql after replacing parameters: SELECT id, address, company, createTime, createUser, personName, personNo, phone, salary, sex , status FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1
2022-06-21 16:00:21.010 INFO 53773 --- [main] c.e.e.b.c.q.sql.EsSqlExecuteHandler : http://localhost:9200/_sql?format=json
2022-06-21 16:00:21.010 INFO 53773 --- [ main] c.e.e.b.c.q.sql.EsSqlExecuteHandler : {"query":"SELECT id, address, company, createTime, createUser, personName, personNo, phone, salary, sex, status FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1"}
````

#### 3.3 jooq

1) Add maven dependencies

````xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-jooq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

````

2) The dao implementation class adds the corresponding es query annotation

````java

@EsQueryIndex("person_es_index")
@Component
public class PersonJooqDaoImpl implements PersonJooqDao {

    @Autowired
    private DSLContext context;

    private final Person PERSON = Tables.PERSON;

    /**
     * @param personNo
     * @param status
     * @return
     */
    @JooqEsQuery
    @Override
    public PersonEntity getByPersonNoAndStatus(String personNo, Integer status) {
        return context.selectFrom(PERSON).where(
                PERSON.PERSON_NO.eq(personNo).and(PERSON.STATUS.eq(status.byteValue()))
        ).fetchOneInto(PersonEntity.class);
    }
}
````

3) Test example

````java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendJooqQueryTest {
    @Resource
    private PersonJooqDao personJooqDao;

    /**
     * single query
     */
    @Test
    public void testSqlOne() {
        PersonEntity personEntity = personJooqDao.getByPersonNoAndStatus("US2022060100001", 4);
        log.info("res:{}", JsonParser.asJson(personEntity));
    }
}
````
4) Query effect

````
2022-06-21 16:03:05.629 INFO 53945 --- [main] c.e.e.b.c.parse.sql.EsSqlQueryHelper: raw sql: select
  `user`.`person`.`id`,
  `user`.`person`.`person_no`,
  `user`.`person`.`person_name`,
  `user`.`person`.`phone`,
  `user`.`person`.`salary`,
  `user`.`person`.`company`,
  `user`.`person`.`status`,
  `user`.`person`.`sex`,
  `user`.`person`.`address`,
  `user`.`person`.`create_time`,
  `user`.`person`.`create_user`
from `user`.`person`
where (
  `user`.`person`.`person_no` = 'US2022060100001'
  and `user`.`person`.`status` = 4
)
2022-06-21 16:03:05.674 INFO 53945 --- [main] c.e.e.b.c.parse.sql.EsSqlQueryHelper : rewritten sql: SELECT `id`, `personNo`, `personName`, `phone`, `salary`, `company`, `status`, `sex`, `address`, `createTime`, `createUser` FROM person_es_index WHERE (`personNo` = 'US2022060100001' AND `status` = 4)
2022-06-21 16:03:05.675 INFO 53945 --- [main] c.e.e.b.c.parse.sql.EsSqlQueryHelper : After replacing parameters sql: SELECT id, personNo, personName, phone, salary, company, status, sex, address, createTime , createUser FROM person_es_index WHERE (personNo = 'US2022060100001' AND status = 4)
2022-06-21 16:03:05.676 INFO 53945 --- [main] c.e.e.b.c.q.sql.EsSqlExecuteHandler: http://localhost:9200/_sql?format=json
2022-06-21 16:03:05.676 INFO 53945 --- [ main] c.e.e.b.c.q.sql.EsSqlExecuteHandler : {"query":"SELECT id, personNo, personName, phone, salary, company, status, sex, address, createTime, createUser FROM person_es_index WHERE (personNo = 'US2022060100001' AND status = 4)"}
````

## Usage example

https://gitee.com/my-source-project/elasticsearch-engine-demo

https://github.com/wanghuan9/elasticsearch-engine-demo

## Related documents

To be completed...

## Compatibility

elasticsearch field naming supports camel case and underscore

elasticsearch version supports v6 and v7

## References and references

The annotation query of this project refers to the open source project https://gitee.com/JohenTeng/elasticsearch-helper