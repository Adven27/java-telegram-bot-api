package net.mamot.bot.services.holidays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static java.time.DayOfWeek.*;
import static java.time.LocalDateTime.now;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class WeekendsHolidayServiceTest {

    private WeekendsHolidayService sut;

    @BeforeMethod
    public void setUp() {
        sut = new WeekendsHolidayService();
    }

    @Test(dataProvider = "weekends")
    public void weekendsShouldBeHolidays(LocalDateTime weekend) {
        assertTrue(sut.isHoliday(weekend));
    }

    @Test(dataProvider = "workingDays")
    public void workingDaysShouldBeHolidays(LocalDateTime workingDay) {
        assertFalse(sut.isHoliday(workingDay));
    }

    @DataProvider(name = "weekends")
    public Object[][] weekends() {
        return new LocalDateTime[][]{
                {dayOfWeek(SATURDAY)},
                {dayOfWeek(SUNDAY)}
        };
    }

    @DataProvider(name = "workingDays")
    public Object[][] workingDays() {
        return new LocalDateTime[][]{
                {dayOfWeek(MONDAY)},
                {dayOfWeek(TUESDAY)},
                {dayOfWeek(WEDNESDAY)},
                {dayOfWeek(THURSDAY)},
                {dayOfWeek(FRIDAY)},
        };
    }

    private LocalDateTime dayOfWeek(DayOfWeek dayOfWeek) {
        return now().with(TemporalAdjusters.next(dayOfWeek));
    }
}