package org.example.gameshop.config;

import org.apache.commons.csv.CSVFormat;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "import")
public class ImportConfigurationProperties {
    private String uploadPath = "work";
    private String csvDelimiter = ",";
    private String csvHeaders = "game_no,game_name,game_code,type,cost_price,date_of_sale";
    private boolean csvSkipHeaders = false;
    private int chunkSize = 5000;
    private int threadPoolSize = 100;
    private int timeoutSecond = 300;
    private int timeoutFetchSize = 10;
    private int maxRetry = 3;
    private int retryDelay = 300;
    private int retryFetchSize = 5;
    private int executorCorePoolSize = 5;
    private int executorMaxPoolSize = 10;
    private int executorQueueCapacity = 20;

    public int getExecutorCorePoolSize() {
        return executorCorePoolSize;
    }

    public void setExecutorCorePoolSize(int executorCorePoolSize) {
        this.executorCorePoolSize = executorCorePoolSize;
    }

    public int getExecutorMaxPoolSize() {
        return executorMaxPoolSize;
    }

    public void setExecutorMaxPoolSize(int executorMaxPoolSize) {
        this.executorMaxPoolSize = executorMaxPoolSize;
    }

    public int getExecutorQueueCapacity() {
        return executorQueueCapacity;
    }

    public void setExecutorQueueCapacity(int executorQueueCapacity) {
        this.executorQueueCapacity = executorQueueCapacity;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    public String getCsvHeaders() {
        return csvHeaders;
    }

    public boolean isCsvSkipHeaders() {
        return csvSkipHeaders;
    }

    public int getTimeoutSecond() {
        return timeoutSecond;
    }

    public void setTimeoutSecond(int timeoutSecond) {
        this.timeoutSecond = timeoutSecond;
    }

    public int getTimeoutFetchSize() {
        return timeoutFetchSize;
    }

    public void setTimeoutFetchSize(int timeoutFetchSize) {
        this.timeoutFetchSize = timeoutFetchSize;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public int getRetryFetchSize() {
        return retryFetchSize;
    }

    public void setRetryFetchSize(int retryFetchSize) {
        this.retryFetchSize = retryFetchSize;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public void setCsvDelimiter(String csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
    }

    public void setCsvHeaders(String csvHeaders) {
        this.csvHeaders = csvHeaders;
    }

    public void setCsvSkipHeaders(boolean csvSkipHeaders) {
        this.csvSkipHeaders = csvSkipHeaders;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public CSVFormat csvFormat() {
        CSVFormat.Builder formatBuilder = CSVFormat.Builder.create()
                .setDelimiter(csvDelimiter);

        if (StringUtils.hasText(csvHeaders)) {
            formatBuilder = formatBuilder
                    .setHeader(StringUtils.commaDelimitedListToStringArray(csvHeaders));
        }
        return formatBuilder.setSkipHeaderRecord(csvSkipHeaders).build().withFirstRecordAsHeader();
    }
}
