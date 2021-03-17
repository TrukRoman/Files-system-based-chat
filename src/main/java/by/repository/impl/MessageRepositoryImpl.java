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
        Set<String> files = Arrays.stream(Objects.requireNonNull(findFile.list()))
                .map(file -> MESSAGES_FOLDER + FILE_SEPARATOR + file)
                .filter(s -> Files.isDirectory(Paths.get(s))).collect(Collectors.toSet());

        boolean isAdded = false;
        String senderName = userRepository.findById(message.getSenderId()).getLogin();
        String recipientName = userRepository.findById(message.getToUser()).getLogin();

        if (!files.isEmpty()) {
            for (String fileName : files) {
                if (fileName.endsWith(senderName.toLowerCase() + "_" + recipientName.toLowerCase())
                        || fileName.endsWith(recipientName.toLowerCase() + "_" + senderName.toLowerCase())) {
                    writeToFileMessage(fileName, message);
                    isAdded = true;
                }
            }
        }

        if (!isAdded) {
            folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + senderName.toLowerCase()
                    + "_" + recipientName.toLowerCase());
            folder.mkdir();
            writeToFileMessage(folder.getAbsolutePath(), message);
        }
    }

    @Override
    public void edit(Message message) {
        save(message);
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
            } else if (message.getDate().equals(file.getName().substring(recipient.getLogin().length() + 1, recipient.getLogin().length() + 20))) {
                Files.delete(Path.of(file.getAbsolutePath()));
            }
        }
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
    public List<Message> getMessagesHistory(int senderId, int toUserId) {
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
                int recipientId = 0;
                int sendId = 0;

                if (file.getName().startsWith(sender.getLogin())) {
                    sendId = sender.getId();
                    recipientId = recipient.getId();
                } else if (file.getName().startsWith(recipient.getLogin())) {
                    sendId = recipient.getId();
                    recipientId = sender.getId();
                }

                int userSendLoginLength = userRepository.findById(sendId).getLogin().length();

                String text = "";

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    text = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String date = file.getName().substring(userSendLoginLength + 1, userSendLoginLength + 20);

                messageList.add(new Message(sendId, recipientId, text, date));
            }
        }

        messageList = messageList.stream()
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .collect(Collectors.toList());

        return messageList;
    }
}
