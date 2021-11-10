package com.dabarobjects.storeharmony.droidstore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeepObjectUtil {
    public static String convertObjectToString(Object ops) {
        ByteArrayOutputStream outStream = null;
        ObjectOutputStream out = null;
        try {
            outStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(outStream);

            out.writeObject(ops);
            byte[] object = outStream.toByteArray();

            String objectDataStr = encodeBinToHex(object);

            String str1 = objectDataStr;
            return str1;
        } catch (Exception iOException) {
            iOException.printStackTrace();
        } finally {
            try {
                out.close();
                outStream.close();
            } catch (IOException iOException) {
            }
        }
        return "";
    }

    public static String encodeBinToHex(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String dataHex = "";
        for (byte b : data) {
            if (b >= 0) {
                dataHex = Integer.toHexString(b);
            }
            if (b < 0) {
                dataHex = Integer.toHexString(b).substring(6);
            }

            if (dataHex.length() == 1) {
                builder.append(0).append(dataHex);
            } else {
                builder.append(dataHex);
            }
        }
        return builder.toString();
    }
    public static Date parseDateWithPattern(String dateStr, String patternFrom) {
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
    public static class MadexParserUtils {




        public static String[] splitUp(String item, char seperator) {
            return splitUp(item, seperator, countParts(item, seperator));
        }

        public static int countParts(String item, char seperator) {
            int len = item.length();
            int partCount = 1;
            for (int i = 0; i < len; i++) {
                char it = item.charAt(i);
                if (it == seperator) {
                    partCount++;
                }
            }
            return partCount;
        }

        public static String[] splitUp(String item, char seperator, int partCount) {
            int len = item.length();
            String[] items = new String[partCount];

            int cursor = 0;
            StringBuffer itemCharsBuf = new StringBuffer();
            for (int i = 0; i < len; i++) {
                char it = item.charAt(i);
                if (it == seperator) {
                    items[cursor] = itemCharsBuf.toString();
                    itemCharsBuf = new StringBuffer();
                    cursor++;
                } else {
                    itemCharsBuf.append(it);
                }
            }

            items[cursor] = itemCharsBuf.toString();

            return items;
        }



        public static Map<String, String> stringToHashTable(String indexStrData) {
            return stringToHashTable(indexStrData, ":", ";", false);
        }

        public static Map<String, String> stringToHashTable(
                Map<String, String> keyValue, String indexStrData) {
            return stringToHashTable(keyValue, indexStrData, ":", ";", false);
        }

        public static Map<String, Long> convertMapOfStringsToLong(Map<String, String> items) {
            Map<String, Long> convertedMap = new HashMap<String, Long>();
            Set<String> keyElements = items.keySet();
            for (String keyElement : keyElements) {
                String listExpression = items.get(keyElement);
                Long convVal = Long.parseLong(listExpression);
                convertedMap.put(keyElement, convVal);

            }
            return convertedMap;
        }

        public static Map<String, String> simpleParseLevel1(String madexLevel1Str) {
            String[] parts = madexLevel1Str.split(",");
            Map madexMap = new HashMap();
            for (String string : parts) {
                String madexKey = string.substring(0, 2);
                String madexValue = string.substring(2);
                madexMap.put(madexKey, madexValue);
            }
            return madexMap;
        }

        public static Map<String, String> parserMadexLevel1(String indexStrData) {
            return parserMadexLevel1(indexStrData, ',', '&');
        }

        public static Map<String, String> parserMadexLevel1(String indexStrData,
                                                            char seperator, char escaper) {
            int len = indexStrData.length();
            StringBuilder indexValueBuffer = new StringBuilder();

            boolean escapeSeparator = false;

            LinkedList<String> keySeq = new LinkedList<String>();
            for (int i = 0; i < len; i++) {
                char it = indexStrData.charAt(i);

                if (it == escaper) {
                    escapeSeparator = true;
                }

                if ((it == seperator) && (!escapeSeparator)) {
                    keySeq.add(indexValueBuffer.toString());
                    indexValueBuffer = new StringBuilder();
                    escapeSeparator = false;
                } else {
                    indexValueBuffer.append(it);
                }
            }
            keySeq.add(indexValueBuffer.toString());

            Map madexMap = new HashMap();
            for (String string : keySeq) {
                String madexKey = string.substring(0, 2);
                String madexValue = string.substring(2);
                madexMap.put(madexKey, madexValue);
            }

            return madexMap;
        }

        public static Map<String, String> stringToHashTable(String indexStrData,
                                                            String startChar, String endChar) {
            return stringToHashTable(indexStrData, startChar, endChar, true);
        }

        public static Map<String, String> stringToHashTable(String indexStrData,
                                                            String startChar, String endChar, boolean processBraces) {
            Map keyValue = new HashMap();
            if (indexStrData == null || indexStrData.isEmpty()) {
                return keyValue;
            }
            return stringToHashTable(keyValue, indexStrData, startChar, endChar,
                    processBraces, true);
        }

        public static Map<String, String> stringToHashTable(
                Map<String, String> keyValue, String indexStrData,
                String startChar, String endChar, boolean processBraces) {

            if (indexStrData == null || indexStrData.isEmpty()) {
                return keyValue;
            }
            return stringToHashTable(keyValue, indexStrData, startChar, endChar,
                    processBraces, true);
        }

        public static Map<String, String> stringToHashTable(
                Map<String, String> keyValue, String indexStrData,
                String startChar, String endChar, boolean processBraces,
                boolean stripEnclosingContentBraces) {
            int len = indexStrData.length();
            boolean startValueAppend = false;
            boolean startKeyAppend = true;

            StringBuilder indexValueBuffer = new StringBuilder();
            StringBuilder indexKeyBuffer = new StringBuilder();
            int pointer = 0;
            boolean skipingMadexBracedContent = false;
            int bracesFound = 0;

            for (int i = 0; i < len; i++) {
                String it = indexStrData.substring(i, i + 1);

                if (!processBraces) {
                    if (it.equalsIgnoreCase("{")) {
                        bracesFound++;
                        skipingMadexBracedContent = true;
                    }
                    if (it.equalsIgnoreCase("}")) {
                        bracesFound--;
                        if (bracesFound == 0) {
                            skipingMadexBracedContent = false;
                        }
                    }

                    if (skipingMadexBracedContent) {
                        indexValueBuffer.append(it);
                        continue;
                    }

                }

                if (it.equalsIgnoreCase(startChar)) {
                    pointer++;
                    indexValueBuffer = new StringBuilder();
                    startValueAppend = true;
                    startKeyAppend = false;
                }

                if (it.equalsIgnoreCase(endChar)) {
                    startValueAppend = false;
                    startKeyAppend = true;
                    String protocolHeaderKey = indexKeyBuffer.toString();
                    String protocolHeaderValue = indexValueBuffer.toString();

                    if ((stripEnclosingContentBraces)
                            && (!protocolHeaderValue.isEmpty())) {
                        if (protocolHeaderValue.charAt(0) == '{') {
                            protocolHeaderValue = protocolHeaderValue.substring(1);
                        }
                        if (protocolHeaderValue
                                .charAt(protocolHeaderValue.length() - 1) == '}') {
                            protocolHeaderValue = protocolHeaderValue.substring(0,
                                    protocolHeaderValue.length() - 1);
                        }

                    }

                    if (keyValue.containsKey(protocolHeaderKey)) {
                        // protocolHeaderValue =
                        // protocolHeaderValue.concat("#").concat(((String)
                        // keyValue.get(protocolHeaderKey)).toString());
                    }
                    keyValue.put(protocolHeaderKey, protocolHeaderValue);

                    indexKeyBuffer = new StringBuilder();
                }
                if ((startValueAppend) && (!it.equalsIgnoreCase(startChar))) {
                    indexValueBuffer.append(it);
                }

                if (!startKeyAppend) {
                    continue;
                }
                if (!it.equalsIgnoreCase(endChar)) {
                    indexKeyBuffer.append(it);
                }
            }

            return keyValue;
        }

        public static String hashTableToString(Map indexMapping, String startChar,
                                               String endChar) {
            Iterator ennKeys = indexMapping.keySet().iterator();
            StringBuilder strPattBuffer = new StringBuilder();
            while (ennKeys.hasNext()) {
                String keyElem = (String) ennKeys.next();

                strPattBuffer.append(keyElem).append(startChar)
                        .append(indexMapping.get(keyElem)).append(endChar);
            }
            return strPattBuffer.toString();
        }
    }

    public static class MadexDataEncoder {
        private static final String[] encodeMap = {"(", "&OB!", ")", "&CB!", "{",
                "&OC!", "}", "&CC!", "[", "&OQ!", "]", "&CQ!", ";", "&QT!", ":",
                "&CL!"};
        private static final MadexDataEncoder INSTANCE = new MadexDataEncoder();

        public static String encode(String unencodeddata) {
            return INSTANCE.encodeData(unencodeddata);
        }

        public static String decode(String encoded) {
            return INSTANCE.decodeData(encoded);
        }


        public static void main(String[] args) {

        }

        public static String encodeObjectToMadex(Object obj) {
            if (obj == null) {
                return "";
            }

            if (obj.getClass().isPrimitive()
                    || obj.getClass() == Integer.class
                    || obj.getClass() == String.class
                    || obj.getClass() == Long.class
                    || obj.getClass() == Float.class
                    || obj.getClass() == Short.class) {
                return obj.toString();
            }

            if (obj.getClass() == Date.class) {
                Date dateObj = (Date) obj;

                Calendar c = Calendar.getInstance();
                c.setTime(dateObj);
                return "" + c.getTimeInMillis();
            }
            if (Date.class.isAssignableFrom(obj.getClass())) {
                Date dateObj = (Date) obj;

                Calendar c = Calendar.getInstance();
                c.setTime(dateObj);
                return "" + c.getTimeInMillis();
            }
            Field[] ifields = obj.getClass().getDeclaredFields();
            StringBuilder buf = new StringBuilder();
            for (Field field : ifields) {
                String val = getValuesToObject(obj, field);
                buf.append(val);
            }
            return buf.toString();
        }

        private static String innerencodeObjectToMadex(Object obj) {
            if (obj == null) {
                return "";
            }

            if (obj.getClass().isPrimitive()
                    || obj.getClass() == Integer.class
                    || obj.getClass() == String.class
                    || obj.getClass() == Long.class
                    || obj.getClass() == Float.class
                    || obj.getClass() == Short.class) {
                return obj.toString();
            }

            if (obj.getClass() == Date.class) {
                Date dateObj = (Date) obj;

                Calendar c = Calendar.getInstance();
                c.setTime(dateObj);
                return "" + c.getTimeInMillis();
            }
            if (Date.class.isAssignableFrom(obj.getClass())) {
                Date dateObj = (Date) obj;

                Calendar c = Calendar.getInstance();
                c.setTime(dateObj);
                return "" + c.getTimeInMillis();
            }

            return ""+obj;
        }



        private static String getValuesToObject(Object data, Field cf) {

            try {
                cf.setAccessible(true);

                Object obj = cf.get(data);


                if (obj == null) {
                    return "";
                }
                if (obj.getClass().isArray()) {
                    Object[] arrs = (Object[]) obj;
                    return encodeArray(cf.getName(), arrs);
                    // return cf.getName() + ":{" + obj + "};";
                }
                if (obj.getClass().isAssignableFrom(List.class)) {
                    List list = (List) obj;
                    return encodeArray(cf.getName(), list);
                }
                return cf.getName() + ":{" + obj + "};";
            } catch (Exception noSuchMethodException) {
            }
            return "";
        }

        private static String encodeArray(String tag, Object[] array) {
            int i = 0;
            StringBuilder arrayEntryBuilder = new StringBuilder();

            for (Object obj : array) {
                String arrayKey = "" + i + ":{" + encodeObjectToMadex(obj) + "};";
                i++;
                arrayEntryBuilder.append(arrayKey);
            }

            return tag + ":{" + arrayEntryBuilder.toString() + "};";
        }

        private static String encodeArray(String tag, List array) {
            int i = 0;
            StringBuilder arrayEntryBuilder = new StringBuilder();

            for (Object obj : array) {
                String arrayKey = "" + i + ":{" + innerencodeObjectToMadex(obj) + "};";
                i++;
                arrayEntryBuilder.append(arrayKey);
            }

            return tag + ":{" + arrayEntryBuilder.toString() + "};";
        }

        private String encodeData(String cmplxnotation) {
            if (cmplxnotation == null)
                return null;
            int len = cmplxnotation.length();
            StringBuilder encoded = new StringBuilder();

            for (int i = 0; i < len; i++) {
                String it = cmplxnotation.substring(i, i + 1);
                for (int j = 0; j < encodeMap.length; j += 2) {
                    String string = encodeMap[j];
                    if (string.equalsIgnoreCase(it)) {
                        it = encodeMap[(j + 1)];
                    }
                }
                encoded.append(it);
            }
            return encoded.toString();
        }

        private boolean isEncodingProperty(String enc) {
            for (int j = 1; j < encodeMap.length; j += 2) {
                String string = encodeMap[j];
                if (string.equalsIgnoreCase(enc)) {
                    return true;
                }
            }
            return false;
        }

        private String getEncodingProperty(String enc) {
            for (int j = 1; j < encodeMap.length; j += 2) {
                String string = encodeMap[j];
                if (string.equalsIgnoreCase(enc)) {
                    return encodeMap[(j - 1)];
                }
            }
            return enc;
        }

        private String decodeData(String cmplxnotation) {
            if (cmplxnotation == null)
                return null;
            int len = cmplxnotation.length();
            StringBuffer encoded = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String it = cmplxnotation.substring(i, i + 1);
                if (it.equalsIgnoreCase("&")) {
                    String encodeCheckStr = cmplxnotation.substring(i, i + 4);
                    if (isEncodingProperty(encodeCheckStr)) {
                        i += 3;
                        encoded.append(getEncodingProperty(encodeCheckStr));
                    }
                } else {
                    encoded.append(it);
                }
            }
            return encoded.toString();
        }
    }
}
