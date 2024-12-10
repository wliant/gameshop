package org.example.gameshop.model.converter;

import org.apache.commons.csv.CSVRecord;
import org.example.gameshop.model.GameSaleStaging;
import org.example.gameshop.model.GameType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CSVRecordGameSaleStagingConverterImpl implements CSVRecordGameSaleStagingConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public GameSaleStaging convert(CSVRecord csvRecord) {
        GameSaleStaging gameSaleStaging = new GameSaleStaging();

        gameSaleStaging.setGameNumber(Integer.parseInt(csvRecord.get("game_no")));
        gameSaleStaging.setGameName(csvRecord.get("game_name"));
        gameSaleStaging.setGameCode(csvRecord.get("game_code"));
        gameSaleStaging.setGameType(GameType.fromCode(Integer.parseInt(csvRecord.get("type"))));
        gameSaleStaging.setCostPrice(new BigDecimal(csvRecord.get("cost_price")));
        LocalDate dateOfSale = LocalDate.parse(csvRecord.get("date_of_sale"), formatter);
        gameSaleStaging.setDateOfSale(dateOfSale);

        return gameSaleStaging;
    }
}
