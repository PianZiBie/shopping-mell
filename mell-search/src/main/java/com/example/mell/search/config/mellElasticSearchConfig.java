package com.example.mell.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class mellElasticSearchConfig {
    public static final RequestOptions COMM_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMM_OPTIONS=builder.build();
    }
    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("43.139.59.161", 9200, "http"))
        );
        return restHighLevelClient;
    }
}
