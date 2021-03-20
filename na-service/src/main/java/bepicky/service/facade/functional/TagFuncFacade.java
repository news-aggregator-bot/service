package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.TagDto;
import bepicky.common.domain.response.SubscribeTagResponse;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Tag;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ITagService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public SubscribeTagResponse subscribe(Long chatId, String value) {
        Reader r = readerService.findById(chatId).orElse(null);
        if (r == null) {
            log.error("tag:subscribe:failed:reader:404");
            return new SubscribeTagResponse(ErrorUtil.readerNotFound());
        }
        Tag tag = tagService.get(value);
        tag.addReader(r);
        log.info("tag:subscribe:{}:reader:{}", value, r.getChatId());
        Tag saved = tagService.save(tag);
        return new SubscribeTagResponse(modelMapper.map(r, ReaderDto.class), modelMapper.map(saved, TagDto.class));
    }
}
