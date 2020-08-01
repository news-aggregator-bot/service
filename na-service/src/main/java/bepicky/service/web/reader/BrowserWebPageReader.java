package bepicky.service.web.reader;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserWebPageReader implements WebPageReader {

    private final FirefoxOptions options;

    public BrowserWebPageReader(FirefoxOptions options) {
        this.options = options;
    }

    @Override
    public Document read(String path) {
        WebDriver driver = new FirefoxDriver(options);
        try {
            driver.navigate().to(path);
            return Parser.parse(driver.getPageSource(), path);
        } finally {
            driver.quit();
        }
    }

}