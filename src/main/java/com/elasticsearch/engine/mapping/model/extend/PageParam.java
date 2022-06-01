package com.elasticsearch.engine.mapping.model.extend;

import com.elasticsearch.engine.model.domain.EsComplexParam;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

import java.util.LinkedHashMap;

/**
 * @author wanghuan
 * @description: PageParam
 * @date 2022-01-26 11:28
 */
@Data
public class PageParam implements EsComplexParam {

    /**
     * 当前页码
     */
    private int currentPage = 1;

    private int pageSize = 10;

    private LinkedHashMap<String, SortOrder> orderMap;

    public static PageBuilder builderPage() {
        return new PageBuilder();
    }

    public static OrderBuilder builderOrder() {
        return new OrderBuilder();
    }

    public int getExclude() {
        return Math.max((currentPage - 1), 0) * pageSize;
    }

    public static class PageBuilder {

        private int currentPage;

        private int pageSize;

        private LinkedHashMap<String, SortOrder> orderMap = new LinkedHashMap<>();

        public PageBuilder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PageBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PageBuilder order(OrderBuilder order) {
            this.orderMap.put(order.orderFiled, order.orderType);
            return this;
        }

        public PageParam build() {
            PageParam param = new PageParam();
            param.setCurrentPage(this.currentPage);
            param.setPageSize(this.pageSize);
            param.setOrderMap(orderMap);
            return param;
        }
    }

    public static class OrderBuilder {

        private String orderFiled;

        private SortOrder orderType;

        public OrderBuilder orderFiled(String orderFiled) {
            this.orderFiled = orderFiled;
            return this;
        }

        public OrderBuilder orderType(SortOrder orderType) {
            this.orderType = orderType;
            return this;
        }

        public OrderBuilder build() {
            OrderBuilder param = new OrderBuilder();
            param.orderFiled(this.orderFiled);
            param.orderType(this.orderType);
            return param;
        }
    }
}
