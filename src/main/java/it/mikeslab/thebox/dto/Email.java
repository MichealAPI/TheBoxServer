package it.mikeslab.thebox.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Email {

    private final static String[] REQUIRED_FIELDS = {
            "subject",
            "template",
            "targetEmail"
    };
    private final Map<String, Object> data;

    public Email() {
        data = new HashMap<>();
    }

    public Email(Map<String, Object> data) {
        this.data = data;
    }

    public Email addData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Email putAllData(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public Map<String, Object> getData() {

        for (String field : REQUIRED_FIELDS) {
            if (!data.containsKey(field)) {
                throw new IllegalArgumentException("Missing required field: " + field);
            }
        }

        return data;
    }

    public String getTo() {
        return (String) data.get("targetEmail");
    }

    public String getSubject() {
        return (String) data.get("subject");
    }

    public String getTemplate() {
        return (String) data.get("template");
    }

}
