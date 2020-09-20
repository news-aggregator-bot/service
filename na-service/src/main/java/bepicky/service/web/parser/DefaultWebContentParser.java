package bepicky.service.web.parser;

import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.entity.SourcePage;
import bepicky.service.web.reader.WebPageReaderContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DefaultWebContentParser implements WebContentParser {

    @Autowired
    private WebPageReaderContext readerContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Autowired
    private UrlNormalisationContext urlNormalisationContext;

    @Override
    public List<PageParsedData> parse(SourcePage page) {
        String srcName = page.getSource().getName();
        do {
            Document doc = readerContext.read(srcName, page.getUrl());
            List<PageParsedData> parsedData = page.getContentBlocks()
                .stream()
                .map(block -> parseDoc(page, doc, block))
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            if (!parsedData.isEmpty()) {
                return parsedData;
            }
            readerContext.goNext(srcName);
        } while (readerContext.hasNext(srcName));
        return Collections.emptyList();
    }

    private List<PageParsedData> parseDoc(SourcePage page, Document doc, ContentBlock block) {
        ContentTag mainTag = block.findByType(ContentTagType.MAIN);
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        ContentTag authorTag = block.findByType(ContentTagType.AUTHOR);

        if (mainTag != null) {
            Elements mainClassElems = doc.select(evaluatorFactory.get(mainTag));

            Builder<PageParsedData> datas = ImmutableList.builder();
            for (Element wrapper : mainClassElems) {

                PageParsedData.PageParsedDataBuilder dataBuilder = PageParsedData.builder();
                if (titleTag == null && linkTag != null) {
                    Element linkEl = wrapper.selectFirst(evaluatorFactory.get(linkTag));
                    dataBuilder.title(linkEl.text());
                    dataBuilder.link(getHref(page, linkEl));
                } else if (titleTag != null) {
                    Element titleEl = wrapper.selectFirst(evaluatorFactory.get(titleTag));
                    if (titleEl == null) {
                        continue;
                    }
                    dataBuilder.title(titleEl.text());
                    if (linkTag == null) {
                        Element a = getLinkEl(titleEl, wrapper);
                        if (a == null) {
                            continue;
                        }
                        dataBuilder.link(getHref(page, a));
                    } else {
                        Element linkEl = wrapper.selectFirst(evaluatorFactory.get(linkTag));
                        dataBuilder.link(getHref(page, linkEl));
                    }
                } else {
                    continue;
                }

                dataBuilder.author(getAuthor(authorTag, wrapper));

                datas.add(dataBuilder.build());
            }
            return datas.build();
        }
        return Collections.emptyList();
    }

    private String getHref(SourcePage page, Element a) {
        return urlNormalisationContext.normaliseUrl(page, a);
    }


    private Element getLinkEl(Element titleEl, Element wrapper) {
        Evaluator.Tag linkTag = new Evaluator.Tag("a");
        Element link = titleEl.selectFirst(linkTag);
        return link == null ? wrapper.selectFirst(linkTag) : link;
    }

    private String getAuthor(ContentTag authorTag, Element wrapper) {
        if (authorTag != null) {
            Element element = wrapper.selectFirst(evaluatorFactory.get(authorTag));
            return element == null ? null : element.text();
        }
        return null;
    }

}
