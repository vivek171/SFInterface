package com.htc.remedy.model;

public class keyvaluemodel {
    String key;
    String text;

    public keyvaluemodel(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public keyvaluemodel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return "keyvaluemodel{" +
                "key='" + key + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
