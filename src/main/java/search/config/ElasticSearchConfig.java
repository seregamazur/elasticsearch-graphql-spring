package search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"search"})
@PropertySource(value = "classpath:elastic.yml")
public class ElasticSearchConfig {

    @Value("${elasticsearchHome}")
    private String elasticsearchHome;

    @Value("${clusterName}")
    private String clusterName;

    @Value("${host}")
    private String host;

    @Value("${port}")
    private int port;

    @Value("${index}")
    private String index;

    public String getElasticsearchHome() {
        return elasticsearchHome;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getIndex() {
        return index;
    }


    @Qualifier(value = "restClient")
    @Bean(destroyMethod = "close")
    public RestHighLevelClient restClient() {

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));

        return new RestHighLevelClient(builder);
    }

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(ElasticSearchConfig.class);
    }

}
