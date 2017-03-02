package net.mamot.bot.services.poll;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static net.mamot.bot.services.poll.Poll.OPTIONS_SPLITTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PollTest {

    @Test
    public void shouldRecognizeQuestionByQuestionMark() throws Exception {
        assertEquals("Is this question?", new Poll("Is this question?yes").question());
    }

    @Test(expected = Poll.InvalidQuestionFormatEx.class)
    public void pollWithoutQuestionMarkIsInvalid() throws Exception {
        new Poll("where is question mark");
    }

    @Test(expected = Poll.InvalidQuestionFormatEx.class)
    public void pollWithoutQuestionMarkIsInvalid_EmptyStringBorderCase() throws Exception {
        new Poll("");
    }

    @Test(expected = Poll.InvalidQuestionFormatEx.class)
    public void pollWithoutQuestionMarkIsInvalid_NullBorderCase() throws Exception {
        new Poll(null);
    }

    @Test
    public void onlyQuestionMarkQuestionsAreAllowed() throws Exception {
        assertEquals("?", new Poll("?yes").question());
    }

    @Test(expected = Poll.InvalidQuestionFormatEx.class)
    public void pollWithoutTextAfterQuestionMarkIsInvalid() throws Exception {
        new Poll("where is options?");
    }

    @Test(expected = Poll.InvalidQuestionFormatEx.class)
    public void pollWithoutTextAfterQuestionMarkIsInvalid_EmptyQuestionBorderCase() throws Exception {
        new Poll("?");
    }

    @Test
    public void partAfterQuestionMarkIsNotAQuestion() throws Exception {
        assertEquals("after?", new Poll("after?before").question());
    }

    @Test
    public void partAfterQuestionMarkIsSlashSeparatedOptions() throws Exception {
        assertEquals(new HashSet(asList("option1", "option2")), new Poll("question?option1" + OPTIONS_SPLITTER + "option2").options());
    }

    @Test
    public void questionWithSingleOptionIsAllowed() throws Exception {
        assertEquals(new HashSet(asList("option")), new Poll("question?option").options());
    }

    @Test(expected = Poll.InvalidQuestionFormatEx.class)
    public void questionWithAllOptionsEmptyIsInvalid() throws Exception {
        new Poll("question?" + OPTIONS_SPLITTER + "");
    }

    @Test
    public void emptyOptionsAreIgnored() throws Exception {
        assertEquals(new HashSet(asList("1")), new Poll("question?1" + OPTIONS_SPLITTER + "").options());
    }

    @Test
    public void emptyOptionsAreIgnored_2() throws Exception {
        assertEquals(new HashSet(asList("2")), new Poll("question?" + OPTIONS_SPLITTER + "2").options());
    }

    @Test
    public void emptyOptionsAreIgnored_3() throws Exception {
        assertEquals(new HashSet(asList("2")), new Poll("question?" + OPTIONS_SPLITTER + "2" + OPTIONS_SPLITTER + "").options());
    }

    @Test
    public void emptyOptionsAreIgnored_4() throws Exception {
        assertEquals(new HashSet(asList("1", "3")), new Poll("question?1" + OPTIONS_SPLITTER + OPTIONS_SPLITTER + "3").options());
    }

    @Test
    public void canVoteForOption() throws Exception {
        Poll sut = new Poll("question?1" + OPTIONS_SPLITTER + "2");
        String optionToVote = sut.options().iterator().next();

        sut.vote(optionToVote, "some voter");

        assertEquals(asList("some voter"), sut.votesFor(optionToVote));
    }

    @Test
    public void canVoteForOption_2() throws Exception {
        Poll sut = new Poll("question?1" + OPTIONS_SPLITTER + "2");
        String opt = sut.options().iterator().next();

        sut.vote(opt, "some voter 1");
        sut.vote(opt, "some voter 2");

        assertEquals(asList("some voter 1", "some voter 2"), sut.votesFor(opt));
    }

    @Test
    public void canVoteForOption_3() throws Exception {
        Poll sut = new Poll("question?1" + OPTIONS_SPLITTER + "2");
        Iterator<String> i = sut.options().iterator();
        String option1 = i.next();
        String option2 = i.next();

        sut.vote(option1, "some voter 1");
        sut.vote(option2, "some voter 2");
        sut.vote(option2, "some voter 3");

        assertEquals(asList("some voter 1"), sut.votesFor(option1));
        assertEquals(asList("some voter 2", "some voter 3"), sut.votesFor(option2));
    }

    @Test
    public void canUnvoteForOption() throws Exception {
        Poll sut = new Poll("question?1" + OPTIONS_SPLITTER + "2");
        String opt = sut.options().iterator().next();

        sut.vote(opt, "some voter");
        sut.unvote(opt, "some voter");

        assertTrue(sut.votesFor(opt).isEmpty());
    }

    @Test
    public void unvoteForNotVotedOptionDoesNothing() throws Exception {
        Poll sut = new Poll("question?1" + OPTIONS_SPLITTER + "2");
        String opt = sut.options().iterator().next();

        sut.unvote(opt, "some voter");

        assertTrue(sut.votesFor(opt).isEmpty());
    }

    @Test
    public void canGetOptionOfVoter() throws Exception {
        Poll sut = new Poll("question?1" + OPTIONS_SPLITTER + "2");
        String opt = sut.options().iterator().next();

        sut.vote(opt, "some voter");

        assertEquals("1", sut.optionOf("some voter").get());
    }

    @Test
    public void ifVoterDoesNotVote_GetOptionOfVoterReturnEmpty() throws Exception {
        assertFalse(new Poll("question?1" + OPTIONS_SPLITTER + "2").optionOf("some voter").isPresent());
    }
}