package com.example.demo2bot.repo;

import com.example.demo2bot.entities.TUser;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Data
public class TUserSqlService implements TUserService
{
    @Autowired
    protected TUserRepository tUserRepository;

    public Optional<TUser> getTUserById(Long id)
    {
        return tUserRepository.findById(id);
    }

    public TUser saveOrUpdate(TUser user)
    {
        return tUserRepository.save(user);
    }

    @Override
    public List<TUser> getAllTUsers() {
        return tUserRepository.findAll();
    }
}
