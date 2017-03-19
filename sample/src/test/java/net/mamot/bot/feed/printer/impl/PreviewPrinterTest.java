package net.mamot.bot.feed.printer.impl;

import net.mamot.bot.feed.Entry;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;

public class PreviewPrinterTest {

    @Test
    public void shouldPrintPreview() {
        assertEquals(new PreviewPrinter().print(getEntry()), getEntryPreview());
    }

    private Entry getEntry() {
        return new Entry("id", "title", new Date(), "content", "link");
    }

    private String getEntryPreview() {
        return "title\nlink";
    }

}