package com.google.mw.backend;

import java.util.Date;

/**
 * The object model for the data we are sending through endpoints
 */
public class CaseBean {

    public enum CaseStatus {
        ACTIVE, EMERGENCY, CLOSED
    }

    private long id;
    private String title;
    private String owner;
    private Date dateCreated;
    private Date dateClosed;
    private String status;
    private String comments;
    private double latitude;
    private double longitude;

    //need to establish rules for when to mark notificationSent true or false.
    //presumably, tasks created by Mobile don't need a notification.
    //Tasks set to Urgent by the web client should definitely send notification.
    private Boolean notificationNeeded;
//    private GeoPt geoPt;


    public Boolean getNotificationNeeded() {
        return notificationNeeded;
    }

    public void setNotificationNeeded(Boolean notificationNeeded) {
        this.notificationNeeded = notificationNeeded;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String data) {
        title = data;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
//    public GeoPt getGeoPt() {
//        return geoPt;
//    }
//
//    public void setGeoPt(GeoPt geoPt) {
//        this.geoPt = geoPt;
//    }
}