package vlad110kg.news.aggregator.web.reader;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
public class WebPageReaderContextTest {

    private static final String SRC_NAME = "name";
    private static final String SRC_PAGE_URL = "url";

    @Test
    public void read_OneNullReader_ShouldReturnNullDocument() {
        WebPageReaderContext context = context(new NullWebPageReader());

        Document read = context.read(SRC_NAME, SRC_PAGE_URL);

        assertNull(read);
    }

    @Test
    public void read_TwoExceptionAndNullReader_ShouldReturnNullDocument() {
        WebPageReaderContext context = context(new ExceptionThrowerWebPageReader(), new NullWebPageReader());

        Document read = context.read(SRC_NAME, SRC_PAGE_URL);

        assertNull(read);
    }

    @Test(expected = IllegalStateException.class)
    public void read_OneExceptionReader_ShouldThrowISE() {
        WebPageReaderContext context = context(new ExceptionThrowerWebPageReader());

        context.read(SRC_NAME, SRC_PAGE_URL);
    }

    @Test(expected = IllegalStateException.class)
    public void read_TwoExceptionReaders_ShouldThrowISE() {
        WebPageReaderContext context = context(
            new ExceptionThrowerWebPageReader(),
            new ExceptionThrowerWebPageReader()
        );

        context.read(SRC_NAME, SRC_PAGE_URL);
    }

    private WebPageReaderContext context(WebPageReader... readers) {
        return new WebPageReaderContext(Arrays.asList(readers));
    }

    public static class NullWebPageReader implements WebPageReader {

        @Override
        public Document read(String path) {
            return null;
        }
    }

    public static class ExceptionThrowerWebPageReader implements WebPageReader {

        @Override
        public Document read(String path) {
            throw new IllegalStateException("error");
        }
    }
}