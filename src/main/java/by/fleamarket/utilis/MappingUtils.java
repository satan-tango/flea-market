package by.fleamarket.utilis;

import by.fleamarket.dao.UserDAO;
import by.fleamarket.entity.Ad;
import by.fleamarket.entity.AdPhoto;
import by.fleamarket.entity.BinaryContent;
import by.fleamarket.entity.User;
import by.fleamarket.utilis.adUtils.AdBinaryContentUtils;
import by.fleamarket.utilis.adUtils.AdPhotoUtils;
import by.fleamarket.utilis.adUtils.AdUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MappingUtils {

    private final UserDAO userDAO;

    public Ad mapToAd(AdUtils adUtils, long userId) {
        User user = userDAO.findByTelegramUserId(userId);

        Ad ad = Ad.builder()
                .title(adUtils.getTitle())
                .category(adUtils.getCategory())
                .description(adUtils.getDescription())
                .location(adUtils.getLocation())
                .price(adUtils.getPrice())
                .user(user)
                .build();

        if (adUtils.getPhotos() != null) {
            List<AdPhoto> photos = new ArrayList<>();
            for (int i = 0; i < adUtils.getPhotos().size(); i++) {
                photos.add(mapToAdPhoto(adUtils.getPhotos().get(i), ad));
            }
            ad.setPhotos(photos);
        }
        return ad;
    }

    public AdUtils mapToAdUtils(Ad ad) {
        AdUtils adUtils = AdUtils.builder()
                .title(ad.getTitle())
                .category(ad.getCategory())
                .description(ad.getDescription())
                .location(ad.getLocation())
                .price(ad.getPrice())
                .build();

        if (ad.getPhotos() != null) {
            List<AdPhotoUtils> photos = new ArrayList<>();
            for (int i = 0; i < ad.getPhotos().size(); i++) {
                photos.add(mapToAdPhotoUtils(ad.getPhotos().get(i)));
            }
            adUtils.setPhotos(photos);
        }

        return adUtils;
    }

    private AdPhoto mapToAdPhoto(AdPhotoUtils adPhotoUtils, Ad ad) {
        AdPhoto adPhoto = AdPhoto.builder()
                .telegramFileId(adPhotoUtils.getTelegramFileId())
                .fileSize(adPhotoUtils.getFileSize())
                .binaryContent(mapToBinaryContent(adPhotoUtils.getBinaryContent()))
                .ad(ad)
                .build();
        return adPhoto;
    }

    private BinaryContent mapToBinaryContent(AdBinaryContentUtils adBinaryContentUtils) {
        BinaryContent binaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(adBinaryContentUtils.getFileAsArrayOfBytes())
                .build();
        return binaryContent;
    }

    private AdPhotoUtils mapToAdPhotoUtils(AdPhoto adPhoto) {
        AdPhotoUtils adPhotoUtils = AdPhotoUtils.builder()
                .telegramFileId(adPhoto.getTelegramFileId())
                .fileSize(adPhoto.getFileSize())
                .binaryContent(mapToBinaryContent(adPhoto.getBinaryContent()))
                .build();

        return adPhotoUtils;
    }

    private AdBinaryContentUtils mapToBinaryContent(BinaryContent binaryContent) {
        AdBinaryContentUtils binaryContentUtils = AdBinaryContentUtils.builder()
                .fileAsArrayOfBytes(binaryContent.getFileAsArrayOfBytes())
                .build();
        return binaryContentUtils;
    }
}
