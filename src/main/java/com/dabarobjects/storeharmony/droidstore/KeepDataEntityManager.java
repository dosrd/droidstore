package com.dabarobjects.storeharmony.droidstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KeepDataEntityManager {

    private SharedPreferences sharedPref;

    // public final static String MADEX_PREF =
    // "com.dabarobjects.droid.utils.keep.alpha1";
    private final static Object LOCK = new Object();
    private final static Map<String, KeepDataEntityManager> STORE_MAP = Collections.synchronizedMap(new HashMap<String, KeepDataEntityManager>());

    public KeepDataEntityManager(Context ctx, String storeName) {
        sharedPref = ctx.getSharedPreferences(storeName, Context.MODE_PRIVATE);
    }

    public KeepDataEntityManager(Context ctx) {
        sharedPref = ctx.getSharedPreferences(ctx.getClass().getName()
                + ".store", Context.MODE_PRIVATE);
    }

    /**
     * @param storename
     * @param ctx
     * @return
     */
    public static KeepDataEntityManager loadStore(Context ctx, String storename) {
        synchronized (LOCK) {
            //we get a default manager
            //we dont want any threads calling this while we are yet to complete creation
            final KeepDataEntityManager em = STORE_MAP.get(storename);
            if (em == null) {
                final KeepDataEntityManager em1 = new KeepDataEntityManager(ctx, storename);
                STORE_MAP.put(storename, em1);
                return em1;
            } else {
                return em;
            }
        }
    }

    private boolean containsIndex(String storageKey){
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");
        return !indexes.equalsIgnoreCase("-1");
    }
    private Object createIndex(String storageKey) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");
        String counterIndexes = sharedPref.getString(getCounterIndexStorageTag(storageKey),
                "0");

        String[] indexArr = indexes.split(",");
        StringBuilder reHash = new StringBuilder();
        long indexCursor = Long.parseLong(counterIndexes);
        indexCursor++;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getCounterIndexStorageTag(storageKey), "" + indexCursor);

        for (int i = 0; i < indexArr.length; i++) {
            String index1 = indexArr[i];
            reHash.append(index1).append(",");

        }
        reHash.append(indexCursor);

        editor.putString(getIndexStorageTag(storageKey), reHash.toString());
        editor.commit();
        return indexCursor;

    }

    private Object deleteIndex(String storageKey, Object objectId) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");

        if(objectId == null){
            return -1;
        }

        String[] indexArr = indexes.split(",");
        StringBuilder reHash = new StringBuilder();

        for (int i = 0; i < indexArr.length; i++) {
            String index1 = indexArr[i];
            if (objectId.toString().equals(index1)) {
                //indexCursor++;
                //we remove by not adding to the index rehash
                continue;
            } else {
                //indexCursor++;
                reHash.append(index1).append(",");
            }

        }

        reHash.deleteCharAt(reHash.length() - 1);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getIndexStorageTag(storageKey), reHash.toString());
        editor.commit();
        return objectId;
    }


    private long indexCustom(String storageKey, long customIndex) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");

        String[] indexArr = indexes.split(",");
        StringBuilder reHash = new StringBuilder();

        for (int i = 0; i < indexArr.length; i++) {
            String index1 = indexArr[i];

            Long pIndexInt = Long.parseLong(index1);
            if (customIndex == pIndexInt)
                return customIndex;

            reHash.append(index1).append(",");
        }
        reHash.append(customIndex);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getIndexStorageTag(storageKey), reHash.toString());
        editor.commit();
        return customIndex;

    }

    private String indexCustomStr(String storageKey, String customIndex) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");

        String[] indexArr = indexes.split(",");
        StringBuilder reHash = new StringBuilder();

        for (int i = 0; i < indexArr.length; i++) {
            String index1 = indexArr[i];

            if (customIndex.equals(index1))
                return customIndex;

            reHash.append(index1).append(",");
        }
        reHash.append(customIndex);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getIndexStorageTag(storageKey), reHash.toString());
        editor.commit();
        return customIndex;

    }

    private boolean containsIndexKey(String storageKey, String customIndex) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");

        String[] indexArr = indexes.split(",");
        StringBuilder reHash = new StringBuilder();

        for (int i = 0; i < indexArr.length; i++) {
            String index1 = indexArr[i];

            if (customIndex.equals(index1))
                return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }

    private long[] loadIndexes(String storageKey) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");
        String[] indexArr = indexes.split(",");
        if (indexArr.length == 1) {
            return null;
        }
        long[] indexIntArr = new long[indexArr.length - 1];
        for (int i = 1; i < indexArr.length; i++) {
            String index1 = indexArr[i];
            indexIntArr[i - 1] = Long.parseLong(index1);
        }
        return indexIntArr;
    }

    private long loadIndexesSequence(String storageKey, int sequence) {
        String indexes = sharedPref.getString(getIndexStorageTag(storageKey),
                "-1");
        String[] indexArr = indexes.split(",");
        if (indexArr.length == 1) {
            return 0;
        }
        long[] indexIntArr = new long[indexArr.length - 1];
        for (int i = 1; i < indexArr.length; i++) {
            String index1 = indexArr[i];
            indexIntArr[i - 1] = Long.parseLong(index1);
        }
        return indexIntArr[sequence];
    }

    private String getIndexStorageTag(String storageKey) {
        return KeepConstants.DATA_INDEX + "-"
                + storageKey.toUpperCase(Locale.ENGLISH);
    }

    private String getCounterIndexStorageTag(String storageKey) {
        return KeepConstants.DATA_INDEX_COUNT + "-"
                + storageKey.toUpperCase(Locale.ENGLISH);
    }

    public boolean encryptAndSaveEntity(final KeepDataEntity<?> entity, String encryptKey) {
        return false;
    }

    public boolean deleteEntity(final KeepDataEntity<?> entity) {
        Object storageIndex = entity.getIndex();
        storageIndex = deleteIndex(entity.getStorageKey(), storageIndex);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(entity.getStorageKey() + "-" + storageIndex);
        return editor.commit();
    }

    public boolean saveEntity(final KeepDataEntity<?> entity) {
        if (entity == null)
            return false;
        if (entity.isSingleEntity()) {
            return saveSingleEntity(entity);
        } else {
            try {
                Object rindex = createIndex(entity.getStorageKey());
                KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();
                Log.v("New Index: " + entity.getClass().getSimpleName() + ":", "" + rindex);
                entity.setIndex(rindex);
                entity.save(pMap);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(entity.getStorageKey() + "-" + rindex,
                        pMap.getStorageForm());
                editor.commit();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

            return true;
        }

    }

    public <T> T loadEntityByKey(final Class<? extends KeepDataEntity<T>> eClazz,
                                 String uKey) {
        KeepDataEntity<T> blank;
        try {
            blank = (KeepDataEntity<T>) eClazz.newInstance();
            Long index = sharedPref.getLong(blank.getStorageKey() + "-"
                    + uKey, -1L);

            if (index != -1) {
                return loadEntity(eClazz, index);
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }


    public boolean entityExistsByKey(final KeepDataEntity<?> entity, String key){
        return containsIndexKey(entity.getStorageKey(),key);
    }

    public String persistEntityByKey(final KeepDataEntity<?> entity, String customIndex) {
        if (entity == null)
            return null;
        try {
            String rindex = indexCustomStr(entity.getStorageKey(), customIndex);
            KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();
            entity.setIndex(rindex);
            entity.save(pMap);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(entity.getStorageKey() + "-" + rindex,
                    pMap.getStorageForm());
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return customIndex;

    }

    public Long persistEntity(final KeepDataEntity<?> entity) {
        if (entity == null)
            return -1l;
        if (entity.isSingleEntity()) {
            boolean ok = saveSingleEntity(entity);
            if (ok)
                return 0l;
            return -1l;
        } else {
            try {
                Object rindex = createIndex(entity.getStorageKey());
                KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();
                entity.setIndex(rindex);
                entity.save(pMap);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(entity.getStorageKey() + "-" + rindex,
                        pMap.getStorageForm());


                String[] uniqueKeys = entity.getUniqueKeys();
                if (uniqueKeys.length > 0) {
                    for (String ukey : uniqueKeys) {
                        editor.putString(entity.getStorageKey() + "-" + ukey,
                                rindex.toString());
                    }
                }
                editor.commit();
                return (long)rindex;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1L;
            }

        }

    }

    public boolean saveEntity(final KeepDataEntity<?> entity, long customIndex) {
        if (entity == null)
            return false;
        if (entity.isSingleEntity()) {
            return saveSingleEntity(entity);
        } else {
            try {
                long rindex = indexCustom(entity.getStorageKey(), customIndex);
                KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();
                entity.setIndex(rindex);
                entity.save(pMap);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(entity.getStorageKey() + "-" + rindex,
                        pMap.getStorageForm());
                editor.commit();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

            return true;
        }

    }

    public boolean saveSingleEntity(final KeepDataEntity<?> entity) {

        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();

        entity.save(pMap);
        Log.v("Saving Entity: ", pMap.toString());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(entity.getStorageKey(), pMap.getStorageForm());
        editor.commit();

        return true;
    }

    public boolean saveSingleEntity(final KeepDataEntity<?> entity, String storageKey) {

        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();

        entity.save(pMap);
        Log.v("Saving Entity: ", pMap.toString());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(storageKey, pMap.getStorageForm());
        editor.commit();

        return true;
    }




    public boolean saveSingleEntityByKey(String key, final KeepDataEntity<?> entity) {

        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap();

        entity.save(pMap);
        Log.v("Saving Entity: ", pMap.toString());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(entity.getStorageKey(), pMap.getStorageForm());
        editor.commit();

        return true;
    }

    /**
     * Pass a blank, blank cannot be null
     *
     * @param blank
     * @param index
     * @return
     */
    public <T> T loadEntity(final KeepDataEntity<T> blank, long index) {
        String itemEntry = sharedPref.getString(blank.getStorageKey() + "-"
                + index, "");
        if (itemEntry.isEmpty()) {
            return null;
        }
        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);

        blank.load(pMap);
        blank.setIndex(index);
        return blank.getThis();

    }

    public <T> KeepDataEntity<T> loadNativeEntity(
            final KeepDataEntity<T> blank, long index) {
        String itemEntry = sharedPref.getString(blank.getStorageKey() + "-"
                + index, "");
        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);

        blank.load(pMap);
        blank.setIndex(index);
        return blank;

    }

    public <T> T loadEntity(final Class<? extends KeepDataEntity<T>> eClazz,
                            long index) {
        KeepDataEntity<T> blank;
        try {
            blank = (KeepDataEntity<T>) eClazz.newInstance();
            String itemEntry = sharedPref.getString(blank.getStorageKey() + "-"
                    + index, "");
            if (itemEntry.isEmpty())
                return null;
            KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);

            blank.load(pMap);
            blank.setIndex(index);
            return blank.getThis();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private <T> void loadEntityNative(final KeepDataEntity<T> blank, long index) {
        String itemEntry = sharedPref.getString(blank.getStorageKey() + "-"
                + index, "");
        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);

        blank.load(pMap);

    }

    public <T> T loadSingleEntity(final KeepDataEntity<T> blank) {
        String itemEntry = sharedPref.getString(blank.getStorageKey(), "");
        KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);

        blank.load(pMap);
        return blank.getThis();

    }

    public <T> T loadSingleEntity(final Class<? extends KeepDataEntity<T>> eClazz, final String storageKey) {


        KeepDataEntity<T> blank;
        try {
            blank = (KeepDataEntity<T>) eClazz.newInstance();

            String itemEntry = sharedPref.getString(storageKey, "");
            KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);
            //Log.v("DB Load>>>", "" + pMap.toString());
            blank.load(pMap);
            return blank.getThis();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;


    }

    public <T> T loadSingleEntity(
            final Class<? extends KeepDataEntity<T>> eClazz) {
        KeepDataEntity<T> blank;
        try {
            blank = (KeepDataEntity<T>) eClazz.newInstance();
            String itemEntry = sharedPref.getString(blank.getStorageKey(), "");
            KeepDataEntityParamMap pMap = new KeepDataEntityParamMap(itemEntry);
            //Log.v("DB Load>>>", "" + pMap.toString());
            blank.load(pMap);
            return blank.getThis();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public <T> int countEntities(final Class<? extends KeepDataEntity<T>> eClazz) {
        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long[] indexes = loadIndexes(storageKey);

            if (indexes == null)
                return 0;
            return indexes.length;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public <T> List<T> queryEntities(
            final Class<? extends KeepDataEntity<T>> eClazz, String queryParams) {
        List<T> items = new ArrayList<T>();
        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long[] indexes = loadIndexes(storageKey);
            for (int i = 0; i < indexes.length; i++) {
                long index = indexes[i];
                KeepDataEntity<T> et1 = (KeepDataEntity<T>) eClazz
                        .newInstance();
                et1.setIndex(index);
                KeepDataEntity<T> ti = loadNativeEntity(et1, index);
                if (ti.foundThis(queryParams)) {
                    items.add(ti.getThis());
                }

            }
            return items;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return items;
    }

    public <T> List<T> listEntities(
            final Class<? extends KeepDataEntity<T>> eClazz) {
        List<T> items = new ArrayList<T>();
        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long[] indexes = loadIndexes(storageKey);
            if (indexes == null) {
                return items;
            }
            for (int i = 0; i < indexes.length; i++) {

                long index = indexes[i];
                KeepDataEntity<T> et1 = (KeepDataEntity<T>) eClazz
                        .newInstance();
                et1.setIndex(index);
                T ti = loadEntity(et1, index);
                if (ti != null)
                    items.add(ti);

            }
            return items;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return items;

    }

    public <T> List<KeepDataEntity<T>> listNativeEntities(
            final Class<? extends KeepDataEntity<T>> eClazz) {
        List<KeepDataEntity<T>> items = new ArrayList<KeepDataEntity<T>>();
        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long[] indexes = loadIndexes(storageKey);
            if (indexes == null)
                return items;

            for (int i = 0; i < indexes.length; i++) {
                long index = indexes[i];
                KeepDataEntity<T> et1 = (KeepDataEntity<T>) eClazz
                        .newInstance();
                et1.setIndex(index);
                KeepDataEntity<T> ti = loadNativeEntity(et1, index);
                items.add(ti);

            }
            return items;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return items;

    }

    public <T> T loadEntityBySequence(
            final Class<? extends KeepDataEntity<T>> eClazz, int sequence) {

        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long index = loadIndexesSequence(storageKey, sequence);
            KeepDataEntity<T> et1 = (KeepDataEntity<T>) eClazz.newInstance();
            et1.setIndex(index);
            T ti = loadEntity(et1, index);
            return ti;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public <T> List<T> listEntities(
            final Class<? extends KeepDataEntity<T>> eClazz, String condition) {
        List<T> items = new ArrayList<T>();
        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long[] indexes = loadIndexes(storageKey);
            for (int i = 0; i < indexes.length; i++) {
                long index = indexes[i];
                KeepDataEntity<T> et1 = (KeepDataEntity<T>) eClazz
                        .newInstance();
                et1.setIndex(index);
                loadEntityNative(et1, index);
                if (et1.foundThis(condition)) {
                    items.add(et1.getThis());
                }

            }
            return items;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return items;

    }

    public <T> int countEntities(
            final Class<? extends KeepDataEntity<T>> eClazz, String condition) {
        int count = 0;
        try {

            KeepDataEntity<T> et = (KeepDataEntity<T>) eClazz.newInstance();
            String storageKey = et.getStorageKey();
            long[] indexes = loadIndexes(storageKey);
            for (int i = 0; i < indexes.length; i++) {
                long index = indexes[i];
                KeepDataEntity<T> et1 = (KeepDataEntity<T>) eClazz
                        .newInstance();
                loadEntityNative(et1, index);
                if (et1.foundThis(condition)) {
                    count++;
                }

            }
            return count;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;

    }

    public boolean deleteEntities(final List<? extends KeepDataEntity<?>> entityList) {
        SharedPreferences.Editor editor = sharedPref.edit();
        for (KeepDataEntity entity : entityList) {
            Object storageIndex = entity.getIndex();
            storageIndex = deleteIndex(entity.getStorageKey(), storageIndex);

            editor.remove(entity.getStorageKey() + "-" + storageIndex);

        }
        return editor.commit();
    }

}