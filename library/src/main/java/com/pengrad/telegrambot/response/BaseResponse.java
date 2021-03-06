package com.pengrad.telegrambot.response;

import com.pengrad.telegrambot.model.ResponseParameters;

/**
 * stas
 * 1/13/16.
 */
public class BaseResponse {

    private boolean ok;
    private int error_code;
    private String description;
    private ResponseParameters parameters;

    BaseResponse() {
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParameters(ResponseParameters parameters) {
        this.parameters = parameters;
    }

    public boolean isOk() {
        return ok;
    }

    public int errorCode() {
        return error_code;
    }

    public String description() {
        return description;
    }

    public ResponseParameters parameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "ok=" + ok +
                ", error_code=" + error_code +
                ", description='" + description + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
