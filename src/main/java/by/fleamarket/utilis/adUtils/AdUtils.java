package by.fleamarket.utilis.adUtils;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdUtils {

    private String title;
    private String category;
    private String description;
    private String location;
    private String price;
    private List<AdPhotoUtils> photos;
}
