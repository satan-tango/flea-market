package by.fleamarket.dao;

import by.fleamarket.entity.BinaryContent;
import by.fleamarket.repository.BinaryContentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BinaryContentDAO {

    private final BinaryContentRepository binaryContentRepository;

    public void saveBinaryContent(BinaryContent binaryContent) {
        binaryContentRepository.save(binaryContent);
    }
}
