package com.elasticsearch.engine.proxy.entity.condition;


import com.elasticsearch.engine.mapping.handler.EsConditionHandle;
import com.elasticsearch.engine.mapping.model.extend.RangeParam;

import java.util.Objects;

/**
 * AgeUseCondition
 *
 * @author JohenTeng
 * @date 2021/12/9
 */
public class AgeUseCondition implements EsConditionHandle<RangeParam> {

    @Override
    public boolean test(RangeParam val) {
        if (Objects.isNull(val)) {
            return false;
        }
        Object left = val.getLeft();
        if (Objects.nonNull(left)) {
            int leftVal = (int) left;
            if (leftVal >= 20) {
                return true;
            }
        }
        return false;
    }
}
