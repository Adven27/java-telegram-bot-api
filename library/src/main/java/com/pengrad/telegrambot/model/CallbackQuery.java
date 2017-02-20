package com.pengrad.telegrambot.model;

/**
 * Stas Parshin
 * 07 May 2016
 */
public class CallbackQuery {

    private String id;
    private User from;
    private Message message;
    private String inline_message_id;
    private String chat_instance;
    private String data;
    private String game_short_name;

    public void setId(String id) {
        this.id = id;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setInline_message_id(String inline_message_id) {
        this.inline_message_id = inline_message_id;
    }

    public void setChat_instance(String chat_instance) {
        this.chat_instance = chat_instance;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setGame_short_name(String game_short_name) {
        this.game_short_name = game_short_name;
    }

    public String id() {
        return id;
    }

    public User from() {
        return from;
    }

    public Message message() {
        return message;
    }

    public String inlineMessageId() {
        return inline_message_id;
    }

    public String chatInstance() {
        return chat_instance;
    }

    public String data() {
        return data;
    }

    public String gameShortName() {
        return game_short_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackQuery that = (CallbackQuery) o;

        if (!id.equals(that.id)) return false;
        if (!from.equals(that.from)) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (inline_message_id != null ? !inline_message_id.equals(that.inline_message_id) : that.inline_message_id != null)
            return false;
        if (chat_instance != null ? !chat_instance.equals(that.chat_instance) : that.chat_instance != null) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return game_short_name != null ? game_short_name.equals(that.game_short_name) : that.game_short_name == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CallbackQuery{" +
                "id='" + id + '\'' +
                ", from=" + from +
                ", message=" + message +
                ", inline_message_id='" + inline_message_id + '\'' +
                ", chat_instance='" + chat_instance + '\'' +
                ", data='" + data + '\'' +
                ", game_short_name='" + game_short_name + '\'' +
                '}';
    }
}
