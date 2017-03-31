package com.lewish.start.downloadmanagerdemo;

import java.util.HashMap;

/**
 * author: sundong
 * created at 2017/3/30 22:03
 */
public class DLManagerConfig {

    private static final int DEFAULT_QUERY_INTERVAL = 1000;

    private String downloadUrl;
    private String fileName;
    private String notificationTitle;
    private String notificationDesc;
    private long queryInterval = DEFAULT_QUERY_INTERVAL;

    private HashMap<String,String> downLoadRequestHeaders = new HashMap<>();

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationDesc() {
        return notificationDesc;
    }

    public long getQueryInterval() {
        return queryInterval;
    }

    public HashMap<String, String> getDownLoadRequestHeaders() {
        return downLoadRequestHeaders;
    }

    public static class Builder {
        private DLManagerConfig dlManagerConfig;

        public Builder() {
            dlManagerConfig = new DLManagerConfig();
        }

        public Builder downLoadUrl(String downLoadUrl){
            dlManagerConfig.downloadUrl = downLoadUrl;
            return this;
        }

        public Builder fileName(String fileName){
            dlManagerConfig.fileName = fileName;
            return this;
        }

        public Builder notificationTitle(String notificationTitle){
            dlManagerConfig.notificationTitle = notificationTitle;
            return this;
        }

        public Builder notificationDesc(String notificationDesc){
            dlManagerConfig.notificationDesc = notificationDesc;
            return this;
        }

        public Builder queryInterval(long queryInterval){
            dlManagerConfig.queryInterval = queryInterval;
            return this;
        }

        public Builder requestHeader(String header,String value){
            dlManagerConfig.downLoadRequestHeaders.put(header,value);
            return this;
        }

        public DLManagerConfig build(){
            return dlManagerConfig;
        }

    }
}
