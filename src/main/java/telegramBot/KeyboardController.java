package telegramBot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardController {
    private KeyboardController() {
    }

    public static InlineKeyboardMarkup getInlinePlaceSelector(int keyboardMode, int MaxResoursSize) {
        InlineKeyboardMarkup inlinekeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();

        //for admin
        if (keyboardMode == 0) {
            keyboard.add(getRow(1, 5));
            keyboard.add(getRow(6, 10));
        }
        //for user
        if (keyboardMode == 1) {
            if (MaxResoursSize > 0) {
                if (MaxResoursSize > 5)
                    keyboard.add(getRow(1, 5));
                else
                    keyboard.add(getRow(1, MaxResoursSize));
            }
            if (MaxResoursSize > 5) {
                keyboard.add(getRow(6, MaxResoursSize));
            }
        }
        inlinekeyboard.setKeyboard(keyboard);
        return inlinekeyboard;
    }

    private static InlineKeyboardButton getButton(String i) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(i);
        button.setCallbackData(i);
        return button;
    }

    private static List<InlineKeyboardButton> getRow(int begin, int end) {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        for (int i = begin; i <= end; i++) {
            row.add(getButton(String.valueOf(i)));
        }
        return row;
    }

    private static List<InlineKeyboardButton> getRow(String resourseName){
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        row.add(getButton(resourseName));
        return row;
    }

    public static InlineKeyboardMarkup getInlineResourseSelector(int amountOfResourses) {
        InlineKeyboardMarkup inlinekeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
        int kol_rows = (amountOfResourses / 8) + 1;
        int begin = 1, end = 0;
        if (amountOfResourses <= 8)
            end = amountOfResourses;
        else end = 8;
        for (int i = 0; i < kol_rows; i++) {
            if (end <= amountOfResourses && begin <= amountOfResourses) {
                keyboard.add(getRow(begin, end));
            }
            begin = end + 1;
            end += 8;
            if (amountOfResourses < end)
                end = amountOfResourses;
        }
        inlinekeyboard.setKeyboard(keyboard);
        return inlinekeyboard;
    }

    public static InlineKeyboardMarkup getInlineResourseSelector(List<String> resourseNames) {
        InlineKeyboardMarkup inlinekeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        for (String resourse: resourseNames) {
            keyboard.add(getRow(resourse));
        }
        inlinekeyboard.setKeyboard(keyboard);
        return inlinekeyboard;
    }

    public static ReplyKeyboardMarkup getUserKeyboard(int keyboardMode) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        if (keyboardMode == 1) {
            // Первая строчка клавиатуры
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add("Занять очередь");
            keyboard.add(keyboardFirstRow);
        } else if (keyboardMode == 2) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add("Посмотреть позицию");
            KeyboardRow keyboardSecondRow = new KeyboardRow();
            keyboardSecondRow.add("Выйти из очереди");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
        } else if (keyboardMode == 3) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add("Закончить");
            keyboard.add(keyboardFirstRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
