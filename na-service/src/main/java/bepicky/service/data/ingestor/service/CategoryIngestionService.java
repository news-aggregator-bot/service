package bepicky.service.data.ingestor.service;

import bepicky.service.data.ingestor.exception.DataIngestionException;
import bepicky.service.dto.CategoryDto;
import bepicky.service.dto.Ids;
import bepicky.service.dto.LocalisationDto;
import bepicky.service.entity.CategoryType;
import bepicky.service.facade.IngestionCategoryFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service("category")
@Slf4j
public class CategoryIngestionService implements IngestionService {

    private static final int LANGUAGE_START_CELL = 2;

    @Autowired
    private IngestionCategoryFacade facade;

    @Override
    public void ingest(InputStream data) {
        try {
            Workbook wb = WorkbookFactory.create(data);
            for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
                Sheet sheet = wb.getSheetAt(sheetNum);
                int rows = sheet.getPhysicalNumberOfRows();
                Row initial = sheet.getRow(0);

                List<String> languages = getLanguages(initial);

                List<CategoryDto> categories = new ArrayList<>();
                for (int r = 1; r < rows; r++) {
                    Row row = sheet.getRow(r);
                    if (row.getCell(0) == null) {
                        continue;
                    }

                    String category = row.getCell(0).getStringCellValue();
                    if (StringUtils.isBlank(category)) {
                        continue;
                    }
                    List<LocalisationDto> localisations = getLocalisations(
                        languages,
                        row,
                        category
                    );

                    categories.add(CategoryDto.builder()
                        .name(row.getCell(0).getStringCellValue().toLowerCase())
                        .type(CategoryType.valueOf(wb.getSheetName(sheetNum)))
                        .parent(getParent(row))
                        .localisations(localisations)
                        .build());
                }
                facade.ingest(categories);
            }
        } catch (Exception ioe) {
            throw new DataIngestionException("Failed to ingest categories", ioe);
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                log.error("Unable to close source stream");
            }
        }
    }

    private List<LocalisationDto> getLocalisations(
        List<String> languages,
        Row row,
        String category
    ) {
        List<LocalisationDto> localisations = new ArrayList<>();
        for (int c = LANGUAGE_START_CELL; c < languages.size() + LANGUAGE_START_CELL; c++) {
            LocalisationDto localisation = new LocalisationDto();
            localisations.add(localisation);
            String cellValue = row.getCell(c).getStringCellValue();
            if (StringUtils.isNotBlank(cellValue)) {
                String lang = languages.get(c - LANGUAGE_START_CELL);
                localisation.setCategory(category.toLowerCase());
                localisation.setValue(cellValue);
                localisation.setLanguage(lang);
            }
        }
        return localisations;
    }

    private List<String> getLanguages(Row initial) {
        List<String> languages = new ArrayList<>();
        for (int i = LANGUAGE_START_CELL; i < 100; i++) {
            Cell cell = initial.getCell(i);
            if (cell != null) {
                String cellValue = cell.getStringCellValue();
                languages.add(cellValue);
            } else {
                break;
            }
        }
        return languages;
    }

    private String getParent(Row row) {
        Cell cell = row.getCell(1);
        return cell == null ? null : cell.getStringCellValue();
    }
}
