package by.parser;

import by.repository.UserRepository;
import by.model.User;
import by.service.TxtFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ParserToJSON {
    public static final String JSON_FOLDER = "storage/json";
    public static final String MESSAGES_FOLDER = "storage/messages";
    public static final String FILE_SEPARATOR = File.separator;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private TxtFileReader txtFileReader;

    @Autowired
    public void setTxtFileReader(TxtFileReader txtFileReader) {
        this.txtFileReader = txtFileReader;
    }

    public void saveJsonFile(String sender, String recipient) throws IOException {
        User senderUser = userRepository.findByLogin(sender);
        User recipientUser = userRepository.findByLogin(recipient);

        File folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + sender + "_" + recipient);

        if (!folder.exists()) {
            folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + recipient + "_" + sender);
        }

        File[] listOfFiles = folder.listFiles();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ \n");

        stringBuilder.append(" \"user_1\": \n");
        stringBuilder.append("   { \n");
        stringBuilder.append("    \"id\":" + " " + senderUser.getId() + ", \n");
        stringBuilder.append("    \"login\":" + " \"" + senderUser.getLogin() + "\" \n");
        stringBuilder.append("}, \n \n");

        stringBuilder.append(" \"user_2\": \n");
        stringBuilder.append("    { \n");
        stringBuilder.append("    \"id\":" + " " + recipientUser.getId() + ", \n");
        stringBuilder.append("    \"login\":" + " \"" + recipientUser.getLogin() + "\" \n");
        stringBuilder.append("}, \n \n");

        stringBuilder.append(" \"messages\": [ \n");

        for (File file : listOfFiles) {
            if (file.isFile()) {
                int sendId = 0;

                if (file.getName().startsWith(senderUser.getLogin())) {
                    sendId = senderUser.getId();
                } else if (file.getName().startsWith(recipientUser.getLogin())) {
                    sendId = recipientUser.getId();
                }

                User sendUser = userRepository.findById(sendId);
                int senderNameLength = sendUser.getLogin().length();

                stringBuilder.append("  { \n");
                stringBuilder.append("    \"sender\":" + " \"" + sendUser.getLogin() + "\", \n");
                stringBuilder.append("     \"data\":" + " " + "\"" + file.getName().substring(senderNameLength + 1, senderNameLength + 20) + "\", \n");
                stringBuilder.append("     \"text\":" + " " + "\"" + txtFileReader.readFile(file) + "\" \n");
                stringBuilder.append("  }, \n");
            }
        }

        stringBuilder.append("   ] \n");
        stringBuilder.append("} \n");

        File catalog = new File(JSON_FOLDER);
        catalog.mkdir();

        File file = new File(JSON_FOLDER + FILE_SEPARATOR + senderUser.getLogin().toLowerCase() + "_" + recipientUser.getLogin().toLowerCase() + ".json");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
