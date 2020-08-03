package bepicky.service.web.reader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.List;

@Slf4j
public class WebPageReaderContext {

    private final List<WebPageReader> webPageReaders;

    private Multimap<String, WebPageReader> sourceReaders;

    public WebPageReaderContext(List<WebPageReader> webPageReaders) {
        this.webPageReaders = webPageReaders;
        sourceReaders = ArrayListMultimap.create();
    }

    public Document read(String sourceName, String url) {
        WebPageReader webPageReader = get(sourceName);
        try {
            log.info("webpagereader:read:{}:{}", url, webPageReader.getClass().getSimpleName());
            return webPageReader.read(url);
        } catch (RuntimeException e) {
            log.warn("read:source:failed:{}:{}:{}", sourceName, url, webPageReader.getClass().getSimpleName());
            boolean removed = sourceReaders.remove(sourceName, webPageReader);
            if (removed && sourceReaders.containsKey(sourceName)) {
                log.info("webpagereader:removed:{}", webPageReader.getClass().getSimpleName());
                return read(sourceName, url);
            }
            throw new IllegalStateException("Unable to find a reader to read: " + url, e);
        }
    }

    public WebPageReader get(String sourceName) {
        Collection<WebPageReader> readers = sourceReaders.get(sourceName);
        if (readers == null || readers.isEmpty()) {
            sourceReaders.putAll(sourceName, webPageReaders);
            return webPageReaders.get(0);
        }
        return readers.stream().findFirst().get();
    }

    @Scheduled(cron = "${na.schedule.webpagereader.cron:0 0 */12 * * *}")
    public void refreshReaders() {
        log.info("webpagereader:refresh");
        sourceReaders = ArrayListMultimap.create();
    }
}
