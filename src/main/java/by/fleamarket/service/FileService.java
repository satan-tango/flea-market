package by.fleamarket.service;

import by.fleamarket.cash.AdCash;
import by.fleamarket.entity.Ad;
import by.fleamarket.exceptions.UploadFileException;
import by.fleamarket.utilis.adUtils.AdBinaryContentUtils;
import by.fleamarket.utilis.adUtils.AdPhotoUtils;


import by.fleamarket.utilis.adUtils.AdUtils;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j
public class FileService {

    @Value("${telegrambot.botToken}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private AdCash adCash;

    public AdUtils processPhoto(Message message, AdUtils preparingAd, long userId) {
        int photoSizeCount = message.getPhoto().size();
        int photoIndex = photoSizeCount > 1 ? message.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = message.getPhoto().get(photoIndex);
        List<AdPhotoUtils> adPhotoList;
        if (preparingAd.getPhotos() == null) {
            adPhotoList = new ArrayList<>();
        } else {
            adPhotoList = preparingAd.getPhotos();
        }
        AdPhotoUtils adPhoto;
        adPhoto = processPhoto(telegramPhoto, userId);
        adPhotoList.add(adPhoto);
        log.debug("\nPhoto was saved\nUser:" + userId + "\nPhoto_id: " + message.getPhoto().get(photoIndex).getFileId());
        preparingAd.setPhotos(adPhotoList);

        return preparingAd;
    }

    private AdPhotoUtils processPhoto(PhotoSize telegramPhoto, long userId) {
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            AdBinaryContentUtils binaryContent = getBinaryContent(response);
            AdPhotoUtils adPhoto = buildAdPhoto(telegramPhoto, binaryContent);
            log.debug("Photo was successfully build, user: " + userId);

            return adPhoto;

        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    //Persistent - постоянный
    //Transient - переходящий
    private AdBinaryContentUtils getBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        AdBinaryContentUtils binaryContent = AdBinaryContentUtils.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContent;
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }


        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private AdPhotoUtils buildAdPhoto(PhotoSize telegramPhoto, AdBinaryContentUtils binaryContent) {
        return AdPhotoUtils.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(binaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }
}
