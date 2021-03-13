package by.service;

import by.model.Message;

public interface MessageService {
    void save(Message message);

    void edit(Message message);

    void delete(String messageName);
}
