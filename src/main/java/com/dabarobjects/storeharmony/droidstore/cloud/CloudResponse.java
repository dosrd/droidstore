package com.dabarobjects.storeharmony.droidstore.cloud;

import com.dabarobjects.droid.utils.tools.MadexParserUtils;
import com.dabarobjects.droid.utils.tools.ObjectHexUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class CloudResponse implements Serializable {

    private final Map<String, String> responseMap;
    private String responseStr;
    private List<String> messageLines;

    public CloudResponse(Map<String, String> responseMap) {
        this.responseMap = responseMap;
    }

    public CloudResponse(String responseStr) {
        this.responseMap = MadexParserUtils.stringToHashTable(responseStr);
        this.responseStr = responseStr;
    }

    public void addResponse(Map<String, String> nmap) {
        this.responseMap.putAll(nmap);
    }

    public void addResponse(String key, String value) {
        this.responseMap.put(key, value);
    }

    public Map<String, String> getResponseMap() {
        return new HashMap(this.responseMap);
    }

    public String getResponse() {
        return this.responseStr;
    }

    public Boolean getBoolStatus() {
        return Boolean.valueOf(Boolean.parseBoolean((String) this.responseMap.get("STATUS")));
    }

    public Boolean isSuccess() {
        return getBoolStatus();
    }

    public String getFileCloudId() {
        return (String) this.responseMap.get("FILE_ID");
    }

    public String getDFormId() {
        return (String) this.responseMap.get("FORM_ID");
    }

    public String getContentCloudId() {
        return (String) this.responseMap.get("PUB_ID");
    }

    public String getStatus() {
        return (String) this.responseMap.get("STATUS");
    }

    public String getObjectMessageStr() {
        String objStr = (String) this.responseMap.get("OBJ");
        return objStr;
    }

    public Object getObjectMessage() {
        String objStr = (String) this.responseMap.get("OBJ");
        if (objStr != null) {
            return ObjectHexUtils.stringInObjectLoader(objStr);
        }
        return null;
    }

    public Long getLongInfo() {
        try {
            return Long.valueOf(Long.parseLong((String) this.responseMap.get("VALUE")));
        } catch (Exception numberFormatException) {
        }
        return Long.valueOf(0L);
    }

    public Integer getIntegerInfo() {
        try {
            return Integer.valueOf(Integer.parseInt((String) this.responseMap.get("VALUE")));
        } catch (Exception numberFormatException) {
        }
        return Integer.valueOf(0);
    }

    public Integer getCreditBalance() {
        return getIntegerInfo();
    }

    public String getServerSessionId() {
        return (String) this.responseMap.get("SID");
    }

    public String getMessage() {
        String response = (String) this.responseMap.get("MSG");
        if (response == null) {
            return "Unable to Secure a Connection to Spike Central Server Due To A Temporary Downtime in Your Internet Gateway or Spike Service";
        }
        return response;
    }

    public String toString() {
        return "ResponseMap{responseMap=" + this.responseMap + ", messageLines=" + this.messageLines + '}';
    }

    public String getNextMessageLine() {
        String message = getMessage();
        if (message == null) {
            return null;
        }

        if (this.messageLines == null) {
            String[] lines = message.split("\n|\r|\r\n|\n\r");

            this.messageLines = new ArrayList(Arrays.asList(lines));
            if (this.messageLines.size() > 0) {
                return (String) this.messageLines.remove(0);
            }
            return null;
        }

        if (this.messageLines.isEmpty()) {
            return null;
        }
        return (String) this.messageLines.remove(0);
    }

    public Integer getStatusCode() {
        try {
            return Integer.parseInt((String) this.responseMap.get("CDE"));
        } catch (Exception numberFormatException) {
        }
        return Integer.valueOf(0);
    }

    public String get(String key) {
        return (String) this.responseMap.get(key);
    }

    public final Date getAsDate(String key) {
        String value = (String) this.responseMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(value);
            return date;
        } catch (Exception parseException) {
        }
        return null;
    }

    public final Date getTimeExecuted() {
        return getAsDate("TS");
    }

    public Boolean getAsBool(String key) {
        if (this.responseMap.get(key) == null)
            return Boolean.FALSE;

        return Boolean.valueOf(Boolean.parseBoolean((String) this.responseMap.get(key)));
    }

    public Integer getAsInt(String key) {
        return Integer.valueOf(Integer.parseInt((String) this.responseMap.get(key)));
    }


    private Map<String, List<Long>> translateMap(Map<String, List<Long>> mapOfStringAndListofLong,
                                                 Map<String, String> headings) {
        Set<String> locationsRows = mapOfStringAndListofLong.keySet();
        Map<String, List<Long>> destKeyListOfValueMap = new HashMap<String, List<Long>>();
        Set<String> keys = headings.keySet();
        List<String> keyslist = new ArrayList<String>(keys);
        Collections.sort(keyslist);

        int i = 0;
        for (String key : keyslist) {
            //we need to put key against its values
            List<Long> keyValueList = new ArrayList<Long>();


            for (String loca : locationsRows) {
                //we need to put key against its values
                List<Long> locValues = mapOfStringAndListofLong.get(loca);

                Long v = locValues.get(i);

                keyValueList.add(v);


            }
            i++;
            destKeyListOfValueMap.put(key, keyValueList);
        }
        return destKeyListOfValueMap;
    }


    public Long getAsLong(String key) {
        return Long.valueOf(Long.parseLong((String) this.responseMap.get(key)));
    }

    public String getLastSentBatchTrackingId() {
        return (String) this.responseMap.get("TCODE");
    }

    public Integer getMessageBatchNo() {
        return Integer.valueOf(Integer.parseInt((String) this.responseMap.get("BATCH")));
    }

    public String getDataExchangeId() {
        return (String) this.responseMap.get("EXCH");
    }

    public String getUserAccountCode() {
        return (String) this.responseMap.get("CODE");
    }

    public Properties getMailerInfo() {
        Properties mailer = new Properties();
        String mailhost = (String) this.responseMap.get("SMH");
        String username = (String) this.responseMap.get("US");
        String password = (String) this.responseMap.get("PS");
        String fromaddress = (String) this.responseMap.get("FA");
        String smtpauth = (String) this.responseMap.get("MSA");
        mailer.put("mail.smtp.host", mailhost);
        mailer.put("username", username);
        mailer.put("password", password);
        mailer.put("from.address", fromaddress);
        mailer.put("mail.smtp.auth", smtpauth);
        return mailer;
    }


}

