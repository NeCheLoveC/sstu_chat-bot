package com.example.demo2bot.entities;

public enum ClaimType
{
    BUDGET_GENERAL_LIST("Бюджетые места"),
    BUDGET_SPECIAL_QUOTA("Отдельная квота"),
    BUDGET_TARGET_QUOTA("Целевая квота"),
    BUDGET_UNUSUAL_QUOTA("Особая квота"),
    COMMERCE_GENERAL_LIST("На платной основе");

    private String rusName;

    ClaimType(String rusName)
    {
        this.rusName = rusName;
    }

}
