package com.cliplay.model;


/**
 * Created by Manohar Peswani on 17/05/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
public class SMS {
    private String category;
    private String sms;
    private String id;
    private int approvedCount;
    private int disapprovedCount;
    private long timeStamp;
    private String uuid;
    private String node;

    public SMS(String category, String sms, String id, int approvedCount) {
        this.category = category;
        this.sms = sms;
        this.id = id;
        this.approvedCount = approvedCount;
    }

    public SMS() {
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getDisapprovedCount() {
        return disapprovedCount;
    }

    public void setDisapprovedCount(int disapprovedCount) {
        this.disapprovedCount = disapprovedCount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp() {
        this.timeStamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(int approvedCount) {
        this.approvedCount = approvedCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }
}
