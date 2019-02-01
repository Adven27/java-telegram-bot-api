package net.mamot.bot.feed.printer;

import net.mamot.bot.publications.Publication;

public interface PublicationPrinter<T extends Publication> {
    String print(Publication publication);
}
