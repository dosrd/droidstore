package com.dabarobjects.storeharmony.droidstore;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by deji aladejebi on 29/08/2018.
 */
public class KeepDataEntityParamMap implements Serializable {

    private String responseInfo;
    private Integer statusCode;
    private Long valueData;
    private Map<String, Object> inputStorageMap;
    private Map<String, String> outputDataMap;

    public KeepDataEntityParamMap() {
        this.inputStorageMap = new HashMap<String, Object>();
        this.outputDataMap = new HashMap<String, String>();
    }

    private boolean paimMode;

    public KeepDataEntityParamMap(boolean paimMode) {
        this.paimMode = paimMode;
        this.inputStorageMap = new HashMap<String, Object>();
        this.outputDataMap = new HashMap<String, String>();
    }

    public KeepDataEntityParamMap(String inputMapString) {
        this.inputStorageMap = new HashMap<String, Object>();
        this.outputDataMap = new HashMap<String, String>();
        outputDataMap
                .putAll(KeepObjectUtil.MadexParserUtils.stringToHashTable(inputMapString));
    }

    public void setValueData(Long valueData) {
        this.valueData = valueData;
    }

    public boolean isEmpty() {
        return outputDataMap.isEmpty();
    }

    public void putParam(String tag, String value) {
        if (value == null)
            return;
        if (paimMode) {
            putParamForPIM(tag, value);
        } else {
            String encoded = KeepObjectUtil.MadexDataEncoder.encode(value);

            this.inputStorageMap.put(tag, "{" + encoded + "}");
        }

    }

    public void putObjectParam(String tag, Object value) {
        if (value == null)
            return;
        if (paimMode) {
            putParamForPIM(tag, value);
        } else {
            this.inputStorageMap.put(tag, "{" + value + "}");
        }

    }

    public void putParamForPIM(String tag, Object value) {
        if (value == null)
            return;
        this.inputStorageMap.put(tag, value.toString());
    }

    public String toString() {
        return "IN: " + this.inputStorageMap.toString() + "\nOUT:"
                + outputDataMap.toString();
    }

    public Map<String, Object> getInputStorageMap() {
        return new HashMap<String, Object>(inputStorageMap);
    }

    public void putMapDirect(Map dParams) {
        this.inputStorageMap.putAll(dParams);
    }

    public void putDateParam(String tag, Date value) {
        if (value == null)
            return;
        this.inputStorageMap.put(tag, "{"
                + new SimpleDateFormat("yyyy/MM/dd HH:ss").format(value) + "}");
    }

    public void putDateParamInternal(String tag, Date value) {
        if (value == null)
            return;
        this.inputStorageMap.put(
                tag,
                "{"
                        + new SimpleDateFormat(KeepConstants.SDATE_PATTERN)
                        .format(value) + "}");
    }

    public void putLongParam(String tag, Long value) {
        if (value == null)
            return;
        this.inputStorageMap.put(tag, value);
    }

    public Long getLong(String tag) {
        try {
            return Long.parseLong(outputDataMap.get(tag));
        } catch (Exception numberFormatException) {
        }
        return Long.valueOf(0);
    }

    public void putIntegerParam(String tag, Integer value) {
        if (value == null)
            return;
        this.inputStorageMap.put(tag, value);
    }

    public void putDoubleParam(String tag, Double value) {
        if (value == null)
            return;
        this.inputStorageMap.put(tag, value);
    }


    public Object getFieldObjectfromJson(String tag, Type t, KeepType kt, KeepDataEntityAbstract inType) {
        Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmssZ").create();
        try {


            String value = outputDataMap.get(tag);
            if (value != null)
                Log.v("DB", value);

            if (kt != null) {
                Type tt = inType.getListType(kt);
                Object o = gson.fromJson(value, tt);
                return o;
            } else {
                Object o = gson.fromJson(value, t);

                return o;
            }


        } catch (com.google.gson.JsonParseException e) {

            //e.printStackTrace();
            String value = "{" + outputDataMap.get(tag) + "}";
            //System.err.println(e.toString());
            //System.out.println("Bad: " + gson.fromJson(value, t));

            return gson.fromJson(value, t);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }

    public Double getDouble(String tag) {
        try {
            return Double.parseDouble(outputDataMap.get(tag));
        } catch (Exception numberFormatException) {
        }
        return Double.valueOf(0);
    }

    public String getString(String tag) {

        String value = outputDataMap.get(tag);
        String decoded = KeepObjectUtil.MadexDataEncoder.decode(value);

        return decoded;
    }

    public Integer getInt(String tag) {
        try {
            return Integer.parseInt(outputDataMap.get(tag));
        } catch (Exception numberFormatException) {
        }
        return Integer.valueOf(0);
    }

    public Date getDateInternal(String tag) {

        try {
            String tagVal = this.outputDataMap.get(tag);
            return KeepObjectUtil.parseDateWithPattern(tagVal,
                    KeepConstants.SDATE_PATTERN);

        } catch (Exception e) {

        }
        return null;
    }

    public void putBooleanParamInternal(String tag, Boolean value) {
        if (value == null) {
            value = Boolean.FALSE;
        }
        this.inputStorageMap.put(tag, value ? "1" : "0");
    }

    public Boolean getBoolInternal(String tag) {
        try {
            String tagVal = this.outputDataMap.get(tag);

            return tagVal != null && tagVal.equalsIgnoreCase("1");
        } catch (Exception numberFormatException) {
        }
        return Boolean.FALSE;
    }

    public void putMadexParam(String tag, String madexExpr) {
        this.inputStorageMap.put(tag, '{' + madexExpr + '}');
    }

    public void putArrayParam(String tag, String[] array) {
        int i = 0;
        StringBuilder arrayEntryBuilder = new StringBuilder();
        arrayEntryBuilder.append('{');
        for (String string : array) {
            String arrayKey = "" + i + ":{" + string + "};";
            i++;
            arrayEntryBuilder.append(arrayKey);
        }
        arrayEntryBuilder.append('}');
        this.inputStorageMap.put(tag, arrayEntryBuilder.toString());
    }

    public void putListParam(String tag, List<String> array) {
        int i = 0;
        StringBuilder arrayEntryBuilder = new StringBuilder();
        arrayEntryBuilder.append('{');
        for (String string : array) {
            String arrayKey = "" + i + ":{" + string + "};";
            i++;
            arrayEntryBuilder.append(arrayKey);
        }
        arrayEntryBuilder.append('}');
        this.inputStorageMap.put(tag, arrayEntryBuilder.toString());
    }

    public void putAsJson(String tag, Object data, Type srcType) {
        Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmssZ").create();
        String jsdata = gson.toJson(data, srcType);
        this.inputStorageMap.put(tag, "{"+jsdata+"}");
    }

    public void putMapParam(String tag, Map<String, ?> mapParam) {
        int i = 0;
        StringBuilder arrayEntryBuilder = new StringBuilder();
        arrayEntryBuilder.append('{');
        Set<String> array = mapParam.keySet();
        for (String string : array) {
            String arrayKey = ""
                    + string
                    + ":{"
                    + KeepObjectUtil
                    .convertObjectToString(mapParam.get(string)) + "};";
            i++;
            arrayEntryBuilder.append(arrayKey);
        }
        arrayEntryBuilder.append('}');
        this.inputStorageMap.put(tag, arrayEntryBuilder.toString());
    }

    public String getStorageForm() {
        Map respMap = new HashMap();

        respMap.putAll(this.inputStorageMap);
        return KeepObjectUtil.MadexParserUtils.hashTableToString(respMap, ":", ";");
    }

    /**
     * PIM Transport can only be applied for whole words without spaces or
     * special characters
     *
     * @return
     */
    public String getPIMTransport() {

        Set<String> keys = inputStorageMap.keySet();

        List<String> keysList = new LinkedList<String>(keys);
        List<Integer> keysIntList = new LinkedList<Integer>();
        for (String ikey : keysList) {
            try {
                keysIntList.add(Integer.parseInt(ikey));
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        Collections.sort(keysIntList);

        StringBuffer buf = new StringBuffer();
        for (Integer ikey : keysIntList) {
            String string = inputStorageMap.get("" + ikey).toString();
            buf.append(string.replaceAll(";", "")).append(";");

        }
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
}