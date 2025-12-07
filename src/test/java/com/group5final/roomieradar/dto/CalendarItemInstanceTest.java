// java
package com.group5final.roomieradar.dto;

import com.group5final.roomieradar.entities.CalendarItem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CalendarItemInstanceTest {

    @Test
    void constructorAndGetters_returnProvidedValues() {
        CalendarItem item = new CalendarItem();
        item.setId(1L);
        LocalDate date = LocalDate.of(2025, 12, 1);

        CalendarItemInstance inst = new CalendarItemInstance(item, date, true, false);

        assertSame(item, inst.getItem());
        assertEquals(date, inst.getDate());
        assertTrue(inst.isStartsOnDate());
        assertFalse(inst.isEndsOnDate());
    }

    @Test
    void flagsBothTrue_andBothFalse_behaveAsExpected() {
        CalendarItem item = new CalendarItem();
        LocalDate date = LocalDate.of(2025, 12, 2);

        CalendarItemInstance bothTrue = new CalendarItemInstance(item, date, true, true);
        assertTrue(bothTrue.isStartsOnDate());
        assertTrue(bothTrue.isEndsOnDate());

        CalendarItemInstance bothFalse = new CalendarItemInstance(item, date, false, false);
        assertFalse(bothFalse.isStartsOnDate());
        assertFalse(bothFalse.isEndsOnDate());
    }

    @Test
    void nullItem_isAllowed_andReturnedAsNull() {
        LocalDate date = LocalDate.of(2025, 11, 30);
        CalendarItemInstance inst = new CalendarItemInstance(null, date, false, true);

        assertNull(inst.getItem());
        assertEquals(date, inst.getDate());
        assertFalse(inst.isStartsOnDate());
        assertTrue(inst.isEndsOnDate());
    }

    @Test
    void nullDate_isAllowed_andGetDateReturnsNull() {
        CalendarItem item = new CalendarItem();
        CalendarItemInstance inst = new CalendarItemInstance(item, null, false, false);

        assertSame(item, inst.getItem());
        assertNull(inst.getDate());
        assertFalse(inst.isStartsOnDate());
        assertFalse(inst.isEndsOnDate());
    }

    @Test
    void usingDateWhenNull_throwsNullPointerException() {
        CalendarItem item = new CalendarItem();
        CalendarItemInstance inst = new CalendarItemInstance(item, null, true, false);

        assertNull(inst.getDate());
        assertThrows(NullPointerException.class, () -> inst.getDate().plusDays(1));
    }
}
