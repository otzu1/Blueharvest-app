package com.blueharvest.geocaching;

/**
 * Created by mrwittmer on 11/26/15.
 */
public class RowItem {
    private String title;
    private String code;

    public RowItem(String title, String code) {
        this.title = title;
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + code;
    }
}
