package by.parser;

import by.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class ParserMessageHistoryToJSON {
    public static final String JSON_FOLDER = "storage/json";
    public static final String FILE_SEPARATOR = File.separator;

    public void parseListToJSON(List<Message> messageList) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";

        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        File catalog = new File(JSON_FOLDER);
        catalog.mkdir();

        File file = new File(JSON_FOLDER + FILE_SEPARATOR + messageList.get(0).getSenderId() + "_"
                + messageList.get(0).getToUser() + ".json");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
