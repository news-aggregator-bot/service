package bepicky.service.facade.functional;

import bepicky.common.domain.request.NewsSearchRequest;
import bepicky.common.domain.response.NewsSearchResponse;
import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.entity.NewsNote;
import bepicky.service.service.INewsNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NewsNoteFunctionalFacade implements INewsNoteFunctionalFacade, CommonFunctionalFacade {

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private IReaderFunctionalFacade readerFacade;

    @Autowired
    private NewsNoteDtoMapper newsNoteDtoMapper;

    @Override
    public NewsSearchResponse search(NewsSearchRequest request) {

        Page<NewsNote> notes = newsNoteService.searchByTitle(
            request.getKey(),
            pageReq(request.getPage(), request.getPageSize())
        );
        NewsSearchResponse response = new NewsSearchResponse();
        response.setList(notes.stream().map(newsNoteDtoMapper::toDto).collect(Collectors.toList()));
        response.setFirst(notes.isFirst());
        response.setLast(notes.isLast());
        response.setKey(request.getKey());
        response.setReader(readerFacade.find(request.getChatId()));
        return response;
    }

    @Override
    public void normaliseTitle() {
        newsNoteService.normaliseTitle();
    }
}
