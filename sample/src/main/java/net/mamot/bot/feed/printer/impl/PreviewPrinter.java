package net.mamot.bot.feed.printer.impl;

import net.mamot.bot.feed.printer.PublicationPrinter;
import net.mamot.bot.publications.Publication;

public class PreviewPrinter implements PublicationPrinter {

    @Override
    public String print(Publication publication) {
        return publication.getTitle() + "\n" + publication.getLink();
    }
}
