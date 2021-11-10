package com.dabarobjects.storeharmony.droidstore;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class KeepDataListAdaptor<T> extends BaseAdapter {
    private KeepDataEntityManager eManager;
    private Class<? extends KeepDataEntity<T>> entity;
    private List<KeepDataEntity<T>> entities;
    private Context mContext;


    public KeepDataListAdaptor(Context mContext,
                               KeepDataEntityManager eManager,
                               Class<? extends KeepDataEntity<T>> entity) {
        super();
        this.eManager = eManager;
        this.mContext = mContext;
        this.entity = entity;
        entities = this.eManager.listNativeEntities(entity);
    }

    public KeepDataEntityManager geteManager() {
        return eManager;
    }

    public Class<? extends KeepDataEntity<T>> getEntity() {
        return entity;
    }

    public List<KeepDataEntity<T>> getEntities() {
        return entities;
    }

    public Context getmContext() {
        return mContext;
    }

    public int getCount() {

        int eCount = entities.size();
        Log.v("Entity Count", "" + eCount);
        return eCount;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return entities.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public KeepDataEntity<T> getItemAtPosition(int position) {
        KeepDataEntity<T> t = (KeepDataEntity<T>) entities.get(position);
        return t;
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);
}
