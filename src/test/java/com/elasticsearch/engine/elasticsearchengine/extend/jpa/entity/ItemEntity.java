package com.elasticsearch.engine.elasticsearchengine.extend.jpa.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity // jpa的注解，需要加
@Table(name = "items") // 指定数据库的表名
public class ItemEntity {

    
    @Id
    private String itemNo;

    private Integer status;

    private String productName;

//    private BigDecimal warehousePrice;

    private LocalDateTime createDt;

}

