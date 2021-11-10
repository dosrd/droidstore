package com.dabarobjects.storeharmony.droidstore.cloud;


import java.io.Serializable;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParamUnit implements Serializable {

    public final String paramKey;
    public final String paramValue;

    public ParamUnit(String paramKey, String paramValue) {
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    public ParamUnit(String paramKey, Object paramValue) {
        this.paramKey = paramKey;
        this.paramValue = ("" + paramValue);
    }

    public ParamUnit(String paramKey, Date paramValue) {
        this.paramKey = paramKey;

        SimpleDateFormat DATE_F2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.paramValue = DATE_F2.format(paramValue);
    }

    public ParamUnit(String paramKey, char[] paramValue) {
        this.paramKey = paramKey;
        this.paramValue = new String(paramValue);
    }


    public static ParamUnit U(String paramKey, String paramValue) {
        return new ParamUnit(paramKey, paramValue);
    }


    public String getParamKey() {
        return this.paramKey;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public String expressString() {
        return this.paramKey + "=" + this.paramValue;
    }

    public String toString() {
        return transportString();
    }


    public String transportString() {
        try {
            String data = URLEncoder.encode(this.paramKey, "UTF-8") + "=" + URLEncoder.encode(this.paramValue, "UTF-8");
            return data;
        } catch (Exception e) {
        }
        return "";
    }
}