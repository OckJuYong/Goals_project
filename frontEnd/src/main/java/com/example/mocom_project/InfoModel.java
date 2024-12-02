package com.example.mocom_project;

public class InfoModel {
    private int id;
    private String name;
    private int price;
    private String language;
    private String address;
    private String schedule;
    private String place;
    private String tag;
    private String goal;
    private int time;
    private String day;

    public InfoModel(String name, int price, String language, String address, String schedule,
                     String place, String tag, String goal, int time, String day) {
        this.name = name;
        this.price = price;
        this.language = language;
        this.address = address;
        this.schedule = schedule;
        this.place = place;
        this.tag = tag;
        this.goal = goal;
        this.time = time;
        this.day = day;
    }

    // 각 필드에 대한 getter, setter 메서드
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
