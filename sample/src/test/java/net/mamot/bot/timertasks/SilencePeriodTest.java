package net.mamot.bot.timertasks;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalDateTime.now;
import static net.mamot.bot.timertasks.GentleTask.SilencePeriod;
import static org.testng.Assert.*;

public class SilencePeriodTest {

    @Test
    public void shouldInclude_IfFromBeforeToAndTimeIsAfterFromAndBeforeTo() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(10, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(11, 00)));
    }

    @Test
    public void shouldInclude_IfFromBeforeToAndTimeEqualsFrom() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(10, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(10, 00)));
    }

    @Test
    public void shouldInclude_IfFromBeforeToAndTimeEqualsTo() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(10, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(12, 00)));
    }

    @Test
    public void shouldNotInclude_IfFromBeforeToAndTimeIsBeforeFrom() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(10, 00), LocalTime.of(12, 00));
        assertFalse(sut.includes(LocalTime.of(9, 00)));
    }

    @Test
    public void shouldNotInclude_IfFromBeforeToAndTimeIsAfterTo() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(10, 00), LocalTime.of(12, 00));
        assertFalse(sut.includes(LocalTime.of(13, 00)));
    }


    @Test
    public void shouldInclude_IfToBeforeFromAndTimeIsBeforeTo() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(11, 00)));
    }

    @Test
    public void shouldInclude_IfToBeforeFromAndTimeIsAfterFrom() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(23, 00)));
    }

    @Test
    public void shouldInclude_IfToBeforeFromAndTimeEqualsFrom() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(22, 00)));
    }

    @Test
    public void shouldInclude_IfToBeforeFromAndTimeEqualsTo() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(12, 00));
        assertTrue(sut.includes(LocalTime.of(12, 00)));
    }

    @Test
    public void shouldNotInclude_IfToBeforeFromAndTimeIsBeforeFrom() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(12, 00));
        assertFalse(sut.includes(LocalTime.of(21, 00)));
    }

    @Test
    public void shouldNotInclude_IfToBeforeFromAndTimeIsAfterTo() throws Exception {
        SilencePeriod sut = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(12, 00));
        assertFalse(sut.includes(LocalTime.of(13, 00)));
    }

    @Test
    public void afterShouldBeTheSecondPastTo_IfFromBeforeTo() {
        LocalTime from = LocalTime.of(10, 00);
        LocalTime to = LocalTime.of(12, 00);
        SilencePeriod sut = new SilencePeriod(from, to);

        LocalDateTime day = now().with(LocalTime.of(9, 00));

        assertEquals(sut.after(day), day.with(to).plusSeconds(1));
    }

    @Test
    public void afterShouldBeTheDayAndTheSecondPastTo_IfToBeforeFrom() {
        LocalTime from = LocalTime.of(22, 00);
        LocalTime to = LocalTime.of(12, 00);
        SilencePeriod sut = new SilencePeriod(from, to);

        LocalDateTime day = now().with(LocalTime.of(9, 00));

        assertEquals(sut.after(day), day.with(to).plusDays(1).plusSeconds(1));
    }
}