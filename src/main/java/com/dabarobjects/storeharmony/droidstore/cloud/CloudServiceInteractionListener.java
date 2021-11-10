package com.dabarobjects.storeharmony.droidstore.cloud;

public interface CloudServiceInteractionListener {
    public abstract void onInteractionStarted(Object paramObject);

    public abstract void onInteractionFailed(int paramInt, String paramString, CloudResponse paramSpikeResponseMap);

    public abstract void onInteractionEndedSuccessfully(String paramString);

    public abstract void onInteractionUpdate(long totalParts, long partNo, CloudResponse paramSpikeResponseMap);
}
