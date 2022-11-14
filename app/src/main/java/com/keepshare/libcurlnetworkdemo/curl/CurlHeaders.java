package com.keepshare.libcurlnetworkdemo.curl;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.internal.Util;

public class CurlHeaders {

    private List<String> namesAndValues = new ArrayList<>(20);

    public void addHeader(String name, String value) {
        this.namesAndValues.add(name);
        this.namesAndValues.add(value);
    }

    public void addHeader(String line) {
        int index = line.indexOf(":");
        if (index == -1) {
            throw new IllegalArgumentException("Unexpected header: " + line);
        }
        addHeader(line.substring(0, index).trim(), line.substring(index + 1));
    }

    public void removeAll(String name) {
        for (int i = 0; i < namesAndValues.size(); i += 2) {
            if (name.equalsIgnoreCase(namesAndValues.get(i))) {
                namesAndValues.remove(i); // name
                namesAndValues.remove(i); // value
                i -= 2;
            }
        }
    }

    /**
     * Set a field with the specified value. If the field is not found, it is added. If the field is
     * found, the existing values are replaced.
     */
    public void setHead(String name, String value) {
        checkName(name);
        checkValue(value, name);
        removeAll(name);
        addHeader(name, value);
    }

    /** Equivalent to {@code build().get(name)}, but potentially faster. */
    public String get(String name) {
        for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
            if (name.equalsIgnoreCase(namesAndValues.get(i))) {
                return namesAndValues.get(i + 1);
            }
        }
        return null;
    }

    static void checkName(String name) {
        if (name == null) throw new NullPointerException("name == null");
        if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
        for (int i = 0, length = name.length(); i < length; i++) {
            char c = name.charAt(i);
            if (c <= '\u0020' || c >= '\u007f') {
                throw new IllegalArgumentException(Util.format(
                        "Unexpected char %#04x at %d in header name: %s", (int) c, i, name));
            }
        }
    }

    static void checkValue(String value, String name) {
        if (value == null) throw new NullPointerException("value for name " + name + " == null");
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            if ((c <= '\u001f' && c != '\t') || c >= '\u007f') {
                throw new IllegalArgumentException(Util.format(
                        "Unexpected char %#04x at %d in %s value: %s", (int) c, i, name, value));
            }
        }
    }
}
