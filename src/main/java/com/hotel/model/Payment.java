package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Payment implements Serializable {
    private String reservationId;
    private double amountDue;
    private double amountReceived;
    private double change;
    private String method;
    private LocalDateTime date;

    public Payment(String reservationId, double amountDue, double amountReceived, double change, String method) {
        this.reservationId = reservationId;
        this.amountDue = amountDue;
        this.amountReceived = amountReceived;
        this.change = change;
        this.method = method;
        this.date = LocalDateTime.now();
    }

    public String getReservationId() { return reservationId; }
    public double getAmountDue() { return amountDue; }
    public double getAmountReceived() { return amountReceived; }
    public double getChange() { return change; }
    public String getMethod() { return method; }
    public LocalDateTime getDate() { return date; }
} 