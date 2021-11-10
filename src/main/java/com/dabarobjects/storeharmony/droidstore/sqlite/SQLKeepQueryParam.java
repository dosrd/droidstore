package com.dabarobjects.storeharmony.droidstore.sqlite;

/**
 * Created by dabarobjects on 27/04/2018.
 */

public interface SQLKeepQueryParam<T> {

    public String getQuery();

    public String getQueryOrdering();


    public String[] getQueryParams();

    public void setClassObject(T t);

    public int getStart();

    public int getMaxQuerySize();
}
