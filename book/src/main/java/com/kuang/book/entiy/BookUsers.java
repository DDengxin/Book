package com.kuang.book.entiy;

import java.io.Serializable;

public class BookUsers implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private int auth;
    private double money;

    public BookUsers(int id, String username, String password, String email, int auth, double money) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.auth = auth;
        this.money = money;
    }

    public BookUsers() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
