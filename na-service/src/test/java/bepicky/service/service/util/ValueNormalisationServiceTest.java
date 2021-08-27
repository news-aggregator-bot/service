package bepicky.service.service.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ValueNormalisationServiceTest {

    private final IValueNormalisationService normalisationService = new ValueNormalisationService();

    private static Stream<Arguments> titles() {
        return Stream.of(
            Arguments.of(
                "Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?",
                "соціопат і невдаха якого зруйнував батько що про дональда трампа написала його племінниця"
            ),
            Arguments.of(
                "Про Зеленського, олігархів та коронавірус: інтерв'ю з президенткою Швейцарії Симонеттою Сомаругою",
                "про зеленського олігархів та коронавірус інтервю з президенткою швейцарії симонеттою сомаругою"
            ),
            Arguments.of(
                "Report: MLB season not in jeopardy yet",
                "report mlb season not in jeopardy yet"
            ),
            Arguments.of(
                "Mortgages are wild. Tame yours with rates as low as 2.47% APR (15yr)",
                "mortgages are wild tame yours with rates as low as 247 apr 15yr"
            ),
            Arguments.of(
                "3rd Lockdown In France? Bars Restaurants Fear The Worst Amid Vaccination Controversy",
                "3rd lockdown in france bars restaurants fear the worst amid vaccination controversy"
            ),
            Arguments.of(
                "How To Avoid Seeing The Business World Through 'Tulip-Colored Glasses'",
                "how to avoid seeing the business world through tulipcolored glasses"
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

    private static Stream<Arguments> tags() {
        return Stream.of(
            Arguments.of(
                "Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?",
                "СоціопатіневдахаякогозруйнувавбатькоЩопроДональдаТрампанаписалайогоплемінниця"
            ),
            Arguments.of(
                "Про Зеленського, олігархів та коронавірус: інтерв'ю з президенткою Швейцарії Симонеттою Сомаругою",
                "ПроЗеленськогоолігархівтакоронавірусінтервюзпрезиденткоюШвейцаріїСимонеттоюСомаругою"
            ),
            Arguments.of(
                "Report: MLB season not in jeopardy yet",
                "ReportMLBseasonnotinjeopardyyet"
            ),
            Arguments.of(
                "Mortgages are wild. Tame yours with rates as low as 2.47% APR (15yr)",
                "MortgagesarewildTameyourswithratesaslowas247APR15yr"
            ),
            Arguments.of(
                "3rd Lockdown In France? Bars Restaurants Fear The Worst Amid Vaccination Controversy",
                "3rdLockdownInFranceBarsRestaurantsFearTheWorstAmidVaccinationControversy"
            ),
            Arguments.of(
                "How To Avoid Seeing The Business World Through 'Tulip-Colored Glasses'",
                "HowToAvoidSeeingTheBusinessWorldThroughTulip-ColoredGlasses"
            ),
            Arguments.of(null, ""),
            Arguments.of("''''''''''", ""),
            Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("tags")
    public void normaliseTag_ShouldReturnExpected(String tag, String expected) {
        assertEquals(expected, normalisationService.normaliseTag(tag));
    }

    private static Stream<Arguments> longTitles() {
        return Stream.of(
            Arguments.of(
                " Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?",
                "Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трампа написала його племінниця?Соціопат і невдаха, якого зруйнував батько. Що про Дональда Трамп"
            ),
            Arguments.of(null, ""),
            Arguments.of("''''''''''", "''''''''''"),
            Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("longTitles")
    public void trimTitle_ShouldReturnExpected(String title, String expected) {
        assertEquals(expected, normalisationService.trimTitle(title));
    }
}