package com.dabarobjects.storeharmony.droidstore.cloud;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

public class CloudTransferSystem {

    private String cloudServerPath;
    private static final int SIZE_PER_BLOCK = 16384;
    private String fileExt;
    private boolean running;
    private boolean stopProcess;
    private int elapsedBytes;
    private CloudTransferProgressMonitor feedback;
    private byte[] dataBinary;
    private String itemName;

    public CloudTransferSystem(String fileTypeExt) {
        this.fileExt = fileTypeExt;
        this.cloudServerPath = "http://smsspike.com:8080/service";
    }

    public CloudTransferSystem(String cloudServerPath, String fileTypeExt) {
        this.cloudServerPath = cloudServerPath;
        this.fileExt = fileTypeExt;
    }

    public boolean isRunning() {
        return this.running;
    }

    public synchronized void stopProcess() {
        this.stopProcess = true;
        this.running = false;
    }

    public void prepareCloudTransfer(byte[] dataBinary, String itemName, CloudTransferProgressMonitor feedback) {
        if ((dataBinary == null) || (dataBinary.length == 0)) {
            if (feedback != null) {
                feedback.onTransferFailure("Nothing is available to be sent");
            }
            return;
        }
        if (itemName == null) {
            itemName = "Object";
        }

        this.dataBinary = dataBinary;
        this.itemName = itemName;
        this.feedback = feedback;
    }

    public String doTransfer() {
        int binLen = this.dataBinary.length;
        long blocks = CloudStoreUtil.getBlocksInValue(binLen, 16384L);

        String transferId = acquireTransferId(binLen, blocks, this.itemName);

        if (this.feedback != null) {
            this.feedback.onTransferIdAcquired(binLen, transferId);
        }
        String response = transferDataElements(binLen, blocks, this.dataBinary, transferId, this.feedback);
        if ((response.equalsIgnoreCase("000"))
                && (this.feedback != null)) {
            this.feedback.onTransferFailure(response);
        }

        if ((response.equalsIgnoreCase("OK"))
                && (this.feedback != null)) {
            this.feedback.onEOF(transferId);
            return cloudServerPath
                    + "/cloudget/" + transferId;
        }

        this.running = false;
        return null;
    }

    private String transferDataElements(int dataLength, long blocks, byte[] dataBinary, String transferId, CloudTransferProgressMonitor feedback) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(dataBinary);

            byte[] buffer = new byte[16384];
            int currentBlock = 0;
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                currentBlock++;
                this.running = true;
                if (this.stopProcess) {
                    if (feedback != null) {
                        this.running = false;
                        feedback.onTransferFailure("Process ABorted By User");
                    }
                    return "000";
                }

                byte[] realPack = new byte[read];
                System.arraycopy(buffer, 0, realPack, 0, read);
                this.elapsedBytes += read;
                String hexData = new String(CloudBase64.encode(realPack));

                Vector activities = new Vector();

                ParamUnit hexunit = new ParamUnit("hex", URLEncoder.encode(hexData, "UTF-8"));
                ParamUnit packidunit = new ParamUnit("packid", transferId);
                ParamUnit readunit = new ParamUnit("arrz", Integer.valueOf(read));
                ParamUnit cblkunit = new ParamUnit("cblk", Integer.valueOf(currentBlock));

                activities.addElement(hexunit);
                activities.addElement(packidunit);
                activities.addElement(readunit);
                activities.addElement(cblkunit);
                String dataUrl = convertToTranport(activities);
                String response = "ERROR";
                try {
                    response = readWebActivity(cloudServerPath
                            + "/cloudput", dataUrl);
                } catch (Exception exception) {
                }

                int trials = 0;
                while (response.indexOf(transferId) == -1) {
                    if (this.stopProcess) {
                        if (feedback != null) {
                            this.running = false;
                            feedback.onTransferFailure("Process ABorted By User");
                        }
                        return "000";
                    }

                    if (trials > 7) {
                        if (feedback != null) {
                            this.running = false;
                            feedback.onTransferFailure("Bad Gateway Problem From Server: " + response);
                        }
                        return "000";
                    }
                    try {
                        response = readWebActivity(cloudServerPath
                                + "/cloudput", dataUrl);
                    } catch (Exception exception) {
                        trials++;
                    }

                }

                if (feedback != null) {
                    feedback.onCloudPartSent(currentBlock, blocks, this.elapsedBytes, dataLength);
                }

            }

            in.close();
        } catch (Exception iOException) {
        }
        return "OK";
    }

    private String convertToTranport(Vector units) {
        StringBuffer databuf = new StringBuffer();

        Enumeration emm = units.elements();
        while (emm.hasMoreElements()) {
            ParamUnit paramUnit = (ParamUnit) emm.nextElement();
            databuf.append(paramUnit.transportString()).append("&");
        }

        return databuf.toString();
    }

    private String acquireTransferId(long dataLength, long blocks, String itemName) {
        Vector activities = new Vector();
        ParamUnit fileExtunit = new ParamUnit("ctype", this.fileExt);

        ParamUnit fsizeunit = new ParamUnit("fsize", Long.valueOf(dataLength));
        ParamUnit licIdunit = new ParamUnit("ab", "0000000");
        ParamUnit itemNameunit = new ParamUnit("name", itemName);
        ParamUnit descunit = new ParamUnit("desc", itemName);
        ParamUnit blkunit = new ParamUnit("blk", Long.valueOf(blocks));

        activities.addElement(fileExtunit);
        activities.addElement(fsizeunit);
        activities.addElement(licIdunit);
        activities.addElement(itemNameunit);
        activities.addElement(descunit);
        activities.addElement(blkunit);
        String dataUrl = convertToTranport(activities);
        String response = "ERROR";
        try {
            response = readWebActivity(cloudServerPath
                    + "/cloudput", dataUrl);
        } catch (Exception exception) {
        }
        return response;
    }

    private String sendEOF(String transferId) {
        Vector activities = new Vector();

        ParamUnit packidunit = new ParamUnit("packid", transferId);
        ParamUnit readunit = new ParamUnit("EOF", "true");

        activities.addElement(packidunit);
        activities.addElement(readunit);
        String dataUrl = convertToTranport(activities);
        String response = "ERROR";
        try {
            response = readWebActivity(this.cloudServerPath, dataUrl);
        } catch (Exception exception) {
        }
        return response;
    }

    private String readWebActivity(String appUrl, String data) throws Exception {
        URL url = new URL(appUrl);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = "";
        StringBuilder buf = new StringBuilder();
        while ((response = rd.readLine()) != null) {
            buf.append(response).append("\n");
        }
        if (buf.length() > 1) {
            buf.deleteCharAt(buf.length() - 1);
        }
        rd.close();
        wr.close();

        return buf.toString();
    }
}

/* Location:           C:\Users\admin\Desktop\Harmony2Project\harmony-platform\dist\lib\dos-spike-webconnect.jar
 * Qualified Name:     com.dabarobjects.spikeservice.cloud.CloudTransferSystem
 * JD-Core Version:    0.6.0
 */