package bepicky.service;

import bepicky.service.entity.SourcePage;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public abstract class FuncSupport {

    protected static WireMockServer wireMockServer = new WireMockServer();

    @BeforeClass
    public static void startWireMock() {
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }


    @AfterClass
    public static void stopWireMock() {
        wireMockServer.stop();
    }

    protected void stub(String path, byte[] pageContent) {
        wireMockServer.stubFor(get(urlEqualTo(path))
            .withHeader("Content-Type", equalTo("*/*"))
            .willReturn(aResponse().withBody(pageContent)));
    }

    protected void stubVerify(String path) {
        wireMockServer.verify(getRequestedFor(urlEqualTo(path)));
    }

    protected String getPath(SourcePage sourcePage) {
        try {
            return new URL(sourcePage.getUrl()).getPath();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
