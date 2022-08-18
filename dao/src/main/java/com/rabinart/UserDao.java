package com.rabinart;

import com.rabinart.entity.User;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;


public class UserDao implements Dao<Long,User> {

    private static final UserDao INSTANCE = new UserDao();
    public static final String SAVE_SQL =
            "INSERT INTO users (login, password) VALUES (?,?)";

    private UserDao(){
    }

    public static UserDao getInstance(){
        return INSTANCE;
    }


    @Override
    public List<User> findAll() {

        return null;
    }

    @Override
    public Optional<User> find(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public void update(User entity) {

    }

    @SneakyThrows
    @Override
    public User save(User entity) {

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,entity.getLogin());
            preparedStatement.setString(2,entity.getPassword());
            preparedStatement.executeUpdate();
            var key = preparedStatement.getGeneratedKeys();
            if (key.next()) entity.setId(key.getLong("id"));

            return entity;
        }
    }
}
