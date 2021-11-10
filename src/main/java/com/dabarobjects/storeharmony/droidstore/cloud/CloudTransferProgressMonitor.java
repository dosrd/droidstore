package com.dabarobjects.storeharmony.droidstore.cloud;

public abstract interface CloudTransferProgressMonitor {
    public abstract void onTransferIdAcquired(long paramLong, String paramString);

    public abstract void onCloudPartSent(int paramInt, long paramLong1, long paramLong2, long paramLong3);

    public abstract void onTransferFailure(String paramString);

    public abstract void onEOF(String paramString);
}

/* Location:           C:\Users\admin\Desktop\Harmony2Project\harmony-platform\dist\lib\dos-spike-webconnect.jar
 * Qualified Name:     com.dabarobjects.spikeservice.cloud.CloudTransferProgressMonitor
 * JD-Core Version:    0.6.0
 */