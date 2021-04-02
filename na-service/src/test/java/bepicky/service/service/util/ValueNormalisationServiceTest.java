package bepicky.service.service.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ValueNormalisationServiceTest {

    private final IValueNormalisationService normalisationService = new ValueNormalisationService();

    private static Stream<Arguments> titles() {
        return Stream.of(
            Arguments.of(
                "Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?",
                "соціопатіневдахаякогозруйнувавбатькощопродональдатрампанаписалайогоплемінниця"
            ),
            Arguments.of(
                "Про Зеленського, олігархів та коронавірус: інтерв'ю з президенткою Швейцарії Симонеттою Сомаругою",
                "прозеленськогоолігархівтакоронавірусінтервюзпрезиденткоюшвейцаріїсимонеттоюсомаругою"
            ),
            Arguments.of(
                "Report: MLB season not in jeopardy yet",
                "reportmlbseasonnotinjeopardyyet"
            ),
            Arguments.of(
                "Mortgages are wild. Tame yours with rates as low as 2.47% APR (15yr)",
                "mortgagesarewildtameyourswithratesaslowas247apr15yr"
            ),
            Arguments.of(
                "3rd Lockdown In France? Bars Restaurants Fear The Worst Amid Vaccination Controversy",
                "3rdlockdowninfrancebarsrestaurantsfeartheworstamidvaccinationcontroversy"
            ),
            Arguments.of(
                "How To Avoid Seeing The Business World Through 'Tulip-Colored Glasses'",
                "howtoavoidseeingthebusinessworldthroughtulipcoloredglasses"
            ),
            Arguments.of(null, ""),
            Arguments.of("''''''''''", ""),
            Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("titles")
    public void normaliseTitle_ShouldReturnExpected(String title, String expected) {
        assertEquals(expected, normalisationService.normaliseTitle(title));
    }

    @ParameterizedTest
    @MethodSource("titles")
    public void letters_ShouldReturnExpected(String title, String expected) {
        assertEquals(expected, normalisationService.normaliseTitle(title));
    }
}