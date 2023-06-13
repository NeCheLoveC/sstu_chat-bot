package com.example.demo2bot.entities;

import com.example.demo2bot.config.Lang;
import jakarta.persistence.*;


public class TelegramUser
{
    /*
    @Id
    protected Long id;
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    protected User user;

    @Column(name = "query_state")
    String lastQueryState;//TRY_"AUTH"
    @Enumerated(EnumType.STRING)
    @Column(name = "lang")
    Lang lang;

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
     */
}
