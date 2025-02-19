package by.fleamarket.repository;

import by.fleamarket.entity.Ad;
import by.fleamarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByUser(User user);
}
