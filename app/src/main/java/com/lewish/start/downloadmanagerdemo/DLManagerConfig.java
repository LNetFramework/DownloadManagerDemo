package com.lewish.start.downloadmanagerdemo;

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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDesc() {
        return notificationDesc;
    }

    public void setNotificationDesc(String notificationDesc) {
        this.notificationDesc = notificationDesc;
    }

    public long getQueryInterval() {
        return queryInterval;
    }

    public void setQueryInterval(long queryInterval) {
        this.queryInterval = queryInterval;
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

        public DLManagerConfig build(){
            return dlManagerConfig;
        }

    }
}
