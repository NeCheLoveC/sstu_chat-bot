package com.example.demo2bot.repo;

import com.example.demo2bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    @Query("select u from User u where u.uniqueCode = :uniqueCode")
    public User findUserByUniqueCode(String uniqueCode);
}