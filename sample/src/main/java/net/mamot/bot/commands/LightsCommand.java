package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import net.mamot.bot.services.BridgeAdapter;
import net.mamot.bot.services.HueBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class LightsCommand extends CallbackCommand {

    public static final String CALLBACK_OFF = "off";
    public static final String CALLBACK_ON = "on";
    private final BridgeAdapter bridgeAdapter;
    private InlineKeyboardMarkup inlineKeyboard;

    HueBridge bridge;

    public LightsCommand(BridgeAdapter bridgeAdapter) {
        super("/lights", "Lights");
        this.bridgeAdapter = bridgeAdapter;
        inlineKeyboard = keyboard(callbackDataPrefix()).row(
                "off", CALLBACK_OFF, "on", CALLBACK_ON).build();
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        SendMessage request;
        if (bridge == null) {
            List<HueBridge> bridges = bridgeAdapter.search();
            if (bridges.isEmpty()) {
                request = message(chat, "Sorry. There are no available bridges...");
            } else if (bridges.size() == 1) {
                bridge = bridges.get(0);
                String text = "Bridge: " + bridge.desc();
                request = message(chat, text).replyMarkup(inlineKeyboard);
            } else {
                request = message(chat, "Choose bridge:").replyMarkup(getBridgesOptions(bridges));
            }
        } else {
            String text = "Bridge: " + bridge.desc();
            request = message(chat, text).replyMarkup(inlineKeyboard);
        }
        bot.execute(request);
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        try {
            BaseRequest request = new AnswerCallbackQuery(cb.id()).text("Готово");
            switch (cb.data()) {
                case CALLBACK_OFF: bridge.turnOffAll(); break;
                case CALLBACK_ON: bridge.turnOnAll(); break;
                default: request = tryToSetBridge(cb);
            }
            bot.execute(request);
        } catch (HueBridge.BridgeUnreachableEx e) {
            Message msg = cb.message();
            bot.execute(new SendMessage(msg.chat().id(), "Sorry. There are no available bridges...").replyMarkup(inlineKeyboard));
        }
        return true;
    }

    private BaseRequest tryToSetBridge(CallbackQuery cb) {
        BaseRequest request = new AnswerCallbackQuery(cb.id()).text("Готово");
        List<HueBridge> bridges = bridgeAdapter.search();
        Optional<HueBridge> chosen = bridges.stream().filter(bridge -> cb.data().equals(bridge.id())).findFirst();
        if (chosen.isPresent()) {
            bridge = chosen.get();
        } else {
            request = message(cb.message().chat(), "Em... Bridge " + cb.data() + " suddenly disappeared... Choose again:").replyMarkup(getBridgesOptions(bridges));
        }
        return request;
    }

    HueBridge bridge() {
        return bridge;
    }

    private InlineKeyboardMarkup getBridgesOptions(List<HueBridge> bridges) {
        List<String> btns = new ArrayList<>();
        for (HueBridge bridge : bridges) {
            btns.add(bridge.desc());
            btns.add(bridge.id());
        }
        return keyboard(callbackDataPrefix()).row(btns.toArray(new String[btns.size()])).build();
    }
}