package by.fleamarket.dao;

import by.fleamarket.entity.User;
import by.fleamarket.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDAO {

    private final UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findByTelegramUserId(long userId) {
        return userRepository.findByTelegramUserId(userId);
    }

    public boolean isExistUser(long telegramUserId) {
        return userRepository.existsByTelegramUserId(telegramUserId);
    }

    public void saveNewUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
