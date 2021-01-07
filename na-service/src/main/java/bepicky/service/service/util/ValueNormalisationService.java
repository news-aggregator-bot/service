package bepicky.service.service.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ValueNormalisationService implements IValueNormalisationService {

    @Override
    public String normaliseTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return "";
        }
        return title.chars().filter(c -> Character.isAlphabetic(c) || Character.isDigit(c))
            .map(Character::toLowerCase)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
