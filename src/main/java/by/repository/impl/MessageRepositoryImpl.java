package by.repository.impl;

import by.model.User;
import by.repository.MessageRepository;
import by.model.Message;
import by.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MessageRepositoryImpl implements MessageRepository {
    public static final String STORAGE = "storage";
    public static final String MESSAGES_FOLDER = "storage/messages";
    public static final String FILE_SEPARATOR = File.separator;

    private File folder = new File(STORAGE);

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(Message message) {
        File findFile = new File(MESSAGES_FOLDER);
        Set<String> files = loadToSetNamesFiles(findFile);
        if (addToCreatedFile(files, message)) {
            createAndAddToFile(message);
        }
    }

    @Override
    public void edit(Message message) {

    }

    @Override
    public void delete(Message message) throws IOException {
        User sender = userRepository.findById(message.getSenderId());
        User recipient = userRepository.findById(message.getToUser());

        File folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + sender.getLogin() + "_" + recipient.getLogin());

        if (!folder.exists()) {
            folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + recipient.getLogin() + "_" + sender.getLogin());
        }

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (message.getDate().equals(file.getName().substring(sender.getLogin().length() + 1, sender.getLogin().length() + 20))) {
                Files.delete(Path.of(file.getAbsolutePath()));
            }
        }
    }

    private Set<String> loadToSetNamesFiles(File findFile) {
        return Arrays.stream(Objects.requireNonNull(findFile.list()))
                .map(file -> MESSAGES_FOLDER + FILE_SEPARATOR + file)
                .filter(s -> Files.isDirectory(Paths.get(s))).collect(Collectors.toSet());

    }

    private boolean addToCreatedFile(Set<String> files, Message message) {
        if (!files.isEmpty()) {
            for (String fileName : files) {
                if (fileName.endsWith(userRepository.findById(message.getSenderId()).getLogin().toLowerCase() + "_"
                        + userRepository.findById(message.getToUser()).getLogin().toLowerCase())
                        || fileName.endsWith(userRepository.findById(message.getToUser()).getLogin().toLowerCase() + "_"
                        + userRepository.findById(message.getSenderId()).getLogin().toLowerCase())) {
                    writeToFileMessage(fileName, message);
                    return false;
                }
            }
        }

        return true;
    }

    private void createAndAddToFile(Message message) {
        folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + userRepository.findById(message.getSenderId()).getLogin().toLowerCase()
                + "_" + userRepository.findById(message.getToUser()).getLogin().toLowerCase());
        folder.mkdir();
        writeToFileMessage(folder.getAbsolutePath(), message);
    }

    private void writeToFileMessage(String directory, Message message) {
        folder = new File(directory + FILE_SEPARATOR + userRepository.findById(message.getSenderId()).getLogin().toLowerCase()
                + "_" + message.getDate() + ".txt");
        try (FileWriter fileWriter = new FileWriter(folder)) {
            fileWriter.write(message.getContent());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getMessagesHistory(int senderId, int toUserId) throws IOException {
        List<Message> messageList = new ArrayList<>();

        User sender = userRepository.findById(senderId);
        User recipient = userRepository.findById(toUserId);

        File folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + sender.getLogin() + "_" + recipient.getLogin());

        if (!folder.exists()) {
            folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + recipient.getLogin() + "_" + sender.getLogin());
        }

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                int idSender = userRepository.findByLogin(file.getName().substring(0, sender.getLogin().length())).getId();

                int recipientId;
                int senderNameLength = sender.getLogin().length();
                int recipientNameLength = recipient.getLogin().length();

                if (file.getName().substring(0, sender.getLogin().length()).equals(folder.getName().substring(0, senderNameLength))) {
                    recipientId = userRepository.findByLogin(folder.getName().substring(senderNameLength + 1, recipientNameLength + senderNameLength + 1)).getId();
                } else {
                    recipientId = userRepository.findByLogin(folder.getName().substring(0, senderNameLength)).getId();
                }

                String text = readFile(file);
                String date = file.getName().substring(senderNameLength + 1, senderNameLength + 20);

                messageList.add(new Message(idSender, recipientId, text, date));
            }
        }

        return messageList;
    }

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
}
