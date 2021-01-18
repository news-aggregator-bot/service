package bepicky.service.listener.application;

import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class AppReadyCategoryIngestor implements ApplicationListener<ApplicationReadyEvent> {

    private static final String LANG_SRC = "data/lang/Languages.xlsx";
    private static final String CATEGORY_SRC = "data/category/Categories.xlsx";

    @Autowired
    private LanguageIngestionService langIngestionService;

    @Autowired
    private CategoryIngestionService categoryIngestionService;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent ignore) {
        langIngestionService.ingest(openStream(LANG_SRC));
        categoryIngestionService.ingest(openStream(CATEGORY_SRC));
    }

    private InputStream openStream(String src) {
        try {
            return Resources.getResource(src).openStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
