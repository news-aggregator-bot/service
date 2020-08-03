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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultWebContentParser implements WebContentParser {

    @Autowired
    private WebPageReaderContext readerContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Override
    public List<PageParsedData> parse(SourcePage page) {
        Document doc = readerContext.read(page.getSource().getName(), page.getUrl());
        return page.getContentBlocks()
            .parallelStream()
            .map(block -> parseDoc(page, doc, block))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<PageParsedData> parseDoc(SourcePage page, Document doc, ContentBlock block) {
        ContentTag mainTag = block.findByType(ContentTagType.MAIN);
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        ContentTag descriptionTag = block.findByType(ContentTagType.DESCRIPTION);
        ContentTag authorTag = block.findByType(ContentTagType.AUTHOR);

        if (mainTag != null) {
            Elements mainClassElems = doc.select(evaluatorFactory.get(mainTag));

            Builder<PageParsedData> datas = ImmutableList.builder();
            for (Element wrapper : mainClassElems) {

                Element titleEl = wrapper.selectFirst(evaluatorFactory.get(titleTag));
                if (titleEl == null) {
                    continue;
                }
                PageParsedData.PageParsedDataBuilder dataBuilder = PageParsedData.builder();
                dataBuilder.title(titleEl.text());
                if (linkTag == null) {
                    Element a = getLinkEl(titleEl, wrapper);
                    if (a == null) {
                        continue;
                    }
                    dataBuilder.link(getHref(page.getUrl(), a));
                } else {
                    Element linkEl = wrapper.selectFirst(evaluatorFactory.get(linkTag));
                    dataBuilder.link(getHref(page.getUrl(), linkEl));
                }

                dataBuilder.description(getDescription(descriptionTag, wrapper))
                    .author(getAuthor(authorTag, wrapper));

                datas.add(dataBuilder.build());
            }
            return datas.build();
        }
        return Collections.emptyList();
    }

    private String getHref(String pageUrl, Element a) {
        String href = a.attr("href");
        if (href.contains("?")) {
            href = new StringBuilder(href).delete(href.indexOf("?"), href.length()).toString();
        }
        if (href.startsWith("http")) {
            return href;
        }
        try {
            URL url = new URL(pageUrl);
            return pageUrl.replace(url.getPath(), href);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Element getLinkEl(Element titleEl, Element wrapper) {
        Evaluator.Tag linkTag = new Evaluator.Tag("a");
        Element link = titleEl.selectFirst(linkTag);
        return link == null ? wrapper.selectFirst(linkTag) : link;
    }

    private String getDescription(ContentTag contentTag, Element wrapper) {
        if (contentTag != null) {
            return wrapper.selectFirst(evaluatorFactory.get(contentTag)).text();
        }
        return null;
    }

    private String getAuthor(ContentTag authorTag, Element wrapper) {
        if (authorTag != null) {
            Element element = wrapper.selectFirst(evaluatorFactory.get(authorTag));
            return element == null ? null : element.text();
        }
        return null;
    }

}
