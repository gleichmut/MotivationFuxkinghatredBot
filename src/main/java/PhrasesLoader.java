import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class PhrasesLoader {
    private static final Logger logger =
            LoggerFactory.getLogger(PhrasesLoader.class);
    private static final String INPUT_STREAM_NOT_FOUND = "Input Stream not found";
    private static final String EMPTY_FILE = "Файл с фразами не найден.";

    private static void checkInputStreamOfNull(InputStream inputStream, String fileName) {
        if (inputStream == null) {
            logger.info(INPUT_STREAM_NOT_FOUND);
            throw new RuntimeException(EMPTY_FILE + fileName);
        }
    }

    private PhrasesLoader() {

    }

    public static List<String> loadPhrases(String fileName) {
        InputStream inputStream =
                PhrasesLoader.class
                        .getClassLoader()
                        .getResourceAsStream(fileName);

        checkInputStreamOfNull(inputStream, fileName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            List<String> phrases = reader.lines()
                    .filter(line -> !line.isBlank())
                    .collect(Collectors.toList());

            logger.info(
                    "Загружено {} фраз из файла {}",
                    phrases.size(),
                    fileName
            );
            return phrases;

        } catch (Exception e) {
            logger.error("Ошибка при чтении файла {}", fileName, e);
            throw new RuntimeException(e);
        }
    }
}
