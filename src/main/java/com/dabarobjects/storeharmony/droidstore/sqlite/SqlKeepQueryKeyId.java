package com.dabarobjects.storeharmony.droidstore.sqlite;

public class SqlKeepQueryKeyId implements SQLKeepQueryParam {
    private final String keyName;
    private final String keyValue;

    public SqlKeepQueryKeyId(String keyName, String keyValue) {
        this.keyName = keyName.toUpperCase();
        this.keyValue = keyValue;
    }

    @Override
    public String getQuery() {
        return keyName.toUpperCase() +
                "=?";
    }

    @Override
    public String getQueryOrdering() {
        return keyName.toUpperCase() +
                " DESC";
    }

    @Override
    public String[] getQueryParams() {
        return new String[]{keyValue};
    }

    @Override
    public void setClassObject(Object o) {

    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public int getMaxQuerySize() {
        return 1;
    }
}
