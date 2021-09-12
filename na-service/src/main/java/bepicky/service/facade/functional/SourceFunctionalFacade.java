package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.SourceDto;
import bepicky.common.domain.response.SourceListResponse;
import bepicky.common.domain.response.SourceResponse;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.common.msg.SourceCommandMsg;
import bepicky.service.domain.request.ListRequest;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Source;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SourceFunctionalFacade implements ISourceFunctionalFacade, CommonFunctionalFacade {

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SourceListResponse listAll(ListRequest request) {
        Reader reader = readerService.findByChatId(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:source:reader {} not found", request.getChatId());
            return new SourceListResponse(ErrorUtil.readerNotFound());
        }

        Page<Source> srcPage = sourceService.findAllEnabled(pageReq(request.getPage(), request.getSize()));
        return new SourceListResponse(
            srcPage.stream().map(s -> {
                SourceDto dto = modelMapper.map(s, SourceDto.class);
                dto.setPicked(reader.getSources().contains(s));
                return dto;
            }).collect(Collectors.toList()),
            srcPage.isFirst(),
            srcPage.isLast(),
            modelMapper.map(reader, ReaderDto.class)
        );
    }

    @Override
    public SourceResponse pick(SourceCommandMsg msg) {
        return doAction(msg, Reader::addSource);
    }

    @Override
    public SourceResponse remove(SourceCommandMsg msg) {
        return doAction(msg, Reader::removeSource);
    }

    @Override
    public void changeSource(long sourceId, long sourcePageId) {
        sourceService.findById(sourceId)
            .map(s -> sourcePageService.changeSource(s, sourcePageId))
            .orElseThrow(() -> new ResourceNotFoundException("Change source failed."));
    }

    private SourceResponse doAction(SourceCommandMsg msg, BiConsumer<Reader, Source> action) {
        Reader reader = readerService.findByChatId(msg.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("update:source:reader {} not found", msg.getChatId());
            return new SourceResponse(ErrorUtil.readerNotFound());
        }
        Source source = sourceService.find(msg.getSourceId()).orElse(null);
        if (source == null) {
            log.error("update:source:{} not found:reader:{}", msg.getSourceId(), msg.getChatId());
            return new SourceResponse(ErrorUtil.sourceNotFound());
        }
        action.accept(reader, source);
        readerService.save(reader);

        return new SourceResponse(modelMapper.map(reader, ReaderDto.class), modelMapper.map(source, SourceDto.class));
    }
}
