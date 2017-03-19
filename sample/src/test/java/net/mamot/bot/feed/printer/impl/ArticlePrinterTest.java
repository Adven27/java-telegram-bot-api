package net.mamot.bot.feed.printer.impl;

import net.mamot.bot.feed.Entry;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;

public class ArticlePrinterTest {

    @Test
    public void shouldPrintArticle() {
        assertEquals(new ArticlePrinter().print(getEntry()), getEntryPreview());
    }

    private Entry getEntry() {
        return new Entry("id", "title", new Date(), "content", "link");
    }

    private String getEntryPreview() {
        return "title\nlink\n\ncontent";
    }

}