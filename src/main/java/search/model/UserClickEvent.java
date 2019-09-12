package search.model;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserClickEvent {

    private String id;
    private String sessionId;
    private String country;
    private String browserName;
    private String url;
    private LocalDateTime date;

    public UserClickEvent() {
    }

    public UserClickEvent(String id, String sessionId, String country,
                          String browserName, String url, LocalDateTime date) {
        this.id = id;
        this.sessionId = sessionId;
        this.country = country;
        this.browserName = browserName;
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

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowser(String browserName) {
        this.browserName = browserName;
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
                .add("browser", browserName)
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
                browserName == that.browserName &&
                Objects.equals(url, that.url) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, country, browserName, url, date);
    }
}
