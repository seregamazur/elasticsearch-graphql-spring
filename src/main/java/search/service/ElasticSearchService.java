package search.service;

import search.model.UserClickEvent;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchService {

    List<UserClickEvent> usersQuantityForYesterday() throws IOException;

    List<UserClickEvent> usersQuantityForToday() throws IOException;

    List<UserClickEvent> usersQuantityForWeek() throws IOException;

    List<UserClickEvent> usersQuantityForMonth() throws IOException;

    List<UserClickEvent> yesterdayUsersFromCountry(String country) throws IOException;

    List<UserClickEvent> yesterdayUsersBrowser(String browserName) throws IOException;

}
