package com.elasticsearch.engine.elasticsearchengine.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SqlResponse {
    @JsonProperty("columns")
    private List<ColumnsDTO> columns;
    @JsonProperty("rows")
    private List<List<String>> rows;

    public List<ColumnsDTO> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnsDTO> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public static class ColumnsDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("type")
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
