package com.dabarobjects.storeharmony.droidstore.sqlite;

import java.lang.reflect.Field;

/**
 * Created by dabarobjects on 29/08/2018.
 */

public class SQLKeepUpdateParamAbstract<T> implements SQLKeepUpdateParam<T> {
    private T object;

    public SQLKeepUpdateParamAbstract(T object) {
        this.object = object;
    }

    @Override
    public String getUpdateWhereQuery() throws NoKeyFoundException {
        Field[] ifields = object.getClass().getDeclaredFields();

        for (Field cf : ifields) {
            Class<?> ctype = cf.getType();
            String fname = cf.getName();

            KeepId aa = cf.getAnnotation(KeepId.class);


            if (aa != null) {
                String datatype = fname.toUpperCase();
                String qq = "" +
                        datatype +
                        "=?";
                return qq;
            }
            KeepIDAuto aaAuto = cf.getAnnotation(KeepIDAuto.class);
            if (aaAuto != null) {
                String datatype = fname.toUpperCase();
                String qq = "" +
                        datatype +
                        "=?";
                return qq;
            }
        }
        throw new NoKeyFoundException(object.getClass().getName() + " has provided no data key");
    }

    @Override
    public String[] getWhereArgsParams() throws NoKeyFoundException {
        Field[] ifields = object.getClass().getDeclaredFields();

        for (Field cf : ifields) {
            cf.setAccessible(true);
            KeepId aa = cf.getAnnotation(KeepId.class);

            if (aa != null) {
                try {
                    Object datai = cf.get(object);
                    return new String[]{"" + datai};
                } catch (IllegalArgumentException e) {
                    throw new NoKeyFoundException(object.getClass().getName() + " Error while processing keys", e);
                } catch (IllegalAccessException e) {
                    throw new NoKeyFoundException(object.getClass().getName() + " Error while processing keys", e);
                }


            }
            KeepIDAuto aaAuto = cf.getAnnotation(KeepIDAuto.class);
            if (aaAuto != null) {
                try {
                    Object datai = cf.get(object);
                    return new String[]{"" + datai};
                } catch (IllegalArgumentException e) {
                    throw new NoKeyFoundException(object.getClass().getName() + " Error while processing keys", e);
                } catch (IllegalAccessException e) {
                    throw new NoKeyFoundException(object.getClass().getName() + " Error while processing keys", e);
                }
            }
        }
        throw new NoKeyFoundException(object.getClass().getName() + " has provided no data key");
    }

    @Override
    public final void setClassObject(T t) {
        object = t;

    }
}
