package com.jnu.student.myapplication.home.data.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String cover;
    private int day;
    private String title;
    private String period;
    private String description;
    private Date date;

    public Event(String cover, String title, String description, String period, Date date) {
        this.cover = cover;
        this.title = title;
        this.period = period;
        this.description = description;
        this.date = date;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getDay() {
        return (int) ((date.getTime() - new Date().getTime()) / 1000 / 60 / 60 / 24);
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
