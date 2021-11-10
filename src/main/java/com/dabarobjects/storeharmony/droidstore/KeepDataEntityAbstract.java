package com.dabarobjects.storeharmony.droidstore;


import com.dabarobjects.storeharmony.droidstore.sqlite.KeepId;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Note: For every member of a keep entity, the toString expression must be
 * equal to the actual display value
 * Also All members must be autoboxed to work
 *
 * @param <T>
 * @author deji aladejebi
 */
public abstract class KeepDataEntityAbstract<T extends KeepDataEntityAbstract<?>>
        implements KeepDataEntity<T> {

    public String getStorageKey() {
        // TODO Auto-generated method stub
        return getClass().getSimpleName().toUpperCase(Locale.ENGLISH);
    }

    @SuppressWarnings("unchecked")
    public T getThis() {
        return (T) this;
    }

    private Object index;
    @Override
    public List<String> getColumnNames() {
        List<String> cols = new ArrayList<>();

        Field[] ifields = getClass().getDeclaredFields();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();

            if (ctype.equals(String.class)) {
                cols.add(fname.toUpperCase());
            }
            if (ctype.equals(Integer.class)) {
                cols.add(fname.toUpperCase());
            }
            if (ctype.equals(Long.class)) {
                cols.add(fname.toUpperCase());
            }
            if (ctype.equals(Double.class)) {
                cols.add(fname.toUpperCase());
            }
            if (ctype.equals(Float.class)) {
                cols.add(fname.toUpperCase());
            }
            if (ctype.equals(Boolean.class)) {
                cols.add(fname.toUpperCase());

            }
            if (ctype.equals(Date.class)) {
                cols.add(fname.toUpperCase());
            }
        }
        return  cols;
    }
    public String convertForTransport(String header, String pointId, String pin) {
        KeepDataEntityParamMap maxMap = new KeepDataEntityParamMap(true);
        maxMap.putParam("1", header);
        maxMap.putParam("2", pointId);
        maxMap.putParam("3", pin);
        // save(maxMap);
        return maxMap.getPIMTransport();
    }

    public final Object getIndex() {
        return this.index;
    }

    /**
     * NOTE: Not to be called directly by subclasses of the abstract entity
     * class
     */
    public final void setIndex(Object index) {
        this.index = index;

    }

    @Override
    public String[] getUniqueKeys() {
        Field[] ifields = getClass().getDeclaredFields();
        List<String> keys = new ArrayList<>();

        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();
            String datatype = "";
           // Log.v("DB", "Creating..." + fname.toUpperCase() + " Class: " + ctype.getName() + ", " + cf.getGenericType());

            if (fname.toUpperCase().equalsIgnoreCase("SERIALVERSIONUID")) {
                continue;
            }
            KeepId aa = cf.getAnnotation(KeepId.class);
            boolean pid = false;
            if (aa != null) {
                pid = true;
                keys.add(fname);
            }
        }
        return keys.toArray(new String[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeepDataEntityAbstract<?> that = (KeepDataEntityAbstract<?>) o;
        return Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    //this is where it should load the associated table and persist the object
    @Override
    public void save(KeepDataEntityParamMap pMap) {
        Field[] ifields = getClass().getDeclaredFields();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();

            cf.setAccessible(true);
            try {
                Object datai = cf.get(this);
                pMap.putAsJson(fname, datai, ctype);

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public Type getListType(KeepType ktype) {
        return null;
    }

    @Override
    public void load(KeepDataEntityParamMap pMap) {
        Field[] ifields = getClass().getDeclaredFields();
        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();
            if (fname.equalsIgnoreCase("serialVersionUID")) {
                continue;
            }

            cf.setAccessible(true);

            try {

                KeepType aa = cf.getAnnotation(KeepType.class);


                Object dataref = pMap.getFieldObjectfromJson(fname, ctype, aa, this);
                if (dataref != null) {
                   // Log.v("DB1", "Loaded: " + dataref.getClass());
                }

                cf.set(this, dataref);

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //
    private Object getValueForField(String field) {
        try {
            Field ff = getClass().getDeclaredField(field);
            ff.setAccessible(true);
            return ff.get(this);
        } catch (Exception ex) {
        }
        return null;

    }

    private Object convertParamValueToRealObject(String field, String paramValue) {
        try {
            Field cf = getClass().getDeclaredField(field);
            Class<?> ctype = cf.getType();
            if (ctype.equals(String.class)) {
                return paramValue;
            }
            if (ctype.equals(Integer.class)) {
                return Integer.parseInt(paramValue);

            }
            if (ctype.equals(Long.class)) {
                return Long.parseLong(paramValue);
            }
            if (ctype.equals(Double.class)) {
                return Double.parseDouble(paramValue);
            }
            if (ctype.equals(Boolean.class)) {
                Boolean.parseBoolean(paramValue);
            }
            if (ctype.equals(Date.class)) {
                paramValue = paramValue.substring(0, paramValue.indexOf("#"));

                return  parseDateWithPattern(paramValue,
                        KeepConstants.SDATE_PATTERN_JUSTDATE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private   Date parseDateWithPattern(String dateStr, String patternFrom) {
        SimpleDateFormat DATE_F2 = new SimpleDateFormat(patternFrom);

        if (dateStr == null) {
            return null;
        }
        if (dateStr.equalsIgnoreCase("")) {
            return new Date();
        }
        try {
            Date dateVal = DATE_F2.parse(dateStr);
            return dateVal;
        } catch (ParseException parseException) {
            return null;
        }

    }
    @Override
    public boolean foundThis(String conditions) {
        // conditions will be sent as name=nameSearch tests for equality
        // or just nameSearch will test for similarity
        // or name>nameSearch will compare or name<nameSearch
        // first we split around & and | and if we have 1 we split around the
        // operators
        // if we have only one still, we check for likes
        //
        // Step 1 = Does if contain logical ops
        String[] orArrSplit = conditions.split("\\|");
        String[] andArrSplit = conditions.split("&");
        int orFlag = orArrSplit.length;
        int andFlag = andArrSplit.length;
        if (orFlag > 2) {
            // not complaint
            return false;
        }
        if (andFlag > 2) {
            // not complaint
            return false;
        }
        // first no logical ops
        if (andFlag == 1 && orFlag == 1) {
            return not(conditions);
        }
        if (andFlag == 2) {
            return and(andArrSplit);
        }
        if (orFlag == 2) {
            return or(orArrSplit);
        }

        return false;
    }

    private boolean not(String conditions) {
        // contains no logical ops
        // split around the compare ops
        String[] eqParams = conditions.split("=");
        String[] gtParams = conditions.split(">");
        String[] ltParams = conditions.split("<");
        int eqFlag = eqParams.length;
        int gtFlag = gtParams.length;
        int ltFlag = ltParams.length;
        if (eqFlag == 1 && gtFlag == 1 && ltFlag == 1) {
            // This is the part where the condition is simply a plain
            // expression where any form of operand
            String[] conditionArr = conditions.split("\\p{Space}");
            return like(conditionArr);
        }
        if (eqFlag == 2) {
            // contains equal sign meaning we should have 2 splitable params
            return eq(eqParams);
        }
        if (gtFlag == 2) {
            // contains equal sign meaning we should have 2 splitable params
            return gt(gtParams);
        }

        if (ltFlag == 2) {
            // contains equal sign meaning we should have 2 splitable params
            return lt(ltParams);
        }
        return false;
    }

    private boolean and(String[] andArrSplit) {
        return comp(andArrSplit, "and");
    }

    private boolean or(String[] orArrSplit) {
        return comp(orArrSplit, "or");
    }

    private boolean comp(String[] andArrSplit, String compType) {
        String conditions1 = andArrSplit[0];
        String conditions2 = andArrSplit[1];

        boolean condMatch1 = false;
        boolean condMatch2 = false;
        if (!conditions1.isEmpty()) {
            String[] eqParams = conditions1.split("=");
            String[] gtParams = conditions1.split(">");
            String[] ltParams = conditions1.split("<");
            int eqFlag = eqParams.length;
            int gtFlag = gtParams.length;
            int ltFlag = ltParams.length;
            if (eqFlag == 1 && gtFlag == 1 && ltFlag == 1) {
                String[] conditionArr = conditions1.split("\\p{Space}");
                condMatch1 = like(conditionArr);
            }
            if (eqFlag == 2) {
                condMatch1 = eq(eqParams);
            }
            if (gtFlag == 2) {
                condMatch1 = gt(gtParams);
            }
            if (ltFlag == 2) {
                condMatch1 = lt(ltParams);
            }
        }

        if (!conditions2.isEmpty()) {
            String[] eqParams = conditions2.split("=");
            String[] gtParams = conditions2.split(">");
            String[] ltParams = conditions2.split("<");
            int eqFlag = eqParams.length;
            int gtFlag = gtParams.length;
            int ltFlag = ltParams.length;
            if (eqFlag == 1 && gtFlag == 1 && ltFlag == 1) {
                String[] conditionArr = conditions2.split("\\p{Space}");
                condMatch2 = like(conditionArr);
            }
            if (eqFlag == 2) {
                condMatch2 = eq(eqParams);
            }
            if (gtFlag == 2) {
                condMatch2 = gt(gtParams);
            }
            if (ltFlag == 2) {
                condMatch2 = lt(ltParams);
            }
        }
        if (compType.equalsIgnoreCase("and")) {
            return condMatch1 && condMatch2;
        }
        if (compType.equalsIgnoreCase("or")) {
            return condMatch1 || condMatch2;
        }

        return condMatch1 && condMatch2;

    }

    private boolean like(String[] conditionsArr) {
        for (String conditions : conditionsArr) {
            Field[] ifields = getClass().getDeclaredFields();
            for (Field cf : ifields) {

                String fname = cf.getName();
                Object value = getValueForField(fname);
                if (value == null) {
                    continue;
                }
                if (value.toString().contains(conditions.trim())) {
                    // we just check for basic similarities and if this works we
                    // pick this object
                    // this is just basic
                    return true;
                }

            }
        }
        return false;
    }

    private boolean eq(String[] eqParams) {
        String paramKey = eqParams[0];
        String paramValue = eqParams[1];
        Object value = getValueForField(paramKey.trim());
        if (value != null) {
            if (value.toString().equals(paramValue.trim())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean gt(String[] gtParams) {
        String paramKey = gtParams[0];
        String paramValue = gtParams[1];
        Object value = getValueForField(paramKey.trim());
        Object compareToValue = convertParamValueToRealObject(paramKey.trim(),
                paramValue.trim());

        if (value != null && compareToValue != null) {
            if (value instanceof Comparable
                    && compareToValue instanceof Comparable) {
                Comparable valueComp = (Comparable) value;
                Comparable valueCompTo = (Comparable) compareToValue;
                int gtBit = valueComp.compareTo(valueCompTo);
                if (gtBit == 1) {
                    // this is greater truly
                    return true;
                } else {
                    return false;
                }

            }
        }

        return false;
    }

    private boolean lt(String[] ltParams) {
        String paramKey = ltParams[0];
        String paramValue = ltParams[1];
        Object value = getValueForField(paramKey.trim());
        Object compareToValue = convertParamValueToRealObject(paramKey.trim(),
                paramValue.trim());

        if (value != null && compareToValue != null) {
            if (value instanceof Comparable
                    && compareToValue instanceof Comparable) {
                Comparable valueComp = (Comparable) value;
                Comparable valueCompTo = (Comparable) compareToValue;
                int ltBit = valueComp.compareTo(valueCompTo);
                if (ltBit == -1) {
                    // this is greater truly
                    return true;
                } else {
                    return false;
                }

            }
        }

        return false;
    }

    public String convertForRendering() {
        // TODO Auto-generated method stub
        return toString();
    }

    public boolean isSingleEntity() {
        // TODO Auto-generated method stub
        return false;
    }

}