package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private Household testHousehold;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        //Setup test household
        testHousehold = new Household();
        testHousehold.setId(1L);
        testHousehold.setName("Test Household");
        testHousehold.setPassword("password123");

        //Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setHousehold(testHousehold);

        //Setup test event
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setName("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setEventDate(LocalDateTime.now().plusDays(1));
        testEvent.setUserid(testUser);
        testEvent.setHousehold(testHousehold);
    }

    @Nested
    @DisplayName("getEventsForCurrentUserHousehold Tests")
    class GetEventsForCurrentUserHouseholdTests {

        @Test
        @DisplayName("Should return events when user has household")
        void shouldReturnEventsWhenUserHasHousehold() {
            //Arrange
            Event event1 = new Event();
            event1.setId(1L);
            event1.setName("Event 1");
            event1.setHousehold(testHousehold);

            Event event2 = new Event();
            event2.setId(2L);
            event2.setName("Event 2");
            event2.setHousehold(testHousehold);

            List<Event> expectedEvents = Arrays.asList(event1, event2);

            when(currentUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
            when(eventRepository.findByHouseholdOrderByEventDateAsc(testHousehold)).thenReturn(expectedEvents);

            //Act
            List<Event> result = eventService.getEventsForCurrentUserHousehold();

            //Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Event 1", result.get(0).getName());
            assertEquals("Event 2", result.get(1).getName());
            verify(currentUserService, times(1)).getCurrentUser();
            verify(eventRepository, times(1)).findByHouseholdOrderByEventDateAsc(testHousehold);
        }

        @Test
        @DisplayName("Should return empty list when user is not authenticated")
        void shouldReturnEmptyListWhenUserNotAuthenticated() {
            //Arrange
            when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

            //Act
            List<Event> result = eventService.getEventsForCurrentUserHousehold();

            //Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(currentUserService, times(1)).getCurrentUser();
            verify(eventRepository, never()).findByHouseholdOrderByEventDateAsc(any());
        }

        @Test
        @DisplayName("Should return empty list when user has no household")
        void shouldReturnEmptyListWhenUserHasNoHousehold() {
            //Arrange
            User userWithoutHousehold = new User();
            userWithoutHousehold.setId(2L);
            userWithoutHousehold.setUsername("nohousehold");
            userWithoutHousehold.setHousehold(null);

            when(currentUserService.getCurrentUser()).thenReturn(Optional.of(userWithoutHousehold));

            //Act
            List<Event> result = eventService.getEventsForCurrentUserHousehold();

            //Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(currentUserService, times(1)).getCurrentUser();
            verify(eventRepository, never()).findByHouseholdOrderByEventDateAsc(any());
        }

        @Test
        @DisplayName("Should return empty list when household has no events")
        void shouldReturnEmptyListWhenHouseholdHasNoEvents() {
            //Arrange
            when(currentUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
            when(eventRepository.findByHouseholdOrderByEventDateAsc(testHousehold)).thenReturn(Collections.emptyList());

            //Act
            List<Event> result = eventService.getEventsForCurrentUserHousehold();

            //Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(currentUserService, times(1)).getCurrentUser();
            verify(eventRepository, times(1)).findByHouseholdOrderByEventDateAsc(testHousehold);
        }
    }

    @Nested
    @DisplayName("getEventById Tests")
    class GetEventByIdTests {

        @Test
        @DisplayName("Should return event when id exists")
        void shouldReturnEventWhenIdExists() {
            //Arrange
            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

            //Act
            Optional<Event> result = eventService.getEventById(1L);

            //Assert
            assertTrue(result.isPresent());
            assertEquals(testEvent.getId(), result.get().getId());
            assertEquals(testEvent.getName(), result.get().getName());
            verify(eventRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty optional when id does not exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            //Arrange
            when(eventRepository.findById(999L)).thenReturn(Optional.empty());

            //Act
            Optional<Event> result = eventService.getEventById(999L);

            //Assert
            assertFalse(result.isPresent());
            verify(eventRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should return empty optional when id is null")
        void shouldReturnEmptyWhenIdIsNull() {
            //Arrange
            when(eventRepository.findById(null)).thenReturn(Optional.empty());

            //Act
            Optional<Event> result = eventService.getEventById(null);

            //Assert
            assertFalse(result.isPresent());
            verify(eventRepository, times(1)).findById(null);
        }
    }

    @Nested
    @DisplayName("saveEvent Tests")
    class SaveEventTests {

        @Test
        @DisplayName("Should save and return event with valid data")
        void shouldSaveAndReturnEventWithValidData() {
            //Arrange
            Event newEvent = new Event();
            newEvent.setName("New Event");
            newEvent.setDescription("New Description");
            newEvent.setEventDate(LocalDateTime.now().plusDays(5));
            newEvent.setUserid(testUser);
            newEvent.setHousehold(testHousehold);

            Event savedEvent = new Event();
            savedEvent.setId(2L);
            savedEvent.setName(newEvent.getName());
            savedEvent.setDescription(newEvent.getDescription());
            savedEvent.setEventDate(newEvent.getEventDate());
            savedEvent.setUserid(testUser);
            savedEvent.setHousehold(testHousehold);

            when(eventRepository.save(newEvent)).thenReturn(savedEvent);

            //Act
            Event result = eventService.saveEvent(newEvent);

            //Assert
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals("New Event", result.getName());
            assertEquals("New Description", result.getDescription());
            verify(eventRepository, times(1)).save(newEvent);
        }

        @Test
        @DisplayName("Should update existing event")
        void shouldUpdateExistingEvent() {
            //Arrange
            testEvent.setName("Updated Event Name");
            testEvent.setDescription("Updated Description");

            when(eventRepository.save(testEvent)).thenReturn(testEvent);

            //Act
            Event result = eventService.saveEvent(testEvent);

            //Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Updated Event Name", result.getName());
            assertEquals("Updated Description", result.getDescription());
            verify(eventRepository, times(1)).save(testEvent);
        }

        @Test
        @DisplayName("Should throw exception when saving null event")
        void shouldThrowExceptionWhenSavingNullEvent() {
            //Arrange
            when(eventRepository.save(null)).thenThrow(new IllegalArgumentException("Entity must not be null"));

            //Act & Assert
            assertThrows(IllegalArgumentException.class, () -> eventService.saveEvent(null));
            verify(eventRepository, times(1)).save(null);
        }

        @Test
        @DisplayName("Should save event with minimal data")
        void shouldSaveEventWithMinimalData() {
            //Arrange
            Event minimalEvent = new Event();
            minimalEvent.setName("Minimal Event");
            minimalEvent.setUserid(testUser);
            minimalEvent.setHousehold(testHousehold);

            Event savedEvent = new Event();
            savedEvent.setId(3L);
            savedEvent.setName(minimalEvent.getName());
            savedEvent.setUserid(testUser);
            savedEvent.setHousehold(testHousehold);

            when(eventRepository.save(minimalEvent)).thenReturn(savedEvent);

            //Act
            Event result = eventService.saveEvent(minimalEvent);

            //Assert
            assertNotNull(result);
            assertEquals(3L, result.getId());
            assertEquals("Minimal Event", result.getName());
            assertNull(result.getDescription());
            assertNull(result.getEventDate());
            verify(eventRepository, times(1)).save(minimalEvent);
        }
    }

    @Nested
    @DisplayName("deleteEvent Tests")
    class DeleteEventTests {

        @Test
        @DisplayName("Should delete event when id exists")
        void shouldDeleteEventWhenIdExists() {
            //Arrange
            doNothing().when(eventRepository).deleteById(1L);

            //Act
            eventService.deleteEvent(1L);

            //Assert
            verify(eventRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should not throw exception when deleting non-existent id")
        void shouldNotThrowExceptionWhenDeletingNonExistentId() {
            //Arrange
            doNothing().when(eventRepository).deleteById(999L);

            //Act & Assert
            assertDoesNotThrow(() -> eventService.deleteEvent(999L));
            verify(eventRepository, times(1)).deleteById(999L);
        }

        @Test
        @DisplayName("Should throw exception when repository throws exception")
        void shouldThrowExceptionWhenRepositoryThrowsException() {
            //Arrange
            doThrow(new RuntimeException("Database error")).when(eventRepository).deleteById(1L);

            //Act & Assert
            assertThrows(RuntimeException.class, () -> eventService.deleteEvent(1L));
            verify(eventRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should handle null id gracefully or throw appropriate exception")
        void shouldHandleNullId() {
            //Arrange
            doThrow(new IllegalArgumentException("Id must not be null"))
                    .when(eventRepository).deleteById(null);

            //Act & Assert
            assertThrows(IllegalArgumentException.class, () -> eventService.deleteEvent(null));
            verify(eventRepository, times(1)).deleteById(null);
        }
    }
}