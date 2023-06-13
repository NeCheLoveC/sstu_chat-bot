package com.example.demo2bot.entities;

import com.example.demo2bot.config.Lang;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@Entity
@Table
@Data
public class TUser
{
    @Id
    protected Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    protected User user;

    @Column(name = "query_state")
    String lastQueryState;//TRY_"AUTH"
    @Enumerated(EnumType.STRING)
    @Column(name = "lang")
    Lang lang;

    public TUser(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        //user.connectTelegramUser(this);
    }

    public boolean isAuthorizedUser()
    {
        return this.user != null ? true : false;
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

    public void setDefaultLang()
    {
        this.lang = Lang.RU;
    }
}
