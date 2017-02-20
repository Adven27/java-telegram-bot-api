package com.pengrad.telegrambot.model;

/**
 * stas
 * 8/4/15.
 */
public class Update {

    private Integer update_id;
    private Message message;
    private Message edited_message;
    private Message channel_post;
    private Message edited_channel_post;
    private InlineQuery inline_query;
    private ChosenInlineResult chosen_inline_result;
    private CallbackQuery callback_query;

    public void setUpdate_id(Integer update_id) {
        this.update_id = update_id;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setEdited_message(Message edited_message) {
        this.edited_message = edited_message;
    }

    public void setChannel_post(Message channel_post) {
        this.channel_post = channel_post;
    }

    public void setEdited_channel_post(Message edited_channel_post) {
        this.edited_channel_post = edited_channel_post;
    }

    public void setInline_query(InlineQuery inline_query) {
        this.inline_query = inline_query;
    }

    public void setChosen_inline_result(ChosenInlineResult chosen_inline_result) {
        this.chosen_inline_result = chosen_inline_result;
    }

    public void setCallback_query(CallbackQuery callback_query) {
        this.callback_query = callback_query;
    }

    public Integer updateId() {
        return update_id;
    }

    public Message message() {
        return message;
    }

    public Message editedMessage() {
        return edited_message;
    }

    public Message channelPost() {
        return channel_post;
    }

    public Message editedChannelPost() {
        return edited_channel_post;
    }

    public InlineQuery inlineQuery() {
        return inline_query;
    }

    public ChosenInlineResult chosenInlineResult() {
        return chosen_inline_result;
    }

    public CallbackQuery callbackQuery() {
        return callback_query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Update update = (Update) o;

        if (update_id != null ? !update_id.equals(update.update_id) : update.update_id != null) return false;
        if (message != null ? !message.equals(update.message) : update.message != null) return false;
        if (edited_message != null ? !edited_message.equals(update.edited_message) : update.edited_message != null)
            return false;
        if (channel_post != null ? !channel_post.equals(update.channel_post) : update.channel_post != null)
            return false;
        if (edited_channel_post != null ? !edited_channel_post.equals(update.edited_channel_post) : update.edited_channel_post != null)
            return false;
        if (inline_query != null ? !inline_query.equals(update.inline_query) : update.inline_query != null)
            return false;
        if (chosen_inline_result != null ? !chosen_inline_result.equals(update.chosen_inline_result) : update.chosen_inline_result != null)
            return false;
        return callback_query != null ? callback_query.equals(update.callback_query) : update.callback_query == null;

    }

    @Override
    public int hashCode() {
        return update_id != null ? update_id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Update{" +
                "update_id=" + update_id +
                ", message=" + message +
                ", edited_message=" + edited_message +
                ", channel_post=" + channel_post +
                ", edited_channel_post=" + edited_channel_post +
                ", inline_query=" + inline_query +
                ", chosen_inline_result=" + chosen_inline_result +
                ", callback_query=" + callback_query +
                '}';
    }
}
