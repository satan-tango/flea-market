package by.fleamarket.repository;

import by.fleamarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByTelegramUserId(Long telegramUserId);

    User findByTelegramUserId(long telegramUserId);
}
