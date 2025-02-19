package by.fleamarket.utilis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BotState {
    START(0),
    POST_AD(1),
    POST_AD_TITLE(2),
    POST_AD_CATEGORIES(3),
    POST_AD_DESCRIPTION(4),
    POST_AD_LOCATION(5),
    POST_AD_PRICE(6),
    POST_AD_PHOTOS(7),
    POST_AD_CONFIRM(8),
    MY_AD(9),
    FIND_AD(10),
    SETTINGS(11),
    SUPPORT(12),
    DONATE(13),
    NEW_USER(14);

    private int numberOfState;
}
