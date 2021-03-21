package by.repository.impl;

import by.repository.UserRepository;
import by.model.User;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    public static final String USERS_FOLDER = "storage/users";
    public static final String FILE_SEPARATOR = File.separator;

    @Override
    public User findById(int id) {
        return getUserFromListById(findAll(), id);
    }

    private User getUserFromListById(List<User> userList, int id) {
        return userList.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .get();
    }

    @Override
    public User findByLogin(String login) {
        return getUserFromFile(login + ".txt");
    }

    @Override
    public List<User> findAll() {
        Map<String, User> userMap = getUsersMap();

        return getAllUsersList(userMap);
    }

    private List<User> getAllUsersList(Map<String, User> userMap) {
        ArrayList<User> list = new ArrayList<>();

        for (Map.Entry<String, User> pair : userMap.entrySet()) {
            list.add(pair.getValue());
        }

        return list;
    }

    @Override
    public void save(User user) {
        String dataUser = getIdForNewUser() + "-" + user.getLogin() + "-" + user.getPassword();
        File file = new File(USERS_FOLDER + FILE_SEPARATOR + user.getLogin().toLowerCase() + ".txt");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(dataUser);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, User> getUsersMap() {
        Map<String, User> map = new HashMap<>();

        File findFile = new File(USERS_FOLDER);
        String[] files = findFile.list();

        if (files != null) {
            for (String file : files) {
                map.put(file, getUserFromFile(file));
            }
        }
        return map;
    }

    private User getUserFromFile(String fileName) {
        File file = new File(USERS_FOLDER + FILE_SEPARATOR + fileName);

        String str = readFromFile(file);

        return getUserFromString(str);
    }

    private int getIdForNewUser() {
        Map<String, User> userMap = getUsersMap();

        return getLastId(userMap) + 1;
    }

    private int getLastId(Map<String, User> userMap) {
        int id = 0;

        for (Map.Entry<String, User> pair : userMap.entrySet()) {
            if (pair.getValue().getId() >= id) {
                id = pair.getValue().getId();
            }
        }

        return id;
    }

    private String readFromFile(File file) {
        String str = "";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            str = bufferedReader.readLine();
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        return str;
    }

    private User getUserFromString(String str) {
        User user = new User();

        StringBuilder user_id = new StringBuilder();
        StringBuilder user_login = new StringBuilder();
        StringBuilder user_password = new StringBuilder();

        int index = 0;

        while (str.charAt(index) != '-') {
            user_id.append(str.charAt(index));
            index++;
        }

        index++;

        while (str.charAt(index) != '-') {
            user_login.append(str.charAt(index));
            index++;
        }

        index++;

        while (index < str.length()) {
            user_password.append(str.charAt(index));
            index++;
        }

        user.setId(Integer.parseInt(user_id.toString()));
        user.setLogin(user_login.toString());
        user.setPassword(user_password.toString());

        return user;
    }
}
