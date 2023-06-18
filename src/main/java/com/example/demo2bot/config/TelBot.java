package com.example.demo2bot.config;

import com.example.demo2bot.entities.Node;
import com.example.demo2bot.entities.TUser;
import com.example.demo2bot.entities.User;
import com.example.demo2bot.repo.TUserService;
import com.example.demo2bot.services.NodeService;
import com.example.demo2bot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class TelBot extends TelegramLongPollingBot
{
    final BotConfig config;
    @Autowired
    protected UserService userService;
    @Autowired
    protected NodeService nodeService;
    @Autowired
    @Qualifier("TUserSqlService")
    protected TUserService tUserService;

    @Autowired
    protected FormResultOfClaim formResultOfClaim;

    @Autowired
    public TelBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
        }
    }
    @Transactional
    @Override
    public void onUpdateReceived(Update update)
    {
        String callBack = null;
        if(update.hasCallbackQuery()) {
            callBack = update.getCallbackQuery().getData();
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            TUser tUser = getTUserByChatId(chatID);
            if (callBack.equals("AUTH"))
            {
                if(!tUser.isAuthorizedUser())
                {
                    showAuthMenu(chatID);
                    tUser.setLastQueryState("AUTH");
                    tUserService.saveOrUpdate(tUser);
                }
            }
            else if(callBack.equals("LOGOUT"))
            {
                if(tUser.isAuthorizedUser())
                {
                    logout(chatID, tUser);
                    tUser.setLastQueryState("LOGOUT");
                    tUserService.saveOrUpdate(tUser);
                }
            }
            else if(callBack.equals("STATUS"))
            {
                tUser.setLastQueryState("STATUS");
                tUserService.saveOrUpdate(tUser);
                if(tUser.isAuthorizedUser())
                {
                    showUserStatus(tUser);
                }
            }
            //Callback - целое число, указывающее на узел графа-меню
            else
            {
                tUser.setLastQueryState("node:" + callBack);
                tUserService.saveOrUpdate(tUser);
                Optional<Node> node = nodeService.getNodeWithChildren(Long.valueOf(callBack));
                if(node.isPresent())
                    sendElements(chatID, node.get(),tUser);
                else
                //Узел, на который ссылается callback - удален, возвращение в главное меню.
                {
                    Long rootNodeId = nodeService.getIdRootNode();
                    Node rootNode = nodeService.getNodeWithChildren(rootNodeId).get();
                    sendElements(chatID,rootNode,tUser);
                }
            }
        }
        else if(update.hasMessage() && update.getMessage().hasText())
        {
            String messageText = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();
            TUser user = getTUserByChatId(chatID);
            //State state = StateFactory.getStateByTUser(user);
            switch (messageText)
            {
                case "/start":
                    user.setLastQueryState("/start");
                    tUserService.saveOrUpdate(user);
                    showMainMenu(chatID,user);
                    break;
                default:
                    //Это не команда, тогда это id абитуриента - если lastQuery - "AUTH"
                    if(user.getLastQueryState().equals("AUTH"))
                    {
                        //Попробовать авторизироваться -> если абитуриент найден -> отправляем что авторизация прошла успешно
                        Optional<User> claimUser = userService.getUserByUniqueCode(messageText);
                        if(claimUser.isPresent())
                        {
                            //Авторизация прошла успешно
                            user.setUser(claimUser.get());
                            user.setLastQueryState("/start");
                            tUserService.saveOrUpdate(user);
                            showSuccesAuth(chatID);
                        }
                        else
                        {
                            //Авторизация не прошла
                            showUnsuccesAuth(chatID);
                        }
                    }
                    else
                    {
                        /// TODO: 12.06.2023 Реализовать метод
                        showHowToStart(chatID);
                    }
            }
        }
    }

    private void showUserStatus(TUser tUser)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = new LinkedList<>();

        registerButtonLogout(rowInLine);
        registerButtonBackToMainMenu(rowInLine);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText(formResultOfClaim.getStringResultOfClaim(tUser.getUser().getUniqueCode()));
        sendMessage.setChatId(tUser.getId());
        sendMes(sendMessage);
    }

    private void logout(long chatID, TUser tUser)
    {
        tUser.setUser(null);
        showMainMenu(chatID,tUser);
    }

    private void showMainMenu(long chatID, TUser user)
    {
        Node rootNode = nodeService.getRootNode().get();
        sendElements(chatID,rootNode,user);
    }

    private void registerButtonLogout(List<InlineKeyboardButton> rowInLine)
    {
        InlineKeyboardButton logout = new InlineKeyboardButton();
        logout.setText("Выйти из профиля");
        logout.setCallbackData("LOGOUT");
        rowInLine.add(logout);
    }

    private void showMenuReauthorization(long chatID)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = new LinkedList<>();

        registerButtonLogout(rowInLine);
        registerButtonBackToMainMenu(rowInLine);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Для авторизации введите свой уникальный код в ранжированных списках (как правило - это ваш СНИЛС)");
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
    }

    private TUser getTUserByChatId(Long chatID)
    {
        Optional<TUser> tUser = tUserService.getTUserById(chatID);
        if (tUser.isPresent()) {
            //tUser.get().setLastQueryState(callBack);
            //tUserService.saveOrUpdate(tUser.get());
            return tUser.get();
        } else {
            TUser newUser = new TUser();
            newUser.setId(chatID);
            newUser.setDefaultLang();
            newUser.setLastQueryState("null_state");
            //tUser = Optional.of(newUser);
            return tUserService.saveOrUpdate(newUser);
        }
    }

    private void showHowToStart(long chatID)
    {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Для начала работы с чат-ботом введите команду /start.");
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
    }

    private void showAuthMenu(long chatID)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = new LinkedList<>();
        registerButtonBackToMainMenu(rowInLine);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Для авторизации введите свой уникальный код в ранжированных списках (как правило - это ваш СНИЛС)");
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
    }

    private void showUnsuccesAuth(long chatID)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = new LinkedList<>();
        registerButtonBackToMainMenu(rowInLine);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Абитуриент не найден. Повторите ввод...");
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
    }

    private void showSuccesAuth(long chatID)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = new LinkedList<>();
        registerButtonBackToMainMenu(rowInLine);

        markupInLine.setKeyboard(toVertical(sections, rowInLine));


        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText("Авторизация прошла успешно");
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
    }

    @Transactional
    protected void sendElements(long chatID, Node currentNode,TUser user)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = wrapNodeIntoInlineKeyBoard(currentNode);
        if(!currentNode.isRootNode()) {
            //Кнопка НАЗАД
            //Кнопка В ГЛАВНОЕ МЕНЮ
            registerButtonBack(rowInLine, currentNode);
            registerButtonBackToMainMenu(rowInLine);
        }
        else {
            if(user.isAuthorizedUser()) {
                //Кнопка "Выйти из профиля"
                registerButtonUserStatus(rowInLine);
                registerButtonLogout(rowInLine);
            }
            else {
                registerButtonAuth(rowInLine);
            }
        }
        markupInLine.setKeyboard(toVertical(sections, rowInLine));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText(currentNode.getText());
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
    }

    private void registerButtonUserStatus(List<InlineKeyboardButton> rowInLine)
    {
        InlineKeyboardButton buttonUserStatus = new InlineKeyboardButton();
        buttonUserStatus.setText("Получить статус заявления.");
        // TODO: 14.06.2023 Вынести в отдельный класс / поля состояния чата юзера
        buttonUserStatus.setCallbackData("STATUS");
        rowInLine.add(buttonUserStatus);
    }

    private void registerButtonAuth(List<InlineKeyboardButton> rowInLine)
    {
        InlineKeyboardButton backToMainMenu = new InlineKeyboardButton();
        backToMainMenu.setText("Авторизация");
        // TODO: 13.06.2023 Вынести в отдельный класс / поля состояния чата юзера
        backToMainMenu.setCallbackData("AUTH");
        rowInLine.add(backToMainMenu);
    }

    private void sendMes(SendMessage sendMessage)
    {
        try
        {
            execute(sendMessage);
        }
        catch (TelegramApiException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    protected List<InlineKeyboardButton> wrapNodeIntoInlineKeyBoard(Node currentNode)
    {
        List<InlineKeyboardButton> rowInLine = new LinkedList<>();
        List<Node> childrenOfNode = currentNode.getChildList();
        for(Node node : childrenOfNode)
        {
            InlineKeyboardButton newButton = new InlineKeyboardButton();
            newButton.setText(node.getName());
            newButton.setCallbackData(node.getId().toString());
            rowInLine.add(newButton);
        }
        return rowInLine;
    }

    private void registerButtonBack(List<InlineKeyboardButton> rowInLine, Node currentNode)
    {
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData(currentNode.getParent().getId().toString());
        rowInLine.add(back);
    }

    private void registerButtonBackToMainMenu(List<InlineKeyboardButton> rowInLine)
    {
        InlineKeyboardButton backToMainMenu = new InlineKeyboardButton();
        backToMainMenu.setText("В главное меню");
        Node rootNode = nodeService.getRootNode().get();
        backToMainMenu.setCallbackData(rootNode.getId().toString());
        rowInLine.add(backToMainMenu);
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

    /*
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

     */

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
}
