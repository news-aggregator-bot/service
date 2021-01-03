package bepicky.service.controller;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.StatusReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.service.client.NaBotClient;
import bepicky.service.facade.functional.IReaderFunctionalFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/reader")
@Slf4j
public class ReaderController {

    @Autowired
    private IReaderFunctionalFacade readerFunctionalFacade;

    @Autowired
    private NaBotClient naBotClient;

    @PostMapping("/register")
    public ReaderDto register(@Valid @RequestBody ReaderRequest dto) {
        return readerFunctionalFacade.create(dto);
    }

    @PutMapping("/enable/{chatId}")
    public ReaderDto enable(@PathVariable long chatId) {
        return readerFunctionalFacade.enable(chatId);
    }

    @PutMapping("/disable/{chatId}")
    public ReaderDto disable(@PathVariable long chatId) {
        return readerFunctionalFacade.disable(chatId);
    }

    @GetMapping("/{chatId}")
    public ReaderDto find(@PathVariable long chatId) {
        return readerFunctionalFacade.find(chatId);
    }

    @GetMapping("/status/{chatId}")
    public StatusReaderDto status(@PathVariable long chatId) {
        return readerFunctionalFacade.status(chatId);
    }

}
