package by.fleamarket.utilis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CategoriesUtils {
    MALE_WARDROBE("Мужской Гаредероб\uD83E\uDD35\u200D♂\uFE0F", 0),
    FEMALE_WARDROBE("Женский Гардероб\uD83E\uDD35\u200D♀\uFE0F", 1),
    CHILD_WARDROBE("Детский Гардероб\uD83D\uDC67\uD83C\uDFFB", 2),
    ELECTRONICS("Электроника\uD83D\uDCBB", 3),
    ANIMALS("Животные\uD83D\uDC36", 4),
    SERVICES("Услуги🔨", 5),
    OTHERS("Прочее\uD83E\uDEAD", 6);

    private String categoryName;
    private int value;
}
