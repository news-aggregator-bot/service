package bepicky.service.web.reader;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

@Slf4j
public class BrowserWebPageReader implements WebPageReader {

    private final FirefoxOptions options;

    public BrowserWebPageReader(FirefoxOptions options) {
        this.options = options;
    }

    @Override
    public Document read(String path) {
        WebDriver driver = null;
        try {
            driver = new FirefoxDriver(options);
            driver.navigate().to(path);
            log.info("webpagereader:browser:read:{}", path);
            return Parser.parse(driver.getPageSource(), path);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}