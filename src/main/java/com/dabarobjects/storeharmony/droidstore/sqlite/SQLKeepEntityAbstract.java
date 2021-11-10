package com.dabarobjects.storeharmony.droidstore.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.dabarobjects.droid.utils.keep.KeepDataEntityAbstract;
import com.dabarobjects.droid.utils.tools.CommonUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dabarobjects on 26/04/2018.
 */

public abstract class SQLKeepEntityAbstract<T extends SQLKeepEntityAbstract<?>> extends KeepDataEntityAbstract<T> {

    @Override
    public String upgradeScript() {
        return "DROP TABLE IF EXISTS " + getStorageKey().toUpperCase();
    }


    @Override
    public void save(ContentValues pMap) {

        Field[] ifields = getClass().getDeclaredFields();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();

            cf.setAccessible(true);
            try {
                Object datai = cf.get(this);
                String datatype = fname.toUpperCase();

                if (ctype.equals(String.class)) {
                    pMap.put(datatype, (String) datai);
                }
                if (ctype.equals(Integer.class)) {
                    pMap.put(datatype, (Integer) datai);
                }
                if (ctype.equals(Long.class)) {
                    pMap.put(datatype, (Long) datai);
                }
                if (ctype.equals(Double.class)) {
                    pMap.put(datatype, (Double) datai);
                }
                if (ctype.equals(Boolean.class)) {
                    pMap.put(datatype, (Boolean) datai);

                }
                if (ctype.equals(Date.class)) {
                    pMap.put(datatype, CommonUtils.getTimestampForDate((Date) datai));
                }


            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getRowId() {
        return "";
    }

    @Override
    public String createScript() {
        StringBuilder buf = new StringBuilder();
        Field[] ifields = getClass().getDeclaredFields();

        buf.append("CREATE TABLE " +
                getStorageKey().toUpperCase()
                + " (");
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();
            String datatype = "";
            Log.v("DB", "Creating..." + fname.toUpperCase() + " Class: " + ctype.getName() + ", " + cf.getGenericType());

            if (fname.toUpperCase().equalsIgnoreCase("SERIALVERSIONUID")) {
                continue;
            }
            KeepId aa = cf.getAnnotation(KeepId.class);
            boolean pid = false;
            if (aa != null) {
                pid = true;
            }
            boolean autoincrease = false;
            KeepIDAuto aaAuto = cf.getAnnotation(KeepIDAuto.class);
            if (aaAuto != null) {
                autoincrease = true;
            }

            if (ctype.equals(String.class)) {
                if (pid) {
                    datatype = fname.toUpperCase() + " TEXT PRIMARY KEY, ";
                } else {
                    datatype = fname.toUpperCase() + " TEXT, ";
                }

            }
            if (ctype.equals(Integer.class)) {
                if (pid) {
                    if (autoincrease) {
                        datatype = fname.toUpperCase() + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
                    } else {
                        datatype = fname.toUpperCase() + " INTEGER PRIMARY KEY, ";
                    }

                } else {
                    datatype = fname.toUpperCase() + " INTEGER, ";
                }
            }
            if (ctype.equals(Long.class)) {
                if (pid) {
                    if (autoincrease) {
                        datatype = fname.toUpperCase() + " REAL PRIMARY KEY AUTOINCREMENT, ";
                    } else {
                        datatype = fname.toUpperCase() + " REAL PRIMARY KEY, ";
                    }
                } else {
                    datatype = fname.toUpperCase() + " REAL, ";
                }

            }
            if (ctype.equals(Double.class)) {
                datatype = fname.toUpperCase() + " REAL, ";
            }
            if (ctype.equals(Boolean.class)) {
                datatype = fname.toUpperCase() + " INTEGER, ";

            }
            if (ctype.equals(Date.class)) {
                datatype = fname.toUpperCase() + " REAL, ";
            }

            buf.append(datatype);
        }

        buf.append("REF TEXT);");
        //Log.v("DB", buf.toString());
        return buf.toString();
    }

    @Override
    public String[] loadColumns() {

        List<String> fields = new ArrayList<>();
        //fields.add("_id");
        Field[] ifields = getClass().getDeclaredFields();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();
            if (ctype.equals(String.class)) {
                fields.add(fname.toUpperCase());
            }
            if (ctype.equals(Integer.class)) {
                fields.add(fname.toUpperCase());
            }
            if (ctype.equals(Long.class)) {
                fields.add(fname.toUpperCase());
            }
            if (ctype.equals(Double.class)) {
                fields.add(fname.toUpperCase());
            }
            if (ctype.equals(Boolean.class)) {
                fields.add(fname.toUpperCase());
            }
            if (ctype.equals(Date.class)) {
                fields.add(fname.toUpperCase());
            }


        }
        String[] columns = fields.toArray(new String[]{});
        //Log.v("DB","" + Arrays.asList(columns));

        return columns;

    }

    public void loadValues(Cursor cursor) {
        //int index = c.getColumnIndex("_id");
        List<String> fields = new ArrayList<>();
        //fields.add("_id");
        Field[] ifields = getClass().getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();
            if (ctype.equals(String.class)) {
                fields.add(fname.toUpperCase());
                fieldMap.put(fname.toUpperCase(), cf);
            }
            if (ctype.equals(Integer.class)) {
                fields.add(fname.toUpperCase());
                fieldMap.put(fname.toUpperCase(), cf);
            }
            if (ctype.equals(Long.class)) {
                fields.add(fname.toUpperCase());
                fieldMap.put(fname.toUpperCase(), cf);
            }
            if (ctype.equals(Double.class)) {
                fields.add(fname.toUpperCase());
                fieldMap.put(fname.toUpperCase(), cf);
            }
            if (ctype.equals(Boolean.class)) {
                fields.add(fname.toUpperCase());
                fieldMap.put(fname.toUpperCase(), cf);
            }
            if (ctype.equals(Date.class)) {
                fields.add(fname.toUpperCase());
                fieldMap.put(fname.toUpperCase(), cf);
            }


        }
        try {
            String[] columns = fields.toArray(new String[]{});
            for (int i = 0; i < columns.length; i++) {
                String clName = columns[i];

                Field cf = fieldMap.get(clName);
                Class<?> ctype = cf.getType();
                cf.setAccessible(true);
                if (ctype.equals(String.class)) {
                    String iO = cursor.getString(i);
                    cf.set(this, iO);
                    //Log.v("DB","Loading column: "+clName + ", " + iO);
                }
                if (ctype.equals(Integer.class)) {
                    Integer i1 = cursor.getInt(i);
                    cf.set(this, i1);
                }
                if (ctype.equals(Long.class)) {
                    Long i2 = cursor.getLong(i);
                    cf.set(this, i2);
                }
                if (ctype.equals(Double.class)) {
                    Double i3 = cursor.getDouble(i);
                    cf.set(this, i3);
                }
                if (ctype.equals(Boolean.class)) {
                    Integer i4 = cursor.getInt(i);
                    Boolean i4v = i4 == null ? Boolean.FALSE : (i4 == 1 ? Boolean.TRUE : Boolean.FALSE);
                    cf.set(this, i4v);
                }
                if (ctype.equals(Date.class)) {
                    Long i2 = cursor.getLong(i);
                    Date i5v = CommonUtils.getDateFromTimestamp(i2);
                    cf.set(this, i5v);
                }

            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public boolean foundThis(String conditions) {
        return false;
    }

    @Override
    public String getStorageKey() {
        return getClass().getSimpleName().toUpperCase(Locale.ENGLISH);
    }

    @Override
    public String[] getUniqueKeys() {
        return new String[0];
    }

    @Override
    public T getThis() {
        return (T) this;
    }


    @Override
    public boolean isSingleEntity() {
        return false;
    }
}
