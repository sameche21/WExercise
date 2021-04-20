package com.example.webserviceexercise;

import java.io.Serializable;

public class Payer implements Serializable {

    private String payer;
    private int points;
    private String date;

    public Payer(String payer, int points, String date) {
        this.payer = payer;
        this.points = points;
        this.date = date;
    }

    public int getPoints() {
        return points;
    }

    public String getPayer() {
        return payer;
    }

    public String getDate() {
        return date;
    }
}
