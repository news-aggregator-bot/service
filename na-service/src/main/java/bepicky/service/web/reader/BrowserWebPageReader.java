package bepicky.service.web.reader;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.annotation.PreDestroy;

public class BrowserWebPageReader implements WebPageReader {

    private final ChromeDriver driver;

    public BrowserWebPageReader(ChromeDriver driver) {
        this.driver = driver;
    }

    @Override
    public Document read(String path) {
        driver.navigate().to(path);
        return Parser.parse(driver.getPageSource(), path);
    }

    @PreDestroy
    public void quitDriver() {
        driver.close();
        driver.quit();
    }

}