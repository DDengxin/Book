package com.kuang.book.entiy;

import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

public class BookUsers implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private int auth;

    public BookUsers(int id, String username, String password, String email,int auth) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.auth = auth;
    }

    public BookUsers() {
    }

    public int getId() {
        return id;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "BookUsers{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", auth=" + auth +
                '}';
    }
}
