package vlad110kg.news.aggregator.data.ingestor.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vlad110kg.news.aggregator.domain.dto.CategoryLocalisationDto;
import vlad110kg.news.aggregator.facade.IngestionCategoryLocalisationFacade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("localisation")
public class LocalisationIngestionService implements IngestionService {

    @Autowired
    private IngestionCategoryLocalisationFacade ingestLocalisations;

    @Override
    public void ingest(InputStream data) {
        try {
            Workbook wb = WorkbookFactory.create(data);
            Sheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            Row initial = sheet.getRow(0);
            int cellNum = 1;

            Map<Integer, String> languages = new HashMap<>();
            while (true) {
                Cell cell = initial.getCell(cellNum);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue();
                    languages.put(cellNum, cellValue);
                    cellNum++;
                } else {
                    break;
                }
            }

            List<CategoryLocalisationDto> localisations = new ArrayList<>();
            for (int r = 1; r < rows; r++) {
                Row row = sheet.getRow(r);
                String category = row.getCell(0).getStringCellValue();
                for (int c = 1; c < cellNum; c++) {
                    CategoryLocalisationDto categoryLocalisation = new CategoryLocalisationDto();
                    localisations.add(categoryLocalisation);
                    String cellValue = row.getCell(c).getStringCellValue();
                    if (StringUtils.isNotBlank(cellValue)) {
                        String lang = languages.get(c);
                        categoryLocalisation.setCategory(category);
                        categoryLocalisation.setValue(cellValue);
                        categoryLocalisation.setLanguage(lang);
                    }
                }
            }
            ingestLocalisations.ingest(localisations);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}
