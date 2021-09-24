package bepicky.service.data.ingestor.service;

import bepicky.service.data.ingestor.exception.DataIngestionException;
import bepicky.service.dto.LanguageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import bepicky.service.facade.IngestionLanguageFacade;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LanguageIngestionService implements IngestionService {

    @Autowired
    private IngestionLanguageFacade facade;

    @Override
    public void ingest(InputStream data) {
        try {
            Workbook wb = WorkbookFactory.create(data);
            Sheet sheet = wb.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();

            List<LanguageDto> languages = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                Row row = sheet.getRow(r);
                languages.add(LanguageDto.builder()
                    .lang(row.getCell(0).getStringCellValue())
                    .name(row.getCell(1).getStringCellValue())
                    .localised(row.getCell(2).getStringCellValue())
                    .build());
            }
            facade.ingest(languages);
        } catch (Exception ioe) {
            throw new DataIngestionException("Failed to ingest languages", ioe);
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                log.error("Unable to close language stream");
            }
        }
    }
}
