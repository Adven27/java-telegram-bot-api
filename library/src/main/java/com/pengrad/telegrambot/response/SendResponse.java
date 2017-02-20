package com.pengrad.telegrambot.response;

import com.pengrad.telegrambot.model.Message;

/**
 * stas
 * 8/5/15.
 */
public class SendResponse extends BaseResponse {

    private Message result;

    public SendResponse() {
        setOk(true);
    }

    public Message message() {
        return result;
    }

    @Override
    public String toString() {
        return "SendResponse{" +
                "result=" + result +
                '}';
    }

    public void message(Message result) {
        this.result = result;
    }
}
