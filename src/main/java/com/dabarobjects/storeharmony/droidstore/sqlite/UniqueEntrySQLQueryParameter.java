package com.dabarobjects.storeharmony.droidstore.sqlite;

import java.lang.reflect.Field;

public class UniqueEntrySQLQueryParameter<T> extends AbstractSQLQueryParameter<T> {


    private T object;
    Object keyId;
    String keyIdName;


    @Override
    public String getQuery() {
        return keyIdName +
                "=?";
    }

    @Override
    public String[] getQueryParams() {
        return new String[]{keyId.toString()};
    }

    @Override
    public void setClassObject(T t) {
        this.object = t;
        boolean pid = false;
        Field[] ifields = object.getClass().getDeclaredFields();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();

            KeepId aa = cf.getAnnotation(KeepId.class);

            if (aa != null) {
                keyIdName = fname.toUpperCase();
                cf.setAccessible(true);
                try {
                    keyId = cf.get(object);
                    pid = true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        if(!pid){
            throw new RuntimeException("Key ID not found in domain class: "+ t.getClass().getName());
        }
    }
}
