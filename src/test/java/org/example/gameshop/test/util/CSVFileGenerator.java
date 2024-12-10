package org.example.gameshop.test.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class CSVFileGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String[] HEADERS = {"game_no", "game_name", "game_code", "type", "cost_price", "date_of_sale"};


    public void generateCSVFile(Path path, int size) throws IOException {
        Files.createDirectories(path.getParent());

        LocalDate startDate = LocalDate.of(2024, 4, 1);
        LocalDate endDate = LocalDate.of(2024, 4, 30);

        try (FileWriter writer = new FileWriter(path.toFile());
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(HEADERS))) {

            for (int i = 0; i < size; i++) {
                csvPrinter.printRecord(createCSVRecord(startDate, endDate));
            }
        }
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    private Object[] createCSVRecord(LocalDate startDate, LocalDate endDate) {
        int gameNo = (int) (Math.random() * 100) + 1;
        String gameName = generateRandomString(20);

        String gameCode = generateRandomString(5);
        int type = (int) (Math.random() * 2) + 1;
        BigDecimal costPrice = BigDecimal.valueOf(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);

        long randomDay = ThreadLocalRandom.current().nextLong(startDate.toEpochDay(), endDate.toEpochDay() + 1);
        String dateOfSale = LocalDate.ofEpochDay(randomDay).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        return new Object[]{gameNo, gameName, gameCode, type, costPrice, dateOfSale};
    }

    public static void main(String[] args) {
        CSVFileGenerator generator = new CSVFileGenerator();
        try {
            Path path = Paths.get("src/test/resources").resolve("game_sales1m.csv");
            generator.generateCSVFile(path, 1_000_000);
            System.out.println("CSV file generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}