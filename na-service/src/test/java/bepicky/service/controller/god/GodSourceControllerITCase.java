package bepicky.service.controller.god;

import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.func.FuncSourceDataIngestor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import picky.test.MySQLContainerSupport;
import picky.test.NatsContainerSupport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GodSourceControllerITCase implements NatsContainerSupport, MySQLContainerSupport {

    @Autowired
    private GodSourceController godSourceController;

    @Autowired
    private SourceIngestionService sourceIngestionService;

    private FuncSourceDataIngestor dataIngestor;

    @Test
    @Order(1)
    public void listSources_ShouldReturn2() {
        dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIngestionService)
            .build();
        dataIngestor.listRealSourceFileNames().subList(0, 2).forEach(dataIngestor::ingestSource);

        List<Source> sources = godSourceController.listSources();

        assertEquals(2, sources.size());
    }

    @Test
    public void listSourcePages_ShouldReturnAtLeast1SourcePage() {
        Source source = godSourceController.listSources().get(0);

        List<SourcePage> sourcePages = godSourceController.listSourcePages(source.getId());
        assertFalse(sourcePages.isEmpty());
    }

    @Test
    public void listSourcePages_WhenSourceNotFound_ShouldThrowAnException() {
        IllegalArgumentException iae = assertThrows(
            IllegalArgumentException.class,
            () -> godSourceController.listSourcePages(-1L)
        );
        assertEquals("source not found", iae.getMessage());
    }

    @Test
    public void getSourcePage_WhenSourceAndSourcePageExist_ShouldExpectedSourcePage() {
        Source source = godSourceController.listSources().get(0);
        SourcePage sourcePage = source.getPages().get(0);

        SourcePage actualSourcePage = godSourceController.getSourcePage(
            source.getId(),
            sourcePage.getId()
        );
        assertEquals(sourcePage, actualSourcePage);
    }

    @Test
    public void getSourcePage_WhenSourceNotFound_ShouldThrowAnException() {
        IllegalArgumentException iae = assertThrows(
            IllegalArgumentException.class,
            () -> godSourceController.getSourcePage(-1L, 0L)
        );
        assertEquals("source not found", iae.getMessage());
    }

    @Test
    public void listSourcePages_WhenSourcePageNotFound_ShouldThrowAnException() {
        Source source = godSourceController.listSources().get(0);

        IllegalArgumentException iae = assertThrows(
            IllegalArgumentException.class,
            () -> godSourceController.getSourcePage(source.getId(), -1L)
        );
        assertEquals("source page not found", iae.getMessage());
    }

    @Test
    public void getSourcePage_WhenSourcePageDoesntBelongsToSource_ShouldThrowAnException() {
        List<Source> sources = godSourceController.listSources();
        Source source1 = sources.get(0);
        Source source2 = sources.get(1);
        SourcePage s2SourcePage = source2.getPages().get(0);

        IllegalArgumentException iae = assertThrows(
            IllegalArgumentException.class,
            () -> godSourceController.getSourcePage(
                source1.getId(),
                s2SourcePage.getId()
            )
        );

        assertEquals("source page doesn't belong to the source", iae.getMessage());
    }
}