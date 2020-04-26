package com.kuang.book.entiy;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class BookInfo implements Serializable {

    private int id;
    private String bname;
    private String author;
    private String img;
    private String description;
    private String type;
    private String state;
    private int ticket;
    private double score;

    public BookInfo() {
    }

    public BookInfo(int id, String bname, String author, String img, String description, String type, String state, int ticket, double score) {
        this.id = id;
        this.bname = bname;
        this.author = author;
        this.img = img;
        this.description = description;
        this.type = type;
        this.state = state;
        this.ticket = ticket;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
