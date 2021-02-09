package bepicky.service.configuration;

import bepicky.service.web.reader.BrowserWebPageReader;
import bepicky.service.web.reader.JsoupWebPageReader;
import bepicky.service.web.reader.WebPageReader;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebPageReaderConfiguration {

    @Value("${na.webpagereader.browser:true}")
    private boolean browserReaderEnabled;

    @Value("${na.webpagereader.timeout:20}")
    private int browserReadTimeout;

    public ChromeDriver chromeDriver() {
        if (SystemUtils.IS_OS_MAC) {
            Path drivers = Paths.get(getClass().getResource("/drivers").getPath());
            System.setProperty("webdriver.chrome.driver", drivers.resolve("chromedriver_mac").toString());
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.setAcceptInsecureCerts(true);
        options.setHeadless(true);
        ChromeDriver chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().timeouts().pageLoadTimeout(browserReadTimeout, TimeUnit.SECONDS);
        return chromeDriver;
    }

    @Bean
    public List<WebPageReader> webPageReaders() {
        ImmutableList.Builder<WebPageReader> readers = ImmutableList.builder();
        readers.add(new JsoupWebPageReader());

        if (browserReaderEnabled) {
            log.info("webpagereader:browser:enabled");
            readers.add(new BrowserWebPageReader(chromeDriver()));
        }

        return readers.build();
    }
}
