package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.response.SendResponse;
import net.mamot.bot.services.poll.Poll;
import net.mamot.bot.services.poll.PollsRepo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.Type.TEXT_EQUALS_DATA_LIST;
import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static net.mamot.bot.services.Stickers.ASK;

public class PollCommand extends CallbackCommand {
    public static final String HELP_MSG = "Пример: Чей крым? наш | не наш";
    private final PollsRepo pollsRepo;

    public PollCommand(PollsRepo pollsRepo) {
        super("/poll","polls");
        this.pollsRepo = pollsRepo;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        try {
            Poll p = new Poll(params);
            bot.execute(sticker(chat, ASK.id()));
            SendResponse resp = bot.execute(message(chat, p.question()).replyMarkup(keyboardFrom(p.options())));
            p.id(resp.message().messageId());
            pollsRepo.add(p);
        } catch (Poll.InvalidQuestionFormatEx e) {
            bot.execute(message(chat, HELP_MSG));
        }
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        int pollId = cb.message().messageId();
        String option = cb.data();
        Optional<Poll> nullablePoll = pollsRepo.get(pollId);
        Message originalMessage = cb.message();

        if (!nullablePoll.isPresent()) {
            bot.execute(editMessage(originalMessage, originalMessage.text()));
            return true;
        }
        Poll poll = nullablePoll.get();
        pollsRepo.update(poll.revote(option, voter(cb)));

        bot.execute(new AnswerCallbackQuery(cb.id()).text("Thanks"));
        bot.execute(editMessage(originalMessage, screen(poll)).replyMarkup(keyboardFrom(poll.options())));
        return true;
    }

    private InlineKeyboardMarkup keyboardFrom(Set<String> vars) {
        return keyboard().row(TEXT_EQUALS_DATA_LIST, vars.toArray(new String[vars.size()])).build();
    }

    private String voter(CallbackQuery cb) {
        return cb.from().lastName() + " " + cb.from().firstName();
    }

    private String screen(Poll poll) {
        String text = poll.question() + "\n\n";
        for (Map.Entry<String, List<String>> e : poll.votes().entrySet()) {
            text += e.getKey() + " - " + (e.getValue().size() == 0 ? "" : e.getValue()) + "\n";
        }
        return text;
    }
}