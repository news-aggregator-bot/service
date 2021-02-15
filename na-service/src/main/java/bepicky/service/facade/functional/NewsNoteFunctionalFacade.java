package bepicky.service.facade.functional;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.NewsSearchRequest;
import bepicky.common.domain.response.NewsSearchResponse;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.dto.Ids;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
import bepicky.service.service.INewsNoteService;
import bepicky.service.service.IReaderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NewsNoteFunctionalFacade implements INewsNoteFunctionalFacade, CommonFunctionalFacade {

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private NewsNoteDtoMapper newsNoteDtoMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public NewsSearchResponse search(NewsSearchRequest request) {

        Page<NewsNote> notes = newsNoteService.searchByTitle(
            request.getKey(),
            pageReq(request.getPage(), request.getPageSize())
        );
        Reader reader = readerService.find(request.getChatId())
            .orElseThrow(() -> new ResourceNotFoundException("news_note:search:reader:" + request.getChatId()));
        NewsSearchResponse response = new NewsSearchResponse();
        response.setList(notes.stream()
            .map(n -> newsNoteDtoMapper.toDto(n, reader.getPrimaryLanguage()))
            .collect(Collectors.toList()));
        response.setFirst(notes.isFirst());
        response.setLast(notes.isLast());
        response.setKey(request.getKey());
        response.setReader(modelMapper.map(reader, ReaderDto.class));
        response.setTotalElements(notes.getTotalElements());
        response.setTotalPages(notes.getTotalPages());
        return response;
    }

    @Override
    public void refresh(Ids ids) {
        newsNoteService.refresh(ids.getFrom(), ids.getTo());
    }

}
