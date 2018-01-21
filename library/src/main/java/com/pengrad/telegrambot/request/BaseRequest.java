package com.pengrad.telegrambot.request;

import com.google.gson.Gson;
import com.pengrad.telegrambot.response.BaseResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Objects;

/**
 * stas
 * 5/1/16.
 */
abstract public class BaseRequest<T extends BaseRequest, R extends BaseResponse> {

    private static final Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    protected final T thisAsT = (T) this;

    private final Class<? extends R> responseClass;
    private final Map<String, Object> parameters;

    public BaseRequest(Class<? extends R> responseClass) {
        this.responseClass = responseClass;
        this.parameters = new HashMap<>();
    }

    protected T add(String name, Object val) {
        parameters.put(name, val);
        return thisAsT;
    }

    public String getMethod() {
        String className = this.getClass().getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Type getResponseType() {
        return responseClass;
    }

    public boolean isMultipart() {
        return false;
    }

    public String getFileName() {
        return ContentTypes.GENERAL_FILE_NAME;
    }

    public String getContentType() {
        return ContentTypes.GENERAL_MIME_TYPE;
    }

    public int getTimeoutSeconds() {
        return 0;
    }

    public String toWebhookResponse() {
        Map<String, Object> fullMap = new HashMap<String, Object>(parameters);
        fullMap.put("method", getMethod());
        return gson.toJson(fullMap);
    }

    // Serialize model objects. Basically convert to json
    // todo move to TelegramBotClient, let it serialize everything in request time
    protected String serialize(Object o) {
        return gson.toJson(o);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseRequest<?, ?> that = (BaseRequest<?, ?>) o;
        for (Map.Entry<String, Object> param : that.getParameters().entrySet()) {
            String exp = getParameters().get(param.getKey()) == null ? "null" : getParameters().get(param.getKey()).toString();
            String act = param.getValue() == null ? "null" : param.getValue().toString();
            if (!act.equals(exp)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + getParameters().toString();
    }
}
