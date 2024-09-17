package com.eirs.lsm.alert;

public enum AlertMessagePlaceholders {
    URL("<URL>"), LIST("<LIST>"), EXCEPTION("<EXCEPTION>"), OPERATOR("<OPERATOR>"), FILE_TYPE("<FILE_TYPE>"),
    CONFIG_KEY("<CONFIG_KEY>"), CONFIG_VALUE("<CONFIG_VALUE>"), FEATURE_NAME("<FEATURE_NAME>");

    String placeholder;

    AlertMessagePlaceholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
}
