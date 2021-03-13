package by.repository;

import by.model.User;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public interface UserRepository {
    User findById(int id);

    User findByLogin(String login) throws IOException;

    List<User> findAll() throws IOException;

    void save(User user);
}
