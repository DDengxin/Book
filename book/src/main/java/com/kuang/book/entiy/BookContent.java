package com.kuang.book.entiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class BookContent {
    private int tid;
    private String bname;
    private String title;
    private String content;
    private int book_id;

    public BookContent() {
    }

    public BookContent(int tid, String bname, String title, String content, int book_id) {
        this.tid = tid;
        this.bname = bname;
        this.title = title;
        this.content = content;
        this.book_id = book_id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }
}