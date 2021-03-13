package by.repository;

import by.model.Message;

public interface MessageRepository {
    void save(Message message);

    void edit(Message message);

    void delete(String messageName);
}
