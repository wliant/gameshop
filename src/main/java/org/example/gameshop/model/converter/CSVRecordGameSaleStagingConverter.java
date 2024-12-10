package org.example.gameshop.model.converter;

import org.apache.commons.csv.CSVRecord;
import org.example.gameshop.model.GameSaleStaging;

public interface CSVRecordGameSaleStagingConverter {
    GameSaleStaging convert(CSVRecord csvRecord);
}
