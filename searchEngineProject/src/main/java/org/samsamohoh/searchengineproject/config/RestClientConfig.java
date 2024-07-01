package org.samsamohoh.searchengineproject.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.samsamohoh.searchengineproject.repository")
public class RestClientConfig extends AbstractOpenSearchConfiguration {

    @Value("${aws.open-search.region}")
    private String region;

    @Value("${aws.open-search.endpoint}")
    private String endpoint;

    @Value("${aws.open-search.id}")
    private String id;

    @Value("${aws.open-search.pwd}")
    private String pwd;

    private int port = 443;
    private String protocol = "https";

    private final AWSCredentialsProvider awsCredentialsProvider;

    @Autowired
    public RestClientConfig(AWSCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(id, pwd));

        RestClientBuilder builder = RestClient.builder(
                        new HttpHost(endpoint, port, protocol))
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(builder);
    }

    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(Arrays.asList(new DateToStringConverter(), new StringToDateConverter()));
    }

    @ReadingConverter
    static class StringToDateConverter implements org.springframework.core.convert.converter.Converter<String, Date> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        @Override
        public Date convert(String source) {
            return Date.from(OffsetDateTime.parse(source, formatter).toInstant());
        }
    }

    @WritingConverter
    static class DateToStringConverter implements org.springframework.core.convert.converter.Converter<Date, String> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        @Override
        public String convert(Date source) {
            return formatter.format(source.toInstant().atOffset(ZoneOffset.UTC));
        }
    }
}
