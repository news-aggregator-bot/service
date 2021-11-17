package bepicky.service.data.ingestor.service;

import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.ISourceService;
import bepicky.service.service.func.FuncSourceDataIngestor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import picky.test.NatsContainerSupport;
import picky.test.SingletonMySQLContainerSupport;

import java.util.List;
import java.util.Set;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SourceIngestionServiceITCase implements NatsContainerSupport,
    SingletonMySQLContainerSupport {

    @Autowired
    private SourceIngestionService sourceIngestionService;

    @Autowired
    private ISourceService sourceService;

    @Test
    public void ingest_ShouldIngestCorrectData() {
        FuncSourceDataIngestor dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIngestionService)
            .build();
        List<String> sourceNames = dataIngestor.listRealSourceFileNames();

        sourceNames.forEach(dataIngestor::ingestSource);

        List<Source> allSources = sourceService.findAll();
        Assertions.assertEquals(sourceNames.size(), allSources.size());

        Source anySource = allSources.stream().findAny().get();
        Assertions.assertFalse(anySource.isActive());

        List<SourcePage> anyPages = anySource.getPages();
        Assertions.assertFalse(anyPages.isEmpty());

        SourcePage anySourcePage = anyPages.stream().findAny().get();
        Assertions.assertTrue(anySourcePage.isEnabled());
        Assertions.assertNotNull(anySourcePage.getUrl());
        Assertions.assertNotNull(anySourcePage.getUrlNormalisation());
        Assertions.assertNotNull(anySourcePage.getCategories());
        Assertions.assertFalse(anySourcePage.getCategories().isEmpty());
        Assertions.assertNotNull(anySourcePage.getLanguages());
        Assertions.assertFalse(anySourcePage.getLanguages().isEmpty());
        Assertions.assertNull(anySourcePage.getWebReader());

        Set<ContentBlock> anyContentBlocks = anySourcePage.getContentBlocks();
        Assertions.assertFalse(anyContentBlocks.isEmpty());

        ContentBlock anyContentBlock = anyContentBlocks.stream().findAny().get();
        Assertions.assertEquals(anySourcePage, anyContentBlock.getSourcePage());

        Set<ContentTag> anyContentTags = anyContentBlock.getTags();
        Assertions.assertTrue(anyContentTags.size() > 1);
        Assertions.assertNotNull(anyContentBlock.findByType(ContentTagType.MAIN));
    }



}
