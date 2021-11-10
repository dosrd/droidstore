package com.dabarobjects.storeharmony.droidstore.sqlite;

/**
 * Created by dabarobjects on 23/08/2018. SQLKeepQueryParam
 */

public abstract class AbstractSQLQueryParameter<T> implements SQLKeepQueryParam<T> {

    @Override
    public String getQueryOrdering() {
        return null;
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public int getMaxQuerySize() {
        return 1000;
    }
}
