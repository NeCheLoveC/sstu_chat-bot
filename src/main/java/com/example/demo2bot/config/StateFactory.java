package com.example.demo2bot.config;

import com.example.demo2bot.entities.TUser;

import java.util.Optional;

public class StateFactory
{
    public static State getStateByTUser(Optional<TUser> tUser)
    {
        if(tUser.isEmpty())
        {
            return State.EMPTY_STATE;
        }
        //Данный ChatId же отправлял сообщения
        else
        {
            if(tUser.get().getLastQueryState() == "AUTH")
                return State.EMPTY_STATE.AUTH;
            else if(tUser.get().getLastQueryState() == "SUCCESSFUL_AUTH" )
                return State.SUCCESSFUL_AUTH;
            else
                return State.EMPTY_STATE.ANOTHER_COMMAND;
        }
    }
}
