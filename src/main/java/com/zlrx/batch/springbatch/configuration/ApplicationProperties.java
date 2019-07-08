package com.zlrx.batch.springbatch.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private Batch batch;

    @Getter
    @Setter
    public static class Batch {
        private String inputPath;
    }

}
