package bepicky.service.data.ingestor.service;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import bepicky.service.domain.dto.ContentBlockDto;
import bepicky.service.domain.dto.ContentTagDto;
import bepicky.service.domain.dto.SourceDto;
import bepicky.service.domain.dto.SourcePageDto;
import bepicky.service.entity.ContentTagMatchStrategy;
import bepicky.service.facade.IngestionSourceFacade;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import static bepicky.service.entity.ContentTagType.AUTHOR;
import static bepicky.service.entity.ContentTagType.DESCRIPTION;
import static bepicky.service.entity.ContentTagType.LINK;
import static bepicky.service.entity.ContentTagType.MAIN;
import static bepicky.service.entity.ContentTagType.TITLE;

@Service("source")
@Slf4j
public class SourceIngestionService implements IngestionService {

    private static final IngestionConsumer ingestionConsumer = (v, o) -> {
        String[] info = v.trim().split(":");
        if (info.length < 2) {
            log.error(v + " is not well formatted");
        }
        String value = info[0];
        String matchStrategy = info[1];
        ((ContentTagDto) o).setValue(value);
        ((ContentTagDto) o).setMatchStrategy(ContentTagMatchStrategy.valueOf(matchStrategy));
    };

    private final Map<Integer, Supplier<Object>> entityMapping =
        ImmutableMap.<Integer, Supplier<Object>>builder()
            .put(0, SourcePageDto::new)
            .put(4, () -> new ContentTagDto(MAIN))
            .put(5, () -> new ContentTagDto(TITLE))
            .put(6, () -> new ContentTagDto(LINK))
            .put(7, () -> new ContentTagDto(DESCRIPTION))
            .put(8, () -> new ContentTagDto(AUTHOR))
            .build();

    private final Map<Integer, IngestionConsumer> fieldMapping =
        ImmutableMap.<Integer, IngestionConsumer>builder()
            .put(0, (v, o) -> ((SourcePageDto) o).setName(v.trim()))
            .put(1, (v, o) -> ((SourcePageDto) o).setUrl(v.trim()))
            .put(2, (v, o) -> ((SourcePageDto) o).setCategories(Arrays.asList(v.split(","))))
            .put(3, (v, o) -> ((SourcePageDto) o).setLanguage(v.trim()))
            .put(4, ingestionConsumer)
            .put(5, ingestionConsumer)
            .put(6, ingestionConsumer)
            .put(7, ingestionConsumer)
            .put(8, ingestionConsumer)
            .build();

    @Autowired
    private IngestionSourceFacade sourceFacade;

    @Override
    public void ingest(InputStream data) {
        try {
            Workbook wb = WorkbookFactory.create(data);
            for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
                SourceDto source = new SourceDto();
                source.setName(wb.getSheetName(sheetNum));

                Sheet sheet = wb.getSheetAt(sheetNum);
                int rows = sheet.getPhysicalNumberOfRows();
                parseSourcePages(source, sheet, rows);
                sourceFacade.ingest(source);
            }

        } catch (Exception ioe) {
            log.error("Unable to ingest sources", ioe);
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                log.error("Unable to close source stream");
            }
        }
    }

    private void parseSourcePages(SourceDto source, Sheet sheet, int rows) {
        SourcePageDto srcPage = null;
        for (int r = 1; r < rows; r++) {
            Row row = sheet.getRow(r);
            ContentBlockDto block = new ContentBlockDto();
            for (Map.Entry<Integer, IngestionConsumer> e : fieldMapping.entrySet()) {
                Cell cell = row.getCell(e.getKey());

                if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    String cellValue = cell.getStringCellValue();
                    ContentTagDto contentTag = null;
                    Supplier<Object> entitySupplier = entityMapping.get(e.getKey());
                    if (entitySupplier != null) {
                        Object entity = entitySupplier.get();

                        if (entity instanceof SourcePageDto) {
                            srcPage = (SourcePageDto) entity;
                            source.addPage(srcPage);
                        } else {
                            contentTag = (ContentTagDto) entity;
                            block.add(contentTag);
                        }
                    }

                    if (e.getKey() < 4) {
                        e.getValue().consume(cellValue, srcPage);
                    } else {
                        e.getValue().consume(cellValue, contentTag);
                    }
                }
            }
            if (!block.getContentTags().isEmpty()) {
                srcPage.addBlock(block);
            }
        }
    }
}
