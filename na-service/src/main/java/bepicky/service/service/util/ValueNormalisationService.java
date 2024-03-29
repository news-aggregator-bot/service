package bepicky.service.service.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ValueNormalisationService implements IValueNormalisationService {

    @Override
    public String normaliseTag(String val) {
        if (StringUtils.isBlank(val)) {
            return "";
        }
        return val.chars().filter(c -> Character.isAlphabetic(c) || Character.isDigit(c) || c == '-')
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    @Override
    public String normaliseTitle(String val) {
        if (StringUtils.isBlank(val)) {
            return "";
        }
        return val.chars().filter(c -> Character.isAlphabetic(c) || Character.isDigit(c) || Character.isWhitespace(c))
            .map(Character::toLowerCase)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    @Override
    public String trimTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return "";
        }
        if (title.length() < 250) {
            return title.trim();
        }
        return title.substring(0, 250).trim();
    }
}
