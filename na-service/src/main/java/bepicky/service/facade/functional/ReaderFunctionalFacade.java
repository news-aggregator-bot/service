package bepicky.service.facade.functional;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.service.service.IReaderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReaderFunctionalFacade implements IReaderFunctionalFacade {

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ReaderDto delete(long id) {
        return readerService.delete(id).map(r -> modelMapper.map(r, ReaderDto.class)).orElse(null);
    }
}
