package com.pengrad.telegrambot.model.request;

/**
 * stas
 * 8/4/15.
 */
public class InlineKeyboardMarkup extends Keyboard {

    private final InlineKeyboardButton[][] inline_keyboard;

    public InlineKeyboardMarkup(InlineKeyboardButton[]... keyboard) {
        this.inline_keyboard = keyboard;
    }

    public static InlineKeyboardMarkup keyboard(InlineKeyboardButton[]... row) {
        return new InlineKeyboardMarkup(row);
    }

    public static InlineKeyboardButton[] row(InlineKeyboardButton... buttons) {
        return buttons;
    }

    public static InlineKeyboardButton btn(String text, String callbackData) {
        return new InlineKeyboardButton(text).callbackData(callbackData);
    }

}
