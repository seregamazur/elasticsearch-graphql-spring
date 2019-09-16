package search.model;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserClickEvent {

    private String id;
    private String sessionId;
    private String country;
    private String browser;
    private String url;
    private LocalDateTime date;

    public UserClickEvent() {
    }

    public UserClickEvent(String id, String sessionId, String country,
                          String browser, String url, LocalDateTime date) {
        this.id = id;
        this.sessionId = sessionId;
        this.country = country;
        this.browser = browser;
        this.url = url;
        this.date = date;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browserName) {
        this.browser = browserName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sessionId", sessionId)
                .add("country", country)
                .add("browser", browser)
                .add("url", url)
                .add("date", date)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserClickEvent that = (UserClickEvent) o;
        return Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(country, that.country) &&
                browser == that.browser &&
                Objects.equals(url, that.url) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, country, browser, url, date);
    }
}
