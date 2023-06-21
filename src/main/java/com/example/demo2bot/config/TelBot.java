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
                            showSuccessfulAuth(chatID);
                        }
                        else
                        {
                            //Авторизация не прошла
                            showUnsuccessfulAuth(chatID);
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

    private TUser getTUserByChatId(Long chatID)
    {
        Optional<TUser> tUser = tUserService.getTUserById(chatID);
        if (tUser.isPresent()) {
            return tUser.get();
        } else {
            TUser newUser = new TUser();
            newUser.setId(chatID);
            newUser.setDefaultLang();
            newUser.setLastQueryState("null_state");
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

    private void showUnsuccessfulAuth(long chatID)
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

    private void showSuccessfulAuth(long chatID)
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
            registerButtonBack(rowInLine, currentNode);
            registerButtonBackToMainMenu(rowInLine);
        }
        else {
            if(user.isAuthorizedUser()) {
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

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
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

    public void sendMessageToAllSubscribes(String message)
    {
        List<TUser> users = tUserService.getAllTUsers();
        for(TUser tUser : users)
        {
            sendMessageToUser(message,tUser);
        }
    }

    private void sendMessageToUser(String mes,TUser tUser)
    {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(mes);
        sendMessage.setChatId(tUser.getId());
        sendMes(sendMessage);
    }
}
