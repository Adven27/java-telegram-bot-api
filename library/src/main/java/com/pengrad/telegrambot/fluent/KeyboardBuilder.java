package com.pengrad.telegrambot.fluent;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

import static com.pengrad.telegrambot.model.request.InlineKeyboardButton.button;

public class KeyboardBuilder {
    private List<InlineKeyboardButton[]> rows = new ArrayList<>();

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

    public KeyboardBuilder row(Type type, String... vars) {
        if (Type.TEXT_DATA_PAIRS.equals(type)) {
            return row(vars);
        } else if (Type.TEXT_EQUALS_DATA_LIST.equals(type)) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (String v : vars) {
                row.add(button(v, v));
            }
            rows.add(row.toArray(new InlineKeyboardButton[row.size()]));
        }
        return this;
    }

    public InlineKeyboardMarkup build() {
        return new InlineKeyboardMarkup(rows.toArray(new InlineKeyboardButton[rows.size()][]));
    }

    public enum Type {
        TEXT_DATA_PAIRS,
        TEXT_EQUALS_DATA_LIST
    }
}