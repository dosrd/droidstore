package com.dabarobjects.storeharmony.droidstore.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dabarobjects.droid.utils.keep.KeepDataEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabarobjects on 26/04/2018.
 */

public class SQLKeepDataEntityManager<T> extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public Context ourContext;


    public SQLKeepDataEntityManager(Context context, String dbname) {
        super(context, "DB_" + dbname, null, DATABASE_VERSION);
        ourContext = context;

        trackedDomains = new ArrayList<>();

    }

    public void updateDomain(Class<? extends KeepDataEntity<T>> domain) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            db.execSQL(blank.createScript());

        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //db.close();

        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    private boolean checkForTableExists(SQLiteDatabase db, String table) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
    }

    public void createDomainIfNotExists(Class<? extends KeepDataEntity<T>> domain) {

        setActiveDomain(domain);
        SQLiteDatabase database = getWritableDatabase();
        try {
            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();

            String tableName = blank.getStorageKey().toUpperCase();
            boolean exists = checkForTableExists(database, tableName);

            if (!exists) {
                database.execSQL(blank.createScript());
            }else{
                Cursor c = getReadableDatabase().query(blank.getStorageKey(),
                        null, null, null, null, null, null, "0,1");

                String[] names = c.getColumnNames();

                List<String> COLS = blank.getColumnNames();
                COLS.add("REF");



                if(c.getColumnCount() -1 != blank.loadColumns().length){
                    Log.v("COLUMN_COUNT","Column length has changed: "+ tableName + " upgrading...");
                    database.execSQL(blank.upgradeScript());
                    database.execSQL(blank.createScript());
                }

            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //database.close();
        }
    }

    public void updateDomainIfExists(Class<? extends KeepDataEntity<T>> domain) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();

            String tableName = blank.getStorageKey().toUpperCase();

            boolean exists = checkForTableExists(database, tableName);

            if (!exists) {
                database.execSQL(blank.createScript());
            }else{

                database.execSQL(blank.upgradeScript());
                database.execSQL(blank.createScript());


            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            database.close();
        }
    }


    private List<Class<? extends KeepDataEntity<T>>> trackedDomains;
    public void addTrackedDomain(Class<? extends KeepDataEntity<T>> c){
        trackedDomains.add(c);
        //for testing
        //next time only when upgrade is done
        //updateDomainIfExists(c);

        createDomainIfNotExists(c);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (Class<? extends KeepDataEntity<T>> domain : trackedDomains) {
            updateDomainIfExists(domain);
        }
    }


    public void saveAll(List<KeepDataEntity<T>> items) {
        for (KeepDataEntity<T> it : items) {
            persistEntity(it);
        }
    }

    public void saveIfNotIncludedInQuery(Class<? extends KeepDataEntity<T>> domain,
                                         List<KeepDataEntity<T>> items, SQLKeepQueryParam queryParam) {
        for (KeepDataEntity<T> it : items) {
            queryParam.setClassObject(it);
            if (isAvailable(domain, queryParam)) {
                //Log.v("DB","Already available object in store: " + it);
            } else {
                persistEntity(it);
            }

        }
    }


    public void updateListIfNotAvailableInDB(Class<? extends KeepDataEntity<T>> domain, List<KeepDataEntity<T>> items) throws NoKeyFoundException {
        if (!items.isEmpty()) {
            SQLKeepUpdateParamAbstract queryParam = new SQLKeepUpdateParamAbstract(items.get(0));

            for (KeepDataEntity<T> it : items) {

                queryParam.setClassObject(it);
                if (isUpdateAvailable(domain, queryParam)) {
                    //Log.v("DB","Updating..."+it);
                    updateEntity(it, queryParam);
                } else {
                    persistEntity(it);
                }

            }
        }

    }

    public void updateItemIfNotAvailableInDB(Class<? extends KeepDataEntity<T>> domain, KeepDataEntity<T> item) throws NoKeyFoundException {
        SQLKeepUpdateParamAbstract queryParam = new SQLKeepUpdateParamAbstract(item);

        if (isUpdateAvailable(domain, queryParam)) {
            updateEntity(item, queryParam);
        } else {
            persistEntity(item);
        }
    }


    public void persistEntity(final KeepDataEntity<?> entity) {
        if(entity.getClass().getName() == "Product"){
            Log.v("PRODUCT-SAVE",entity.toString());
        }
        ContentValues cv = new ContentValues();
        entity.save(cv);
        getWritableDatabase().insert(entity.getStorageKey(), null, cv);

    }

    public void updateEntity(final KeepDataEntity<?> entity, SQLKeepUpdateParam param)   {
        try{
            param.setClassObject(entity);
            ContentValues cv = new ContentValues();

            entity.save(cv);

            getWritableDatabase().update(entity.getStorageKey(), cv,
                    param.getUpdateWhereQuery(), param.getWhereArgsParams());
        } catch (NoKeyFoundException e) {
            Log.v("DB","Error with Unique keys",e);
        }
    }

    //SQLKeepQueryParam
    public void updateEntity(final KeepDataEntity<?> entity, SQLKeepQueryParam param)   {
        try{
            param.setClassObject(entity);
            ContentValues cv = new ContentValues();

            entity.save(cv);

            getWritableDatabase().update(entity.getStorageKey(), cv,
                    param.getQuery(), param.getQueryParams());
        } catch ( Exception e) {
            Log.v("DB","Error with Unique keys",e);
        }
    }

    public void deleteEntity(final KeepDataEntity<?> entity, SQLKeepUpdateParam param) {


        try {
            param.setClassObject(entity);
            getWritableDatabase().delete(entity.getStorageKey(),
                    param.getUpdateWhereQuery(), param.getWhereArgsParams());
        } catch (NoKeyFoundException e) {
            Log.v("DB","Error with Unique keys",e);
        }


    }

    public void deleteEntity2(Class<? extends KeepDataEntity<T>> domain, SQLKeepQueryParam param) {

        try {
            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            //param.setClassObject(entity);
            getWritableDatabase().delete(blank.getStorageKey(),
                    param.getQuery(), param.getQueryParams());
        }catch (Exception e){

        }

    }

    //SQLKeepUpdateParamAbstract

    public void updateEntity(final KeepDataEntity<?> entity) throws NoKeyFoundException {
        ContentValues cv = new ContentValues();
        SQLKeepUpdateParamAbstract param = new SQLKeepUpdateParamAbstract(entity);

        getWritableDatabase().update(entity.getStorageKey(), cv,
                param.getUpdateWhereQuery(), param.getWhereArgsParams());
    }



    public boolean persistEntityIfNotAvailable(Class<? extends KeepDataEntity<T>> domain,
                                               final KeepDataEntity<?> entity,
                                               SQLKeepQueryParam queryParam) {
        queryParam.setClassObject(entity);
        if (isAvailable(domain, queryParam)) {
            return false;
        } else {
            persistEntity(entity);
        }
        return true;

    }


    public boolean persistEntityOrUpdateIfAvailable(Class<? extends KeepDataEntity<T>> domain,
                                                    final KeepDataEntity<?> entity,
                                                    SQLKeepQueryParam queryParam,
                                                    SQLKeepUpdateParam qupdateParam) {
        queryParam.setClassObject(entity);
        if (isAvailable(domain, queryParam)) {
            updateEntity(entity,qupdateParam);
        } else {
            persistEntity(entity);
        }
        return true;

    }

    public boolean persistEntityOrUpdateIfAvailable(Class<? extends KeepDataEntity<T>> domain, final KeepDataEntity<?> entity, SQLKeepQueryParam queryParam) {
        queryParam.setClassObject(entity);
        if (isAvailable(domain, queryParam)) {

            updateEntity(entity,queryParam);
        } else {
            persistEntity(entity);
        }
        return true;

    }

    public boolean persistEntityOrUpdateIfAvailable(Class<? extends KeepDataEntity<T>> domain, final KeepDataEntity<?> entity ) {
        UniqueEntrySQLQueryParameter queryParam = new UniqueEntrySQLQueryParameter();
        queryParam.setClassObject(entity);
        if (isAvailable(domain, queryParam)) {

            updateEntity(entity,queryParam);
        } else {
            persistEntity(entity);
        }
        return true;

    }




    public void deleteAllRecords(Class<? extends KeepDataEntity<T>> domain) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            KeepDataEntity<T> blank;
            blank = domain.newInstance();

            database.delete(blank.getStorageKey(), null, null);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
    }


    public void deleteTable(Class<? extends KeepDataEntity<T>> domain) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            KeepDataEntity<T> blank;
            blank = domain.newInstance();
            final String DROP_TABLE = "DROP TABLE IF EXISTS " + blank.getStorageKey();
            database.execSQL(DROP_TABLE);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }

    }



    public <T> List<KeepDataEntity<T>> listItems(Class<? extends KeepDataEntity<T>> domain) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, null, "0,1000");
            int cc = c.getColumnCount();
            Log.v("COLUMN_COUNT",domain.getName() +" - "+cc);
            Log.v("COLUMN_COUNT_OBJ",domain.getName() +" - "+columns.length);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }




        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }

    public <T> List<KeepDataEntity<T>> listItems(Class<? extends KeepDataEntity<T>> domain, String orderParam, String type) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            String orderKey = null;
            if(orderParam != null){
                if(type != null && (type.equalsIgnoreCase("ASC") || type.equalsIgnoreCase("DESC"))){
                    orderKey = orderParam.toUpperCase() + " " +
                            type.toUpperCase();
                }else{
                    orderKey = orderParam.toUpperCase() + " ASC";
                }

            }

            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, orderKey, "0,1000");
            int cc = c.getColumnCount();
            Log.v("COLUMN_COUNT",domain.getName() +" - "+cc);
            Log.v("COLUMN_COUNT_OBJ",domain.getName() +" - "+columns.length);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }

    public List<String> listItemProperty(String column) {
        Cursor c = null;
        List<String> objects = new ArrayList<>();
        try {
            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) activeDomain.newInstance();
            String[] columns = new String[]{column};
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, null, "0,1000");

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                objects.add(c.getString(0));
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return objects;

    }
    public List<String> listItemProperty(Class<? extends KeepDataEntity<T>> domain, String column) {
        Cursor c = null;
        List<String> objects = new ArrayList<>();
        try {
            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = new String[]{column};
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, null, "0,1000");

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                objects.add(c.getString(0));
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return objects;

    }

    public <T> List<KeepDataEntity<T>> listItems(Class<? extends KeepDataEntity<T>> domain, SQLKeepQueryParam queryParam) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, queryParam.getQueryOrdering(), "0,1000");
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }

    public <T> List<KeepDataEntity<T>> listItems(Class<? extends KeepDataEntity<T>> domain, String ordering) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, ordering, "0,1000");
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }



    private Class<? extends KeepDataEntity<T>> activeDomain;

    public Class<? extends KeepDataEntity<T>> getActiveDomain() {
        return activeDomain;
    }

    public void setActiveDomain(Class<? extends KeepDataEntity<T>> activeDomain) {
        if(activeDomain == null){
            return;
        }
        this.activeDomain = activeDomain;
    }

    public List<KeepDataEntity<T>> listItems() {

        if(activeDomain == null){
            return null;
        }
        return listItems(activeDomain);

    }


    public <T> List<KeepDataEntity<T>> listItems(Class<? extends KeepDataEntity<T>> domain, int start, int size) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, null,
                    "" + start + "," + size);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }

    public <T> List<KeepDataEntity<T>> listItems(Class<? extends KeepDataEntity<T>> domain, SQLKeepQueryParam queryParam, int start, int size) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns, null, null, null, null, queryParam.getQueryOrdering(),
                    "" + start + "," + size);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }




    public <T> List<KeepDataEntity<T>> searchItems(Class<? extends KeepDataEntity<T>> domain, SQLKeepQueryParam queryParam) {
        Cursor c = null;
        List<KeepDataEntity<T>> items = new ArrayList<>();
        try {
            String selection = queryParam.getQuery().toUpperCase();
            String[] selectionArgs = queryParam.getQueryParams();

// How you want the results sorted in the resulting Cursor
            String sortOrder = null;
            if(queryParam.getQueryOrdering() != null)
                sortOrder = queryParam.getQueryOrdering().toUpperCase();


            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) domain.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder,
                    "" + queryParam.getStart() + "," + queryParam.getMaxQuerySize()
            );
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) domain.newInstance();
                blank1.loadValues(c);
                items.add(blank1);
            }


        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return items;

    }

    public Object loadSingleItem(SQLKeepQueryParam queryParam) {

        return loadSingleItem(activeDomain, queryParam);

    }


    public <T> T loadSingleItem(Class<? extends KeepDataEntity<T>> clazz
            , SQLKeepQueryParam queryParam) {

        T tfb = null;
        Cursor c = null;
        try {
            String selection = queryParam.getQuery().toUpperCase();
            String[] selectionArgs = queryParam.getQueryParams();

// How you want the results sorted in the resulting Cursor
            String sortOrder = null;
            if(queryParam != null && queryParam.getQueryOrdering() != null)
                sortOrder = queryParam.getQueryOrdering().toUpperCase();

            KeepDataEntity<T> blank;
            blank = (KeepDataEntity<T>) clazz.newInstance();
            String[] columns = blank.loadColumns();
            c = getReadableDatabase().query(blank.getStorageKey(),
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder, "0,1");
            if (c.moveToFirst()) {
                KeepDataEntity<T> blank1;
                blank1 = (KeepDataEntity<T>) clazz.newInstance();
                blank1.loadValues(c);
                tfb = blank1.getThis();
            }



        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
            }
        }

        return tfb;

    }


    public boolean isAvailable(Class<? extends KeepDataEntity<T>> domain, SQLKeepQueryParam queryParam) {


        String selection = queryParam.getQuery();
        String[] selectionArgs = queryParam.getQueryParams();
        if(selection == null){
            return false;
        }
        if(selectionArgs == null){
            return false;
        }
        if(selectionArgs.length == 0){
            return false;
        }
        if(selectionArgs[0] == null){
            return false;
        }
        //String sortOrder = queryParam.getQueryOrdering();

        Boolean data = Boolean.FALSE;
        Cursor c = null;
        try {
            c = getReadableDatabase().query(domain.getSimpleName().toUpperCase(),
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null, "0,1");

            // get data from cursor
            data = c.moveToFirst();

        } catch (Exception e) {
            // exception handling
        } finally {
            if(c != null){
                c.close();
            }
        }
        return data;


    }

    public boolean isAvailable(SQLKeepQueryParam queryParam) {

        return isAvailable(activeDomain, queryParam);


    }

    public boolean isUpdateAvailable(Class<? extends KeepDataEntity<T>> domain, SQLKeepUpdateParam queryParam) throws NoKeyFoundException {


        String selection = queryParam.getUpdateWhereQuery();
        String[] selectionArgs = queryParam.getWhereArgsParams();
        //String sortOrder = queryParam.getQueryOrdering();

        Boolean data = Boolean.FALSE;
        Cursor c = null;


        try {
            // get data from cursor
            c = getReadableDatabase().query(domain.getSimpleName().toUpperCase(),
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null, "0,1");
            data = c.moveToFirst();

        } catch (Exception e) {
            // exception handling
        } finally {
            if(c != null){
                c.close();
            }
        }


        return data;

    }

    public boolean isUpdateAvailable(SQLKeepUpdateParam queryParam) throws NoKeyFoundException {

        return isUpdateAvailable(activeDomain, queryParam);


    }


}
