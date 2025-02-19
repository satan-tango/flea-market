package by.fleamarket.cash;

import by.fleamarket.utilis.adUtils.AdUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdCash {

    private final Map<Long, AdUtils> adCashMap = new HashMap<>();

    public AdUtils getPreparingAd(long userId) {
        return adCashMap.get(userId);
    }

    public void savePreparingAd(long userId, AdUtils ad) {
        adCashMap.put(userId, ad);
    }

    public void deletePreparingAd(long userId) {
        adCashMap.remove(userId);
    }


}
