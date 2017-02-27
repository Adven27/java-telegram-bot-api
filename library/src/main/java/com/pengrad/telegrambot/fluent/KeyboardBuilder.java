package com.pengrad.telegrambot.fluent;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {
    final String prefix;

    List<InlineKeyboardButton[]> rows = new ArrayList<>();

    public KeyboardBuilder(String prefix) {
        this.prefix = prefix;
    }

    public static KeyboardBuilder keyboard(String callbackDataPrefix) {
        return new KeyboardBuilder(callbackDataPrefix);
    }

    public static KeyboardBuilder keyboard() {
        return keyboard("");
    }

    public KeyboardBuilder row(String... textDataPairs) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i =0 ; i < textDataPairs.length; i = i + 2) {
            row.add(new InlineKeyboardButton(textDataPairs[i]).callbackData(prefix + textDataPairs[i + 1]));
        }
        rows.add(row.toArray(new InlineKeyboardButton[row.size()]));
        return this;
    }

    public InlineKeyboardMarkup build() {
        return new InlineKeyboardMarkup(rows.toArray(new InlineKeyboardButton[rows.size()][]));
    }
}