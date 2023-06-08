package com.example.demo2bot.config;
import com.example.demo2bot.model.TelegramUser;
import com.example.demo2bot.model.User;
import com.example.demo2bot.services.TelegramUserService;
import com.example.demo2bot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    @Autowired
    protected TelegramUserService telegramUserService;
    @Autowired
    protected UserService userService;

    public TelegramBot(BotConfig config) {

        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        listofCommands.add(new BotCommand("/deletedata", "delete my data"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
        }
    }

    @Override
    public void onUpdateReceived(Update update)
    {
        String callBack = null;
        if(update.hasCallbackQuery())
        {
            String query = update.getCallbackQuery().getData();
            long userId = update.getCallbackQuery().getMessage().getChatId();
            switch (query)
            {
                case "INVITE":
                    menuInvite(userId);
                    break;
                case "HOUSE":
                    //
                    break;
                case "LIFE":
                    //
                    break;
                case "CONTACTS":
                    //
                    contactsMenu(userId);
                    break;
                case "CONTACTS_PHONE":
                    //
                    showPhone(userId);
                    break;
                case "CONTACTS_ADRRES":
                    showAdress(userId);
                    break;
                case "CONTACTS_ADRESS_CAMPUS":
                    showAdresCampus(userId);
                    break;
                case "CONTACTS_POST":
                    showAdressPost(userId);
                    break;
                case "MENU":
                    getValidMenu(userId);
                    break;
                case "INVITE_DATE":
                    dateOfDoc(userId);
                    break;
                case "INVITE_DOC":
                    docForInvite(userId);
                    break;
                case "INVITE_COUNT_BUDGET":
                    amountBudget(userId);
                    break;
                case "INVITE_HOW":
                    howInvite(userId);
                    break;
                case "INVITE_SPECIAL":
                    typeOfSpecial(userId);
                    break;
                case "INVITE_STAGES":
                    stageOfInvite(userId);
                    break;
                case "AUTH":
                    auth(userId);
                    break;
                case "LOGOUT":
                    logout(userId);
                    break;
                case "LOGOUT_YES":
                    logoutYes(userId);
                    break;
            }
        }
        else if(update.hasMessage() && update.getMessage().hasText())
        {
                String messageText = update.getMessage().getText();
                //Попытка авторизации
                if(messageText.matches("\\d\\d\\d-\\d\\d\\d-\\d\\d\\d \\d\\d") || (messageText.matches("\\d+")))
                {
                    tryAuth(update.getMessage().getChatId(), messageText);
                }
                else
                {
                    long chatID = update.getMessage().getChatId();
                    switch (messageText)
                    {
                        case "/start":
                            getValidMenu(chatID);
                            break;
                    }
                }

        }
        }




    public void showPhone(long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Назад");
        invite.setCallbackData("CONTACTS");
        rowInLine.add(invite);
        sections.add(rowInLine);
        markupInLine.setKeyboard(sections);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("+7 88888 888 88");
        sendMessage.setReplyMarkup(markupInLine);
        sendMes(sendMessage);
    }

    public void showAdresCampus(long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Назад");
        invite.setCallbackData("CONTACTS");
        rowInLine.add(invite);
        sections.add(rowInLine);
        markupInLine.setKeyboard(sections);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Адрес учебных корпусов : 1 корпус - Политехническая 77\n2 корпус - Стрельцова 2\n3 корпус - Одинцова 3");
        sendMessage.setReplyMarkup(markupInLine);
        sendMes(sendMessage);
    }

    public void showAdress(long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Назад");
        invite.setCallbackData("CONTACTS");
        rowInLine.add(invite);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Адрес приемной комисии : Политехническая 77");
        sendMessage.setReplyMarkup(markupInLine);
        sendMes(sendMessage);
    }

    public void showAdressPost(long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Назад");
        invite.setCallbackData("CONTACTS");
        rowInLine.add(invite);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Адрес почтовых отделений : Политехническая 77 индекс - 440231");
        sendMessage.setReplyMarkup(markupInLine);
        sendMes(sendMessage);
    }

    public void contactsMenu(Long chatId)
    {
        //long chatId = update.getMessage().getChatId();
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Телефон");
        invite.setCallbackData("CONTACTS_PHONE");
        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Адрес приемной комиссии");
        houses.setCallbackData("CONTACTS_ADRRES");
        InlineKeyboardButton contacts = new InlineKeyboardButton();
        contacts.setText("Адреса почтовых отделений");
        contacts.setCallbackData("CONTACTS_POST");
        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Адреса учебных корпусов");
        life.setCallbackData("CONTACTS_ADRESS_CAMPUS");
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("MENU");
        rowInLine.add(invite);
        rowInLine.add(houses);
        rowInLine.add(contacts);
        rowInLine.add(life);
        rowInLine.add(back);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Главное меню - ");
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void startMessage(Update update)
    {
        Chat chat = update.getMessage().getChat();
        long userId = update.getMessage().getChatId();
        String userName = chat.getFirstName();
        String textMessage = "Hello, " + userName;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(textMessage);


        try{
            execute(sendMessage);
        }
        catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<List<InlineKeyboardButton>> toVertical(List<List<InlineKeyboardButton>> sections, List<InlineKeyboardButton> rowInLine)
    {
        for(InlineKeyboardButton b : rowInLine)
        {
            List<InlineKeyboardButton> button = new LinkedList<>();
            button.add(b);
            sections.add(button);
        }
        return sections;
    }
    public void getValidMenu(Long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Поступление");
        invite.setCallbackData("INVITE");


        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Общежитие");
        houses.setCallbackData("HOUSE");

        InlineKeyboardButton contacts = new InlineKeyboardButton();
        contacts.setText("Контакты");
        contacts.setCallbackData("CONTACTS");

        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Студенческая жизнь");
        life.setCallbackData("LIFE");

        InlineKeyboardButton auth = new InlineKeyboardButton();
        auth.setText("Авторизоваться");
        auth.setCallbackData("AUTH");

        InlineKeyboardButton logout = new InlineKeyboardButton();
        logout.setText("Выйти из системы ");
        logout.setCallbackData("LOGOUT");

        TelegramUser tUser = telegramUserService.findByChatId(chatId);


        rowInLine.add(invite);
        rowInLine.add(houses);
        rowInLine.add(contacts);
        //Авторизован в системе
        if(tUser == null)
        {
            rowInLine.add(auth);
        }
        //Неавторизован
        else
        {
            rowInLine.add(logout);
        }
        rowInLine.add(life);
        //sections.add(rowInLine);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Главное меню:\n ");
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }
    public void tryAuth(Long chatId, String login)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        SendMessage sendMessage = new SendMessage();
        User user = userService.getUserByUniqueCode(login);
        if(user != null)
        {
            TelegramUser telegramUser = new TelegramUser(chatId,user);
            sendMessage.setText("Авторизация прошла успешно!");

        }
        else
        {
            sendMessage.setText("Авторизация не удалась, попробуйте позже.");

        }
        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Главное меню");
        houses.setCallbackData("MAIN");




        rowInLine.add(houses);

        //sections.add(rowInLine);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }

    public void auth(Long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();




        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Главное меню");
        houses.setCallbackData("MAIN");



        rowInLine.add(houses);

        //sections.add(rowInLine);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Для авторизации введите ваш СНИЛС или уникальный код, который указан в ранжированы списках (данный код вам выдает приемная комиссия)");
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }

    public void logout(Long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Да");
        invite.setCallbackData("LOGOUT_YES");


        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Нет");
        houses.setCallbackData("MAIN");



        rowInLine.add(invite);
        rowInLine.add(houses);

        //sections.add(rowInLine);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Вы уверены, что хотите выйти из системы?");
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }

    public void logoutYes(Long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Главное меню");
        houses.setCallbackData("MAIN");

        rowInLine.add(houses);
        TelegramUser telegramUser = telegramUserService.findByChatId(chatId);
        if(telegramUser != null)
        {
            telegramUserService.remove(telegramUser);
        }
        //sections.add(rowInLine);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Вы успешно вышли из системы.");
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }

    public void sendMes(SendMessage sendMessage)
    {
        if(sendMessage.getText() == null)
            sendMessage.setText("");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e)
        {
            throw new RuntimeException(e);
        }
    }

    //Сроки подачи документов

    public void menuInvite(long chatId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton invite = new InlineKeyboardButton();
        invite.setText("Сроки подачи");
        invite.setCallbackData("INVITE_DATE");


        InlineKeyboardButton houses = new InlineKeyboardButton();
        houses.setText("Перечень документов");
        houses.setCallbackData("INVITE_DOC");

        InlineKeyboardButton contacts = new InlineKeyboardButton();
        contacts.setText("Количество бюджетных в 2023 (КЦП)");
        contacts.setCallbackData("INVITE_COUNT_BUDGET");

        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Способы подачи заявления");
        life.setCallbackData("INVITE_HOW");

        InlineKeyboardButton stages = new InlineKeyboardButton();
        stages.setText("Этапы зачисления");
        stages.setCallbackData("INVITE_STAGES");

        InlineKeyboardButton lgot = new InlineKeyboardButton();
        lgot.setText("Льготы для поступления");
        lgot.setCallbackData("INVITE_SPECIAL");

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("MENU");

        rowInLine.add(invite);
        rowInLine.add(houses);
        rowInLine.add(contacts);
        rowInLine.add(life);
        rowInLine.add(stages);
        rowInLine.add(lgot);
        rowInLine.add(back);
        //sections.add(rowInLine);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Главное меню - ");
        sendMessage.setChatId(chatId);
        sendMes(sendMessage);
    }

    public void dateOfDoc(long userId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Назад");
        life.setCallbackData("INVITE");


        rowInLine.add(life);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Очное, очно-заочное и заочное обучение, на бюджетные места по ЕГЭ - с 16 июня по 25 июля" +
                "\nНа направление 'Архитектура' и 'Дизайн архитектурной среды - с 16 июня по 10 июля'" +
                "\nОчное, очно-заочное и заочное обучение, на бюджетные места по тестам СГТУ - с 16 июня по 20 июля" +
                "\nОчное, очно-заочное и заочное обучение, на платные места - с 16 июня по 19 августа" +
                "\nВ магистратуру на все формы обучения - с 16 июня по 8 августа");
        sendMessage.setChatId(userId);
        sendMes(sendMessage);
    }

    public void stageOfInvite(long userId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Назад");
        life.setCallbackData("INVITE");


        rowInLine.add(life);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Зачисление будет проводиться в 2 этапа: приоритетного зачисления (абитуриентов без вступительных испытаний, абитуриентов, имеющих льготы и заключивших договоры о целевом обучении) и основного зачисления.");
        sendMessage.setChatId(userId);
        sendMes(sendMessage);
    }

    public void amountBudget(long userId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Назад");
        life.setCallbackData("INVITE");


        rowInLine.add(life);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Выделено бюджетных мест в 2023 году:\nОЧНО - 1136 мест\nЗАОЧНО - 151 мест");
        sendMessage.setChatId(userId);
        sendMes(sendMessage);
    }

    public void typeOfSpecial(long userId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Назад");
        life.setCallbackData("INVITE");


        rowInLine.add(life);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Особая квота - инвалиды 1 и 3 группы, дети-сироты, ветераны боевых действий\nОтдельная квота - участники СВО\nЦелевая квота - абитуриенты, заключившие договор на обучение с предприятием");
        sendMessage.setChatId(userId);
        sendMes(sendMessage);
    }

    public void docForInvite(long userId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Назад");
        life.setCallbackData("INVITE");


        rowInLine.add(life);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("документ об образовании\nдокументы, подтверждающие индивидуальные достижения (при необходимости)\nсведения о документах, подтверждающих особые права на зачисление(при необходимости)\nсведения об инвалидности или ограничениях по здоровью(при необходимости)\nдоговор о целевом обучении (при необходимости)");
        sendMessage.setChatId(userId);
        sendMes(sendMessage);
    }

    public void howInvite(long userId)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();


        InlineKeyboardButton life = new InlineKeyboardButton();
        life.setText("Назад");
        life.setCallbackData("INVITE");


        rowInLine.add(life);
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("лично\nпо почте\nпо элекронной почте\nчерех Госуслуги посредством сервиса 'Поступление в ВУЗ онлайн'");
        sendMessage.setChatId(userId);
        sendMes(sendMessage);
    }

}
