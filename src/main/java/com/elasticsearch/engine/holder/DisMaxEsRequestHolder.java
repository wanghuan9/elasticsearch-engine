package com.elasticsearch.engine.holder;

import com.elasticsearch.engine.model.emenu.EsConnector;
import org.elasticsearch.index.query.DisMaxQueryBuilder;

/**
 * @author wanghuan
 * @description: DisMaxEsRequestHolder
 * @date 2022-01-26 11:28
 */
public class DisMaxEsRequestHolder extends AbstractEsRequestHolder<DisMaxQueryBuilder> {

    @Override
    public AbstractEsRequestHolder changeLogicConnector(EsConnector connector) {
        return this;
    }

    @Override
    protected void defineDefaultLogicConnector() {

    }

    @Override
    protected void defineQueryBuilder() {
    }
}
