package com.dabarobjects.storeharmony.droidstore;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.util.List;


public interface KeepDataEntity<T> extends Serializable {

    public void save(KeepDataEntityParamMap pMap);

    public void load(KeepDataEntityParamMap pMap);
    public List<String> getColumnNames();
    public boolean foundThis(String conditions);

    public String getStorageKey();

    public String[] getUniqueKeys();

    public T getThis();

    public String convertForTransport(String header, String pointId, String pin);

    public String convertForRendering();

    /**
     * Not to be called directly. if it is needed, it is recommended to override
     * an asbtract implementation. Also if a custom index has to be used, call EntityManager.saveCustomIndex
     * and pass the custom index
     *
     * @param index
     */
    public void setIndex(Object index);

    public Object getIndex();

    public boolean isSingleEntity();

    public void save(ContentValues pMap);

    public String[] loadColumns();

    public void loadValues(Cursor c);

    public String createScript();

    public String upgradeScript();
}
