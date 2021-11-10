package com.dabarobjects.storeharmony.droidstore.cloud;

public abstract interface CloudElementReady {
    public abstract void onCloudElementPutComplete(String paramString,
                                                   Integer paramInteger, long paramLong);

    public abstract void onCloudElementPutFailure(String paramString,
                                                  Integer paramInteger, long paramLong);
}

/*
 * Location:
 * C:\Users\admin\Desktop\Harmony2Project\harmony-platform\dist\lib\dos
 * -spike-webconnect.jar Qualified Name:
 * com.dabarobjects.spikeservice.cloud.CloudElementReady JD-Core Version: 0.6.0
 */