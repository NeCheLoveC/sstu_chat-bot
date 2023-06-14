package com.example.demo2bot.entities;

import jakarta.persistence.*;

@Entity
@Table
public class Score
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "score")
    protected Integer score = null;

    @ManyToOne
    @JoinColumns(value = {
                    @JoinColumn(name = "user_id",referencedColumnName = "user_id"),
                    @JoinColumn(name = "direction_id", referencedColumnName = "direction_id"),
                    @JoinColumn(name = "claim_type", referencedColumnName = "claim_type")
            })
    protected Claim claim;
    @Column(name = "absence")
    protected boolean absence = false;//Неявка

    public Score() {}

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public boolean isAbsence() {
        return absence;
    }

    public void setAbsence(boolean absence) {
        this.absence = absence;
        claim.setAbsence(absence);
    }
}
