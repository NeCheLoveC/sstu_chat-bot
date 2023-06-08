package com.example.demo2bot.config;

import com.example.demo2bot.model.Node;
import com.example.demo2bot.model.TelegramUser;
import com.example.demo2bot.model.User;
import com.example.demo2bot.services.NodeService;
import com.example.demo2bot.services.TelegramUserService;
import com.example.demo2bot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected TelegramUserService telegramUserService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected NodeService nodeService;

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
        if(update.hasCallbackQuery())
        {
            callBack = update.getCallbackQuery().getData();
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            Optional<Node> node = nodeService.getNodeWithChildren(Long.valueOf(callBack));
            if(node.isPresent())
                sendElements(chatID, node.get());
            //Текущий узел был удален, он больше не доступен -> откат в главное меню
            else
            {
                Long rootNodeId = nodeService.getIdRootNode();
                Node rootNode = nodeService.getNodeWithChildren(rootNodeId).get();
                sendElements(chatID,rootNode);
                //У нас запрос на авторизацию...
            }
        }
        else if(update.hasMessage() && update.getMessage().hasText())
        {
            String messageText = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();
            Long rootNodeId = nodeService.getIdRootNode();
            Node rootNode = nodeService.getNodeWithChildren(rootNodeId).get();
            switch (messageText)
            {
                case "/start":
                    sendElements(chatID,rootNode);
                    break;
            }
            //Попытка авторизации
            /*
            if(messageText.matches("\\d\\d\\d-\\d\\d\\d-\\d\\d\\d \\d\\d") || (messageText.matches("\\d+")))
            {
                tryAuth(update.getMessage().getChatId(), messageText);
            }
             */
        }
    }

    @Transactional
    protected void sendElements(long chatID, Node currentNode)
    {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> sections = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInLine = wrapNodeIntoInlineKeyBoard(currentNode);

        if(!currentNode.isRootNode())
        {
            //Кнопка НАЗАД
            //Кнопка В ГЛАВНОЕ МЕНЮ
            registerButtonBack(rowInLine, currentNode);
            registerButtonBackToMainMenu(rowInLine);
        }
        markupInLine.setKeyboard(toVertical(sections, rowInLine));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText(currentNode.getText());
        sendMessage.setChatId(chatID);
        sendMes(sendMessage);
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


}
