package com.example.demo2bot.services;

import com.example.demo2bot.model.User;
import com.example.demo2bot.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserService
{
    public UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUniqueCode(String uniqueCode)
    {
        if (uniqueCode == null)
            return null;
        return this.userRepository.findUserByUniqueCode(uniqueCode);
    }
}
