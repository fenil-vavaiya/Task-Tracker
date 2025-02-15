package com.example.googletaskproject.domain;

public class modelDate {

    int date;
    int month;

    public modelDate(int date, int month) {
        this.date = date;
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
