package by.service;

import by.model.Message;

import java.util.List;

public interface ParserToJson {
    void parsingListToJSON(List<Message> messageList);
}
