package com.dabarobjects.storeharmony.droidstore.cloud;

import java.io.InputStream;

public class CloudStore {
    private final String cloudNode;
    private final String cloudAccessToken;

    public CloudStore(String cloudNode, String cloudAccessToken) {
        super();
        this.cloudNode = cloudNode;
        this.cloudAccessToken = cloudAccessToken;
    }

    public String getCloudNode() {
        return cloudNode;
    }

    public String getCloudAccessToken() {
        return cloudAccessToken;
    }

    public CloudResponse storeFileInCloud(InputStream document,
                                          CloudContent cmetaData, CloudServiceInteractionListener listener) {

        try {
            CloudFileTransferMonitor fileMonitor = new CloudFileTransferMonitor(
                    listener);


            return new CloudResponse(
                    "STATUS:FALSE;MSG:Unable To Send File To Destination;ERROR:{Communication Thread Breakage};");
        } catch (Exception exception) {
            return new CloudResponse(
                    "STATUS:FALSE;MSG:Unable To Send File To Destination;ERROR:{"
                            + exception.getMessage() + "};");
        }

    }
}
