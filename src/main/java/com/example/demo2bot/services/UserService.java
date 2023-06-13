package com.example.demo2bot.services;

import com.example.demo2bot.entities.User;
import com.example.demo2bot.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class UserService
{
    public UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByUniqueCode(String uniqueCode)
    {
        return this.userRepository.findUserByUniqueCode(uniqueCode);
    }
}
