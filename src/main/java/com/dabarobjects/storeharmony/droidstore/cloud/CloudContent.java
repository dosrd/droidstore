package com.dabarobjects.storeharmony.droidstore.cloud;


import com.dabarobjects.storeharmony.droidstore.sqlite.SQLKeepEntityAbstract;

public final class CloudContent extends SQLKeepEntityAbstract<CloudContent> {

    private String itemId;
    private String ownerId; // the storeId
    private String ownerCloudId;//
    private String ownerEmail;
    private String ownerName;
    private Double longLocation;
    private Double latLocation;

    private String itemDetails;
    private String itemName;
    private String deviceReference;
    private String cloudTrackingId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerCloudId() {
        return ownerCloudId;
    }

    public void setOwnerCloudId(String ownerCloudId) {
        this.ownerCloudId = ownerCloudId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getLongLocation() {
        return longLocation;
    }

    public void setLongLocation(Double longLocation) {
        this.longLocation = longLocation;
    }

    public Double getLatLocation() {
        return latLocation;
    }

    public void setLatLocation(Double latLocation) {
        this.latLocation = latLocation;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDeviceReference() {
        return deviceReference;
    }

    public void setDeviceReference(String deviceReference) {
        this.deviceReference = deviceReference;
    }

    public String getCloudTrackingId() {
        return cloudTrackingId;
    }

    public void setCloudTrackingId(String cloudTrackingId) {
        this.cloudTrackingId = cloudTrackingId;
    }


}
