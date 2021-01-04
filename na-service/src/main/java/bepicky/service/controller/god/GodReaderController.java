package bepicky.service.controller.god;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.service.facade.functional.IReaderFunctionalFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/god/reader")
public class GodReaderController {

    @Autowired
    private IReaderFunctionalFacade facade;

    @GetMapping("/all")
    public List<ReaderDto> getReaders() {
        return facade.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ReaderDto deleteById(@PathVariable long id) {
        return facade.delete(id);
    }

    @PutMapping("/block/{chatId}")
    public ReaderDto block(@PathVariable long chatId) {
        return facade.block(chatId);
    }

}
