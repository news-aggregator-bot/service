package bepicky.service.web.reader;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class HtmlUnitWebPageReader implements WebPageReader {

    private final WebClient client;

    public HtmlUnitWebPageReader() {
        client = new WebClient();
        client.setJavaScriptTimeout(0);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        client.getOptions().setTimeout(0);
        client.getOptions().setDownloadImages(false);
        client.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScriptStartingBefore(200);
        client.waitForBackgroundJavaScript(20000);
    }

    @Override
    public Document read(String path) {

        try {
            HtmlPage page = client.getPage(path);
            return Parser.parse(page.asXml(), path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @PreDestroy
    public void closeClient() {
        client.close();
    }

}