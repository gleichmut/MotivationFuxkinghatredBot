import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

private static final Logger logger =
        LoggerFactory.getLogger(PhrasesLoader.class);

void main() {
    try {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new MyMotivationBot());
        IO.println("Бот запущен");
    } catch (TelegramApiException e) {
        if (e.getMessage().contains("deleteWebhook")) {
            logger.warn("Не удалось удалить webhook — возможно, его и не было. Продолжаем...");
        }
    }
}
