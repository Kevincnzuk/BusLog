/**
 * Component to support date category.
 * Copyright (C) 2026  Leyuan Chang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
