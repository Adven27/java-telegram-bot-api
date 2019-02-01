package net.mamot.bot.feed.printer.impl;

import net.mamot.bot.feed.printer.PublicationPrinter;
import net.mamot.bot.publications.Publication;

public class DetailedPrinter implements PublicationPrinter {

    @Override
    public String print(Publication publication) {
        return publication.getTitle() + "\n" + publication.getLink() + "\n\n" + publication.getContent();
    }
}
