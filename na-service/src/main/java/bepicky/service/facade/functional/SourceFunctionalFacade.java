package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.SourceDto;
import bepicky.common.domain.request.SourceRequest;
import bepicky.common.domain.response.SourceListResponse;
import bepicky.common.domain.response.SourceResponse;
import bepicky.service.domain.request.ListRequest;
import bepicky.service.entity.Category;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourceService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SourceFunctionalFacade implements ISourceFunctionalFacade {

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SourceListResponse listAll(ListRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:source:reader {} not found", request.getChatId());
            return new SourceListResponse(ErrorUtil.readerNotFound());
        }
        Set<SourceDto> sources = reader.getCategories().stream()
            .map(Category::getSourcePages)
            .flatMap(List::stream)
            .filter(sp -> sp.getLanguages().stream().anyMatch(l -> reader.getLanguages().contains(l)))
            .map(SourcePage::getSource)
            .map(s -> {
                SourceDto dto = modelMapper.map(s, SourceDto.class);
                if (reader.getSources().contains(s)) {
                    dto.setPicked(true);
                }
                return dto;
            })
            .collect(Collectors.toSet());

        List<List<SourceDto>> partionedSources = Lists.newArrayList(
            Iterables.partition(
                sources,
                request.getSize()
            ).iterator());
        int page = request.getPage() - 1;
        return new SourceListResponse(
            partionedSources.get(page),
            page == 0,
            partionedSources.size() == request.getPage(),
            modelMapper.map(reader, ReaderDto.class)
        );
    }

    @Override
    public SourceResponse pick(SourceRequest request) {
        return doAction(request, Reader::addSource);
    }

    @Override
    public SourceResponse remove(SourceRequest request) {
        return doAction(request, Reader::removeSource);
    }

    private SourceResponse doAction(SourceRequest request, BiConsumer<Reader, Source> action) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("update:source:reader {} not found", request.getChatId());
            return new SourceResponse(ErrorUtil.readerNotFound());
        }
        Source source = sourceService.find(request.getSourceId()).orElse(null);
        if (source == null) {
            log.error("update:source:{} not found:reader:{}", request.getSourceId(), request.getChatId());
            return new SourceResponse(ErrorUtil.sourceNotFound());
        }
        action.accept(reader, source);
        readerService.save(reader);

        return new SourceResponse(modelMapper.map(reader, ReaderDto.class), modelMapper.map(source, SourceDto.class));
    }
}
