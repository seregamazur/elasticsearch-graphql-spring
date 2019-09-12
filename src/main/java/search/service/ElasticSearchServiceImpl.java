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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import search.config.ElasticSearchConfig;
import search.model.UserClickEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService,
        GraphQLQueryResolver {

    private RestHighLevelClient client;
    private ElasticSearchConfig config;
    private ObjectMapper mapper;

    @Autowired
    public ElasticSearchServiceImpl(
            @Qualifier("restClient") RestHighLevelClient client,
            ElasticSearchConfig config, ObjectMapper mapper) {
        this.client = client;
        this.config = config;
        this.mapper = mapper;
    }

    @Override
    public List<UserClickEvent> usersQuantityForYesterday() throws IOException {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_YESTERDAY);
    }

    @Override
    public List<UserClickEvent> usersQuantityForToday() throws IOException {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_TODAY);
    }

    @Override
    public List<UserClickEvent> usersQuantityForWeek() throws IOException {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_WEEK);
    }

    @Override
    public List<UserClickEvent> usersQuantityForMonth() throws IOException {
        return getUserClickEvents(RangeQueries.FIND_USER_CLICK_EVENTS_FOR_MONTH);
    }

    @Override
    public List<UserClickEvent> yesterdayUsersFromCountry(String country) throws IOException {
        return getUserClickEvents(CountryQueries.FIND_USER_CLICK_EVENTS_COUNTRY_FOR_YESTERDAY(country));
    }

    @Override
    public List<UserClickEvent> yesterdayUsersBrowser(String browserName) throws IOException {

        return getUserClickEvents(BrowserQueries.FIND_USER_CLICK_EVENTS_BROWSER_FOR_YESTERDAY(
                browserName));
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

    private List<UserClickEvent> getUserClickEvents(QueryBuilder queryBuilder)
            throws IOException {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(config.getIndex());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(queryBuilder);

        searchRequest.source(searchSourceBuilder);

        return getSearchResult(client.search(searchRequest, RequestOptions.DEFAULT));
    }

    private static class RangeQueries {

        /**
         * Find user clicks for whole yesterday's date
         */
        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_YESTERDAY =
                QueryBuilders
                        .rangeQuery("date")
                        .gte("now-1d/d")
                        .lt("now/d");

        /**
         * Find user clicks for today's date
         */
        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_TODAY =
                QueryBuilders
                        .rangeQuery("date")
                        .gte("now/d")
                        .lt("now");

        /**
         * Find user clicks for 7 days not including today's date
         */
        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_WEEK =
                QueryBuilders
                        .rangeQuery("date")
                        .gte("now-7/d")
                        .lt("now/d");
        /**
         * Find user clicks for month not including today's date
         */
        static final RangeQueryBuilder FIND_USER_CLICK_EVENTS_FOR_MONTH =
                QueryBuilders
                        .rangeQuery("date")
                        .gte(LocalDateTime.now().minusMonths(1))
                        .lt("now");
    }

    private static class BrowserQueries {

        static BoolQueryBuilder FIND_USER_CLICK_EVENTS_BROWSER_FOR_YESTERDAY(String browserName) {
            return QueryBuilders.boolQuery().filter(QueryBuilders
                    .matchQuery("browser", browserName))
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
