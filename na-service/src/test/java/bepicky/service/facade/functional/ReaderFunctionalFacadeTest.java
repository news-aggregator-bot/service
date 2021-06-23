package bepicky.service.facade.functional;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SpringBootTest(classes = {NAService.class, ReaderFunctionalFacadeTest.FacadeTestConfiguration.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@Slf4j
public class ReaderFunctionalFacadeTest {

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

    @Test(expected = IllegalArgumentException.class)
    public void create_NoChatIdReaderRequest_ShouldThrowAnException() {
        ReaderRequest rr = new ReaderRequest();
        rr.setPlatform(Platform.TELEGRAM.name());

        functionalFacade.create(rr);
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value ="classpath:application-it.yml")
    @EnableTransactionManagement
    static class FacadeTestConfiguration {

    }
}