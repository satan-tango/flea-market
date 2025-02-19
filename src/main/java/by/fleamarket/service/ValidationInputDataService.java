package by.fleamarket.service;

import by.fleamarket.utilis.enums.AvailableCurrency;
import by.fleamarket.utilis.enums.ValidationState;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidationInputDataService {
    //+ - один или несколько раз
    //? - один или отсутвует
    //s - пробел
    //w - a-zA-Z0-9_

    public ValidationState validateAdTitle(String title) {

        if (title.length() < 2 || title.length() > 30) {
            return ValidationState.INVALID_LENGTH;
        }


        Pattern pattern = Pattern.compile("[\\wа-яА-Я ]*");
        Matcher matcher = pattern.matcher(title);
        if (!matcher.matches()) {
            return ValidationState.INVALID_CONTENT;
        }

        return ValidationState.DATE_IS_VALID;
    }

    public ValidationState validateAdDescription(String descr) {

        if (isSkipping(descr)) {
            return ValidationState.SKIP_CONTENT;
        }

        if (descr.length() < 3 || descr.length() > 80) {
            return ValidationState.INVALID_LENGTH;
        }

        Pattern pattern = Pattern.compile("[\\wа-яА-Я ,.\\-]*");
        Matcher matcher = pattern.matcher(descr);

        if (!matcher.matches()) {
            return ValidationState.INVALID_CONTENT;
        }

        return ValidationState.DATE_IS_VALID;

    }

    public ValidationState validateAdLocation(String location) {
        if (isSkipping(location)) {
            return ValidationState.SKIP_CONTENT;
        }

        if (location.length() < 3 || location.length() > 40) {
            return ValidationState.INVALID_LENGTH;
        }

        Pattern pattern = Pattern.compile("[\\wа-яА-Я ,.\\-]*");
        Matcher matcher = pattern.matcher(location);

        if (!matcher.matches()) {
            return ValidationState.INVALID_CONTENT;
        }

        return ValidationState.DATE_IS_VALID;


    }


    public ValidationState validateAdPrice(String price) {

        if (price.length() < 3 || price.length() > 15) {
            return ValidationState.INVALID_LENGTH;
        }

        if (price.equalsIgnoreCase("договорная")) {
            return ValidationState.AGREED_PRICE;
        }


        Pattern validatingReg = Pattern.compile("\\d+\\s?[A-Z]{3}");
        Matcher validatingRegMatcher = validatingReg.matcher(price);

        if (!validatingRegMatcher.matches()) {
            return ValidationState.INVALID_CONTENT;
        }

        Pattern findCurrencyReg = Pattern.compile("[A-Z]{3}");
        Matcher findCurrencyMatcher = findCurrencyReg.matcher(price);

        if (findCurrencyMatcher.find()) {
            String currency = price.substring(findCurrencyMatcher.start(), findCurrencyMatcher.end());

            for (AvailableCurrency availableCurrency : AvailableCurrency.values()) {
                if (availableCurrency.name().equals(currency)) {
                    return ValidationState.DATE_IS_VALID;
                }
            }

            return ValidationState.UNAVAILABLE_CURRENCY;
        } else {
            return ValidationState.THERE_IS_NO_CURRENCY;
        }

    }


    public boolean isSkipping(String text) {

        if (text.equals("-") || text.equalsIgnoreCase("пропуск")) {
            return true;
        }

        return false;
    }

}
