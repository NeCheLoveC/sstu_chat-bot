package com.example.demo2bot.config;

import com.example.demo2bot.entities.Claim;
import com.example.demo2bot.entities.User;
import com.example.demo2bot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class FormResultOfClaim
{
    @Autowired
    UserService userService;
    @Transactional
    public String getStringResultOfClaim(String uniqueCode)
    {
        Optional<User> user = userService.getUserByUniqueCode(uniqueCode);
        if(user.isPresent())
        {
            String buf = "Абитуриент: " + uniqueCode + "\n"
                       + "Абитуриент подал оригинальные документы : ";;
            if(user.get().isOriginalDocuments())
            {
                buf += "Да\n";
                if(user.get().getWinClaim() != null)
                {
                    buf += "Высший приоритет : \n";
                    buf += user.get().getWinClaim().toString();
                }
                else
                {
                    //buf += "\nАбитуриент не прошел по выбранным специальностям.\n";
                    buf += "Заявления абитуриента на бюджет:\n";
                    for(Claim claim : user.get().getClaims())
                    {
                        if(!claim.isBudget())
                            continue;
                        buf += "\n----------------------------------\n";
                        buf += claim.toString();
                        buf += "\n----------------------------------\n";
                    }

                    buf += "\nЗаявления абитуриента на платной основе:\n";
                    for(Claim claim : user.get().getClaims())
                    {
                        if(claim.isBudget())
                            continue;
                        buf += "\n----------------------------------\n";
                        buf += claim.toString();
                        buf += "\n----------------------------------\n";
                    }
                }
            }
            else
            {
                buf += "Нет\n";
                buf += "Заявления абитуриента на бюджет:\n";
                for(Claim claim : user.get().getClaims())
                {
                    if(!claim.isBudget())
                        continue;
                    buf += "\n----------------------------------\n";
                    buf += claim.toString();
                    buf += "\n----------------------------------\n";
                }
                buf += "\nЗаявления абитуриента на платной основе::\n";
                for(Claim claim : user.get().getClaims())
                {
                    if(claim.isBudget())
                        continue;
                    buf += "\n----------------------------------\n";
                    buf += claim.toString();
                    buf += "\n----------------------------------\n";
                }
            }
            return buf;
        }
        else
        {
            return "\nДанный абитуриент отозвал свою заявку...";
        }

    }
}
