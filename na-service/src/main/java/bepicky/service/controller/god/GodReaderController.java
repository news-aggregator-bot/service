package bepicky.service.controller.god;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.service.facade.functional.IReaderFunctionalFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/god/reader")
public class GodReaderController {

    @Autowired
    private IReaderFunctionalFacade readerFacade;

    @DeleteMapping("/delete/{id}")
    public ReaderDto deleteById(@PathVariable long id) {
        return readerFacade.delete(id);
    }

}
