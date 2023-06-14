package com.example.demo2bot.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(indexes = {@Index(unique = true, columnList = "unique_code")}, name = "\"user\"")
public class User
{
    @Id
    protected Long id;
    @Column(name = "unique_code", unique = true)
    protected String uniqueCode;
    @Column(name = "original_documents")
    protected boolean originalDocuments = false;
    @OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST})
    protected List<Claim> claims = new ArrayList();
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    protected Collection<TUser> telegramUser = new ArrayList<>();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "win_claim_user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "win_claim_department_id", referencedColumnName = "direction_id"),
            @JoinColumn(name = "win_claim_type", referencedColumnName = "claim_type")
    })
    protected Claim winClaim;

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

    public boolean isOriginalDocuments()
    {
        return originalDocuments;
    }

    public Long getId() {
        return id;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public Collection<TUser> getTelegramUser() {
        return telegramUser;
    }

    public Claim getWinClaim() {
        return winClaim;
    }
}
