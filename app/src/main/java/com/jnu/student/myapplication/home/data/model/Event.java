package com.jnu.student.myapplication.home.data.model;

import android.media.Image;

import java.util.Date;

public class Event {
    private int coverId;
    private int day;
    private String title;
    private Date date;

    public Event(int coverId, int day, String title, Date date) {
        this.coverId = coverId;
        this.day = day;
        this.title = title;
        this.date = date;
    }

    public int getCoverId() {
        return coverId;
    }

    public void setCoverId(int cover) {
        this.coverId = cover;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
