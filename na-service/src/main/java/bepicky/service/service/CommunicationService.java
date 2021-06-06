package bepicky.service.service;

import bepicky.common.domain.request.NotifyMessageRequest;
import bepicky.service.client.NaBotClient;
import bepicky.service.entity.Language;
import bepicky.service.entity.Reader;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommunicationService implements ICommunicationService {

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private NaBotClient naBotClient;

    public void communicate(InputStream data) {
        Map<String, String> messages = parseMessages(data);

        messages.forEach((l, v) -> {
            Language language = languageService.find(l)
                .orElseThrow(() -> new IllegalArgumentException(l + " language doesn't exist"));
            Set<Long> readersChatIds = language.getReaders()
                .stream()
                .filter(Reader::isActive)
                .map(Reader::getChatId)
                .collect(Collectors.toSet());
            if (!readersChatIds.isEmpty()) {
                naBotClient.notifyMessage(new NotifyMessageRequest(readersChatIds, v));
            }
        });
    }

    private Map<String, String> parseMessages(InputStream data) {
        try {
            Workbook wb = WorkbookFactory.create(data);
            ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
            for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {

                Sheet sheet = wb.getSheetAt(sheetNum);
                int rows = sheet.getPhysicalNumberOfRows();

                for (int r = 1; r < rows; r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) {
                        continue;
                    }
                    b.put(row.getCell(1).getStringCellValue(), row.getCell(0).getStringCellValue()) ;
                }
            }
            return b.build();
        } catch (Exception ioe) {
            log.error("Message parsing failed", ioe);
            throw new IllegalArgumentException("Parsing failed");
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                log.error("Unable to close source stream");
            }
        }
    }
}
