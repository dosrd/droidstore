package com.dabarobjects.storeharmony.droidstore.cloud;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class CloudServiceCalls {
    private final String cloudUrl;
    private final String userId;
    private final String permissionToken;

    public CloudServiceCalls(String cloudUrl, String userId, String permissionToken) {
        this.cloudUrl = cloudUrl;
        this.userId = userId;
        this.permissionToken = permissionToken;
    }

    public String storeFileinCloud(File file, CloudServiceInteractionListener listener){
        try{

            CloudFileTransferMonitor monitor
                    = new CloudFileTransferMonitor(listener);
            CloudTransferOperation transfer = new CloudTransferOperation(cloudUrl,file,file.getName(),userId,permissionToken);
            String fileReference = transfer.doTransfer(monitor,true);
            if(fileReference != null){
                return fileReference;
            }
        }catch(Exception e){

        }
        return null;
    }

    public String storeURIinCloud(Context ctx, Uri uri, CloudServiceInteractionListener listener){

        return storeFileinCloud(new File(getRealPathFromUri(ctx,uri)),listener);
    }


    public void storeFileinCloudAsync(File file, CloudServiceInteractionListener listener){
        try{

            CloudFileTransferMonitor monitor
                    = new CloudFileTransferMonitor(listener);
            CloudTransferOperation transfer = new CloudTransferOperation(cloudUrl,file,file.getName(),userId,permissionToken);
            transfer.doTransferAsync(monitor,true);

        }catch(Exception e){

        }

    }

    public void storeURIinCloudAsync(Context ctx, Uri uri, CloudServiceInteractionListener listener){

        storeFileinCloudAsync(new File(getRealPathFromUri(ctx,uri)),listener);
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
