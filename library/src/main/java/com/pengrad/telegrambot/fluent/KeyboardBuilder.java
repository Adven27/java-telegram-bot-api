package com.pengrad.telegrambot.fluent;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

import static com.pengrad.telegrambot.model.request.InlineKeyboardButton.button;

public class KeyboardBuilder {
    List<InlineKeyboardButton[]> rows = new ArrayList<>();

    public static KeyboardBuilder keyboard() {
        return new KeyboardBuilder();
    }

    public KeyboardBuilder row(String... textDataPairs) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i =0 ; i < textDataPairs.length; i = i + 2) {
            row.add(button(textDataPairs[i], textDataPairs[i + 1]));
        }
        rows.add(row.toArray(new InlineKeyboardButton[row.size()]));
        return this;
    }

    public KeyboardBuilder row(InlineKeyboardButton... btns) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i =0 ; i < btns.length; i = i + 2) {
            row.add(btns[i]);
        }
        rows.add(row.toArray(new InlineKeyboardButton[row.size()]));
        return this;
    }

    public InlineKeyboardMarkup build() {
        return new InlineKeyboardMarkup(rows.toArray(new InlineKeyboardButton[rows.size()][]));
    }
}