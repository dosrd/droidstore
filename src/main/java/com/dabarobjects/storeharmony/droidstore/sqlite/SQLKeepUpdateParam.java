package com.dabarobjects.storeharmony.droidstore.sqlite;

/**
 * Created by dabarobjects on 29/08/2018.
 */

public interface SQLKeepUpdateParam<T> {
    public String getUpdateWhereQuery() throws NoKeyFoundException;


    public String[] getWhereArgsParams() throws NoKeyFoundException;

    public void setClassObject(T t);
}
