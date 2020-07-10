package com.atguigu.spring.getstarted;

import java.util.List;

public class User {

    private String userName;
    private List<Car> cars;

    private String wifeName;

    public String getWifeName() {
        return wifeName;
    }

    public void setWifeName(String wifeName) {
        System.out.println("setWifeName: " + wifeName);
        this.wifeName = wifeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public User() {
        System.out.println("User's Constructor...");
    }

    @Override
    public String toString() {
        return "User [userName=" + userName + ", cars=" + cars + "]";
    }

    public void init() {
        System.out.println("init method...");
    }

    public void destroy() {
        System.out.println("destroy method...");
    }

}
