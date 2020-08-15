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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SourceFunctionalFacade implements ISourceFunctionalFacade {

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SourceListResponse listAll(ListRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:source:reader {} not found", request.getChatId());
            return new SourceListResponse(ErrorUtil.readerNotFound());
        }

        Set<Source> sources = reader.getCategories().stream()
            .map(Category::getSourcePages)
            .flatMap(List::stream)
            .map(SourcePage::getSource)
            .collect(Collectors.toSet());
        List<List<Source>> sourceList = Lists.newArrayList(
            Iterables.partition(
                sources,
                request.getSize()
            ).iterator());
        int page = request.getPage() - 1;
        List<SourceDto> dtos = sourceList.get(page)
            .stream()
            .map(s -> modelMapper.map(s, SourceDto.class))
            .collect(Collectors.toList());
        return new SourceListResponse(
            dtos,
            page == 0,
            sourceList.size() == page,
            modelMapper.map(reader, ReaderDto.class)
        );
    }

    @Override
    public SourceResponse pick(SourceRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:source:reader {} not found", request.getChatId());
            return new SourceResponse(ErrorUtil.readerNotFound());
        }


        return null;
    }

    @Override
    public SourceResponse remove(SourceRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:source:reader {} not found", request.getChatId());
            return new SourceResponse(ErrorUtil.readerNotFound());
        }

        return null;
    }
}
