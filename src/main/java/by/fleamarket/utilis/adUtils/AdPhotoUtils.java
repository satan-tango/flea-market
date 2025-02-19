package by.fleamarket.utilis.adUtils;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdPhotoUtils {

    private String telegramFileId;
    private AdBinaryContentUtils binaryContent;
    private Integer fileSize;
}
