package admin;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

public class AdminController {
    private static String login = "11", password = "22";
    public final static String ADMIN_COMMANDS = "Список команд для администраторов:\n" +
            "/changelogin - поменять логин\n" +
            "/changepassword - поменять пароль\n" +
            "/addresourse - добавить ресурс\n" +
            "/delresourse - удалить ресурс\n" +
            "/setaccsess - открыть/закрыть ресурс для очереди\n" +
            "/resourselist - посмотреть список ресурсов\n" +
            "/resourseinfo - посмотреть информацию о ресурсе\n" +
            "/resourseproc - посмотреть выполняемый процесс в ресурсе\n" +
            "/resoursequeue - посмотреть очередь к ресурсу\n" +
            "/abort - раньше закончить процесс\n" +
            "/delqueue - удаление по номеру из очереди\n" +
            "/out - выйти из режима администратора";

    private static List<Long> adminActiveList = new ArrayList<Long>();
    private static List<Long> adminLoginProccesBuffer = new ArrayList<Long>();

    private AdminController() {
    }

    public static String adminSignin(Message message) {
        if (!adminLoginProccesBuffer.contains(message.getChatId()) && login.equals(message.getText())) {
            adminLoginProccesBuffer.add(message.getChatId());
            return "LOGIN_CORRECT";
        } else if (adminLoginProccesBuffer.contains(message.getChatId()) && password.equals(message.getText())) {
            adminActiveList.add(message.getChatId());
            adminLoginProccesBuffer.remove(message.getChatId());
            return "SIGN_IN_SUCCESS";
        } else if (adminLoginProccesBuffer.contains(message.getChatId()) && !password.equals(message.getText())) {
            adminLoginProccesBuffer.remove(message.getChatId());
            return "FAILED";
        }
        return "NO_AUTORIZE";

    }

    public static boolean isAdmin(Long ChatId) {
        return adminActiveList.contains(ChatId);
    }

    public static void setLogin(String newLogin, Long ChatId) {
        if (adminActiveList.contains(ChatId))
            login = newLogin;
    }

    public static boolean isItLogin(String string) {
        return login.equals(string);
    }

    public static boolean isItPasswoed(String string) {
        return password.equals(string);
    }

    public static void setPassword(String newPassword, Long chatId) {
        if (adminActiveList.contains(chatId))
            password = newPassword;
    }

    public static boolean isAuthorizedNow(Long chatId) {
        return adminLoginProccesBuffer.contains(chatId);
    }

    public static void adminOut(Long chatId) {
        adminActiveList.remove(chatId);
    }
}
