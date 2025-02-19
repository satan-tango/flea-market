package by.fleamarket.dao;

import by.fleamarket.entity.Ad;
import by.fleamarket.entity.User;
import by.fleamarket.repository.AdRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdDAO {

    private final AdRepository adRepository;
    private final AdPhotoDAO adPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;

    public void saveNewAd(Ad ad) {
        if (ad.getPhotos() == null) {
            adRepository.save(ad);
        } else {
            adRepository.save(ad);
            for (int i = 0; i < ad.getPhotos().size(); i++) {
                binaryContentDAO.saveBinaryContent(ad.getPhotos().get(i).getBinaryContent());
                adPhotoDAO.saveAdPhoto(ad.getPhotos().get(i));
            }
        }
    }

    public List<Ad> findByUser(User user) {
        return adRepository.findByUser(user);
    }

    public void deleteAd(Ad ad) {
        adRepository.delete(ad);
    }
}
