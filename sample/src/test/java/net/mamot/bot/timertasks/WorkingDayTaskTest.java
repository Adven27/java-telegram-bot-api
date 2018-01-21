package net.mamot.bot.timertasks;

import net.mamot.bot.services.holidays.HolidayService;
import net.mamot.bot.timertasks.WorkingDayTask.WorkingCalendar;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class WorkingDayTaskTest {

    @Mock
    private HolidayService holidayService;

    private WorkingCalendar sut;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        sut = new WorkingCalendar(holidayService);
    }

    @Test
    public void workingDayIsNotAHoliday() {
        LocalDateTime notAHoliday = now();
        when(holidayService.isHoliday(notAHoliday)).thenReturn(false);
        assertTrue(sut.isWorkingDay(notAHoliday));
    }

    @Test
    public void holidayIsNotWorkingDay() {
        LocalDateTime holiday = now();
        when(holidayService.isHoliday(holiday)).thenReturn(true);
        assertFalse(sut.isWorkingDay(holiday));
    }

    @Test
    public void nextWorkingDayIsNotAHoliday() {
        LocalDateTime now = now();
        LocalDateTime expectedNextWorkingDay = now.plusDays(1);
        when(holidayService.isHoliday(expectedNextWorkingDay)).thenReturn(false);

        assertEquals(sut.nextWorkingDay(now), expectedNextWorkingDay);
    }

    @Test
    public void nextWorkingDaySkipsHolidays() {
        LocalDateTime now = now();

        LocalDateTime holiday = now.plusDays(1);
        when(holidayService.isHoliday(holiday)).thenReturn(true);

        LocalDateTime expectedNextWorkingDay = holiday.plusDays(1);
        when(holidayService.isHoliday(expectedNextWorkingDay)).thenReturn(false);

        assertEquals(sut.nextWorkingDay(now), expectedNextWorkingDay);
    }
}