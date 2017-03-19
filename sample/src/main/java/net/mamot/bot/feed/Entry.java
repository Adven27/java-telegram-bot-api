package net.mamot.bot.feed;

import java.util.Date;

public class Entry {

    private final String id;
    private final String title;
    private final Date publishedDate;
    private final String content;
    private final String link;

    public Entry(String id, String title, Date publishedDate, String content, String link) {
        this.id = id;
        this.title = title;
        this.publishedDate = publishedDate;
        this.content = content;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }
}
