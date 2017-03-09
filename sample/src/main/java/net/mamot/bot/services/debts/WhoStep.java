package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class WhoStep implements WizardStep {
    @Override
    public String screen() {
        return "Who?";
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("jessy", "jessy").
                row("gus", "gus").
                row("saul", "saul");
    }

    @Override
    public WizardStep callback(String data) {
        return this;
    }
}
