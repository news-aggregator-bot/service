package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.SourceDto;
import bepicky.common.domain.dto.TagDto;
import bepicky.common.domain.response.SourceListResponse;
import bepicky.common.domain.response.TagListResponse;
import bepicky.common.domain.response.TagResponse;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Tag;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ITagService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class TagFuncFacade implements ITagFuncFacade {

    @Autowired
    private ITagService tagService;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TagResponse subscribe(Long chatId, String value) {
        Reader r = readerService.findByChatId(chatId).orElse(null);
        if (r == null) {
            log.error("tag:subscribe:failed:reader:404");
            return new TagResponse(ErrorUtil.readerNotFound());
        }
        Tag tag = tagService.get(value);
        tag.addReader(r);
        log.info("tag:subscribe:{}:reader:{}", value, r.getChatId());
        Tag saved = tagService.save(tag);
        return new TagResponse(modelMapper.map(r, ReaderDto.class), modelMapper.map(saved, TagDto.class));
    }

    @Override
    public TagResponse unsubscribe(Long chatId, Long tagId) {
        Reader r = readerService.findByChatId(chatId).orElse(null);
        if (r == null) {
            log.error("tag:unsubscribe:failed:reader:404:{}", chatId);
            return new TagResponse(ErrorUtil.readerNotFound());
        }
        Tag tag = tagService.findById(tagId).orElse(null);
        if (tag == null) {
            log.error("tag:unsubscribe:failed:tag:404:{}", tagId);
            return new TagResponse(ErrorUtil.tagNotFound());
        }

        tag.rmReader(r);
        log.info("tag:unsubscribe:{}:reader:{}", tag, r.getChatId());
        Tag saved = tagService.save(tag);
        return new TagResponse(modelMapper.map(r, ReaderDto.class), modelMapper.map(saved, TagDto.class));

    }

    @Override
    public TagListResponse search(String value) {
        return new TagListResponse(
            tagService.findByValue(value).stream().map(t -> modelMapper.map(t, TagDto.class)).collect(Collectors.toList())
        );
    }
}
