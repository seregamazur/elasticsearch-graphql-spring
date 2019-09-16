package search.service;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import search.config.ElasticSearchConfig;
import search.model.UserClickEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService,
        GraphQLQueryResolver {

    private RestHighLevelClient client;
    private ElasticSearchConfig config;
    private ObjectMapper mapper;
    private Logger LOGGER;

    @Autowired
    public ElasticSearchServiceImpl(
            @Qualifier("restClient") RestHighLevelClient client,
            ElasticSearchConfig config, ObjectMapper mapper, Logger logger) {
        this.client = client;
        this.config = config;
        this.mapper = mapper;
        this.LOGGER = logger;
    }

    /**
     * Find user clicks for previous day
     */
    @Override
    public List<UserClickEvent> usersQuantityForYesterday() {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_YESTERDAY);
    }

    /**
     * Find user clicks for today's date
     */
    @Override
    public List<UserClickEvent> usersQuantityForToday() {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_TODAY);
    }

    /**
     * Find user clicks for 7 days including today's date
     */
    @Override
    public List<UserClickEvent> usersQuantityForWeek() {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_WEEK);
    }

    /**
     * Find user clicks for month not including today's date
     */
    @Override
    public List<UserClickEvent> usersQuantityForMonth() {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_MONTH);
    }

    /**
     * @param country Find user clicks for previous day from country
     */
    @Override
    public List<UserClickEvent> yesterdayUsersCountry(String country) {
        return getUserClickEvents(CountryQueries.FIND_USER_CLICK_EVENTS_COUNTRY_FOR_YESTERDAY(country));
    }

    /**
     * @param browser Find user clicks for previous day using browser
     */
    @Override
    public List<UserClickEvent> yesterdayUsersBrowser(String browser) {
        return getUserClickEvents(BrowserQueries.FIND_USER_CLICK_EVENTS_BROWSER_FOR_YESTERDAY(
                browser));
    }

    private List<UserClickEvent> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();
        List<UserClickEvent> profileDocuments = new ArrayList<>();

        for (SearchHit hit : searchHit) {
            hit.getSourceAsMap();
            profileDocuments
                    .add(mapper
                            .convertValue(hit
                                    .getSourceAsMap(), UserClickEvent.class));
        }

        return profileDocuments;
    }

    private List<UserClickEvent> getUserClickEvents(QueryBuilder queryBuilder) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(config.getIndex());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(queryBuilder);

        searchRequest.source(searchSourceBuilder);

        try {
            return getSearchResult(client.search(searchRequest, RequestOptions.DEFAULT));
        } catch (IOException e) {
            LOGGER.error("An exception occurred while getting user clicks." + e);
            return Collections.emptyList();
        }
    }

    private static class RangeQueries {

        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_YESTERDAY =
                QueryBuilders
                        .rangeQuery("date")
                        .gte("now-1d/d")
                        .lt("now/d");

        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_TODAY =
                QueryBuilders
                        .rangeQuery("date")
                        .gte("now/d")
                        .lt("now");

        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_WEEK =
                QueryBuilders
                        .rangeQuery("date")
                        .from(LocalDateTime.now().minusWeeks(1))
                        .to(LocalDateTime.now());

        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_MONTH =
                QueryBuilders
                        .rangeQuery("date")
                        .gte(LocalDateTime.now().minusMonths(1))
                        .to(LocalDateTime.now());
    }

    private static class BrowserQueries {

        static BoolQueryBuilder FIND_USER_CLICK_EVENTS_BROWSER_FOR_YESTERDAY(String browser) {
            return QueryBuilders.boolQuery().filter(QueryBuilders
                    .matchQuery("browser", browser))
                    .filter(QueryBuilders.rangeQuery("date")
                            .gte("now-1d/d")
                            .lt("now/d"));
        }
    }

    private static class CountryQueries {

        static BoolQueryBuilder FIND_USER_CLICK_EVENTS_COUNTRY_FOR_YESTERDAY(String country) {
            return QueryBuilders.boolQuery().filter(QueryBuilders
                    .matchQuery("country", country))
                    .filter(QueryBuilders.rangeQuery("date")
                            .gte("now-1d/d")
                            .lt("now/d"));
        }
    }

}
