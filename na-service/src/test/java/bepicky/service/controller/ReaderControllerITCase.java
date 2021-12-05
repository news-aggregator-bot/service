package bepicky.service.controller;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import picky.test.NatsContainerSupport;
import picky.test.SingletonMySQLContainerSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@Disabled
class ReaderControllerITCase implements SingletonMySQLContainerSupport, NatsContainerSupport {

    private final ReaderController readerController;

    private final ReaderDto createdReaderDto;

    @Autowired
    ReaderControllerITCase(ReaderController readerController) {
        this.readerController = readerController;
        ReaderRequest rr = new ReaderRequest();
        rr.setUsername("any");
        rr.setLastName("any");
        rr.setFirstName("any");
        rr.setChatId(System.currentTimeMillis());
        rr.setPlatform(Platform.TELEGRAM.name());
        createdReaderDto = readerController.register(rr);
    }

    @Test
    public void findByChatId_ExistingReader_ShouldReturnReader() {

        ReaderDto readerDto = readerController.find(createdReaderDto.getChatId());

        assertEquals(createdReaderDto.getChatId(), readerDto.getChatId());
        assertEquals(createdReaderDto.getFirstName(), readerDto.getFirstName());
        assertEquals(createdReaderDto.getLastName(), readerDto.getLastName());
        assertEquals(createdReaderDto.getUsername(), readerDto.getUsername());
        assertEquals(createdReaderDto.getLang(), readerDto.getLang());
        assertEquals(createdReaderDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void findByChatId_NotExistingReader_ShouldReturnNull() {
        Assertions.assertNull(readerController.find(System.currentTimeMillis()));
    }

    @Test
    public void enable_ExistingReader_ShouldEnable() {

        ReaderDto readerDto = readerController.enable(createdReaderDto.getChatId());

        assertEquals(createdReaderDto.getStatus(), Reader.Status.DISABLED.name());
        assertEquals(readerDto.getStatus(), Reader.Status.ENABLED.name());
    }

    @Test
    public void enable_NotExistingReader_ShouldReturnNull() {
        Assertions.assertNull(readerController.enable(System.currentTimeMillis()));
    }

    @Test
    public void settings_ExistingReader_ShouldEnable() {

        ReaderDto readerDto = readerController.settings(createdReaderDto.getChatId());

        assertEquals(createdReaderDto.getStatus(), Reader.Status.DISABLED.name());
        assertEquals(readerDto.getStatus(), Reader.Status.IN_SETTINGS.name());
    }

    @Test
    public void settings_NotExistingReader_ShouldReturnNull() {
        Assertions.assertNull(readerController.settings(System.currentTimeMillis()));
    }

    @Test
    public void disable_ExistingReader_ShouldEnable() {

        ReaderDto readerDto = readerController.disable(createdReaderDto.getChatId());

        assertEquals(createdReaderDto.getStatus(), Reader.Status.DISABLED.name());
        assertEquals(readerDto.getStatus(), Reader.Status.DISABLED.name());
    }

    @Test
    public void disable_NotExistingReader_ShouldReturnNull() {
        Assertions.assertNull(readerController.disable(System.currentTimeMillis()));
    }

    @Test
    public void pause_ExistingReader_ShouldEnable() {

        ReaderDto readerDto = readerController.pause(createdReaderDto.getChatId());

        assertEquals(createdReaderDto.getStatus(), Reader.Status.DISABLED.name());
        assertEquals(readerDto.getStatus(), Reader.Status.PAUSED.name());
    }

    @Test
    public void pause_NotExistingReader_ShouldReturnNull() {
        Assertions.assertNull(readerController.pause(System.currentTimeMillis()));
    }
}