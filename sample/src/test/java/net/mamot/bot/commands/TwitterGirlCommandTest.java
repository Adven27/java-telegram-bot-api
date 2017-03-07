package net.mamot.bot.commands;

import net.mamot.bot.services.twitter.TwitterService;
import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static net.mamot.bot.commands.TwitterGirlCommand.COMMAND;
import static net.mamot.bot.commands.TwitterGirlCommand.GIRL_NAME_IN_TWITTER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterGirlCommandTest {
    private final TwitterService twitter = mock(TwitterService.class);
    private TwitterGirlCommand sut = new TwitterGirlCommand(twitter);

    @Test
    public void shouldReturnMessageFromAdviceResource() throws Exception {
        when(twitter.getLatestTweet(GIRL_NAME_IN_TWITTER)).thenReturn("yet another stupid tweet");

        given(sut).
            got(COMMAND).
        then().
            shouldAnswer(message("yet another stupid tweet"));
    }
}