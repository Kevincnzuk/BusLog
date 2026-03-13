/**
 * Database entry.
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

public class EntryVO {
    private int id;
    private String busNumber;
    private String busModel;
    private String busRouteNumber;
    private String busRouteDestination;
    private long createTime;
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusModel() {
        return busModel;
    }

    public void setBusModel(String busModel) {
        this.busModel = busModel;
    }

    public String getBusRouteNumber() {
        return busRouteNumber;
    }

    public void setBusRouteNumber(String busRouteNumber) {
        this.busRouteNumber = busRouteNumber;
    }

    public String getBusRouteDestination() {
        return busRouteDestination;
    }

    public void setBusRouteDestination(String busRouteDestination) {
        this.busRouteDestination = busRouteDestination;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
