package com.dabarobjects.storeharmony.droidstore.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by dabarobjects on 26/04/2018.
 */

public interface SQLKeepEntity<T> extends Serializable {

    public void save(ContentValues pMap);

    public String[] loadColumns();

    public void loadValues(Cursor c);

    public String createScript();

    public boolean foundThis(String conditions);

    public String getStorageKey();

    public String[] getUniqueKeys();

    public T getThis();

    public boolean isSingleEntity();
}
