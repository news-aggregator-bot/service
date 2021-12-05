package bepicky.service.controller;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.service.RandomString;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import picky.test.NatsContainerSupport;
import picky.test.SingletonMySQLContainerSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@Disabled
class ReaderCreationControllerITCase implements SingletonMySQLContainerSupport, NatsContainerSupport {

    @Autowired
    private ReaderController readerController;

    static Stream<Arguments> provideReaderRequests() {
        List<ReaderRequest> rrs = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            RandomString randomString = new RandomString(10 * i);
            rrs.add(readerRequest(
                randomString.nextString(),
                randomString.nextString(),
                randomString.nextString()
            ));
        }

        return rrs.stream().map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("provideReaderRequests")
    public void register_DifferentReaders_ShouldCreateAllOfThem(ReaderRequest rr) {

        ReaderDto readerDto = readerController.register(rr);

        assertNotNull(readerDto.getId());
        assertEquals(readerDto.getChatId(), rr.getChatId());
        assertEquals(readerDto.getFirstName(), rr.getFirstName());
        assertEquals(readerDto.getLastName(), rr.getLastName());
        assertEquals(readerDto.getUsername(), rr.getUsername());
        assertEquals(readerDto.getLang(), rr.getPrimaryLanguage());
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    static Stream<Arguments> provideLongNamesReaderRequests() {
        List<ReaderRequest> rrs = new ArrayList<>();
        RandomString randomString = new RandomString(256);
        String longName = randomString.nextString();
        rrs.add(readerRequest(longName, "any", "any"));
        rrs.add(readerRequest("any", longName, "any"));
        rrs.add(readerRequest("any", "any", longName));

        return rrs.stream().map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("provideLongNamesReaderRequests")
    public void register_LongNameReaders_ShouldCreateAllOfThem(ReaderRequest rr) {

        Assertions.assertThrows(IllegalStateException.class, () -> readerController.register(rr));
    }

    @Test
    public void register_UpdatedReaderRequest_ShouldUpdatedExistingReader() {
        ReaderRequest rr = new ReaderRequest();
        rr.setUsername("username");
        rr.setLastName("Yem");
        rr.setFirstName("Vlad");
        rr.setChatId(System.currentTimeMillis());
        rr.setPlatform(Platform.TELEGRAM.name());

        ReaderDto readerDto = readerController.register(rr);
        assertNotNull(readerDto.getId());

        rr.setUsername("any other");
        rr.setLastName("any other last name");
        rr.setFirstName("any other first name");
        rr.setPrimaryLanguage("ukr");

        ReaderDto updatedReaderDto = readerController.register(rr);

        assertEquals(updatedReaderDto.getChatId(), rr.getChatId());
        assertEquals(updatedReaderDto.getFirstName(), rr.getFirstName());
        assertEquals(updatedReaderDto.getLastName(), rr.getLastName());
        assertEquals(updatedReaderDto.getUsername(), rr.getUsername());
        assertEquals(updatedReaderDto.getLang(), rr.getPrimaryLanguage());
        assertEquals(updatedReaderDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void register_EmptyNameReaderRequest_ShouldCreateEmptyReader() {
        ReaderRequest rr = new ReaderRequest();
        rr.setChatId(System.currentTimeMillis());
        rr.setPlatform(Platform.TELEGRAM.name());

        ReaderDto readerDto = readerController.register(rr);

        assertNotNull(readerDto.getId());
        assertNull(readerDto.getFirstName());
        assertNull(readerDto.getLastName());
        assertNull(readerDto.getUsername());
        assertEquals(readerDto.getChatId(), rr.getChatId());
        assertEquals(readerDto.getLang(), rr.getPrimaryLanguage());
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    static Stream<Arguments> provideLanguages() {
        return Stream.of(
            Arguments.of("en", "en"),
            Arguments.of("ru", "ru"),
            Arguments.of("ukr", "ukr"),
            Arguments.of("ukr", null),
            Arguments.of("ukr", "ge"),
            Arguments.of("ukr", "any")
        );
    }

    @ParameterizedTest
    @MethodSource("provideLanguages")
    public void register_LanguageReaderRequest_ShouldCreateEmptyReader(String expectedLang, String language) {
        ReaderRequest rr = new ReaderRequest();
        rr.setChatId(System.currentTimeMillis());
        rr.setPlatform(Platform.TELEGRAM.name());
        rr.setPrimaryLanguage(language);

        ReaderDto readerDto = readerController.register(rr);

        assertNotNull(readerDto.getId());
        assertNull(readerDto.getFirstName());
        assertNull(readerDto.getLastName());
        assertNull(readerDto.getUsername());
        assertEquals(readerDto.getChatId(), rr.getChatId());
        assertEquals(readerDto.getPrimaryLanguage().getLang(), expectedLang);
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void register_NoChatIdReaderRequest_ShouldThrowAnException() {
        ReaderRequest rr = new ReaderRequest();
        rr.setPlatform(Platform.TELEGRAM.name());

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> readerController.register(rr)
        );
    }

    private static ReaderRequest readerRequest(String u, String fn, String ln) {
        ReaderRequest rr = new ReaderRequest();
        rr.setUsername(u);
        rr.setLastName(fn);
        rr.setFirstName(ln);
        rr.setChatId(System.currentTimeMillis());
        rr.setPlatform(Platform.TELEGRAM.name());
        return rr;
    }
}