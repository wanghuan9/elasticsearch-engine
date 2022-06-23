# elasticsearch-engine

## 介绍

elasticsearch-engine是基于 HighLevelRestClient 封装的 ElasticSearch 查询引擎框架. 支持ElasticSearch基于注解的结构化查询;
基于sql语句的方式查询;
并整合常见的ORM框架, 提供基于ORM框架的Mapper接口自动生成ElasticSearch Sql查询语句,并执行ElasticSearch查询;

**在需要查询 ElasticSearch 的Mapper接口标注一个注解即可实现 ElasticSearch 查询,无需额外的代码开发;
并可以通过配置中心配置动态切换ElasticSearch和Mysql之间的查询,
实现ElasticSearch查询降级.**

github地址: https://github.com/wanghuan9/elasticsearch-engine

## 主要功能特性

1. 基于注解的方式实现elasticsearch的查询

2. 基于sql语句的方式实现elasticsearch的查询

3. 基于mybatis mapper接口 自动生成elasticsearch查询,并支持数据库回表查询

4. 基于jpa repository接口 自动生成elasticsearch查询,并支持数据库回表查询

5. 基于jooq dao实现类 自动生成elasticsearch查询,并支持数据库回表查询

## 架构模块

1. elasticsearch-engine-base 提供注解查询,sql语句查询,ORM查询sql解析,sql改写等基础功能
2. elasticsearch-engine-mybatis 基于mybatis拦截器 实现sql拦截,改写,执行elasticsearch查询
3. elasticsearch-engine-jpa 基于aop,hibernate sql拦截器以及重新jpa参数绑定模块 实现sql拦截,改写,执行elasticsearch查询
4. elasticsearch-engine-jooq 基于aop,jooq执行监听器 实现sql拦截,改写,执行elasticsearch查询

## 使用说明
所有完整示例 请参考 [使用示例](https://gitee.com/my-source-project/elasticsearch-engine-demo)

### 1.注解查询

#### 1.1复杂参数

1)添加maven依赖

```xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

2)定义查询model

```java
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
 * @description: 解析查询注解基础测试
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
     * 标记注解不解析value,只解析注解值
     * 需要设置 value值不为空,查询条件才会生效, 但是设置的value不会被解析,仅仅标记是否添加该条件
     * 所以value可以任意设置, 但是注意 string 不能为空串,数组类型不能为null
     *
     * SignParam 表示一种无需解析参数值得 类型
     * 也可以使用 Sign.DEFAULT_STRING 表示
     */
    @Sort
    private SignParam sortStatus;

    @Aggs(value = @Base("status"), type = Aggs.COUNT_DESC)
    private SignParam groupStatus;

    /**
     * 表示忽略某个字段 ,被忽略的字段 无论属性值是否为空, 查询时都不会被解析
     */
    @Ignore
    private String token;

}
```

3)声明查询接口

```java

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

```

4)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineProxyModelQueryTest {
    @Resource
    private PersonEsModelRepository personEsModelRepository;

    /**
     * model查询测试
     */
    @Test
    public void queryByModelTest() {
        PersonBaseQuery person = new PersonBaseQuery();
        person.setPageParam(PageParam.builderPage().currentPage(1).pageSize(100).build());
        person.setSalary(new BigDecimal("67700"));
        person.setPersonName("张");
        person.setAddress("天府");
        person.setCreateTimeStart(LocalDateTime.now().minusDays(300));
        person.setCreateTimeEnd(LocalDateTime.now());
        List<PersonEsEntity> res = personEsModelRepository.queryByMode(person);
        log.info("res:{}", JsonParser.asJson(res));
    }
}
```

5)查询效果

```json
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
              "wildcard": "*天府*"
            }
          }
        },
        {
          "prefix": {
            "personName": {
              "value": "张"
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
```

#### 1.2简单参数

1)声明查询接口

```java

@EsQueryIndex(value = "person_es_index")
public interface PersonEsParamRepository extends BaseESRepository<PersonEsEntity, Long> {
    /**
     * List查询
     *
     * @return
     */
    List<PersonEsEntity> queryList(@Terms List<String> personNoList);
}
```

2)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineProxyModelQueryTest {
    @Resource
    private PersonEsParamRepository personEsParamRepository;

    /**
     * List查询测试
     */
    @Test
    public void queryListResponse() {
        List<String> personNoList = Lists.newArrayList("US2022060100001", "US2022060100002");
        List<PersonEsEntity> res = personEsParamRepository.queryList(personNoList);
        log.info("res:{}", JsonParser.asJson(res));
    }
}

```

3) 查询效果

```json
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

```

### 2.sql查询

1)声明查询接口

```java

@EsQueryIndex("person_es_index")
public interface PersonEsSqlRepository extends BaseEsRepository<PersonEsEntity, Long> {
    /**
     * 对象参数测试
     * @param person
     * @return
     */
    @EsQuery("SELECT * FROM person_es_index WHERE status = #{person.status}  AND sex = #{person.sex}")
    List<PersonEntity> pageQuery(PersonEntity person);
}
```

2)测试示例

```java
/**
 * 对象参数查询 测试
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
```

3)查询效果

```
2022-06-21 15:46:24.781INFO 52845---[main]c.e.e.b.c.q.sql.EsSqlExecuteHandler:http://localhost:9200/_sql?format=json
2022-06-21 15:46:24.781INFO 52845---[main]c.e.e.b.c.q.sql.EsSqlExecuteHandler:{"query":"SELECT * FROM person_es_index WHERE status = 1  AND sex = 1"}
```

### 3.扩展查询

#### 3.1 扩展查询说明

##### 3.1.1扩展查询原理

###### 1)普通查询
拦截orm框架执行过程中生成的sql, 对sql进行改写后, 查询es返回结果

###### 2)回表查询
拦截orm框架执行过程中生成的sql, 对sql进行改写后, 查询es返回唯一索引,通过唯一索引查询 mysql返回明细

##### 3.1.1 sql改写规则

①替换表名为es索引名

②清除关联查询

③清除from,where,group by,having,order by 中的表别名(t.xx,d.xx)

#### 3.2 扩展查询示例

##### 3.2.1 mybatis

1)添加maven依赖

```xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-mybatis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

2)mapper接口添加对应的es查询注解

```java

@EsQueryIndex("person_es_index")
@Mapper
public interface PersonMapper {

    @MybatisEsQuery
    PersonEsEntity queryOne(@Param("personNo") String personNo, @Param("status") Integer status);
}
```

3)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendMybatisQueryTest {
    @Resource
    private PersonMapper personMapper;

    /**
     * 单个查询
     */
    @Test
    public void testSqlOne() {
        PersonEsEntity personEsEntity = personMapper.queryOne("US2022060100001", 1);
        log.info("res:{}", JsonParser.asJson(personEsEntity));
    }
}
```

4)查询效果

```
2022-06-21 15:54:48.017  INFO 53454 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 原始sql: SELECT * FROM person WHERE person_no = ? AND status = ?
2022-06-21 15:54:48.075  INFO 53454 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 改写后sql: SELECT * FROM person_es_index WHERE personNo = ? AND status = ?
2022-06-21 15:54:48.076  INFO 53454 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 替换参数后sql: SELECT * FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1
2022-06-21 15:54:48.076  INFO 53454 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : http://localhost:9200/_sql?format=json
2022-06-21 15:54:48.076  INFO 53454 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : {"query":"SELECT * FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1"}
```

##### 3.2.2 jpa

1)添加maven依赖

```xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-jpa</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

2)repository接口添加对应的es查询注解

```java

@EsQueryIndex("person_es_index")
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    @JpaEsQuery
    PersonEntity getByPersonNoAndStatus(String personNo, Integer status);
}
```

3)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendJpaQueryTest {
    @Resource
    private PersonRepository personRepository;

    /**
     * 单个查询
     */
    @Test
    public void testSqlOne() {
        PersonEntity personEntity = personRepository.getByPersonNoAndStatus("US2022060100001", 1);
        log.info("res:{}", JsonParser.asJson(personEntity));
    }
}
```

4)查询效果

```
2022-06-21 16:00:20.962  INFO 53773 --- [           main] c.e.e.b.c.parse.sql.EsSqlQueryHelper     : 原始sql:  select personenti0_.id as id1_1_, personenti0_.address as address2_1_, personenti0_.company as company3_1_, personenti0_.create_time as create_t4_1_, personenti0_.create_user as create_u5_1_, personenti0_.person_name as person_n6_1_, personenti0_.person_no as person_n7_1_, personenti0_.phone as phone8_1_, personenti0_.salary as salary9_1_, personenti0_.sex as sex10_1_, personenti0_.status as status11_1_ from person personenti0_ where personenti0_.person_no='US2022060100001' and personenti0_.status=1
2022-06-21 16:00:21.008  INFO 53773 --- [           main] c.e.e.b.c.parse.sql.EsSqlQueryHelper     : 改写后sql: SELECT id, address, company, createTime, createUser, personName, personNo, phone, salary, sex, status FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1
2022-06-21 16:00:21.009  INFO 53773 --- [           main] c.e.e.b.c.parse.sql.EsSqlQueryHelper     : 替换参数后sql: SELECT id, address, company, createTime, createUser, personName, personNo, phone, salary, sex, status FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1
2022-06-21 16:00:21.010  INFO 53773 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : http://localhost:9200/_sql?format=json
2022-06-21 16:00:21.010  INFO 53773 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : {"query":"SELECT id, address, company, createTime, createUser, personName, personNo, phone, salary, sex, status FROM person_es_index WHERE personNo = 'US2022060100001' AND status = 1"}
```

##### 3.2.3 jooq

1)添加maven依赖

```xml

<dependency>
    <groupId>com.elasticsearch.engine</groupId>
    <artifactId>elasticsearch-engine-jooq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

2)dao实现类添加对应的es查询注解

```java

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
```

3)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendJooqQueryTest {
    @Resource
    private PersonJooqDao personJooqDao;

    /**
     * 单个查询
     */
    @Test
    public void testSqlOne() {
        PersonEntity personEntity = personJooqDao.getByPersonNoAndStatus("US2022060100001", 4);
        log.info("res:{}", JsonParser.asJson(personEntity));
    }
}
```

4)查询效果

```
2022-06-21 16:03:05.629  INFO 53945 --- [           main] c.e.e.b.c.parse.sql.EsSqlQueryHelper     : 原始sql: select 
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
2022-06-21 16:03:05.674  INFO 53945 --- [           main] c.e.e.b.c.parse.sql.EsSqlQueryHelper     : 改写后sql: SELECT `id`, `personNo`, `personName`, `phone`, `salary`, `company`, `status`, `sex`, `address`, `createTime`, `createUser` FROM person_es_index WHERE (`personNo` = 'US2022060100001' AND `status` = 4)
2022-06-21 16:03:05.675  INFO 53945 --- [           main] c.e.e.b.c.parse.sql.EsSqlQueryHelper     : 替换参数后sql: SELECT id, personNo, personName, phone, salary, company, status, sex, address, createTime, createUser FROM person_es_index WHERE (personNo = 'US2022060100001' AND status = 4)
2022-06-21 16:03:05.676  INFO 53945 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : http://localhost:9200/_sql?format=json
2022-06-21 16:03:05.676  INFO 53945 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : {"query":"SELECT id, personNo, personName, phone, salary, company, status, sex, address, createTime, createUser FROM person_es_index WHERE (personNo = 'US2022060100001' AND status = 4)"}
```
##### 3.2.4 关联查询(以mybatis为例)
###### 3.2.4.1 关联查询说明
1)应用场景

elasticsearch 存储的字段为mysql多张表聚合的字段,mysql 原本的查询为关联多表查询

###### 3.2.4.2 关联查询示例
1)mapper接口添加对应的es查询注解

```java

@EsQueryIndex("person_es_index")
@Mapper
public interface PersonExtendMapper {
    int insertList(List<PersonExtendEntity> persons);

    @MybatisEsQuery
    List<PersonEsEntity> queryList(@Param("status") Integer status, @Param("hobby")String hobby);
}

```

3)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendMybatisQueryTest {
    @Resource
    private PersonExtendMapper personExtendMapper;

    /**
     * 关联查询测试
     */
    @Test
    public void testJoinQueryList() {
        List<PersonEsEntity> results = personExtendMapper.queryList(4,"踢足球");
        System.out.println(JsonParser.asJson(results));
    }
}
```

4)查询效果

```
2022-06-23 00:23:00.012  INFO 37281 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 原始sql: SELECT * FROM person p INNER JOIN person_extend pe
        ON p.person_no = pe.person_no
        WHERE p.status = ? AND pe.hobby=?
2022-06-23 00:23:00.052  INFO 37281 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 改写后sql: SELECT * FROM person_es_index WHERE status = ? AND hobby = ?
2022-06-23 00:23:00.053  INFO 37281 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 替换参数后sql: SELECT * FROM person_es_index WHERE status = 4 AND hobby = '踢足球'
2022-06-23 00:23:00.054  INFO 37281 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : http://localhost:9200/_sql?format=json
2022-06-23 00:23:00.054  INFO 37281 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : {"query":"SELECT * FROM person_es_index WHERE status = 4 AND hobby = '踢足球'"}
```

##### 3.2.5 回表查询(以mybatis为例)
###### 3.2.5.1 回表查询说明
1)应用场景

 ① elasticsearch 存储的非全量字段,而只有搜索字段, 通过es搜索唯一索引后,再用唯一索引回表查询mysql

 ② elasticsearch 存在延迟,通过es搜索出es搜索唯一索引后,再用唯一索引回表查询mysql

2)sql改写规则

 ① es执行的sql,再原改写的基础上 改写查询字段仅查询回表字段

 ② 回表sql, 再原orm框架sql基础上拼接 es执行结果的回表查询条件

###### 3.2.5.2 回表查询示例
1)mapper接口添加对应的es查询注解

```java

@EsQueryIndex("person_es_index")
@Mapper
public interface PersonMapper {

    @MybatisEsQuery(backColumn = "id",backColumnType = Long.class)
    List<PersonEsEntity> findBySex(@Param("sex") Integer sex);

}
```

3)测试示例

```java

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsEngineExtendMybatisQueryTest {
    @Resource
    private PersonMapper personMapper;

    /**
     * 回表查询测试 id
     */
    @Test
    public void testSqlBackById() {
        List<PersonEsEntity> results = personMapper.findBySex(1);
        System.out.println(JsonParser.asJson(results));
    }
}
```

4)查询效果

```
2022-06-22 00:46:23.302  INFO 7723 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 原始sql: SELECT * FROM person WHERE  sex = ?
2022-06-22 00:46:23.347  INFO 7723 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 改写后sql: SELECT id FROM person_es_index WHERE sex = ?
2022-06-22 00:46:23.348  INFO 7723 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 替换参数后sql: SELECT id FROM person_es_index WHERE sex = 1
2022-06-22 00:46:23.349  INFO 7723 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : http://localhost:9200/_sql?format=json
2022-06-22 00:46:23.349  INFO 7723 --- [           main] c.e.e.b.c.q.sql.EsSqlExecuteHandler      : {"query":"SELECT id FROM person_es_index WHERE sex = 1"}
2022-06-22 00:46:24.480  INFO 7723 --- [           main] c.e.e.m.i.MybatisEsQueryInterceptor      : 回表sql :  SELECT * FROM person WHERE sex = ? AND id IN (7, 13, 17, 6, 9, 14, 16, 23, 24)
```

## 使用示例

https://gitee.com/my-source-project/elasticsearch-engine-demo

https://github.com/wanghuan9/elasticsearch-engine-demo

## 相关文档

待补全...

## 兼容性

elasticsearch 字段命名支持 驼峰和下划线

elasticsearch 版本支持 v6 和 v7

## 参考及引用

本项目 注解查询参考了 开源项目 https://gitee.com/JohenTeng/elasticsearch-helper (感谢大佬)