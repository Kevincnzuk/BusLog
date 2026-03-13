package com.github.kevincnzuk.buslog;

public class ListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LOG = 1;

    public int type;
    public String dateText;
    public EntryVO log;

    public ListItem(int type, String dateText) {
        this.type = type;
        this.dateText = dateText;
    }

    public ListItem(int type, EntryVO log) {
        this.type = type;
        this.log = log;
    }

    public int getType() {
        return type;
    }

    public String getDateText() {
        return dateText;
    }

    public EntryVO getLog() {
        return log;
    }
}
