package bepicky.service.service;

import picky.data.reader.dto.ParsedSourcePageDto;

public interface INewsService {

    void handleParsed(ParsedSourcePageDto sourcePage);

}
