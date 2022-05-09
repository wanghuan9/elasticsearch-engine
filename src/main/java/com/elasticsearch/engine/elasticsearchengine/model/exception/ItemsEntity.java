package com.elasticsearch.engine.elasticsearchengine.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * 物品表 数据传输实体模型
 *
 * @author David Young
 * @email yong.yang@aihuishou.com
 * @since 2020-04-17 17:00
 */
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ItemsEntity {

  private Long id;

  private String itemNo;

  private Integer type;

  private Integer merchantId;

  private Integer status;

  private String inspectionReportKey;

  private String itemCode;

  private Integer skuId;

  private String skuName;

  private Integer levelId;

  private String levelName;

}
