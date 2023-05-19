package com.example.demo2bot.model;

import jakarta.persistence.*;

@Entity
@Table
public class TelegramUser
{
    @Id
    protected Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;

    protected TelegramUser(){}

    public TelegramUser(Long id) {
        this.id = id;
    }

    public TelegramUser(Long id, User user) {
        this.id = id;
        this.user = user;
    }

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
        user.connectTelegramUser(this);
    }

    public void unlink()
    {
        if(user != null)
            this.user = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
