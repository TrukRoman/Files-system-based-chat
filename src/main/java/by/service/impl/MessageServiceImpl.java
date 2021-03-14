package by.service.impl;

import by.repository.MessageRepository;
import by.service.MessageService;
import by.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Override
    public void edit(Message message) {
        messageRepository.edit(message);
    }

    @Override
    public void delete(Message message) throws IOException {
        messageRepository.delete(message);
    }

    @Override
    public List<Message> getMessagesHistory(int senderId, int toUserId) throws IOException {
        return messageRepository.getMessagesHistory(senderId, toUserId);
    }
}
