package vlad110kg.news.aggregator.web.reader;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import com.google.common.io.Resources;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

//@Service("browserReader")
public class BrowserWebPageReader implements WebPageReader {

    @PostConstruct
    public void setBrowserDrivers() throws URISyntaxException {
        Path drivers = Paths.get(Resources.getResource("drivers").toURI());
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("webdriver.chrome.driver", drivers.resolve("chromedriver_mac").toString());
            System.setProperty("webdriver.gecko.driver", drivers.resolve("geckodriver_mac").toString());
        }
        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.chrome.driver", drivers.resolve("chromedriver_linux").toString());
            System.setProperty("webdriver.gecko.driver", drivers.resolve("geckodriver_linux").toString());
        }
    }

    @Override
    public String read(String path) {
        WebDriver driver = new ChromeDriver();
        driver.navigate().to(path);
        String html = driver.findElement(By.tagName("html")).getText();
        driver.quit();
        return html;
    }
}
