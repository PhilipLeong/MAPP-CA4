package mapp.com.sg.bookhub.Models;

import java.util.List;

public class Post {
    private String title;
    private String condition;
    private Double mass;
    private Double price;
    private String location;
    private String schedule;
    private String school;
    private List<String> payments;
    private String createdBy;


    public Post(){}

    public Post(String title, String condition, Double mass, Double price, String location, String schedule, String school, List<String> payments, String createdBy){
        this.title = title;
        this.condition = condition;
        this.mass = mass;
        this.price = price;
        this.location = location;
        this.schedule = schedule;
        this.school = school;
        this.payments = payments;
        this.createdBy = createdBy;
    }


    public String getTitle() {
        return title;
    }

    public String getCondition() {
        return condition;
    }

    public Double getMass() {
        return mass;
    }

    public Double getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getSchool() {
        return school;
    }

    public List<String> getPayments() {
        return payments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setPayments(List<String> payments) {
        this.payments = payments;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
