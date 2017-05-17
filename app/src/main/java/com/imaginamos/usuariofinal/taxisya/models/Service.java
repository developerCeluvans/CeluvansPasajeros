package com.imaginamos.usuariofinal.taxisya.models;

/**
 * Created by leo on 9/14/15.
 */
public class Service {
    private String serviceId;
    private String statusId;
    private String score;
    private String userId;
    private String driverId;
    private long dateService;

    public Service(String serviceId, String statusId, String score, String userId, String driverId, long dateService) {
        this.serviceId = serviceId;
        this.statusId = statusId;
        this.score = score;
        this.userId = userId;
        this.driverId = driverId;
        this.dateService = dateService;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public long getDateService() {
        return dateService;
    }

    public void setDateService(long dateService) {
        this.dateService = dateService;
    }
}
