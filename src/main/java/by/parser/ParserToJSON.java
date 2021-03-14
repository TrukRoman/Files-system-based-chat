package by.parser;

import by.repository.UserRepository;
import by.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ParserToJSON {
    public static final String JSON_FOLDER = "storage/json";
    public static final String USERS_FOLDER = "storage/users";
    public static final String MESSAGES_FOLDER = "storage/messages";
    public static final String FILE_SEPARATOR = File.separator;

    @Autowired
    private UserRepository userRepository;

    private static String readFile(File file) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
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

        stringBuilder.append(" \"sender\": \n");
        stringBuilder.append("   { \n");
        stringBuilder.append("    \"id\":" + " " + senderUser.getId() + ", \n");
        stringBuilder.append("    \"login\":" + " \"" + senderUser.getLogin() + "\" \n");
        stringBuilder.append("}, \n \n");

        stringBuilder.append(" \"recipient\": \n");
        stringBuilder.append("    { \n");
        stringBuilder.append("    \"id\":" + " " + recipientUser.getId() + ", \n");
        stringBuilder.append("    \"login\":" + " \"" + recipientUser.getLogin() + "\" \n");
        stringBuilder.append("}, \n \n");

        stringBuilder.append(" \"messages\": [ \n");

        for (File file : listOfFiles) {
            if (file.isFile()) {
                stringBuilder.append("  { \n");

                stringBuilder.append("     \"data\":" + " " + "\"" + file.getName().substring(4, 23) + "\", \n");
                stringBuilder.append("     \"text\":" + " " + "\"" + readFile(file) + "\" \n");

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
