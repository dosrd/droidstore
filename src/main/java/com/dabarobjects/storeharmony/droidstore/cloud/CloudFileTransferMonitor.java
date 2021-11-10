package com.dabarobjects.storeharmony.droidstore.cloud;

public final class CloudFileTransferMonitor implements
        CloudTransferProgressMonitor {

    private CloudServiceInteractionListener listener;

    public CloudFileTransferMonitor(CloudServiceInteractionListener listener) {
        this.listener = listener;
    }

    public void onCloudPartSent(int partNo, long totalParts,
                                long transferedByte, long totalBytes) {
        if (this.listener != null) {
            this.listener.onInteractionUpdate(totalParts, partNo,
                    new CloudResponse("STATUS:TRUE;"));
        }
    }

    public void onTransferFailure(String reason) {
        if (this.listener != null) {
            this.listener.onInteractionFailed(0, "", new CloudResponse(
                    "STATUS:FALSE;MSG:" + reason + ";"));
        }
    }

    public void onEOF(String transferId) {
        if (this.listener != null) {
            this.listener.onInteractionEndedSuccessfully(transferId);
        }
    }

    public void onTransferIdAcquired(long dataLength, String transferId) {
        if (this.listener != null) {
            this.listener.onInteractionStarted(transferId);
        }
    }
}
