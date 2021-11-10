package com.dabarobjects.storeharmony.droidstore.cloud;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CloudStoreUtil {
    public static long getBlocksInValue(long totalValueSize, long sizePerBlock) {
        long blockCount = 0L;
        long divRemainder = totalValueSize % sizePerBlock;
        long completeUnit = totalValueSize - divRemainder;

        blockCount = completeUnit / sizePerBlock;
        if (divRemainder > 0L) {
            blockCount += 1L;
        }
        return blockCount;
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        if (date == null) {
            return "----";
        }
        return formatter.format(date);
    }

    public static String getPresentYear() {
        return String.valueOf(Calendar.getInstance().get(1));
    }

    public static String getFileExtension(File file) {
        if (file == null) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        String name = file.getAbsolutePath();
        int met = name.lastIndexOf(".");
        if (met == -1) {
            return null;
        }
        return name.substring(met);
    }

    public static String getFileExtension(String name) {
        if (name == null) {
            return null;
        }

        int met = name.lastIndexOf(".");
        if (met == -1) {
            return null;
        }
        return name.substring(met);
    }

    public static String getFileExtensionWithoutDot(File file) {
        if (file == null) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        String name = file.getAbsolutePath();
        if (name.length() > 1) {
            int met = name.lastIndexOf(".");
            if (met == -1) {
                return null;
            }
            return name.substring(met + 1);
        }

        return null;
    }

    public static final File saveInputStreamToFile(File out, InputStream ins)
            throws Exception {
        FileOutputStream outByte = new FileOutputStream(out);
        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = ins.read(buffer)) != -1) {
            outByte.write(buffer, 0, read);
        }
        ins.close();
        return out;
    }

    public static final byte[] getFileAsBytes(File file) throws Exception {
        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
        FileInputStream ins = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = ins.read(buffer)) != -1) {
            outByte.write(buffer, 0, read);
        }
        ins.close();
        return outByte.toByteArray();
    }

    public static String getFileExtFromString(String name) {
        int met = name.lastIndexOf(".");
        if (met == -1) {
            return null;
        }
        return name.substring(met);
    }
}
