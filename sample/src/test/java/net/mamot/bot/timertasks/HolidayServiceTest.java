package net.mamot.bot.timertasks;

import net.mamot.bot.services.impl.HttpResource;
import net.mamot.bot.services.impl.HttpsResource;
import org.junit.Test;

import java.util.Date;

public class HolidayServiceTest {

    @Test
    public void ZeroResponseShouldBeHoliday() {
        HolidayService sut = new HolidayService(new HttpsResource());
        sut.isHoliday(new Date());
    }
}