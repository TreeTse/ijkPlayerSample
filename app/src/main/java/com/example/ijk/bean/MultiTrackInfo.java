package com.example.ijk.bean;

public class MultiTrackInfo {
    String lang = "";
    int index = -1;

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "MultiTrackInfo{" +
                "lang='" + lang + '\'' +
                ", index=" + index +
                '}';
    }
}
