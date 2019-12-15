package telegramBot;

import admin.AdminController;
import commandState.CommandStateController;
import commandState.State;
import commandState.UserState;
import core.Host;
import core.STATUS;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private Host host = Host.init();

    public String getBotUsername() {
        return "-";
    }

    @Override
    public String getBotToken() {
        return "-";
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        Long chatId;
        if (message != null) {
            chatId = message.getChatId();
            messageTextProcessing(message);
        } else
            chatId = update.getCallbackQuery().getMessage().getChatId();

        if (message != null && message.hasText() && !AdminController.isAuthorizedNow(chatId)
                && CommandStateController.getCommand(chatId) != State.NULL) {
            messageCommandProcessing(message);
        } else if (update.hasCallbackQuery()) {
            messageCommandPostProcessing(update);
        }
        if (CommandStateController.getCommand(chatId) == State.NULL)
            CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));
    }

    public void messageTextProcessing(Message message) {
        //admin autorization
        if (message != null && message.hasText() && !AdminController.isAdmin(message.getChatId()) &&
                CommandStateController.getCommand(message.getChatId()) == State.DEFAULT) {
            switch (AdminController.adminSignin(message)) {
                case "LOGIN_CORRECT":
                    sendMsg(message.getChatId(), "Логин введен", 0);
                    break;
                case "SIGN_IN_SUCCESS":
                    sendMsg(message.getChatId(), "Режим администратора включен", 0);
                    sendMsg(message.getChatId(), AdminController.ADMIN_COMMANDS, 0);
                    break;
                case "FAILED":
                    sendMsg(message.getChatId(), "Вход невыполнен", 0);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.NULL));
                    break;
            }
        }

        //change login
        if (message != null && CommandStateController.getCommand(message.getChatId()) == State.CHANGE_LOGIN) {
            CommandStateController.setCommand(new UserState(message.getChatId(), State.NULL));
            if (message.hasText()) {
                AdminController.setLogin(message.getText(), message.getChatId());
                sendMsg(message.getChatId(), "Логин успешно изменен", 0);
            } else sendMsg(message.getChatId(), "Ошибка при смене логина", 0);
        }

        //change passwoed
        if (message != null && CommandStateController.getCommand(message.getChatId()) == State.CHANGE_PASSWORD && message.hasText()) {

            CommandStateController.setCommand(new UserState(message.getChatId(), State.NULL));
            if (message.hasText()) {
                AdminController.setPassword(message.getText(), message.getChatId());
                sendMsg(message.getChatId(), "Пароль успешно изменен", 0);
            } else sendMsg(message.getChatId(), "Ошибка при смене пароля", 0);
        }

        //delete User from Queue by ChatId
        if (message != null && CommandStateController.getCommand(message.getChatId()) == State.DEL_QUEUE && message.hasText()) {

            CommandStateController.setCommand(new UserState(message.getChatId(), State.NULL));
            if (message.hasText()) {
                //Queue.delete(Queue.userInQueue(ChatID), ChatID);
                sendMsg(message.getChatId(), "Удаление завершено", 0);
            }

        }

        //add New host
        if (message != null && CommandStateController.getCommand(message.getChatId()) == State.SET_NAME_FOR_NEW_RESOURSE && message.hasText()) {

            CommandStateController.setCommand(new UserState(message.getChatId(), State.NULL));
            if (message.hasText()) {
                host.addHost(new Date().getTime(), message.getText());
                sendMsg(message.getChatId(), "Удаление завершено", 0);
            }

        }

    }

    public void messageCommandProcessing(Message message) {

        String textMessage = message.getText();
        if (textMessage.contains(" ") && textMessage.contains("/")) {
            textMessage = textMessage.substring(0, textMessage.indexOf(" "));
        }

        switch (textMessage) {
            case "/start":
                sendMsg(message.getChatId(), "Добро пожаловать в бот-контролер очереди", 1);
                CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                break;
            case "/help":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), AdminController.ADMIN_COMMANDS, 1);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                } else {
                    sendMsg(message.getChatId(), "Для того чтобы занять очередь к интересующему вас ресурсу, нажмите кнопку <Занять очередь> ", 1);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                }

                break;

            case "Занять очередь":
                if (!host.getNameList(STATUS.OPEN).isEmpty()) {
                    sendMsg(message.getChatId(), "Выберите интересующую вас очередь", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.ADD_IN_QUEUE));

                } else sendMsg(message.getChatId(), "Извините, на данный момент нет доступных ресурсов!", 1);
                break;

            case "Посмотреть позицию":
                //TODO cделать вывод позиции пользователя в очереди и в процссоре, если он там
//                /*if (Queue.userInQueue(ChatID) != -1) {
//                        int kol = Queue.outQueue(Queue.userInQueue(message.getChatId()), message.getChatId());
//                        if (kol == -1)
//                            kol = 0;
//
//                        sendMsg(message.getChatId(), "Ваша позиция в очереди к ресурсу "+
//                                String.valueOf(Queue.userInQueue(message.getChatId())) + " - "
//                                + String.valueOf(kol),1);
//                        sendMsg(message.getChatId(),"Ваш ChatId - " + String.valueOf(message.getChatId()), 1);*/
//                } else if (Queue.userInProcessor(message.getChatId())!= -1) {
//                    sendMsg(message,"Ваша очередь уже подошла, пройдите к столу номер "+ String.valueOf(Queue.userInProcessor(message.getChatId())),3);
//                } else
//                    sendMsg(message.getChatId(), "Вы еще не заняли очередь к столу, чтобы это сделать, нахмите кнопку <Занять очередь>", 1);*
                sendMsg(message.getChatId(), "Функция пока недоступна", 1);
                CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                break;

            case "Выйти из очереди":
                //TODO сделать выход из очереди только по ChatID
//                host.removeFromHost();
//                sendMsg(message.getChatId(), "Вы вышли из очереди, теперь если вы снова захотите занять место, вы попадете в конец очереди", 1);
                sendMsg(message.getChatId(), "Функция пока недоступна", 1);
                CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                break;

            case "Закончить":
                //TODO сделать завершение из процесса по chat id
                sendMsg(message.getChatId(), "Всего хорошего!", 1);
                CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                break;

            case "/changelogin":
                if (AdminController.isAdmin(message.getChatId())) {
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.CHANGE_LOGIN));
                    sendMsg(message.getChatId(), "Введите новый логин", 0);
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);
                }
                break;

            case "/changepassword":
                if (AdminController.isAdmin(message.getChatId())) {
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.CHANGE_PASSWORD));
                    sendMsg(message.getChatId(), "Введите новый пароль", 0);
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/addresourse":
                if (AdminController.isAdmin(message.getChatId())) {
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.ADD_RESOURSE));
                    if (message.getText().length() <= new String("/addresourse").length() + 1) {
                        sendMsg(message.getChatId(), "Вы должны ввести имя ресурса через пробел после команды /addresourse\n" +
                                "Например - /addresourse Иван", 0);
                    } else {
                        //TODO сделать ввод времени
                        String name = message.getText().substring(new String("/addresourse").length() + 1);
                        if (host.addHost(5 * 60000, name) == 1) {
                            sendMsg(message.getChatId(), "Ресурс с таким именем уже существует!", 0);
                        } else {
                            sendMsg(message.getChatId(), "Ресурс добавлен!", 0);
                        }
                    }

                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/delresourse":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Выберите интересующую вас очередь", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.DELL_RESOURSE));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);
                }
                break;
            case "/setaccsess":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Выберите интересующую вас очередь", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.SET_ACCSES));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);
                }
                break;
            case "/resourselist":
                if (AdminController.isAdmin(message.getChatId())) {
                    List<String> hosts = host.getNameList(STATUS.ALL);
                    StringBuilder hostsList = new StringBuilder();
                    for (String hostName : hosts) {
                        hostsList.append(hostName + "\n");
                    }
                    sendMsg(message.getChatId(), "Список ресурсов\n" + hostsList, 1);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/resourseinfo":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Выберите нужный ресурс!", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.GET_RESOURCE_INFO));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/resourseproc":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Выберите нужный ресурс!", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.GET_RESOURCE_PROC));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/resoursequeue":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Выберите нужный ресурс!", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.GET_RESOURCE_QUEUE));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/abort":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Выберите нужный ресурс!", 5);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.ABORT));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/delqueue":
                if (AdminController.isAdmin(message.getChatId())) {
                    sendMsg(message.getChatId(), "Введите ChatID удаляемого человека", 0);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.DEL_QUEUE));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            case "/out":
                if (AdminController.isAdmin(message.getChatId())) {
                    AdminController.adminOut(message.getChatId());
                    sendMsg(message.getChatId(), "Выход из режима администратора", 1);
                    CommandStateController.setCommand(new UserState(message.getChatId(), State.DEFAULT));
                } else {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);

                }
                break;
            default:
                if (!AdminController.isItLogin(message.getText())
                        && (!AdminController.isItPasswoed(message.getText()) && !AdminController.isAdmin(message.getChatId()))) {
                    sendMsg(message.getChatId(), "Команда не распознана", 1);
                }
                break;
        }
    }

    public void messageCommandPostProcessing(Update update) {
        String call_data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (CommandStateController.getCommand(chatId)) {
            case CHANGE_PASSWORD:
                //Queue.addList(data);
                sendMsg(chatId, "Ресурс добавлен!", 0);
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));

                break;
            case DELL_RESOURSE:
                host.removeHost(call_data);
                sendMsg(chatId, "Ресурс " + call_data + " удален!", 0);
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));

                break;
            case SET_ACCSES:
                switch (host.getStatus(call_data)) {
                    case OPEN:
                        sendMsg(chatId, "Доступ к ресуру " + call_data + " закрыт!", 0);
                        host.setStatus(call_data, STATUS.CLOSE);
                        break;
                    case CLOSE:
                        sendMsg(chatId, "Доступ к ресуру " + call_data + " открыт!", 0);
                        host.setStatus(call_data, STATUS.OPEN);
                        break;
                }
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));

                break;
            case GET_RESOURCE_INFO:
                sendMsg(chatId, "Функция пока недоступна", 1);
               //TODO вывод инфы о ресурсе по имени
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));

                break;
            case GET_RESOURCE_PROC:
                if (/*Queue.getWorkerChatID(data) != 0*/false) {
                    sendMsg(chatId, "Выполняемый процесс в ресуре номер " + call_data + ": "
                            + "ChatID - " + /*String.valueOf(Queue.getWorkerChatID(data))+"; "
                            +*/ "Номер талона - " /*+String.valueOf(Queue.getWorkerNumber(data))*/, 0);
                } else
                    sendMsg(chatId, "Выполняемый процесс в ресурсе номер " + call_data + ": отсутствует", 0);

                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));

                break;
            case GET_RESOURCE_QUEUE:
                List<String> queueInHost = host.queueInHost(call_data);
                if (!queueInHost.isEmpty()) {
                    StringBuilder msg = new StringBuilder();
                    for (String queue : queueInHost) {
                        msg.append(queue + "\n");
                    }
                    sendMsg(chatId, "Очередь к ресурсу " + call_data + ":\n" + msg, 0);
                } else
                    sendMsg(chatId, "Очередь к ресурсу " + call_data + " отсутствует", 0);
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));

                break;
            case ABORT:
                //TODO сделать выброс из процессора по имени ресурса
                //Queue.abort(data);
                //sendMsg(chatId, "Выполняемый процесс в ресурсе номер " + call_data + " прерван!", 0);
                sendMsg(chatId, "Функция пока недоступна", 1);
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));
                break;
            case ADD_IN_QUEUE:
                host.addInHost(call_data, chatId);
                sendMsg(chatId, "Вы заняли очередь к " + call_data, 1);
                //TODO сделать вывод позиции по chatId
                /*if (host.)
                {
                    int kol = Queue.outQueue(Queue.userInQueue(ChatID), ChatID);
                    if (kol == -1)
                        kol = 0;

                    sendMsg(ChatID, "Ваша позиция в очереди к ресурс номер "+
                            String.valueOf(Queue.userInQueue(ChatID)) + " - "
                            + String.valueOf(kol),1);
                    sendMsg(ChatID,"Ваш ChatId - " + String.valueOf(ChatID), 1);
                }

                else if (Queue.userInProcessor(ChatID)!= -1) {
                    sendMsg(ChatID,"Ваша очередь уже подошла, пройдите к ресурсу номер "+ String.valueOf(Queue.userInProcessor(ChatID)),1);
                }
                CommandStateController.setCommand(new UserState(chatId, State.DEFAULT));
                */
                break;
            case DEFAULT:
                if (AdminController.isAdmin(chatId)) {
                    sendMsg(chatId, "Нажатие кнопки не распознано, повторите одну из команд ниже\n"
                            + AdminController.ADMIN_COMMANDS, 0);
                } else
                    sendMsg(chatId, "Возникла непредвиденная ошибка, нажмите кнопку внизу экрана", 1);
                break;


        }
    }

    public void sendMsg(Long chatId, String text, int keyboardMode) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        // 0 - Стандартная клавиатура
        // 4 - Инлайн клавиатура выбора количества мест (для админа)
        // 5 - клавиатура выбора столов
        // 6 - Вывод количества мест (для создания столов)
        // В остальных на основе проверок наличия клиента в очереди или в процессе выводится клавиатура
        // Если пользователь в очереди: кнопки ПОСМОТРЕТЬ ПОЗЦИЮ и ВЫЙТИ ИЗ ОЧЕРЕДИ
        // Если пользователь в процессе: кнопка ЗАКОНЧИТЬ
        // иначе выводит кнопку ЗАНЯТЬ ОЧЕРЕДЬ

        if (keyboardMode == 0 || (keyboardMode == 1 && AdminController.isAdmin(chatId))) {
            ReplyKeyboardRemove keyboard = new ReplyKeyboardRemove();
            sendMessage.setReplyMarkup(keyboard);
        } else if (keyboardMode == 4) {
            sendMessage.setReplyMarkup(KeyboardController.getInlinePlaceSelector(1,/*Queue.getMaxTableSize()*/4));
        } else if (keyboardMode == 5) {
            if (AdminController.isAdmin(chatId)) {
                sendMessage.setReplyMarkup(KeyboardController.getInlineResourseSelector(host.getNameList(STATUS.ALL)));
            } else {
                sendMessage.setReplyMarkup(KeyboardController.getInlineResourseSelector(host.getNameList(STATUS.OPEN)));
            }

        } else if (keyboardMode == 6) {
            sendMessage.setReplyMarkup(KeyboardController.getInlinePlaceSelector(0,/*Queue.getMaxTableSize()*/4));
        } else if (/*TODO вывод позиции в очереди по ChatID*/ false)
            sendMessage.setReplyMarkup(KeyboardController.getUserKeyboard(2));
        else if (/*Queue.userInProcessor(chatId) != -1*/false)
            sendMessage.setReplyMarkup(KeyboardController.getUserKeyboard(3));
        else
            sendMessage.setReplyMarkup(KeyboardController.getUserKeyboard(1));

        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
