// java
package com.group5final.roomieradar.dto;

import com.group5final.roomieradar.entities.CalendarItem;

import java.time.LocalDate;

public class CalendarItemInstance {
    private final CalendarItem item;
    private final LocalDate date;
    private final boolean startsOnDate;
    private final boolean endsOnDate;

    public CalendarItemInstance(CalendarItem item, LocalDate date, boolean startsOnDate, boolean endsOnDate) {
        this.item = item;
        this.date = date;
        this.startsOnDate = startsOnDate;
        this.endsOnDate = endsOnDate;
    }

    public CalendarItem getItem() {
        return item;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isStartsOnDate() {
        return startsOnDate;
    }

    public boolean isEndsOnDate() {
        return endsOnDate;
    }
}
