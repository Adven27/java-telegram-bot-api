package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.poll.Poll;

import java.util.*;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static net.mamot.bot.services.Stickers.*;

public class PollCommand extends CallbackCommand {

    private static final String HELP_MSG = "Пример: Чей крым? наш / не наш";
    private static Map<String, Poll> polls = new HashMap();

    public PollCommand() {
        super("/poll","polls");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        try {
            Poll p = new Poll(params);
            String pollId = generatePollId();

            polls.put(pollId, p);
            bot.execute(new SendSticker(chat, ASK.id()));
            bot.execute(message(chat, p.question()).replyMarkup(getKeyboard(pollId, p.options())));
        } catch (Poll.InvalidQuestionFormatEx e) {
            bot.execute(message(chat, HELP_MSG));
        }
    }

    private String generatePollId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private InlineKeyboardMarkup getKeyboard(String pollId, Set<String> vars) {
        LinkedList<String> textDataPairs = new LinkedList<>();
        for (String v : vars) {
            textDataPairs.add(v);
            textDataPairs.add(pollId + "#" + v);
        }
        return keyboard().row(textDataPairs.toArray(new String[textDataPairs.size()])).build();
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        String[] answer = cb.data().split("#");
        String pollId = answer[0];
        String option = answer[1];
        Poll poll = polls.get(pollId);
        Message originalMessage = cb.message();

        if (poll == null) {
            bot.execute(editMessage(originalMessage, originalMessage.text()));
            return true;
        }

        polls.put(pollId,  vote(poll, option, cb.from().lastName() + " " + cb.from().firstName()));

        bot.execute(new AnswerCallbackQuery(cb.id()).text("Спасибушки"));
        bot.execute(editMessage(originalMessage, screen(poll)).replyMarkup(getKeyboard(pollId, poll.options())));
        return true;
    }

    private Poll vote(Poll poll, String option, String voter) {
        Optional<String> prevOpt = poll.optionOf(voter);
        prevOpt.ifPresent(opt -> poll.unvote(opt, voter));
        poll.vote(option, voter);
        return poll;
    }

    private String screen(Poll poll) {
        String text = poll.question() + "\n\n";
        for (Map.Entry<String, List<String>> e : poll.votes().entrySet()) {
            text += e.getKey() + " - " + (e.getValue().size() == 0 ? "" : e.getValue()) + "\n";
        }
        return text;
    }
}