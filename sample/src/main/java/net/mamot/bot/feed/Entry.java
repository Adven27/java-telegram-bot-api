package net.mamot.bot.feed;

import net.mamot.bot.publications.Publication;

import java.util.Date;

public class Entry implements Publication {

    private final String id;
    private final String title;
    private final Date publicationDate;
    private final String content;
    private final String link;

    public Entry(String id, String title, Date publicationDate, String content, String link) {
        this.id = id;
        this.title = title;
        this.publicationDate = publicationDate;
        this.content = content;
        this.link = link;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Date getPublicationDate() {
        return publicationDate;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getLink() {
        return link;
    }
}
