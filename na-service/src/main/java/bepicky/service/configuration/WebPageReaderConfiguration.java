package bepicky.service.configuration;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import bepicky.service.web.reader.BrowserWebPageReader;
import bepicky.service.web.reader.JsoupWebPageReader;
import bepicky.service.web.reader.WebPageReader;
import bepicky.service.web.reader.WebPageReaderContext;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebPageReaderConfiguration {

    @Bean
    public FirefoxOptions firefoxOptions() {
        Path drivers = Paths.get(getClass().getResource("/drivers").getPath());
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("webdriver.gecko.driver", drivers.resolve("geckodriver_mac").toString());
        }
        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.gecko.driver", drivers.resolve("geckodriver_linux").toString());
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
    public WebPageReaderContext webPageReaderContext(FirefoxOptions firefoxOptions) {
        ImmutableList<WebPageReader> readers = ImmutableList.of(
            new JsoupWebPageReader(),
            new BrowserWebPageReader(firefoxOptions)
        );
        return new WebPageReaderContext(readers);
    }
}
