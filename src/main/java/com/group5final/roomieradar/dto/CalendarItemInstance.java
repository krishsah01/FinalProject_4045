// java
package com.group5final.roomieradar.dto;

import com.group5final.roomieradar.entities.CalendarItem;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing an instance of a {@link CalendarItem} on a specific date.
 * <p>
 * This class is used to expand recurring or multi-day calendar items into per-date instances for display purposes,
 * such as in a calendar view. It includes flags indicating if the instance starts or ends on the given date.
 * </p>
 */
public class CalendarItemInstance {

    private final CalendarItem item;
    private final LocalDate date;
    private final boolean startsOnDate;
    private final boolean endsOnDate;

    /**
     * Constructs a new CalendarItemInstance.
     *
     * @param item the underlying {@link CalendarItem} this instance represents
     * @param date the specific date for this instance
     * @param startsOnDate true if this instance starts on the given date
     * @param endsOnDate true if this instance ends on the given date
     */
    public CalendarItemInstance(CalendarItem item, LocalDate date, boolean startsOnDate, boolean endsOnDate) {
        this.item = item;
        this.date = date;
        this.startsOnDate = startsOnDate;
        this.endsOnDate = endsOnDate;
    }

    /**
     * Gets the underlying calendar item.
     *
     * @return the {@link CalendarItem} associated with this instance
     */
    public CalendarItem getItem() {
        return item;
    }

    /**
     * Gets the date of this instance.
     *
     * @return the {@link LocalDate} for this instance
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Checks if this instance starts on the associated date.
     *
     * @return true if it starts on the date, false otherwise
     */
    public boolean isStartsOnDate() {
        return startsOnDate;
    }

    /**
     * Checks if this instance ends on the associated date.
     *
     * @return true if it ends on the date, false otherwise
     */
    public boolean isEndsOnDate() {
        return endsOnDate;
    }
}
