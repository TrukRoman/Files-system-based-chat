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
    public static final String MESSAGES_FOLDER = "storage/messages";
    public static final String FILE_SEPARATOR = File.separator;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(Message message) {
        Set<String> files = findCatalogs();

        String senderName = userRepository.findById(message.getSenderId()).getLogin();
        String recipientName = userRepository.findById(message.getToUser()).getLogin();

        String folderName = getMessagesFolder(files, senderName, recipientName);

        writeToFileMessage(folderName, message);
    }

    private String getMessagesFolder(Set<String> files, String senderName, String recipientName) {

        String folder = getFolderNameIfExist(files, senderName, recipientName);

        if (folder.equals("")) {
            folder = getNewFolderPath(senderName, recipientName);
        }

        return folder;
    }

    private String getFolderNameIfExist(Set<String> files, String senderName, String recipientName) {
        if (!files.isEmpty()) {
            for (String folderName : files) {
                if (folderName.endsWith(senderName.toLowerCase() + "_" + recipientName.toLowerCase())
                        || folderName.endsWith(recipientName.toLowerCase() + "_" + senderName.toLowerCase())) {
                    return folderName;
                }
            }
        }

        return "";
    }

    private String getNewFolderPath(String senderName, String recipientName) {
        File folder = new File(MESSAGES_FOLDER + FILE_SEPARATOR + senderName.toLowerCase()
                + "_" + recipientName.toLowerCase());
        folder.mkdir();

        return folder.getAbsolutePath();
    }

    private Set<String> findCatalogs() {
        File folder = new File(MESSAGES_FOLDER);

        return Arrays.stream(Objects.requireNonNull(folder.list()))
                .map(file -> MESSAGES_FOLDER + FILE_SEPARATOR + file)
                .filter(s -> Files.isDirectory(Paths.get(s))).collect(Collectors.toSet());
    }

    @Override
    public void edit(Message message) {
        save(message);
    }

    @Override
    public void delete(Message message) throws IOException {
        Set<String> catalogs = findCatalogs();

        User sender = userRepository.findById(message.getSenderId());
        User recipient = userRepository.findById(message.getToUser());

        File folder = new File(getFolderNameIfExist(catalogs, sender.getLogin(), recipient.getLogin()));

        Files.delete(Path.of(getMessagePathByDate(folder, message)));
    }

    private String getMessagePathByDate(File folder, Message message) {
        String path = "";

        File[] listOfFiles = folder.listFiles();

        User sender = userRepository.findById(message.getSenderId());
        User recipient = userRepository.findById(message.getToUser());

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (message.getDate().equals(file.getName().substring(sender.getLogin().length() + 1, sender.getLogin().length() + 20))) {
                path = file.getAbsolutePath();
            } else if (message.getDate().equals(file.getName().substring(recipient.getLogin().length() + 1, recipient.getLogin().length() + 20))) {
                path = file.getAbsolutePath();
            }
        }

        return path;
    }

    private void writeToFileMessage(String directory, Message message) {
        File file = new File(directory + FILE_SEPARATOR + userRepository.findById(message.getSenderId()).getLogin().toLowerCase()
                + "_" + message.getDate() + ".txt");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(message.getContent());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getMessagesHistory(int senderId, int toUserId) {
        Set<String> catalogs = findCatalogs();

        User sender = userRepository.findById(senderId);
        User recipient = userRepository.findById(toUserId);

        File folder = new File(getFolderNameIfExist(catalogs, sender.getLogin(), recipient.getLogin()));

        List<Message> messageList = getMessagesListFromFolderByUsers(sender, recipient, folder);

        return reverseSortListByDate(messageList);
    }

    private String readFromFile(File file) {
        String text = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            text = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    private List<Message> reverseSortListByDate(List<Message> messageList) {
        return messageList.stream()
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .collect(Collectors.toList());
    }

    private List<Message> getMessagesListFromFolderByUsers(User sender, User recipient, File folder) {
        List<Message> messageList = new ArrayList<>();

        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
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

                messageList.add(new Message(sendId, recipientId, readFromFile(file),
                        file.getName().substring(userSendLoginLength + 1, userSendLoginLength + 20)));
            }
        }

        return messageList;
    }
}
