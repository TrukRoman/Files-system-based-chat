package by.service;

import by.model.Message;

import java.io.IOException;
import java.util.List;

public interface MessageService {
    void save(Message message);

    void edit(Message message);

    void delete(String messageName);

    List<Message> getMessagesHistory(int senderId, int toUserId) throws IOException;
}
