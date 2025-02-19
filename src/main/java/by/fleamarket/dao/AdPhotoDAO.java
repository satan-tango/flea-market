package by.fleamarket.dao;

import by.fleamarket.entity.AdPhoto;
import by.fleamarket.repository.AdPhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdPhotoDAO {

    private final AdPhotoRepository adPhotoRepository;

    public void saveAdPhoto(AdPhoto adPhoto) {
        adPhotoRepository.save(adPhoto);
    }
}
