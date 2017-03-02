package net.mamot.bot.commands;

import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import net.mamot.bot.services.poll.Poll;
import net.mamot.bot.services.poll.PollsInMemRepo;
import net.mamot.bot.services.poll.PollsRepo;
import org.junit.Test;

import java.util.List;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.Type.TEXT_EQUALS_DATA_LIST;
import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.mamot.bot.commands.PollCommand.HELP_MSG;
import static net.mamot.bot.services.Stickers.ASK;
import static net.mamot.bot.services.poll.Poll.OPTIONS_SPLITTER;
import static net.mamot.bot.services.poll.Poll.poll;
import static org.junit.Assert.assertEquals;

public class PollCommandTest {
    public static final SendMessage HELP_MESSAGE = message(HELP_MSG);
    public static final AnswerCallbackQuery THANKS_CALLBACK_ANSWER = answerCallbackQuery("Thanks");

    //TODO  get rid of magic number (equals original message id)...
    private final Poll freshPoll = poll(1, "question?", "opt1", "opt2");
    private final PollsRepo pollsRepo = new PollsInMemRepo();
    private PollCommand sut = new PollCommand(pollsRepo);

    @Test
    public void shouldReturnHelpMessageForInvalidParams_empty() throws Exception {
        given(sut).
            got("/poll").
        then().
            shouldAnswer(HELP_MESSAGE);
    }

    @Test
    public void shouldReturnHelpMessageForInvalidParams_noOptions() throws Exception {
        given(sut).
            got("/poll where is options?").
        then().
            shouldAnswer(HELP_MESSAGE);
    }

    @Test
    public void shouldReturnHelpMessageForInvalidParams_noQuestionMark() throws Exception {
        given(sut).
            got("/poll where is question").
        then().
            shouldAnswer(HELP_MESSAGE);
    }

    @Test
    public void shouldSendQuestionWithOptionButtonsForValidParams() throws Exception {
        given(sut).
            got("/poll question? opt1" + OPTIONS_SPLITTER + "opt2").
        then(sut).
            shouldAnswer(sticker(ASK.id()),
                         message("question?").replyMarkup(keyboard().row(TEXT_EQUALS_DATA_LIST, "opt1", "opt2").build()));
    }

    @Test
    public void shouldDisplayVoteAfterOptionIsChosen() throws Exception {
        pollsRepo.add(freshPoll);

        givenGotCallbackFor(sut, "opt1").
            from("Walter", "White").
        then(sut).
            shouldAnswer(THANKS_CALLBACK_ANSWER,
                         editMessage(format("%s\n\n%s - [White Walter]\n%s - \n", "question?", "opt1", "opt2")).
                                 replyMarkup(keyboard().row(TEXT_EQUALS_DATA_LIST, "opt1", "opt2").build()));

        assertEquals(asList("White Walter"), votesFor("opt1"));
        assertEquals(emptyList(), votesFor("opt2"));
    }

    @Test
    public void shouldUpdateVoteAfterRevote() throws Exception {
        freshPoll.vote("opt1", "White Walter");
        pollsRepo.add(freshPoll);

        givenGotCallbackFor(sut, "opt2").
            from("Walter", "White").
        then(sut).
            shouldAnswer(THANKS_CALLBACK_ANSWER,
                         editMessage(format("%s\n\n%s - \n%s - [White Walter]\n", "question?", "opt1", "opt2")).
                            replyMarkup(keyboard().row(TEXT_EQUALS_DATA_LIST, "opt1", "opt2").build()));

        assertEquals(emptyList(), votesFor("opt1"));
        assertEquals(asList("White Walter"), votesFor("opt2"));
    }

    private List<String> votesFor(String option) {
        return pollsRepo.get(freshPoll.id()).get().votesFor(option);
    }
}