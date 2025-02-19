package by.fleamarket.repository;

import by.fleamarket.entity.AdPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdPhotoRepository extends JpaRepository<AdPhoto, Long> {
}
