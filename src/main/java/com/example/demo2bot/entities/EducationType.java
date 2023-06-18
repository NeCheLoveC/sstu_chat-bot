package com.example.demo2bot.entities;

public enum EducationType
{
    OChNAYa("Очная"),
    ZAOChNAYa("Заочная"),
    OChNO_ZAOChNAYa("Очно-заочная");

    public String nameRus;

    EducationType(String nameRus)
    {
        this.nameRus = nameRus;
    }
}
