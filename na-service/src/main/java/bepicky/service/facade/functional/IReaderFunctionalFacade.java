package bepicky.service.facade.functional;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.StatusReaderDto;
import bepicky.common.domain.request.ReaderRequest;

import java.util.List;

public interface IReaderFunctionalFacade {

    ReaderDto create(ReaderRequest request);

    ReaderDto enable(long chatId);

    ReaderDto disable(long chatId);

    ReaderDto settings(long chatId);

    ReaderDto block(long chatId);

    ReaderDto pause(long chatId);

    ReaderDto delete(long id);

    ReaderDto find(long chatId);

    StatusReaderDto status(long chatId);

    List<ReaderDto> findAll();

}
