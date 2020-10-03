package bepicky.service.configuration;

import bepicky.service.web.reader.BrowserWebPageReader;
import bepicky.service.web.reader.JsoupWebPageReader;
import bepicky.service.web.reader.WebPageReader;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@Slf4j
public class WebPageReaderConfiguration {

    @Value("${na.webpagereader.browser:true}")
    private boolean browserReaderEnabled;

    private FirefoxOptions firefoxOptions() {
        Path drivers = Paths.get(getClass().getResource("/drivers").getPath());
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("webdriver.gecko.driver", drivers.resolve("geckodriver_mac").toString());
        }
        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.gecko.driver", "/opt/driver/geckodriver");
        }
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        FirefoxBinary binary = new FirefoxBinary();
        binary.addCommandLineOptions("--headless", "--no-sandbox");
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(binary);
        return options;
    }

    @Bean
    public List<WebPageReader> webPageReaders() {
        ImmutableList.Builder<WebPageReader> readers = ImmutableList.builder();
        readers.add(new JsoupWebPageReader());

        if (browserReaderEnabled) {
            log.info("webpagereader:browser:enabled");
            readers.add(new BrowserWebPageReader(firefoxOptions()));
        }

        return readers.build();
    }
}
