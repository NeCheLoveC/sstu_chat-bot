package com.example.demo2bot.repo;

import com.example.demo2bot.entities.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TUserRepository extends JpaRepository<TUser,Long> {}