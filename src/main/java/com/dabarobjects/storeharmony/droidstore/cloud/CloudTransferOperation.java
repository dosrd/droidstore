package com.dabarobjects.storeharmony.droidstore.cloud;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CloudTransferOperation extends Thread
        implements CloudElementReady {

    private File fileData;
    private long elapsedBytes;
    private final String cloudServerPath;
    private final String userAccountId;
    private long startTime;
    private long endTime;
    private final ExecutorService exec = Executors.newFixedThreadPool(3);
    private boolean threaded;
    private long totalBytesToSend;
    private CloudTransferProgressMonitor feedback;
    private String fileTransferId;
    private volatile int currentBlockIndex;
    private boolean successful;
    private final Lock lock = new ReentrantLock(true);
    private boolean complete;
    private String message;
    private long totalBlocksForTransmit;
    private String fileInformation = "";
    private boolean personal;
    private final static Object DEF_SERVER = new Object();
    private String password;

    public CloudTransferOperation(String cloudServerPath, File file, String fileInformation, String userAccountId, String password, boolean pers) {
        this.cloudServerPath = cloudServerPath;
        this.userAccountId = userAccountId;
        this.fileInformation = fileInformation;
        this.fileData = file;
        this.personal = pers;
        this.password = password;
    }

    public CloudTransferOperation(String cloudServerPath, File file, String fileInformation, String userAccountId, String password) {
        this.cloudServerPath = cloudServerPath;
        this.userAccountId = userAccountId;
        this.fileData = file;
        this.fileInformation = fileInformation;
        this.password = password;
    }

    public String getFileTransferId() {
        return this.fileTransferId;
    }
    private InputStream dataStream;
    private String streamFileName;

    public void setFileTransferId(String fileTransferId) {
        this.fileTransferId = fileTransferId;
    }

    public CloudTransferOperation(String cloudServerPath, InputStream dataStream, String fileName, String fileInformation, String userAccountId, String password) {
        this.cloudServerPath = cloudServerPath;
        this.userAccountId = userAccountId;
        this.dataStream = dataStream;
        this.fileInformation = fileInformation;
        this.streamFileName = fileName;
        this.password = password;
    }

    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        try {

            String serverRoute = this.cloudServerPath;

            if (dataStream == null) {
                if (this.fileData != null) {
                    dataStream = new FileInputStream(this.fileData);
                    String fileExt = CloudStoreUtil.getFileExtension(this.fileData);

                    this.totalBytesToSend = this.fileData.length();
                    //this.fileTransferId = requestFileId(dataStream, this.fileData.getName(), fileExt, fileInformation);
                    setFileTransferId(requestFileId(dataStream, this.fileData.getName(), fileExt, fileInformation));
                    if (!checkConditions(getFileTransferId())) {
                        setComplete(true);
                        return;
                    }
                    if (feedback != null) {
                        feedback.onTransferIdAcquired(this.totalBytesToSend, getFileTransferId());
                    }
                } else {
                    if(feedback != null){
                        feedback.onTransferFailure("Error");
                    }
                    return;
                }
            } else {
                String fileExt = CloudStoreUtil.getFileExtension(streamFileName);

                this.totalBytesToSend = dataStream.available();
                setFileTransferId(requestFileId(dataStream, streamFileName, fileExt, fileInformation));
                System.out.println("fileTransferId: " + getFileTransferId());
                if (!checkConditions(getFileTransferId())) {
                    setComplete(true);
                    if (feedback != null) {
                        feedback.onTransferFailure("Unable to retrieve transfer handle");
                    }
                    return;
                }
                if (feedback != null) {
                    feedback.onTransferIdAcquired(this.totalBytesToSend, getFileTransferId());
                }
            }

            this.elapsedBytes = 0L;
            byte[] buffer = new byte[8192];
            int currentBlock = 0;
            int read = 0;
            while ((read = dataStream.read(buffer)) != -1) {
                currentBlock++;

                //Thread.sleep(50L);
                byte[] realPack = new byte[read];
                System.arraycopy(buffer, 0, realPack, 0, read);
                this.elapsedBytes += read;
                String hexData = new String(Base64.encode(realPack,Base64.NO_WRAP));

                String data = URLEncoder.encode("hex", "UTF-8") + "=" + URLEncoder.encode(hexData, "UTF-8");
                data = data + "&packid=" + getFileTransferId();
                data = data + "&arrz=" + read;
                data = data + "&cblk=" + currentBlock;
                if (threaded) {

                    CloudElementRunnable cloudWork = new CloudElementRunnable(data, getFileTransferId(), serverRoute, Integer.valueOf(currentBlock), this, this.elapsedBytes, this.totalBytesToSend);
                    this.exec.execute(cloudWork);
                } else {
                    int trials = 0;
                    String response = readWebActivity(serverRoute + "/cloudput", data);
                    while ((!response.contains(getFileTransferId()))
                            && (trials <= 7)) {
                        try {
                            response = readWebActivity(serverRoute + "/cloudput", data);
                            if (response.equalsIgnoreCase("ERROR")) {
                                trials++;
                                continue;
                            }
                            onCloudElementPutComplete(getFileTransferId(), Integer.valueOf(currentBlock), this.elapsedBytes);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            trials++;
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }

                }

            }

            dataStream.close();

            if (threaded) {
                synchronized (DEF_SERVER) {
                    DEF_SERVER.wait();
                }
            }

        } catch (Exception exception) {
            setMessage("An unknown error had occured during this process.");
            System.out.println("unknown error: ");
            exception.printStackTrace();
            if (feedback != null) {
                feedback.onTransferFailure(exception.getMessage());
            }
        }
    }

    public void doTransferAsync(CloudTransferProgressMonitor feedback, boolean threaded) throws Exception {
        this.threaded = threaded;
        this.feedback = feedback;
        start();
    }

    public String doTransfer(CloudTransferProgressMonitor feedback, boolean threaded) throws Exception {
        this.threaded = threaded;
        this.feedback = feedback;

        this.startTime = System.currentTimeMillis();
        try {

            String serverRoute = this.cloudServerPath;

            if (dataStream == null) {
                if (this.fileData != null) {
                    dataStream = new FileInputStream(this.fileData);
                    String fileExt = CloudStoreUtil.getFileExtension(this.fileData);

                    this.totalBytesToSend = this.fileData.length();
                    //this.fileTransferId = requestFileId(dataStream, this.fileData.getName(), fileExt, fileInformation);
                    setFileTransferId(requestFileId(dataStream, this.fileData.getName(), fileExt, fileInformation));
                    if (!checkConditions(getFileTransferId())) {
                        setComplete(true);
                        return null;
                    }
                    if (feedback != null) {
                        feedback.onTransferIdAcquired(this.totalBytesToSend, getFileTransferId());
                    }
                } else {
                    return "ERROR";
                }
            } else {
                String fileExt = CloudStoreUtil.getFileExtension(streamFileName);

                this.totalBytesToSend = dataStream.available();
                setFileTransferId(requestFileId(dataStream, streamFileName, fileExt, fileInformation));
                System.out.println("fileTransferId: " + getFileTransferId());
                if (!checkConditions(getFileTransferId())) {
                    setComplete(true);
                    return null;
                }
                if (feedback != null) {
                    feedback.onTransferIdAcquired(this.totalBytesToSend, getFileTransferId());
                }
            }

            this.elapsedBytes = 0L;
            byte[] buffer = new byte[8192];
            int currentBlock = 0;
            int read = 0;
            while ((read = dataStream.read(buffer)) != -1) {
                currentBlock++;

                Thread.sleep(50L);
                byte[] realPack = new byte[read];
                System.arraycopy(buffer, 0, realPack, 0, read);
                this.elapsedBytes += read;
                String hexData = new String(Base64.encode(realPack,Base64.NO_WRAP));

                String data = URLEncoder.encode("hex", "UTF-8") + "=" + URLEncoder.encode(hexData, "UTF-8");
                data = data + "&packid=" + getFileTransferId();
                data = data + "&arrz=" + read;
                data = data + "&cblk=" + currentBlock;
                if (threaded) {

                    CloudElementRunnable cloudWork = new CloudElementRunnable(data, getFileTransferId(), serverRoute, Integer.valueOf(currentBlock), this, this.elapsedBytes, this.totalBytesToSend);
                    this.exec.execute(cloudWork);
                } else {
                    int trials = 0;
                    String response = readWebActivity(serverRoute + "/cloudput", data);
                    while ((!response.contains(getFileTransferId()))
                            && (trials <= 7)) {
                        try {
                            response = readWebActivity(serverRoute + "/cloudput", data);
                            if (response.equalsIgnoreCase("ERROR")) {
                                trials++;
                                continue;
                            }
                            onCloudElementPutComplete(getFileTransferId(), Integer.valueOf(currentBlock), this.elapsedBytes);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            trials++;
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }

                }

            }

            dataStream.close();

            if (threaded) {
                synchronized (DEF_SERVER) {
                    DEF_SERVER.wait();
                }
            }
            if (this.successful) {
                return this.cloudServerPath
                        + "/cloudget/" + getFileTransferId();
            }
            return null;
        } catch (Exception exception) {
            setMessage("An unknown error had occured during this process.");
            System.out.println("unknown error: ");
            exception.printStackTrace();
            if (feedback != null) {
                feedback.onTransferFailure(exception.getMessage());
            }
            throw exception;
        }

    }

    public void onCloudElementPutFailure(String fileTransferId, Integer index, long elapsedByte) {
        if (this.feedback != null) {
            this.feedback.onTransferFailure("Segment " + index + " broken in transmission");
        }

        if (this.threaded) {
            synchronized (DEF_SERVER) {
                DEF_SERVER.notify();
            }
        }
        this.successful = false;
    }

    public void onCloudElementPutComplete(String fileTransferId, Integer index, long elapsedByte) {
        try {
            if (this.threaded) {
                this.lock.lock();
            }
            this.currentBlockIndex += 1;

            if (this.feedback != null) {
                this.feedback.onCloudPartSent(this.currentBlockIndex, this.totalBlocksForTransmit, elapsedByte, this.totalBytesToSend);
            }

            if (this.totalBlocksForTransmit == this.currentBlockIndex) {
                System.out.println("Reached End of File");
                sendEOF();
                this.endTime = System.currentTimeMillis();
                System.out.println("Finished in " + (this.endTime - this.startTime) / 1000L);

                setMessage("Completed Successfully");
                if (this.threaded) {
                    synchronized (DEF_SERVER) {
                        DEF_SERVER.notify();
                    }

                }

                this.exec.shutdown();
                setComplete(true);
            }
        } finally {
            if (this.threaded) {
                this.lock.unlock();
            }
        }
    }

    public boolean isComplete() {
        return this.complete;
    }

    private void setComplete(boolean b) {
        this.complete = b;
    }

    public String getMessage() {
        return this.message;
    }

    private void setMessage(String string) {
        this.message = string;
        System.out.println(this.message);
    }

    private boolean checkConditions(String ftid) {
        if (dataStream != null) {
            return true;
        }

        if (this.fileData.length() <= 0L) {
            setMessage("This is not a valid file for transmission as the size reported by your operating system is zero bytes");
            return false;
        }
        if (ftid == null) {
            setMessage("This File transfer process could not be completed due to File Access Error");
            return false;
        }
        if (ftid.equalsIgnoreCase("")) {
            setMessage("This File Transfer Process Could Not be Completed at the Moment...Please try Again");
            return false;
        }
        if (ftid.equalsIgnoreCase("000")) {
            setMessage("Your File Exchange Disk Quota Has been Reached. This File transfer process cannot continue. Please Call Your Nearest Sales Agent to Purchase More Credits to secure a bigger quota or delete some files");
            return false;
        }
        if (ftid.equalsIgnoreCase("ERR")) {
            setMessage("An Unknown Error Has Occured From The Server. Could not obtain a file header. Please Try Again Later");
            return false;
        }
        return true;
    }

    private String requestFileId(InputStream fileSream, String fileName, String fileExt, String fileInformation) {
        try {
            long ll = fileSream.available();

            this.totalBlocksForTransmit = CloudStoreUtil.getBlocksInValue(ll, 8192L);

            String serverRoute = this.cloudServerPath;
            System.out.println("Cloud Path: " + serverRoute);
            System.out.println("fileName Path: " + fileName);
            System.out.println("fileInformation Path: " + fileInformation);

            URL url = new URL(serverRoute + "/cloudput?"
                    + "ctype=" +  fileExt + "&"
                    + "ver=2" + "&"
                    + "ps" + "=" + this.personal + "&"
                    + "fsize=" + ll + "&"
                    + "ab" + "=" + this.userAccountId + "&"
                    + "pw" + "=" + this.password + "&"
                    + "name=" + URLEncoder.encode(fileName, "UTF-8") + "&"
                    + "desc=" + URLEncoder.encode(fileInformation, "UTF-8") + "&"
                    + "blk=" + this.totalBlocksForTransmit);

            //System.out.println(url.toExternalForm());
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String read = rd.readLine();
            rd.close();
            return read;
        } catch (Exception malformedURLException) {
            malformedURLException.printStackTrace();
            exec.shutdownNow();
        }
        return "";
    }

    private String sendEOF() {
        try {
            System.out.println("Sending EOF");
            String serverRoute = this.cloudServerPath;
            URL url = new URL(serverRoute + "/cloudput?EOF=true&packid=" + getFileTransferId());
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String read = rd.readLine();
            rd.close();
            this.successful = true;
            if (this.feedback != null) {
                this.feedback.onEOF(getFileTransferId());
            }
            return read;
        } catch (Exception malformedURLException) {
            malformedURLException.printStackTrace();
            exec.shutdownNow();
        }
        return "";
    }

    private final String readWebActivity(String appUrl, String data) throws Exception {
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
        buf.deleteCharAt(buf.length() - 1);
        rd.close();
        wr.close();

        return buf.toString();
    }

    private static final class CloudElementRunnable
            implements Runnable {

        private String data;
        private final String fileTransferId;
        private String serverRoute;
        private Integer partIndex;
        private CloudElementReady elementReadySignal;
        private long bytesToElapse;
        private long bytesTotal;

        public CloudElementRunnable(String data, String fileTransferId, String serverRoute, Integer partIndex, CloudElementReady elementReadySignal, long bytesToElapse, long bytesTotal) {
            this.data = data;
            this.fileTransferId = fileTransferId;
            this.serverRoute = serverRoute;
            this.partIndex = partIndex;
            this.elementReadySignal = elementReadySignal;
            this.bytesToElapse = bytesToElapse;
            this.bytesTotal = bytesTotal;
        }

        public void run() {
            String response = "";

            int trials = 0;
            while (!response.contains(this.fileTransferId)) {
                if (trials > 7) {
                    this.elementReadySignal.onCloudElementPutFailure(this.fileTransferId, this.partIndex, this.bytesToElapse);
                    break;
                }
                try {
                    response = readWebActivity(this.serverRoute + "/cloudput", this.data);
                    if (response.equalsIgnoreCase("ERROR")) {
                        trials++;
                        continue;
                    }
                    this.elementReadySignal.onCloudElementPutComplete(this.fileTransferId, this.partIndex, this.bytesToElapse);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    trials++;
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
            System.out.println("Trials " + Thread.currentThread().getName() + " " + trials);
        }

        private final String readWebActivity(String appUrl, String data) throws Exception {
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
            buf.deleteCharAt(buf.length() - 1);
            rd.close();
            wr.close();

            return buf.toString();
        }
    }
}

/* Location:           C:\Users\admin\Desktop\Harmony2Project\harmony-platform\dist\lib\dos-spike-webconnect.jar
 * Qualified Name:     com.dabarobjects.spikeservice.cloud.CloudTransferOperation
 * JD-Core Version:    0.6.0
 */

