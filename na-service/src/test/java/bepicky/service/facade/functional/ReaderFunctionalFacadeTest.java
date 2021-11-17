package bepicky.service.facade.functional;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import org.junit.jupiter.api.Disabled;
import picky.test.SingletonMySQLContainerSupport;
import picky.test.NatsContainerSupport;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@Disabled
public class ReaderFunctionalFacadeTest implements SingletonMySQLContainerSupport, NatsContainerSupport {

    @Autowired
    private IReaderFunctionalFacade functionalFacade;

    @Test
    public void create_CorrectReaderRequest_ShouldCreateCorrectReader() {
        ReaderRequest rr = new ReaderRequest();
        rr.setUsername("username");
        rr.setLastName("Yem");
        rr.setFirstName("Vlad");
        rr.setChatId(100L);
        rr.setPlatform(Platform.TELEGRAM.name());

        ReaderDto readerDto = functionalFacade.create(rr);

        assertNotNull(readerDto.getId());
        assertEquals(readerDto.getChatId(), rr.getChatId());
        assertEquals(readerDto.getFirstName(), rr.getFirstName());
        assertEquals(readerDto.getLastName(), rr.getLastName());
        assertEquals(readerDto.getUsername(), rr.getUsername());
        assertEquals(readerDto.getLang(), rr.getPrimaryLanguage());
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void create_EmptyNameReaderRequest_ShouldCreateEmptyReader() {
        ReaderRequest rr = new ReaderRequest();
        rr.setChatId(101L);
        rr.setPlatform(Platform.TELEGRAM.name());

        ReaderDto readerDto = functionalFacade.create(rr);

        assertNotNull(readerDto.getId());
        assertNull(readerDto.getFirstName());
        assertNull(readerDto.getLastName());
        assertNull(readerDto.getUsername());
        assertEquals(readerDto.getChatId(), rr.getChatId());
        assertEquals(readerDto.getLang(), rr.getPrimaryLanguage());
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void create_NullLanguageReaderRequest_ShouldCreateEmptyReader() {
        ReaderRequest rr = new ReaderRequest();
        rr.setChatId(101L);
        rr.setPlatform(Platform.TELEGRAM.name());
        rr.setPrimaryLanguage(null);

        ReaderDto readerDto = functionalFacade.create(rr);

        assertNotNull(readerDto.getId());
        assertNull(readerDto.getFirstName());
        assertNull(readerDto.getLastName());
        assertNull(readerDto.getUsername());
        assertEquals(readerDto.getChatId(), rr.getChatId());
        assertEquals(readerDto.getPrimaryLanguage().getLang(), "ukr");
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void create_NoChatIdReaderRequest_ShouldThrowAnException() {
        ReaderRequest rr = new ReaderRequest();
        rr.setPlatform(Platform.TELEGRAM.name());

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> functionalFacade.create(rr)
        );
    }

}