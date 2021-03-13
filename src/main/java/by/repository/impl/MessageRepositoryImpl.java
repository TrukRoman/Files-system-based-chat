package by.repository.impl;

import by.repository.MessageRepository;
import by.model.Message;
import by.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
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
        File findFile = new File(MESSAGES_FOLDER);
        Set<String> files = loadToSetNamesFiles(findFile);
        addToCreatedFile(files, message);
    }

    @Override
    public void delete(String messageName) {

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
}
