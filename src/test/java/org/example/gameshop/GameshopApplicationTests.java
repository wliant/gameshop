package org.example.gameshop;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.core.ConditionTimeoutException;
import org.example.gameshop.model.FileImport;
import org.example.gameshop.repository.GameSaleRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class GameshopApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(GameshopApplicationTests.class);

    private static final String TEST_FILE_NAME = "game_sales1k.csv";

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameSaleRepository gameSaleRepository;


    @Test
    void simpleSmokeTest() {
        logger.info("Starting testImportGameSales...");

        testImport();
        testGetGameSales();
        testGetTotalSales();

        logger.info("testImportGameSales completed successfully.");
    }

    private void testImport() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource(TEST_FILE_NAME));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<FileImport> response = restTemplate.postForEntity("/import", requestEntity, FileImport.class);
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response should be successful");

        long startTime = System.currentTimeMillis();

        try {
            Awaitility.await().atMost(1, TimeUnit.MINUTES)
                    .conditionEvaluationListener(new ConditionEvaluationLogger(logger::info))
                    .until(() -> gameSaleRepository.count() > 0);
        } catch (ConditionTimeoutException e) {
            logger.error("Condition not met within the timeout", e);
        }

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        logger.info("Time taken for condition to be met: {} ms", timeTaken);

        assertTrue(gameSaleRepository.count() > 0, "Game sales should be imported");
    }

    private void testGetGameSales() {
        String filterByDateRange = "from=2023-01-01&to=2023-12-31";
        String filterByPriceRange = "minPrice=10.0&maxPrice=50.0";


        ResponseEntity<String> responseByDateRange = restTemplate.getForEntity("/getGameSales?" + filterByDateRange, String.class);
        assertTrue(responseByDateRange.getStatusCode().is2xxSuccessful(), "Response should be successful for filter by date range");
        logger.info("Response for filter by date range: {}", responseByDateRange.getBody());


        ResponseEntity<String> responseByPriceRange = restTemplate.getForEntity("/getGameSales?" + filterByPriceRange, String.class);
        assertTrue(responseByPriceRange.getStatusCode().is2xxSuccessful(), "Response should be successful for filter by price range");
        logger.info("Response for filter by price range: {}", responseByPriceRange.getBody());
    }

    private void testGetTotalSales() {
        String filterByDateRange = "from=2023-01-01&to=2023-12-31";
        String filterByGameNo = "gameNo=1";

        ResponseEntity<String> responseByDateRange = restTemplate.getForEntity("/getTotalSales?" + filterByDateRange, String.class);
        assertTrue(responseByDateRange.getStatusCode().is2xxSuccessful(), "Response should be successful for filter by date range");
        logger.info("Response for filter by date range: {}", responseByDateRange.getBody());

        ResponseEntity<String> responseByGameNo = restTemplate.getForEntity("/getTotalSales?" + filterByGameNo, String.class);
        assertTrue(responseByGameNo.getStatusCode().is2xxSuccessful(), "Response should be successful for filter by game no");
        logger.info("Response for filter by game no: {}", responseByGameNo.getBody());
    }

}
