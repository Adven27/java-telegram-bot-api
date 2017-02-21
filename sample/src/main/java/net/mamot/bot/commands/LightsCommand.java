package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import net.mamot.bot.services.BridgeAdapter;
import net.mamot.bot.services.HueBridge;

import java.util.ArrayList;
import java.util.List;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class LightsCommand extends CallbackCommand {

    public static final String CALLBACK_OFF = "off";
    public static final String CALLBACK_ON = "on";
    private final BridgeAdapter bridgeAdapter;
    private Integer messageId;
    private InlineKeyboardMarkup inlineKeyboard;
    private HueBridge bridge;

    public LightsCommand(BridgeAdapter bridgeAdapter) {
        super("/lights", "Lights");
        this.bridgeAdapter = bridgeAdapter;
        inlineKeyboard = keyboard(
                row(btn("off", CALLBACK_OFF), btn("on", CALLBACK_ON))
        );
    }

    public LightsCommand(BridgeAdapter bridgeAdapter, HueBridge bridge) {
        this(bridgeAdapter);
        this.bridge = bridge;
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

        SendResponse response = bot.execute(request);
        messageId = response.message().messageId();
    }

    @Override
    public Integer originalMessage() {
        return messageId;
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        try {
            processCallback(cb);
            bot.execute(new AnswerCallbackQuery(cb.id()).text("Готово"));
        } catch (HueBridge.BridgeUnreachableEx e) {
            Message msg = cb.message();
            bot.execute(new EditMessageText(msg.chat().id(), messageId, "Не шмагля...").replyMarkup(inlineKeyboard));
        }
        return true;
    }

    private InlineKeyboardMarkup getBridgesOptions(List<HueBridge> bridges) {
        List<InlineKeyboardButton> btns = new ArrayList<>();
        for (HueBridge bridge : bridges) {
            btns.add(btn(bridge.desc(), bridge.id()));
        }
        return keyboard(row(btns.toArray(new InlineKeyboardButton[]{})));
    }

    private void processCallback(CallbackQuery cb) {
        switch (cb.data()) {
            case CALLBACK_OFF: bridge.turnOffAll(); break;
            case CALLBACK_ON: bridge.turnOnAll(); break;
        }
    }
}