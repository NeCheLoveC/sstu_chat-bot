package com.example.demo2bot.entities;


import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Direction", indexes = {
        @Index(name = "INDEX_URL_TO_BUDGET", columnList = "url_to_list_of_claims_budget", unique = true),
        @Index(name = "INDEX_URL_TO_COMMERCE", columnList = "url_to_list_of_claims_commerce", unique = true)
})
public class Direction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name")
    protected String name;
    @Column(name = "edu_type")
    @Enumerated(EnumType.STRING)
    protected EducationType educationType;//очка, заочка, очно-заочное

    @ManyToOne
    @JoinColumn(name = "institute_id")
    protected Institute institute;

    //Особая квота
    @Column(name = "unusual_quota")
    protected int amountUnusualQuota = 0;

    //Основные места - план (бюджет)
    @Column(name = "amount_budget")
    protected int amountBudget = 0;

    //Спец квота
    @Column(name = "amount_special_quota")
    protected int amountSpecialQuota = 0;

    //Целевое обучение
    @Column(name = "amount_target_quota")
    protected int amountTargetQuota = 0;
    //Бюджетных мест согласно плану
    @Column(name = "amount_main_budget_plan")
    protected int amountMainBudgetIntoPlan = 0;

    @Column(name = "abbreviation")
    protected String abbreviation;

    @Column(name = "url_to_list_of_claims_budget", unique = true)
    protected String urlToListOfClaims;
    @Column(name = "ignore_direction")
    protected boolean ignoreDirection = false;
    @Column(name = "url_to_list_of_claims_commerce",unique = true)
    protected String urlToListOfClaimsCommerce;
    // TODO: 05.05.2023 Вот тут не уверен насчет hibernate
    //@OneToMany
    //@JoinColumn(name = "direction_id")
    //List<Exam> exams = new LinkedList<>();
    /*
    @Enumerated(EnumType.STRING)
    @Column(name = "direction_type_payment")
    protected DirectionType directionTypePayment;
     */
    @Column(nullable = true)
    protected Integer minScoreGeneralList = null;
    @Column(nullable = true)
    protected Integer minScoreTargetList = null;
    @Column(nullable = true)
    protected Integer minScoreUnusualList = null;
    @Column(nullable = true)
    protected Integer minScoreSpecialList = null;
    @Column(name = "amount_budget_after_first_stage")
    protected int amountBudgetAfterFirstStage = 0;

    public Direction() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
        institute.addDirection(this);
    }

    public int getAmountBudget() {
        return amountBudget;
    }

    public void setAmountBudget(int amountBudget) {
        this.amountBudget = amountBudget;
    }

    public int getAmountSpecialQuota() {
        return amountSpecialQuota;
    }

    public void setAmountSpecialQuota(int amountSpecialQuota) {
        this.amountSpecialQuota = amountSpecialQuota;
    }

    public int getAmountTargetQuota() {
        return amountTargetQuota;
    }

    public void setAmountTargetQuota(int amountTargetQuota) {
        this.amountTargetQuota = amountTargetQuota;
    }

    public int getAmountMainBudgetIntoPlan() {
        return amountMainBudgetIntoPlan;
    }

    public void setAmountMainBudgetIntoPlan(int amountMainBudgetIntoPlan) {
        this.amountMainBudgetIntoPlan = amountMainBudgetIntoPlan;
    }

    public Long getId() {
        return id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getAmountUnusualQuota() {
        return amountUnusualQuota;
    }

    public void setAmountUnusualQuota(int unusualQuota) {
        this.amountUnusualQuota = unusualQuota;
    }

    public String getUrlToListOfClaims() {
        return urlToListOfClaims;
    }

    public void setUrlToListOfClaims(String urlToListOfClaims) {
        this.urlToListOfClaims = urlToListOfClaims;
    }

    public String getUrlToListOfClaimsCommerce() {
        return urlToListOfClaimsCommerce;
    }

    public void setUrlToListOfClaimsCommerce(String urlToListOfClaimsCommerce) {
        this.urlToListOfClaimsCommerce = urlToListOfClaimsCommerce;
    }


    public EducationType getEducationType() {
        return educationType;
    }

    public void setEducationType(EducationType educationType) {
        this.educationType = educationType;
    }

    public boolean isIgnoreDirection() {
        return ignoreDirection;
    }

    public void setIgnoreDirection(boolean ignoreDirection) {
        this.ignoreDirection = ignoreDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Direction direction = (Direction) o;
        return getId() != null && Objects.equals(getId(), direction.getId());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
