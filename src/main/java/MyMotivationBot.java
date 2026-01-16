import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

public class MyMotivationBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(MyMotivationBot.class);
    private final List<String> phrasesMotivate;
    private final List<String> phrasesPaise;
    private static final String PHRASES_MOTIVATE_FILE = "motivate.txt";
    private static final String PHRASES_PRAISE_FILE = "praise.txt";
    private static final String TOKEN = "TOKEN";
    private static final String BOT_USERNAME = "MotivationBot";

    public enum Action {
        MOTIVATE, PRAISE, HELP
    }

    public MyMotivationBot() {
        this.phrasesMotivate = PhrasesLoader.loadPhrases(PHRASES_MOTIVATE_FILE);
        this.phrasesPaise = PhrasesLoader.loadPhrases(PHRASES_PRAISE_FILE);

        Collections.shuffle(phrasesMotivate);
        Collections.shuffle(phrasesPaise);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }

        if (update.hasCallbackQuery()) {
            handleCallback(update);
        }
    }

    private void handleMessage(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String userName = getUserFirstName(update);

        SendMessage response = new SendMessage(chatId,
                "Привет, " + userName + " 👋\nЯ здесь, чтобы поддержать тебя 💛");

        response.setReplyMarkup(mainKeyboard());
        executeSafely(response);
    }

    private void handleCallback(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        Action action;
        try {
            action = Action.valueOf(update.getCallbackQuery().getData());
        } catch (IllegalArgumentException e) {
            logger.error("Неизвестный callback");
            return;
        }

        SendMessage response = new SendMessage(chatId, "");
        response.setReplyMarkup(mainKeyboard());

        switch (action) {
            case MOTIVATE -> response.setText(getRandomPhrase(phrasesMotivate));
            case PRAISE -> response.setText(getRandomPhrase(phrasesPaise));
            case HELP -> response.setText("Нажми кнопку, чтобы получить фразу ✨");
        }
        executeSafely(response);
    }

    private void executeSafely(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка отправки сообщения", e);
        }
    }

    private String getRandomPhrase(List<String> phrases) {
        if (phrases.isEmpty()) {
            return "Фразы закончились 😔";
        }
        return phrases.remove(0); // без повторений
    }

    private InlineKeyboardMarkup mainKeyboard() {
        InlineKeyboardButton motivate = new InlineKeyboardButton();
        motivate.setText("💪 Мотивация");
        motivate.setCallbackData(Action.MOTIVATE.name());

        InlineKeyboardButton praise = new InlineKeyboardButton();
        praise.setText("⭐ Похвала");
        praise.setCallbackData(Action.PRAISE.name());

        InlineKeyboardButton help = new InlineKeyboardButton();
        help.setText("❓ Помощь");
        help.setCallbackData(Action.HELP.name());

        List<List<InlineKeyboardButton>> keyboard = List.of(
                List.of(motivate, praise),
                List.of(help)
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    private String getUserFirstName(Update update) {
        if (update.getMessage() != null && update.getMessage().getFrom() != null) {
            String firstName = update.getMessage().getFrom().getFirstName();
            IO.println(firstName);
            return firstName != null ? firstName : "Друг";
        }
        return "Друг";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
