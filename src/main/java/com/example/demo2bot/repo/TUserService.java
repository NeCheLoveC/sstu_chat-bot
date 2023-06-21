package com.example.demo2bot.repo;

import com.example.demo2bot.entities.TUser;

import java.util.List;
import java.util.Optional;

public interface TUserService
{
    Optional<TUser> getTUserById(Long id);
    TUser saveOrUpdate(TUser tuser);

    List<TUser> getAllTUsers();
}
