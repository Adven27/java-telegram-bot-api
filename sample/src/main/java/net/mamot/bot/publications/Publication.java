package net.mamot.bot.publications;

import java.util.Date;

public interface Publication {
    String getId();

    String getTitle();
    Date getPublicationDate();
    String getContent();
    String getLink();
}
