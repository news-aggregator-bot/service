package vlad110kg.news.aggregator.data.ingestor.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vlad110kg.news.aggregator.domain.dto.CategoryDto;
import vlad110kg.news.aggregator.facade.IngestionCategoryFacade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service("category")
public class CategoryIngestionService implements IngestionService {

    @Autowired
    private IngestionCategoryFacade facade;

    @Override
    public void ingest(InputStream data) {
        try {
            Workbook wb = WorkbookFactory.create(data);
            Sheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();

            List<CategoryDto> categories = new ArrayList<>();
            for (int r = 1; r < rows; r++) {
                Row row = sheet.getRow(r);
                categories.add(CategoryDto.builder()
                    .name(row.getCell(0).getStringCellValue())
                    .parent(getParent(row))
                    .build());
            }
            facade.ingest(categories);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private String getParent(Row row) {
        Cell cell = row.getCell(1);
        return cell == null ? null : cell.getStringCellValue();
    }
}
