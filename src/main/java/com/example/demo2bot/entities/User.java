package com.example.demo2bot.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "userr")
public class User
{
    @Id
    protected Long id;
    @Column(name = "unique_code", unique = true)
    protected String uniqueCode;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    protected Collection<TUser> telegramUser = new ArrayList<>();

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public void connectTelegramUser(TUser tUser)
    {
        telegramUser.add(tUser);
    }

}
