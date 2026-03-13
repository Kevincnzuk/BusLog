package com.github.kevincnzuk.buslog;

public class EntryVO {
    private int id;
    private String busNumber;
    private String busModel;
    private String busRouteNumber;
    private String busRouteDestination;
    private long createTime;
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusModel() {
        return busModel;
    }

    public void setBusModel(String busModel) {
        this.busModel = busModel;
    }

    public String getBusRouteNumber() {
        return busRouteNumber;
    }

    public void setBusRouteNumber(String busRouteNumber) {
        this.busRouteNumber = busRouteNumber;
    }

    public String getBusRouteDestination() {
        return busRouteDestination;
    }

    public void setBusRouteDestination(String busRouteDestination) {
        this.busRouteDestination = busRouteDestination;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
