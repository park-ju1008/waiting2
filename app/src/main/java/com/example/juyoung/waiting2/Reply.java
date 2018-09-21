package com.example.juyoung.waiting2;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Reply implements Serializable {
    private String nickname;
    private String content;
    private int like;
    private String date;

    public Reply(String nickname, String content, int like, String date) {
        this.nickname = nickname;
        this.content = content;
        this.like = like;
        this.date = date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
