package vlad110kg.news.aggregator.data.ingestor.service;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vlad110kg.news.aggregator.domain.dto.ContentBlockDto;
import vlad110kg.news.aggregator.domain.dto.ContentTagDto;
import vlad110kg.news.aggregator.domain.dto.SourceDto;
import vlad110kg.news.aggregator.domain.dto.SourcePageDto;
import vlad110kg.news.aggregator.facade.IngestionSourceFacade;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import static vlad110kg.news.aggregator.entity.ContentTagType.AUTHOR;
import static vlad110kg.news.aggregator.entity.ContentTagType.DESCRIPTION;
import static vlad110kg.news.aggregator.entity.ContentTagType.LINK;
import static vlad110kg.news.aggregator.entity.ContentTagType.MAIN;
import static vlad110kg.news.aggregator.entity.ContentTagType.TITLE;

@Service("source")
public class SourceIngestionService implements IngestionService {

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
            .put(4, (v, o) -> ((ContentTagDto) o).setValue(v.trim()))
            .put(5, (v, o) -> ((ContentTagDto) o).setValue(v.trim()))
            .put(6, (v, o) -> ((ContentTagDto) o).setValue(v.trim()))
            .put(7, (v, o) -> ((ContentTagDto) o).setValue(v.trim()))
            .put(8, (v, o) -> ((ContentTagDto) o).setValue(v.trim()))
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
            ioe.printStackTrace();
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
