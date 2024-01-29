package com.example.demo2bot.entities;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.*;

@Entity
@Table(name = "claim")
public class Claim
{
    @EmbeddedId
    protected PrimaryKey id = new PrimaryKey();
    @ManyToOne
    //@JoinColumn(name = "user_id")
    @MapsId(value = "userId")
    protected User user;
    @ManyToOne
    //@JoinColumn(name = "direction_id")
    @MapsId(value = "directionId")
    protected Direction direction;

    @Column(name = "countScore_for_individual_achievements")
    protected int countScoreForIndividualAchievements = 0;

    @Column(name = "champion")
    protected boolean champion = false;
    @Column(name = "absence")
    protected boolean absence = false;
    @OneToMany(mappedBy = "claim", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    protected List<Score> scoreList = new LinkedList<>();
    @Column(name = "summary_of_score")
    protected int summaryOfScore = 0;
    @Column(name = "is_win")
    protected boolean isWin = false;
    @Column(name = "position_win_list")
    protected int positionIntoWinList = -1;//-1 - если позиция заявления не выигрывает в конкурсе
    protected Claim(){}

    public User getUser() {
        return user;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public PrimaryKey getId() {
        return id;
    }

    public void addScore(Score score)
    {
        this.scoreList.add(score);
    }

    public void addScore(Collection<Score> score)
    {
        this.scoreList.addAll(score);
    }

    public int getCountScoreForIndividualAchievements() {
        return countScoreForIndividualAchievements;
    }

    public void setCountScoreForIndividualAchievements(int countScoreForIndividualAchievements) {
        this.countScoreForIndividualAchievements = countScoreForIndividualAchievements;
    }

    public boolean isChampion() {
        return champion;
    }

    public void setChampion(boolean champion) {
        this.champion = champion;
    }

    public ClaimType getClaimType() {
        return this.id.claimType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Claim claim = (Claim) o;
        return getId() != null && Objects.equals(getId(), claim.getId())
                && getId() != null && Objects.equals(getId(), claim.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isBudget()
    {
        return id.claimType == ClaimType.COMMERCE_GENERAL_LIST ? false : true;
    }

    public boolean isAbsence() {
        return absence;
    }

    public void setAbsence(boolean absence) {
        this.absence = absence;
    }

    public int getSummaryOfScore() {
        return summaryOfScore;
    }

    public void setSummaryOfScore(int summaryOfScore) {
        this.summaryOfScore = summaryOfScore;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public boolean claimWithOriginalDoc()
    {
        return this.user.isOriginalDocuments();
    }

    @Override
    public String toString() {
        String quota = "";
        Integer minScore = 0;
        int max = 0;
        if(this.getClaimType() == ClaimType.BUDGET_SPECIAL_QUOTA)
        {
            quota = "Отдельная квота";
            minScore = direction.minScoreSpecialList;
            max = this.direction.getAmountSpecialQuota();
        }
        else if(this.getClaimType() == ClaimType.BUDGET_TARGET_QUOTA)
        {
            quota = "Целевое направление";
            minScore = direction.minScoreTargetList;
            max = this.direction.getAmountTargetQuota();
        }
        else if(this.getClaimType() == ClaimType.BUDGET_UNUSUAL_QUOTA)
        {
            quota = "Особая квота";
            minScore = direction.minScoreUnusualList;
            max = this.direction.getAmountUnusualQuota();
        }
        else if(this.getClaimType() == ClaimType.BUDGET_GENERAL_LIST)
        {
            quota = "Без квоты (бюджет)";
            minScore = direction.minScoreGeneralList;
            max = this.direction.amountBudgetAfterFirstStage;
        }
        else if(this.getClaimType() == ClaimType.COMMERCE_GENERAL_LIST)
            quota = "Без квоты (коммерция)";


        String result = "Направление: ";
        result += this.direction.name;
        result += " (" + this.direction.abbreviation + ")\n";
        result += "Сумма баллов абитуриента: " + this.summaryOfScore + "\n";
        result += "Использованная квота : ";
        result += quota + "\n";
        result += "Форма обучения: " + this.direction.getEducationType().nameRus + "\n";
        //Абитуриент проходит по заявлению - это его высший приоритет
        if(this.isWin() && getClaimType()!=ClaimType.COMMERCE_GENERAL_LIST && user.originalDocuments)
        {
            if(!this.absence)
            {
                result += "Место в очереди: " + (this.positionIntoWinList + 1) + " / " + max;
            }

            else
                result += "Абитуриент не явился на экзамен (исключается из конкурса).";
        }
        //Документы подал, а вот на бюджет по данному место не прошел (не хватило баллов / уже имеет высший приоритет)
        else if(user.originalDocuments)
        {
            if(!this.absence)
            {
                if(this.isBudget())
                    result += "Проходной балл: " + minScore;
                else
                    result += "Абитуриент проходит по минимальным баллам";
            }
            else
            {
                result += "Абитуриент не явился на экзамен (исключается из конкурса).";
            }
        }
        else if(!user.originalDocuments)
        {

            if(this.absence)
            {
                result += "Абитуриент не явился на экзамен (исключается из конкурса).";
            }
            //Заявка не проходит по минимальному баллу
            else if(positionIntoWinList == -1 && minScore != null)
            {
                result += "Заявление не проходит по минимальному баллу - " + minScore.toString();
            }
            else
            {
                if(!this.getClaimType().equals(ClaimType.COMMERCE_GENERAL_LIST))
                    result += "Место в очереди: " + (this.positionIntoWinList + 1) + " / " + max;
                else
                    result += "Абитуриент проходит по минимальным баллам.";
                //result += "Место в очереди: " + (this.positionIntoWinList + 1) + " / " + max + "\n";
            }
        }
        return result;
    }
}
