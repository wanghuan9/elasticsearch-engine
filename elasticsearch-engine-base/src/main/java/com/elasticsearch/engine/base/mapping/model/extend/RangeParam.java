package com.elasticsearch.engine.base.mapping.model.extend;


import com.elasticsearch.engine.base.model.domain.EsComplexParam;
import lombok.Data;

import javax.annotation.Nullable;

/**
 * @author wanghuan
 * @description: RangeParam
 * @date 2022-01-26 11:28
 */
@Data
public class RangeParam implements EsComplexParam {
    /**
     * from
     */
    @Nullable
    private Object left;

    /**
     * to
     */
    @Nullable
    private Object right;

    public static RangeBuilder builder() {
        return new RangeBuilder();
    }

    public static class RangeBuilder {

        private Object left;

        private Object right;

        public RangeBuilder left(Object left) {
            this.left = left;
            return this;
        }

        public RangeBuilder right(Object right) {
            this.right = right;
            return this;
        }

        public RangeParam build() {
            RangeParam param = new RangeParam();
            param.setLeft(this.left);
            param.setRight(this.right);
            return param;
        }
    }
}
