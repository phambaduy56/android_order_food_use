package com.example.order_foods.Model;

public class User {
    private String name;
    private String pass;
    private String Phone;

    public User(){}

    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
