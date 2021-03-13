package by.service;

import by.model.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User findById(int id);

    User findByLogin(String login) throws IOException;

    List<User> findAll() throws IOException;

    void save(User user);
}
